<template>
  <div class="page">
    <div class="page-head"><div><h2 class="page-title">角色与权限</h2><p class="page-sub">RBAC 三级模型：用户 → 角色 → 菜单/按钮权限 + 数据范围</p></div></div>
    <el-row :gutter="16">
      <el-col :span="8"><div class="card card-pad">
        <div class="c-head"><b>角色列表</b><span class="c-sub">{{ roles.length }} 个角色</span></div>
        <div v-for="r in roles" :key="r.id" class="role" :class="{ on: cur && cur.id === r.id }" @click="choose(r)">
          <div class="role-ic"><el-icon><Lock /></el-icon></div>
          <div class="role-txt"><b>{{ r.roleName }}</b><span>{{ r.roleCode }} · {{ scopeText(r.dataScope) }}</span></div>
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div></el-col>
      <el-col :span="16"><div class="card card-pad">
        <div class="c-head"><b>{{ cur ? cur.roleName : '请选择角色' }} · 菜单权限</b><el-button type="primary" size="small" :disabled="!cur" @click="save">保存授权</el-button></div>
        <el-alert type="info" :closable="false" title="权限码由后端 JwtInterceptor + @RequirePerm 在接口层二次校验，前端仅做展示控制" class="mb16" />
        <el-tree ref="treeRef" :data="menuTree" show-checkbox node-key="id" :props="{ label: 'menuName', children: 'children' }" default-expand-all>
          <template #default="{ data }"><span>{{ data.menuName }}<em v-if="data.permCode">{{ data.permCode }}</em></span></template>
        </el-tree>
      </div></el-col>
    </el-row>
  </div>
</template>
<script setup>
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { orgApi } from '@/api'
const roles = ref([]), menus = ref([]), cur = ref(null), treeRef = ref(), checked = ref([])
const menuTree = computed(() => {
  const map = new Map(), roots = []
  menus.value.forEach(m => map.set(m.id, { ...m, children: [] }))
  menus.value.forEach(m => { const n = map.get(m.id); if (m.parentId && map.has(m.parentId)) map.get(m.parentId).children.push(n); else roots.push(n) })
  return roots
})
function scopeText(s) { return { ALL: '全部数据', DEPT: '本部门', SELF: '仅本人' }[s] || s }
async function choose(r) { cur.value = r; checked.value = await orgApi.roleMenus(r.id); await nextTick(); treeRef.value?.setCheckedKeys(checked.value, false) }
async function save() {
  const keys = [...treeRef.value.getCheckedKeys(), ...treeRef.value.getHalfCheckedKeys()]
  await orgApi.saveRoleMenus({ roleId: cur.value.id, menuIds: keys })
  ElMessage.success('权限已保存，重新登录后生效')
}
onMounted(async () => { roles.value = await orgApi.roles(); menus.value = await orgApi.menus(); if (roles.value.length) choose(roles.value[0]) })
</script>
<style scoped>.c-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; font-size: 14px; } .c-sub { font-size: 11px; color: #a4adbd; font-weight: 400; } .role { display: flex; align-items: center; gap: 12px; padding: 12px; border: 1px solid #eef1f6; border-radius: 9px; margin-bottom: 9px; cursor: pointer; } .role.on { border-color: #2f6bff; background: #f4f7ff; } .role-ic { width: 34px; height: 34px; border-radius: 8px; background: #eef3ff; color: #2f6bff; display: flex; align-items: center; justify-content: center; } .role-txt { flex: 1; } .role-txt b { display: block; font-size: 13px; } .role-txt span { font-size: 11px; color: #98a1b2; } .el-tree em { font-style: normal; font-size: 11px; color: #a4adbd; margin-left: 10px; }</style>
