<template>
  <div class="page"><div class="page-head"><div><h2 class="page-title">项目列表</h2><p class="page-sub">项目台账、负责人、周期与任务完成度</p></div><el-button type="primary" :icon="Plus" @click="dlg = true">新建项目</el-button></div>
    <div class="card card-pad"><el-table :data="rows" border stripe>
      <el-table-column prop="projectCode" label="项目编号" width="150" /><el-table-column prop="name" label="项目名称" min-width="220" />
      <el-table-column prop="ownerName" label="负责人" width="110" /><el-table-column label="状态" width="100"><template #default="{ row }"><el-tag size="small" :type="row.status === 'ACTIVE' ? 'success' : 'info'" effect="plain">{{ row.status === 'ACTIVE' ? '进行中' : (row.status === 'PLANNING' ? '规划中' : '已结项') }}</el-tag></template></el-table-column>
      <el-table-column label="任务进度" min-width="180"><template #default="{ row }"><el-progress :percentage="row.taskCount ? Math.round(row.doneCount / row.taskCount * 100) : 0" /><small>{{ row.doneCount }} / {{ row.taskCount }} 已完成</small></template></el-table-column>
      <el-table-column prop="startDate" label="开始" width="120" /><el-table-column prop="endDate" label="结束" width="120" />
    </el-table></div>
    <el-dialog v-model="dlg" title="新建项目" width="520px"><el-form :model="form" label-width="90px"><el-form-item label="项目名称"><el-input v-model="form.name" /></el-form-item><el-form-item label="项目编号"><el-input v-model="form.projectCode" /></el-form-item><el-form-item label="周期"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" placeholder="开始" /> ~ <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" placeholder="结束" /></el-form-item><el-form-item label="项目描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item></el-form><template #footer><el-button @click="dlg = false">取消</el-button><el-button type="primary" @click="save">创建</el-button></template></el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { workApi } from '@/api'
const rows = ref([]), dlg = ref(false), form = reactive({ name: '', projectCode: '', startDate: '', endDate: '', description: '', status: 'ACTIVE' })
async function load() { rows.value = await workApi.projects({}) }
async function save() { await workApi.saveProject(form); ElMessage.success('项目已创建'); dlg.value = false; load() }
onMounted(load)
</script>
