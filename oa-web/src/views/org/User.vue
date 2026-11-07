<template>
  <div class="page">
    <div class="page-head"><div><h2 class="page-title">员工管理</h2><p class="page-sub">员工台账、岗位归属、账号状态与入职信息维护</p></div>
      <el-button type="primary" :icon="Plus" @click="open()">新增员工</el-button></div>
    <div class="card card-pad">
      <div class="toolbar">
        <el-input v-model="q.kw" placeholder="姓名 / 账号 / 手机号" style="width:220px" :prefix-icon="Search" clearable @keyup.enter="load(1)" />
        <el-select v-model="q.deptId" placeholder="全部部门" style="width:170px" clearable><el-option v-for="d in depts" :key="d.id" :label="d.deptName" :value="d.id" /></el-select>
        <el-select v-model="q.status" placeholder="全部状态" style="width:130px" clearable><el-option label="在职" :value="1" /><el-option label="停用" :value="0" /></el-select>
        <el-button type="primary" @click="load(1)">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <span class="spacer"></span><span class="total">共 {{ total }} 条</span>
      </div>
      <el-table :data="rows" border stripe v-loading="loading">
        <el-table-column prop="id" label="工号" width="80" />
        <el-table-column label="姓名" width="110"><template #default="{ row }"><span class="who"><i>{{ row.realName.slice(0,1) }}</i>{{ row.realName }}</span></template></el-table-column>
        <el-table-column prop="username" label="登录账号" width="120" />
        <el-table-column prop="deptName" label="所属部门" min-width="130" />
        <el-table-column prop="postName" label="岗位" width="130" />
        <el-table-column prop="phone" label="手机号" width="135" />
        <el-table-column prop="hireDate" label="入职日期" width="120" />
        <el-table-column label="状态" width="90"><template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="plain">{{ row.status === 1 ? '在职' : '停用' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="140" fixed="right"><template #default="{ row }"><el-button link type="primary" @click="open(row)">编辑</el-button><el-button link type="danger" @click="del(row)">删除</el-button></template></el-table-column>
      </el-table>
      <div class="pager"><el-pagination background layout="prev, pager, next, sizes" :total="total" v-model:current-page="q.page" v-model:page-size="q.size" :page-sizes="[10, 20, 50]" @current-change="load()" @size-change="load(1)" /></div>
    </div>
    <el-dialog v-model="dlg" :title="form.id ? '编辑员工' : '新增员工'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-row :gutter="12"><el-col :span="12"><el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="登录账号"><el-input v-model="form.username" :disabled="!!form.id" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="所属部门"><el-select v-model="form.deptId" style="width:100%"><el-option v-for="d in depts" :key="d.id" :label="d.deptName" :value="d.id" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="岗位"><el-select v-model="form.postId" style="width:100%"><el-option v-for="p in posts" :key="p.id" :label="p.postName" :value="p.id" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="入职日期"><el-date-picker v-model="form.hireDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="账号状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="在职" /></el-form-item></el-col>
        </el-row>
        <el-alert v-if="!form.id" title="新增账号初始密码为 123456，使用 PBKDF2 加盐散列存储" type="info" :closable="false" />
      </el-form>
      <template #footer><el-button @click="dlg = false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { orgApi } from '@/api'
const rows = ref([]), depts = ref([]), posts = ref([]), total = ref(0), loading = ref(false), dlg = ref(false)
const q = reactive({ kw: '', deptId: null, status: null, page: 1, size: 10 })
const form = ref({})
async function load(p) { if (p) q.page = p; loading.value = true; try { const r = await orgApi.users(q); rows.value = r.rows || []; total.value = r.total || 0 } finally { loading.value = false } }
function reset() { Object.assign(q, { kw: '', deptId: null, status: null, page: 1 }); load() }
function open(row) { form.value = row ? { ...row } : { realName: '', username: '', deptId: null, postId: null, phone: '', email: '', hireDate: '', status: 1 }; dlg.value = true }
async function save() { await orgApi.saveUser(form.value); ElMessage.success('保存成功'); dlg.value = false; load() }
async function del(row) { await ElMessageBox.confirm('确认删除员工「' + row.realName + '」？', '提示', { type: 'warning' }); await orgApi.delUser(row.id); ElMessage.success('已删除'); load() }
onMounted(async () => { depts.value = await orgApi.depts(); posts.value = await orgApi.posts(); load() })
</script>
<style scoped>.toolbar .spacer { flex: 1; } .total { font-size: 12px; color: #8b94a6; } .pager { display: flex; justify-content: flex-end; margin-top: 16px; } .who { display: flex; align-items: center; gap: 8px; } .who i { width: 24px; height: 24px; border-radius: 50%; background: #eef3ff; color: #2f6bff; font-style: normal; font-size: 11px; display: inline-flex; align-items: center; justify-content: center; }</style>
