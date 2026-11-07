<template>
  <div class="page">
    <div class="page-head"><div><h2 class="page-title">我的申请</h2><p class="page-sub">发起请假 / 报销 / 加班 / 出差，流程按定义串行流转并全程留痕</p></div>
      <el-button type="primary" :icon="Plus" @click="startDlg = true">发起审批</el-button></div>
    <div class="card card-pad">
      <div class="toolbar"><el-input v-model="kw" placeholder="搜索标题" style="width:220px" clearable @keyup.enter="load(1)" /><el-select v-model="status" placeholder="全部状态" style="width:150px" clearable><el-option label="审批中" value="RUNNING" /><el-option label="已通过" value="APPROVED" /><el-option label="已驳回" value="REJECTED" /><el-option label="已撤回" value="WITHDRAWN" /></el-select><el-button type="primary" @click="load(1)">查询</el-button><span class="spacer"></span><span class="total">共 {{ total }} 条</span></div>
      <el-table :data="rows" border stripe v-loading="loading">
        <el-table-column prop="id" label="单号" width="80" /><el-table-column prop="title" label="标题" min-width="260" show-overflow-tooltip />
        <el-table-column prop="bizType" label="业务类型" width="110"><template #default="{ row }"><el-tag size="small" effect="plain">{{ typeText(row.bizType) }}</el-tag></template></el-table-column>
        <el-table-column prop="currentNodeName" label="当前节点" width="140" /><el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag size="small" :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag></template></el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right"><template #default="{ row }"><el-button link type="primary" @click="view(row)">详情</el-button><el-button v-if="row.status === 'RUNNING'" link type="warning" @click="withdraw(row)">撤回</el-button></template></el-table-column>
      </el-table>
      <div class="pager"><el-pagination background layout="prev, pager, next" :total="total" v-model:current-page="page" :page-size="10" @current-change="load()" /></div>
    </div>

    <el-dialog v-model="startDlg" title="发起审批" width="620px" class="start-dlg">
      <el-form :model="form" label-width="100px">
        <el-form-item label="审批类型"><el-radio-group v-model="form.procKey"><el-radio-button v-for="d in defs" :key="d.procKey" :value="d.procKey">{{ d.procName }}</el-radio-button></el-radio-group></el-form-item>
        <el-form-item label="申请标题"><el-input v-model="form.title" placeholder="例如：年假申请 11/13 - 11/14" /></el-form-item>
        <el-divider content-position="left">表单内容</el-divider>
        <el-row :gutter="12">
          <template v-if="form.procKey === 'leave'">
            <el-col :span="12"><el-form-item label="假期类型"><el-select v-model="form.form.leaveType" style="width:100%"><el-option v-for="x in ['年假','事假','病假','调休','婚假']" :key="x" :label="x" :value="x" /></el-select></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="天数"><el-input-number v-model="form.form.days" :min="0.5" :step="0.5" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="开始日期"><el-date-picker v-model="form.form.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="结束日期"><el-date-picker v-model="form.form.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          </template>
          <template v-else-if="form.procKey === 'expense'">
            <el-col :span="12"><el-form-item label="报销金额"><el-input-number v-model="form.form.amount" :min="0" :precision="2" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="费用类别"><el-select v-model="form.form.category" style="width:100%"><el-option v-for="x in ['差旅','市场物料','办公用品','招待']" :key="x" :label="x" :value="x" /></el-select></el-form-item></el-col>
          </template>
          <template v-else-if="form.procKey === 'overtime'">
            <el-col :span="12"><el-form-item label="加班日期"><el-date-picker v-model="form.form.date" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="加班小时"><el-input-number v-model="form.form.hours" :min="0.5" :step="0.5" /></el-form-item></el-col>
          </template>
          <template v-else>
            <el-col :span="12"><el-form-item label="出差城市"><el-input v-model="form.form.destination" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="天数"><el-input-number v-model="form.form.days" :min="1" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="开始日期"><el-date-picker v-model="form.form.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="结束日期"><el-date-picker v-model="form.form.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          </template>
          <el-col :span="24"><el-form-item label="申请事由"><el-input v-model="form.form.reason" type="textarea" :rows="3" placeholder="请填写具体事由" /></el-form-item></el-col>
        </el-row>
        <el-alert type="info" :closable="false" :title="'流程节点：' + nodeText" />
      </el-form>
      <template #footer><el-button @click="startDlg = false">取消</el-button><el-button type="primary" :loading="submitting" @click="submit">提交申请</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { approvalApi } from '@/api'
const router = useRouter()
const rows = ref([]), defs = ref([]), total = ref(0), page = ref(1), kw = ref(''), status = ref(''), loading = ref(false)
const startDlg = ref(false), submitting = ref(false)
const form = reactive({ procKey: 'leave', title: '', form: { leaveType: '年假', days: 2, startDate: '', endDate: '', reason: '' } })
const nodeText = computed(() => { const d = defs.value.find(x => x.procKey === form.procKey); if (!d) return ''; try { return JSON.parse(d.nodesJson).map(n => n.name).join(' → ') } catch (e) { return '' } })
function typeText(t) { return { LEAVE: '请假', EXPENSE: '报销', OVERTIME: '加班', TRIP: '出差' }[t] || t }
function statusText(s) { return { RUNNING: '审批中', APPROVED: '已通过', REJECTED: '已驳回', WITHDRAWN: '已撤回' }[s] || s }
function statusType(s) { return { RUNNING: 'primary', APPROVED: 'success', REJECTED: 'danger', WITHDRAWN: 'info' }[s] || 'info' }
async function load(p) { if (p) page.value = p; loading.value = true; try { const r = await approvalApi.instances({ scope: 'mine', status: status.value, kw: kw.value, page: page.value, size: 10 }); rows.value = r.rows || []; total.value = r.total || 0 } finally { loading.value = false } }
async function submit() {
  if (!form.title) return ElMessage.warning('请填写申请标题')
  submitting.value = true
  try { await approvalApi.start({ procKey: form.procKey, title: form.title, form: { ...form.form } }); ElMessage.success('申请已提交，流程已启动'); startDlg.value = false; load() }
  finally { submitting.value = false }
}
async function withdraw(row) { await ElMessageBox.confirm('确认撤回该申请？', '提示', { type: 'warning' }); await approvalApi.withdraw(row.id); ElMessage.success('已撤回'); load() }
function view(row) { router.push('/approval/detail/' + row.id) }
onMounted(async () => { defs.value = await approvalApi.defs(); load() })
</script>
<style scoped>.toolbar .spacer { flex: 1; } .total { font-size: 12px; color: #8b94a6; } .pager { display: flex; justify-content: flex-end; margin-top: 16px; }</style>
