<template>
  <div class="page">
    <div class="page-head"><div><h2 class="page-title">部门管理</h2><p class="page-sub">维护公司组织架构树、部门负责人与编制人数</p></div>
      <el-button type="primary" :icon="Plus" @click="open()">新增部门</el-button></div>
    <el-row :gutter="16">
      <el-col :span="8"><div class="card card-pad">
        <div class="c-head"><b>组织架构</b><span class="c-sub">共 {{ depts.length }} 个节点</span></div>
        <el-tree :data="tree" :props="{ label: 'deptName', children: 'children' }" default-expand-all highlight-current
                 @node-click="onNode" :expand-on-click-node="false">
          <template #default="{ data }"><span class="tree-row"><span>{{ data.deptName }}</span><em>{{ data.staffCount }} 人</em></span></template>
        </el-tree>
      </div></el-col>
      <el-col :span="16"><div class="card card-pad">
        <div class="c-head"><b>部门明细</b><el-tag size="small" effect="plain">{{ selected ? selected.deptName : '全部' }}</el-tag></div>
        <el-table :data="rows" border stripe size="default">
          <el-table-column prop="deptName" label="部门名称" min-width="150" />
          <el-table-column prop="deptCode" label="部门编码" width="130" />
          <el-table-column prop="leaderName" label="负责人" width="110" />
          <el-table-column prop="staffCount" label="编制人数" width="100" />
          <el-table-column prop="sortNo" label="排序" width="80" />
          <el-table-column label="状态" width="90"><template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="plain">{{ row.status === 1 ? '启用' : '停用' }}</el-tag></template></el-table-column>
          <el-table-column label="操作" width="140" fixed="right"><template #default="{ row }">
            <el-button link type="primary" @click="open(row)">编辑</el-button>
            <el-button link type="danger" @click="del(row)">删除</el-button>
          </template></el-table-column>
        </el-table>
      </div></el-col>
    </el-row>
    <el-dialog v-model="dlg" title="部门信息" width="460px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="上级部门"><el-select v-model="form.parentId" style="width:100%"><el-option label="根节点" :value="0" /><el-option v-for="d in depts" :key="d.id" :label="d.deptName" :value="d.id" /></el-select></el-form-item>
        <el-form-item label="部门名称"><el-input v-model="form.deptName" /></el-form-item>
        <el-form-item label="部门编码"><el-input v-model="form.deptCode" /></el-form-item>
        <el-form-item label="负责人"><el-select v-model="form.leaderId" style="width:100%" clearable><el-option v-for="u in users" :key="u.id" :label="u.realName" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="排序号"><el-input-number v-model="form.sortNo" :min="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dlg = false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { orgApi } from '@/api'

const depts = ref([]), users = ref([]), selected = ref(null), dlg = ref(false)
const form = ref({})
const tree = computed(() => {
  const map = new Map(), roots = []
  depts.value.forEach(d => map.set(d.id, { ...d, children: [] }))
  depts.value.forEach(d => { const node = map.get(d.id); if (d.parentId && map.has(d.parentId)) map.get(d.parentId).children.push(node); else roots.push(node) })
  return roots
})
const rows = computed(() => selected.value ? depts.value.filter(d => d.id === selected.value.id || d.parentId === selected.value.id) : depts.value)
async function load() {
  depts.value = await orgApi.depts()
  const r = await orgApi.users({ size: 100 }); users.value = r.rows || []
}
function onNode(d) { selected.value = d }
function open(row) { form.value = row ? { ...row } : { parentId: 0, deptName: '', deptCode: '', leaderId: null, sortNo: 0, status: 1 }; dlg.value = true }
async function save() { await orgApi.saveDept(form.value); ElMessage.success('保存成功'); dlg.value = false; load() }
async function del(row) { await ElMessageBox.confirm('确认删除部门「' + row.deptName + '」？', '提示', { type: 'warning' }); await orgApi.delDept(row.id); ElMessage.success('已删除'); selected.value = null; load() }
onMounted(load)
</script>
<style scoped>.c-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; font-size: 14px; } .c-sub { font-size: 11px; color: #a4adbd; font-weight: 400; } .tree-row { display: flex; width: 100%; justify-content: space-between; padding-right: 10px; font-size: 13px; } .tree-row em { color: #a4adbd; font-style: normal; font-size: 11px; }</style>
