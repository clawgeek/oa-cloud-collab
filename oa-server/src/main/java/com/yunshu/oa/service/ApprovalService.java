package com.yunshu.oa.service;

import com.yunshu.oa.common.BizException;
import com.yunshu.oa.common.LoginUser;
import com.yunshu.oa.common.PageResult;
import com.yunshu.oa.common.Params;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.entity.*;
import com.yunshu.oa.jsoncodec.JsonCodec;
import com.yunshu.oa.mapper.ApprovalMapper;
import com.yunshu.oa.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/** 轻量工作流引擎：流程定义 → 实例 → 串行节点任务 → 通过/驳回/撤回 + 全程留痕 */
@Service
@RequiredArgsConstructor
public class ApprovalService {
    private final ApprovalMapper mapper;
    private final AuthMapper authMapper;
    private final OrgService orgService;

    public List<ProcessDef> defs() { return mapper.listDefs(); }

    public PageResult<FlowInstance> instances(String scope, Long userId, String status, String kw,
                                              int page, int size) {
        int p = Math.max(page, 1), s = Math.min(Math.max(size, 1), 100);
        return PageResult.of(mapper.countInstances(scope, userId, status, kw),
                mapper.pageInstances(scope, userId, status, kw, (p - 1) * s, s), p, s);
    }

    public PageResult<FlowTask> todo(Long userId, int page, int size) {
        int p = Math.max(page, 1), s = Math.min(Math.max(size, 1), 100);
        return PageResult.of(mapper.countTodo(userId), mapper.pageTodo(userId, (p - 1) * s, s), p, s);
    }

    public Map<String, Object> detail(Long id) {
        FlowInstance inst = mapper.findInstance(id);
        if (inst == null) throw new BizException("审批单不存在");
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("instance", inst);
        m.put("tasks", mapper.listTasks(id));
        m.put("form", JsonCodec.parse(inst.getFormJson() == null ? "{}" : inst.getFormJson()));
        ProcessDef def = mapper.findDef(inst.getProcKey());
        m.put("nodes", def == null ? List.of() : JsonCodec.parseArray(def.getNodesJson()));
        return m;
    }

    @Transactional
    public FlowInstance start(Map<String, Object> body) {
        String procKey = Params.s(body, "procKey");
        ProcessDef def = mapper.findDef(procKey);
        if (def == null) throw new BizException("流程定义不存在：" + procKey);
        List<Object> nodes = JsonCodec.parseArray(def.getNodesJson());
        if (nodes.isEmpty()) throw new BizException("流程未配置审批节点");

        LoginUser u = UserContext.get();
        FlowInstance inst = new FlowInstance();
        inst.setProcKey(procKey);
        inst.setBizType(def.getBizType());
        inst.setBizId(System.currentTimeMillis() % 1000000);
        inst.setTitle(Params.s(body, "title", def.getProcName() + " - " + u.getRealName()));
        inst.setApplicantId(u.getUserId());
        inst.setApplicantName(u.getRealName());
        inst.setStatus("RUNNING");
        inst.setFormJson(JsonCodec.stringify(Params.map(body.get("form"))));
        Map<String, Object> first = Params.map(nodes.get(0));
        inst.setCurrentNode(String.valueOf(first.get("key")));
        inst.setCurrentNodeName(String.valueOf(first.get("name")));
        mapper.insertInstance(inst);
        createTask(inst.getId(), first, u.getDeptId());
        return inst;
    }

    @Transactional
    public void audit(Long id, String action, String comment) {
        FlowInstance inst = mapper.findInstance(id);
        if (inst == null || !"RUNNING".equals(inst.getStatus())) throw new BizException("该审批单已结束");
        LoginUser u = UserContext.get();
        FlowTask task = mapper.findPendingTask(id, u.getUserId());
        if (task == null) throw new BizException(403, "当前节点不是待您审批，或您没有处理权限");
        mapper.finishTask(task.getId(), action, comment);

        if ("REJECT".equalsIgnoreCase(action)) {
            mapper.cancelTasks(id);
            mapper.updateInstance(id, "", "", "REJECTED");
            return;
        }
        ProcessDef def = mapper.findDef(inst.getProcKey());
        List<Object> nodes = JsonCodec.parseArray(def.getNodesJson());
        int next = -1;
        for (int i = 0; i < nodes.size(); i++) {
            if (Objects.equals(String.valueOf(Params.map(nodes.get(i)).get("key")), inst.getCurrentNode())) {
                next = i + 1; break;
            }
        }
        if (next < nodes.size()) {
            Map<String, Object> node = Params.map(nodes.get(next));
            mapper.updateInstance(id, String.valueOf(node.get("key")), String.valueOf(node.get("name")), "RUNNING");
            createTask(id, node, u.getDeptId());
        } else {
            mapper.updateInstance(id, "", "", "APPROVED");
        }
    }

    @Transactional
    public void withdraw(Long id) {
        FlowInstance inst = mapper.findInstance(id);
        if (inst == null || !"RUNNING".equals(inst.getStatus())) throw new BizException("只有进行中的申请可以撤回");
        if (!Objects.equals(inst.getApplicantId(), UserContext.userId())) throw new BizException("只能撤回本人发起的申请");
        mapper.cancelTasks(id);
        mapper.updateInstance(id, "", "", "WITHDRAWN");
    }

    private void createTask(Long instanceId, Map<String, Object> node, Long deptId) {
        Long assigneeId = orgService.resolveAssignee(String.valueOf(node.get("assignee")), deptId);
        User assignee = authMapper.findById(assigneeId);
        FlowTask t = new FlowTask();
        t.setInstanceId(instanceId);
        t.setNodeKey(String.valueOf(node.get("key")));
        t.setNodeName(String.valueOf(node.get("name")));
        t.setAssigneeId(assigneeId);
        t.setAssigneeName(assignee == null ? "系统" : assignee.getRealName());
        mapper.insertTask(t);
    }
}
