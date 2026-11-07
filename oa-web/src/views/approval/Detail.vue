<template>
  <div class="page">
    <div class="page-head"><div><h2 class="page-title">审批详情</h2><p class="page-sub">表单快照、流程节点与完整审批留痕</p></div><el-button @click="$router.back()">返回</el-button></div>
    <el-row :gutter="16"><el-col :span="16">
      <div class="card card-pad mb16"><div class="c-head"><b>{{ inst.title || '加载中' }}</b><el-tag :type="statusType(inst.status)">{{ statusText(inst.status) }}</el-tag></div>
        <el-descriptions :column="2" border size="small"><el-descriptions-item label="单号">{{ inst.id }}</el-descriptions-item><el-descriptions-item label="业务类型">{{ inst.bizType }}</el-descriptions-item><el-descriptions-item label="申请人">{{ inst.applicantName }}</el-descriptions-item><el-descriptions-item label="提交时间">{{ inst.createTime }}</el-descriptions-item><el-descriptions-item label="当前节点">{{ inst.currentNodeName || '已结束' }}</el-descriptions-item><el-descriptions-item label="完成时间">{{ inst.finishTime || '—' }}</el-descriptions-item></el-descriptions>
        <el-divider content-position="left">表单内容</el-divider><el-descriptions :column="2" border size="small"><el-descriptions-item v-for="(v, k) in form" :key="k" :label="labelOf(k)">{{ v }}</el-descriptions-item></el-descriptions>
      </div>
      <div class="card card-pad"><div class="c-head"><b>审批记录</b></div>
        <el-timeline><el-timeline-item v-for="t in tasks" :key="t.id" :timestamp="t.createTime" placement="top" :type="t.action === 'REJECT' ? 'danger' : (t.status === 'DONE' ? 'success' : 'primary')">
          <div class="tl"><b>{{ t.nodeName }}</b> · {{ t.assigneeName }} <el-tag size="small" effect="plain">{{ t.status === 'DONE' ? (t.action === 'APPROVE' ? '已通过' : '已驳回') : '待处理' }}</el-tag><p>{{ t.comment || '—' }}</p></div>
        </el-timeline-item></el-timeline>
      </div>
    </el-col><el-col :span="8"><div class="card card-pad"><div class="c-head"><b>流程节点</b></div>
      <div class="step" v-for="(n, i) in nodes" :key="i" :class="{ done: isDone(n) }"><span class="sidx">{{ i + 1 }}</span><div><b>{{ n.name }}</b><small>{{ n.assignee }}</small></div></div>
    </div></el-col></el-row>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { approvalApi } from '@/api'
const route = useRoute()
const inst = ref({}), form = ref({}), tasks = ref([]), nodes = ref([])
const LABEL = { leaveType: '假期类型', startDate: '开始日期', endDate: '结束日期', days: '天数', reason: '申请事由', amount: '报销金额', category: '费用类别', hours: '加班小时', date: '加班日期', destination: '出差城市' }
function labelOf(k) { return LABEL[k] || k }
function statusText(s) { return { RUNNING: '审批中', APPROVED: '已通过', REJECTED: '已驳回', WITHDRAWN: '已撤回' }[s] || s }
function statusType(s) { return { RUNNING: 'primary', APPROVED: 'success', REJECTED: 'danger', WITHDRAWN: 'info' }[s] || 'info' }
function isDone(n) { return tasks.value.some(t => t.nodeKey === n.key && t.status === 'DONE' && t.action === 'APPROVE') }
onMounted(async () => { const d = await approvalApi.detail(route.params.id); inst.value = d.instance || {}; form.value = d.form || {}; tasks.value = d.tasks || []; nodes.value = d.nodes || [] })
</script>
<style scoped>.c-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; font-size: 14px; } .tl { font-size: 13px; } .tl p { color: #8b94a6; font-size: 12px; margin: 5px 0 0; } .step { display: flex; gap: 10px; align-items: center; padding: 11px; border: 1px solid #eef1f6; border-radius: 8px; margin-bottom: 9px; } .step.done { background: #f0fbf7; border-color: #cceee2; } .sidx { width: 24px; height: 24px; border-radius: 50%; background: #eef3ff; color: #2f6bff; display: flex; align-items: center; justify-content: center; font-size: 11px; } .step b { display: block; font-size: 12px; } .step small { font-size: 10px; color: #98a1b2; }</style>
