<template>
  <div class="page">
    <div class="page-head"><div><h2 class="page-title">待我审批</h2><p class="page-sub">审批任务由流程引擎按节点自动派发，通过后推进到下一节点</p></div>
      <el-button :icon="Refresh" @click="load">刷新</el-button></div>
    <el-row :gutter="16" class="mb16"><el-col :span="6" v-for="c in cards" :key="c.t"><div class="stat-card"><div class="stat-label">{{ c.t }}</div><div class="stat-value" :style="{ color: c.c }">{{ c.v }}</div></div></el-col></el-row>
    <div class="card card-pad">
      <div class="c-head"><b>待办任务</b><el-tag type="primary" effect="plain" size="small">{{ total }} 条</el-tag></div>
      <el-table :data="rows" border stripe v-loading="loading">
        <el-table-column prop="instanceId" label="单号" width="90" /><el-table-column prop="nodeName" label="审批节点" width="160" />
        <el-table-column label="申请标题" min-width="260"><template #default="{ row }"><span class="link" @click="view(row)">{{ titleOf(row) }}</span></template></el-table-column>
        <el-table-column prop="assigneeName" label="审批人" width="110" /><el-table-column prop="createTime" label="到达时间" width="170" />
        <el-table-column label="操作" width="190" fixed="right"><template #default="{ row }"><el-button link type="primary" @click="view(row)">查看</el-button><el-button link type="success" @click="audit(row, 'APPROVE')">通过</el-button><el-button link type="danger" @click="audit(row, 'REJECT')">驳回</el-button></template></el-table-column>
      </el-table>
      <div class="pager"><el-pagination background layout="prev, pager, next" :total="total" v-model:current-page="page" :page-size="10" @current-change="load" /></div>
    </div>
    <el-dialog v-model="dlg" :title="action === 'APPROVE' ? '审批通过' : '驳回申请'" width="460px">
      <el-form label-width="80px"><el-form-item label="审批意见"><el-input v-model="comment" type="textarea" :rows="3" :placeholder="action === 'APPROVE' ? '填写通过意见（可选）' : '请填写驳回原因'" /></el-form-item></el-form>
      <template #footer><el-button @click="dlg = false">取消</el-button><el-button :type="action === 'APPROVE' ? 'primary' : 'danger'" @click="confirm">确认{{ action === 'APPROVE' ? '通过' : '驳回' }}</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { approvalApi } from '@/api'
const router = useRouter()
const rows = ref([]), total = ref(0), page = ref(1), loading = ref(false), dlg = ref(false), action = ref('APPROVE'), comment = ref(''), cur = ref(null)
const cards = computed(() => [
  { t: '待我审批', v: total.value, c: '#2f6bff' }, { t: '今日已处理', v: 3, c: '#20b58a' },
  { t: '流程定义', v: 4, c: '#f7a13c' }, { t: '平均耗时', v: '1.4h', c: '#8b64e8' }
])
function titleOf(row) { return row.instanceTitle || ('审批单 #' + row.instanceId) }
async function load() { loading.value = true; try { const r = await approvalApi.todo({ page: page.value, size: 10 }); rows.value = r.rows || []; total.value = r.total || 0 } finally { loading.value = false } }
function view(row) { router.push('/approval/detail/' + row.instanceId) }
function audit(row, a) { cur.value = row; action.value = a; comment.value = a === 'APPROVE' ? '同意' : ''; dlg.value = true }
async function confirm() {
  const id = cur.value.instanceId
  if (action.value === 'APPROVE') await approvalApi.approve(id, { comment: comment.value || '同意' })
  else await approvalApi.reject(id, { comment: comment.value || '不同意' })
  ElMessage.success(action.value === 'APPROVE' ? '已通过，流程已推进' : '已驳回'); dlg.value = false; load()
}
onMounted(load)
</script>
<style scoped>.c-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; font-size: 14px; } .link { color: #2f6bff; cursor: pointer; } .pager { display: flex; justify-content: flex-end; margin-top: 16px; }</style>
