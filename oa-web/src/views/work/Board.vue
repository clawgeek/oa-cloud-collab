<template>
  <div class="page">
    <div class="page-head"><div><h2 class="page-title">项目看板</h2><p class="page-sub">四列 Kanban：待办 / 进行中 / 待验收 / 已完成，拖动状态由后端统一流转</p></div>
      <div><el-select v-model="projectId" placeholder="全部项目" style="width:220px" clearable @change="load"><el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" /></el-select><el-button type="primary" :icon="Plus" style="margin-left:10px" @click="open()">新建任务</el-button></div></div>
    <el-row :gutter="14"><el-col :span="6" v-for="c in cols" :key="c.k"><div class="col">
      <div class="col-head"><span class="dot" :style="{ background: c.c }"></span><b>{{ c.t }}</b><el-tag size="small" effect="plain">{{ (board[c.k] || []).length }}</el-tag></div>
      <div class="col-body">
        <div v-for="t in board[c.k] || []" :key="t.id" class="tcard" @click="cur = t; drawer = true">
          <div class="t-top"><el-tag size="small" :type="priType(t.priority)" effect="plain">{{ priText(t.priority) }}</el-tag><span class="proj">{{ t.projectName }}</span></div>
          <div class="t-title">{{ t.title }}</div>
          <el-progress :percentage="t.progress" :stroke-width="4" :show-text="false" :color="c.c" />
          <div class="t-foot"><span><el-icon><User /></el-icon>{{ t.assigneeName || '未分配' }}</span><span>{{ t.dueDate || '—' }}</span></div>
        </div>
        <el-empty v-if="!(board[c.k] || []).length" description="暂无任务" :image-size="42" />
      </div>
      <div class="col-add" @click="open(c.k)">+ 添加任务</div>
    </div></el-col></el-row>
    <el-dialog v-model="dlg" title="新建任务" width="540px"><el-form :model="form" label-width="90px">
      <el-form-item label="所属项目"><el-select v-model="form.projectId" style="width:100%"><el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" /></el-select></el-form-item>
      <el-form-item label="任务标题"><el-input v-model="form.title" /></el-form-item><el-form-item label="任务描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
      <el-row :gutter="12"><el-col :span="12"><el-form-item label="优先级"><el-select v-model="form.priority" style="width:100%"><el-option label="高" value="HIGH" /><el-option label="中" value="MEDIUM" /><el-option label="低" value="LOW" /></el-select></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="负责人"><el-select v-model="form.assigneeId" style="width:100%"><el-option v-for="u in users" :key="u.id" :label="u.realName" :value="u.id" /></el-select></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option v-for="c in cols" :key="c.k" :label="c.t" :value="c.k" /></el-select></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="截止日期"><el-date-picker v-model="form.dueDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col></el-row>
    </el-form><template #footer><el-button @click="dlg = false">取消</el-button><el-button type="primary" @click="save">创建</el-button></template></el-dialog>
    <el-drawer v-model="drawer" title="任务详情" size="420px"><template v-if="cur"><h3>{{ cur.title }}</h3><p class="desc">{{ cur.description }}</p><el-descriptions :column="1" border size="small"><el-descriptions-item label="所属项目">{{ cur.projectName }}</el-descriptions-item><el-descriptions-item label="负责人">{{ cur.assigneeName }}</el-descriptions-item><el-descriptions-item label="优先级">{{ priText(cur.priority) }}</el-descriptions-item><el-descriptions-item label="进度">{{ cur.progress }}%</el-descriptions-item></el-descriptions>
      <el-divider content-position="left">流转状态</el-divider><div class="moves"><el-button v-for="c in cols" :key="c.k" size="small" :type="cur.status === c.k ? 'primary' : ''" @click="move(c.k)">{{ c.t }}</el-button></div>
      <el-divider content-position="left">评论</el-divider><div v-for="(cm, i) in comments" :key="i" class="cm"><b>{{ cm.userName }}</b><span>{{ cm.content }}</span></div>
      <el-input v-model="newComment" placeholder="写下评论..." class="mt16"><template #append><el-button @click="send">发送</el-button></template></el-input>
    </template></el-drawer>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { workApi, orgApi } from '@/api'
const cols = [{ k: 'TODO', t: '待办', c: '#9aa7bd' }, { k: 'DOING', t: '进行中', c: '#2f6bff' }, { k: 'REVIEW', t: '待验收', c: '#f7a13c' }, { k: 'DONE', t: '已完成', c: '#20b58a' }]
const board = ref({}), projects = ref([]), users = ref([]), projectId = ref(null), dlg = ref(false), drawer = ref(false), cur = ref(null), comments = ref([]), newComment = ref('')
const form = reactive({ projectId: null, title: '', description: '', priority: 'MEDIUM', status: 'TODO', assigneeId: null, dueDate: '' })
function priText(p) { return { HIGH: '高', MEDIUM: '中', LOW: '低' }[p] || p }
function priType(p) { return { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }[p] || 'info' }
async function load() { board.value = await workApi.board({ projectId: projectId.value }) }
function open(status) { Object.assign(form, { projectId: projectId.value || projects.value[0]?.id, title: '', description: '', priority: 'MEDIUM', status: status || 'TODO', assigneeId: null, dueDate: '' }); dlg.value = true }
async function save() { await workApi.saveTask(form); ElMessage.success('任务已创建'); dlg.value = false; load() }
async function move(s) { await workApi.move(cur.value.id, s); cur.value.status = s; cur.value.progress = { TODO: 0, DOING: 50, REVIEW: 90, DONE: 100 }[s]; ElMessage.success('状态已更新'); load() }
async function send() { if (!newComment.value) return; await workApi.comment(cur.value.id, newComment.value); newComment.value = ''; comments.value = await workApi.comments(cur.value.id) }
onMounted(async () => { projects.value = await workApi.projects({}); const r = await orgApi.users({ size: 100 }); users.value = r.rows || []; load() })
</script>
<style scoped>.col { background: #eef1f7; border-radius: 11px; padding: 12px; min-height: 480px; } .col-head { display: flex; align-items: center; gap: 8px; font-size: 13px; margin-bottom: 12px; } .col-head .el-tag { margin-left: auto; } .col-body { min-height: 380px; } .tcard { background: #fff; border-radius: 9px; padding: 12px; margin-bottom: 10px; cursor: pointer; box-shadow: 0 1px 4px rgba(24,39,75,.06); } .tcard:hover { box-shadow: 0 4px 14px rgba(47,107,255,.14); } .t-top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; } .proj { font-size: 10px; color: #a4adbd; } .t-title { font-size: 13px; font-weight: 600; line-height: 1.5; margin-bottom: 10px; } .t-foot { display: flex; justify-content: space-between; font-size: 11px; color: #98a1b2; margin-top: 9px; } .t-foot span { display: flex; align-items: center; gap: 4px; } .col-add { text-align: center; font-size: 12px; color: #8b94a6; padding: 9px; cursor: pointer; border: 1px dashed #cfd6e3; border-radius: 7px; } .desc { font-size: 13px; color: #6b7488; line-height: 1.8; } .moves { display: flex; gap: 7px; flex-wrap: wrap; } .cm { font-size: 12px; padding: 8px 0; border-bottom: 1px dashed #eef1f6; } .cm b { color: #2f6bff; margin-right: 8px; }</style>
