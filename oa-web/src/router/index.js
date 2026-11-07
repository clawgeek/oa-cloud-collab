import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const Layout = () => import('@/layout/index.vue')

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { public: true, title: '登录' } },
  {
    path: '/', component: Layout, redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '工作台', icon: 'DataBoard' } },
      { path: 'org/dept', name: 'OrgDept', component: () => import('@/views/org/Dept.vue'), meta: { title: '部门管理', icon: 'OfficeBuilding' } },
      { path: 'org/user', name: 'OrgUser', component: () => import('@/views/org/User.vue'), meta: { title: '员工管理', icon: 'User' } },
      { path: 'org/role', name: 'OrgRole', component: () => import('@/views/org/Role.vue'), meta: { title: '角色权限', icon: 'Lock' } },
      { path: 'attendance', name: 'Attendance', component: () => import('@/views/Attendance.vue'), meta: { title: '考勤管理', icon: 'AlarmClock' } },
      { path: 'approval/mine', name: 'ApprovalMine', component: () => import('@/views/approval/Mine.vue'), meta: { title: '我的申请', icon: 'Document' } },
      { path: 'approval/todo', name: 'ApprovalTodo', component: () => import('@/views/approval/Todo.vue'), meta: { title: '待我审批', icon: 'Tickets' } },
      { path: 'approval/def', name: 'ApprovalDef', component: () => import('@/views/approval/Def.vue'), meta: { title: '流程定义', icon: 'Operation' } },
      { path: 'approval/detail/:id', name: 'ApprovalDetail', component: () => import('@/views/approval/Detail.vue'), meta: { title: '审批详情', hidden: true } },
      { path: 'notice', name: 'Notice', component: () => import('@/views/Notice.vue'), meta: { title: '公告通知', icon: 'Bell' } },
      { path: 'doc', name: 'DocCenter', component: () => import('@/views/DocCenter.vue'), meta: { title: '云文档', icon: 'FolderOpened' } },
      { path: 'work/board', name: 'WorkBoard', component: () => import('@/views/work/Board.vue'), meta: { title: '项目看板', icon: 'Grid' } },
      { path: 'work/project', name: 'WorkProject', component: () => import('@/views/work/Project.vue'), meta: { title: '项目列表', icon: 'Files' } },
      { path: 'calendar', name: 'Calendar', component: () => import('@/views/Calendar.vue'), meta: { title: '日程会议', icon: 'Calendar' } },
      { path: 'log', name: 'SysLog', component: () => import('@/views/SysLog.vue'), meta: { title: '系统审计', icon: 'Monitor' } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { title: '个人中心', hidden: true } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title ? to.meta.title + ' · ' : '') + '云枢 OA'
  const store = useUserStore()
  if (to.meta.public) return next()
  if (!store.logged) return next({ path: '/login', query: { redirect: to.fullPath } })
  next()
})

export default router
