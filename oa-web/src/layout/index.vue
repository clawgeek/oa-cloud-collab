<template>
  <div class="layout">
    <aside class="side" :class="{ collapsed }">
      <div class="brand">
        <div class="brand-logo">云</div>
        <div v-if="!collapsed" class="brand-text">
          <div class="brand-name">云枢 OA</div>
          <div class="brand-sub">Cloud Collaboration</div>
        </div>
      </div>
      <el-scrollbar class="menu-wrap">
        <el-menu :default-active="active" :collapse="collapsed" :collapse-transition="false"
                 background-color="#172033" text-color="#aeb8c9" active-text-color="#ffffff"
                 unique-opened router>
          <template v-for="group in groups" :key="group.key">
            <el-sub-menu v-if="group.children.length > 1" :index="group.key">
              <template #title>
                <el-icon><component :is="group.icon" /></el-icon><span>{{ group.title }}</span>
              </template>
              <el-menu-item v-for="m in group.children" :key="m.path" :index="m.path">
                <el-icon><component :is="m.icon" /></el-icon><span>{{ m.title }}</span>
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item v-else :index="group.children[0].path">
              <el-icon><component :is="group.children[0].icon" /></el-icon><span>{{ group.children[0].title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
      <div class="side-foot" v-if="!collapsed">v1.0.0 · SpringBoot 3 + Vue 3</div>
    </aside>

    <div class="main">
      <header class="topbar">
        <div class="top-left">
          <el-icon class="fold" @click="collapsed = !collapsed"><Fold v-if="!collapsed" /><Expand v-else /></el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>云枢 OA</el-breadcrumb-item>
            <el-breadcrumb-item>{{ route.meta.title || '工作台' }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="top-right">
          <span class="env-tag">DEV / H2</span>
          <el-tooltip content="全屏" placement="bottom"><el-icon class="top-icon" @click="toggleFull"><FullScreen /></el-icon></el-tooltip>
          <el-badge :value="unread" :hidden="unread === 0" class="bell">
            <el-icon class="top-icon" @click="$router.push('/notice')"><Bell /></el-icon>
          </el-badge>
          <el-dropdown @command="onCommand">
            <span class="user-chip">
              <span class="avatar">{{ store.displayName.slice(0, 1) }}</span>
              <span class="u-name">{{ store.displayName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile"><el-icon><User /></el-icon>个人中心</el-dropdown-item>
                <el-dropdown-item command="about"><el-icon><InfoFilled /></el-icon>关于系统</el-dropdown-item>
                <el-dropdown-item command="logout" divided><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <div class="tabs-bar">
        <el-tag v-for="t in tabs" :key="t.path" :effect="t.path === active ? 'dark' : 'plain'"
                :closable="t.path !== '/dashboard'" size="small" class="tab-tag"
                @click="$router.push(t.path)" @close="closeTab(t.path)">{{ t.title }}</el-tag>
      </div>
      <main class="content"><router-view v-slot="{ Component }"><transition name="fade" mode="out-in"><component :is="Component" /></transition></router-view></main>
      <footer class="foot">© 2026 云枢科技 · OA 云协同办公平台　|　<a :href="repo" target="_blank">{{ repo }}</a></footer>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { noticeApi, authApi } from '@/api'

const route = useRoute(), router = useRouter(), store = useUserStore()
const collapsed = ref(false)
const unread = ref(0)
const tabs = ref([{ path: '/dashboard', title: '工作台' }])
const repo = 'https://github.com/clawgeek/oa-cloud-collab'
const active = computed(() => route.path)

const groups = computed(() => {
  const items = [
    { path: '/dashboard', title: '工作台', icon: 'DataBoard', group: '概览' },
    { path: '/org/dept', title: '部门管理', icon: 'OfficeBuilding', group: '组织权限' },
    { path: '/org/user', title: '员工管理', icon: 'User', group: '组织权限' },
    { path: '/org/role', title: '角色权限', icon: 'Lock', group: '组织权限' },
    { path: '/attendance', title: '考勤管理', icon: 'AlarmClock', group: '考勤与审批' },
    { path: '/approval/mine', title: '我的申请', icon: 'Document', group: '考勤与审批' },
    { path: '/approval/todo', title: '待我审批', icon: 'Tickets', group: '考勤与审批' },
    { path: '/approval/def', title: '流程定义', icon: 'Operation', group: '考勤与审批' },
    { path: '/notice', title: '公告通知', icon: 'Bell', group: '协同办公' },
    { path: '/doc', title: '云文档', icon: 'FolderOpened', group: '协同办公' },
    { path: '/work/board', title: '项目看板', icon: 'Grid', group: '协同办公' },
    { path: '/work/project', title: '项目列表', icon: 'Files', group: '协同办公' },
    { path: '/calendar', title: '日程会议', icon: 'Calendar', group: '协同办公' },
    { path: '/log', title: '系统审计', icon: 'Monitor', group: '系统' }
  ]
  const map = new Map()
  items.forEach(i => {
    if (!map.has(i.group)) map.set(i.group, { key: i.group, title: i.group, icon: items.find(x => x.group === i.group).icon, children: [] })
    map.get(i.group).children.push(i)
  })
  return [...map.values()]
})

watch(() => route.path, p => {
  if (route.meta.hidden) return
  const title = route.meta.title || '页面'
  if (!tabs.value.some(t => t.path === p)) tabs.value.push({ path: p, title })
  if (tabs.value.length > 8) tabs.value.shift()
}, { immediate: true })

function closeTab(p) {
  tabs.value = tabs.value.filter(t => t.path !== p)
  if (route.path === p) router.push(tabs.value[tabs.value.length - 1]?.path || '/dashboard')
}
function toggleFull() {
  if (document.fullscreenElement) document.exitFullscreen()
  else document.documentElement.requestFullscreen()
}
async function onCommand(cmd) {
  if (cmd === 'profile') router.push('/profile')
  else if (cmd === 'about') ElMessage.info('云枢 OA v1.0.0 · SpringBoot 3 + MyBatis + Vue 3 + Element Plus')
  else if (cmd === 'logout') {
    await ElMessageBox.confirm('确认退出当前账号？', '提示', { type: 'warning' })
    try { await authApi.logout() } catch (e) { /* 忽略 */ }
    store.logout(); router.push('/login')
  }
}
onMounted(async () => {
  try { unread.value = await noticeApi.unread() } catch (e) { unread.value = 0 }
})
</script>

<style scoped>
.layout { display: flex; height: 100vh; overflow: hidden; }
.side { width: 226px; flex: none; background: var(--oa-side); display: flex; flex-direction: column; transition: width .22s; }
.side.collapsed { width: 64px; }
.brand { height: 68px; display: flex; align-items: center; gap: 11px; padding: 0 18px; border-bottom: 1px solid rgba(255,255,255,.07); }
.brand-logo { width: 34px; height: 34px; flex: none; border-radius: 9px; background: linear-gradient(135deg,#4a83ff,#2f6bff); color: #fff; font-weight: 700; display: flex; align-items: center; justify-content: center; font-size: 17px; }
.brand-name { color: #fff; font-size: 16px; font-weight: 700; letter-spacing: 1px; }
.brand-sub { color: #6f7d95; font-size: 10px; letter-spacing: .6px; margin-top: 2px; }
.menu-wrap { flex: 1; overflow-y: auto; padding: 10px 0; }
.side :deep(.el-menu) { border-right: none; }
.side :deep(.el-menu-item.is-active) { background: var(--oa-side-active) !important; border-radius: 6px; }
.side :deep(.el-menu-item), .side :deep(.el-sub-menu__title) { height: 44px; line-height: 44px; margin: 2px 10px; border-radius: 6px; }
.side-foot { padding: 14px 18px; color: #5d6a80; font-size: 11px; border-top: 1px solid rgba(255,255,255,.06); }
.main { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.topbar { height: 62px; flex: none; background: #fff; border-bottom: 1px solid var(--oa-border); display: flex; align-items: center; justify-content: space-between; padding: 0 22px; }
.top-left, .top-right { display: flex; align-items: center; gap: 16px; }
.fold, .top-icon { font-size: 18px; color: #6b7488; cursor: pointer; }
.fold:hover, .top-icon:hover { color: var(--oa-primary); }
.env-tag { font-size: 11px; color: #2f6bff; background: #eef3ff; border: 1px solid #d9e4ff; padding: 3px 9px; border-radius: 20px; font-weight: 600; }
.bell { line-height: 1; }
.user-chip { display: flex; align-items: center; gap: 8px; cursor: pointer; outline: none; }
.avatar { width: 30px; height: 30px; border-radius: 50%; background: linear-gradient(135deg,#4a83ff,#2f6bff); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 700; }
.u-name { font-size: 13px; color: #374151; }
.tabs-bar { height: 38px; flex: none; background: #fff; border-bottom: 1px solid var(--oa-border); display: flex; align-items: center; gap: 7px; padding: 0 18px; overflow-x: auto; }
.tab-tag { cursor: pointer; border-radius: 4px; }
.content { flex: 1; overflow-y: auto; background: #f3f5f9; }
.foot { height: 30px; flex: none; display: flex; align-items: center; justify-content: center; gap: 6px; font-size: 11px; color: #9aa3b2; background: #fff; border-top: 1px solid var(--oa-border); }
.foot a { color: #6f8dd6; text-decoration: none; }
.fade-enter-active, .fade-leave-active { transition: opacity .16s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
