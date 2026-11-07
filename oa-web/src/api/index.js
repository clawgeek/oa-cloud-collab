import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 15000
})

http.interceptors.request.use(cfg => {
  const token = localStorage.getItem('oa_token')
  if (token) cfg.headers.Authorization = 'Bearer ' + token
  return cfg
})

http.interceptors.response.use(
  res => {
    const body = res.data
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code === 200) return body.data
      if (body.code === 401) {
        localStorage.removeItem('oa_token')
        router.push('/login')
      }
      ElMessage.error(body.msg || '请求失败')
      return Promise.reject(new Error(body.msg || '请求失败'))
    }
    return body
  },
  err => {
    ElMessage.error(err.response?.data?.msg || '网络异常，请检查后端服务')
    return Promise.reject(err)
  }
)

export default http

// ---- 按模块导出的接口清单（与 docs/接口清单.md 一致）----
export const authApi = {
  login: d => http.post('/auth/login', d),
  info: () => http.get('/auth/info'),
  password: d => http.put('/auth/password', d),
  logout: () => http.post('/auth/logout')
}
export const dashboardApi = { overview: () => http.get('/dashboard/overview') }
export const orgApi = {
  depts: () => http.get('/org/depts'),
  posts: () => http.get('/org/posts'),
  roles: () => http.get('/org/roles'),
  menus: () => http.get('/org/menus'),
  users: p => http.get('/org/users', { params: p }),
  saveUser: d => http.post('/org/users', d),
  delUser: id => http.delete('/org/users/' + id),
  saveDept: d => http.post('/org/depts', d),
  delDept: id => http.delete('/org/depts/' + id),
  roleMenus: id => http.get('/org/role-menus', { params: { roleId: id } }),
  saveRoleMenus: d => http.put('/org/role-menus', d)
}
export const attApi = {
  today: () => http.get('/attendance/today'),
  checkin: d => http.post('/attendance/checkin', d),
  records: p => http.get('/attendance/records', { params: p }),
  summary: p => http.get('/attendance/summary', { params: p })
}
export const approvalApi = {
  defs: () => http.get('/approvals/defs'),
  instances: p => http.get('/approvals/instances', { params: p }),
  todo: p => http.get('/approvals/todo', { params: p }),
  detail: id => http.get('/approvals/instances/' + id),
  start: d => http.post('/approvals/instances', d),
  approve: (id, d) => http.post(`/approvals/${id}/approve`, d),
  reject: (id, d) => http.post(`/approvals/${id}/reject`, d),
  withdraw: id => http.post(`/approvals/${id}/withdraw`)
}
export const noticeApi = {
  list: p => http.get('/notices', { params: p }),
  unread: () => http.get('/notices/unread'),
  detail: id => http.get('/notices/' + id),
  save: d => http.post('/notices', d),
  del: id => http.delete('/notices/' + id)
}
export const docApi = {
  folders: () => http.get('/docs/folders'),
  addFolder: d => http.post('/docs/folders', d),
  delFolder: id => http.delete('/docs/folders/' + id),
  files: p => http.get('/docs/files', { params: p }),
  upload: (folderId, file) => {
    const fd = new FormData()
    fd.append('file', file)
    if (folderId) fd.append('folderId', folderId)
    return http.post('/docs/files/upload', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
  },
  downloadUrl: id => (import.meta.env.VITE_API_BASE || '/api') + '/docs/files/' + id + '/download',
  del: id => http.delete('/docs/files/' + id)
}
export const workApi = {
  projects: p => http.get('/work/projects', { params: p }),
  saveProject: d => http.post('/work/projects', d),
  board: p => http.get('/work/board', { params: p }),
  tasks: p => http.get('/work/tasks', { params: p }),
  saveTask: d => http.post('/work/tasks', d),
  move: (id, status) => http.put(`/work/tasks/${id}/status`, { status }),
  delTask: id => http.delete('/work/tasks/' + id),
  comments: id => http.get(`/work/tasks/${id}/comments`),
  comment: (id, content) => http.post(`/work/tasks/${id}/comments`, { content })
}
export const calApi = {
  events: p => http.get('/calendar/events', { params: p }),
  rooms: () => http.get('/calendar/rooms'),
  save: d => http.post('/calendar/events', d),
  del: id => http.delete('/calendar/events/' + id)
}
export const logApi = { page: p => http.get('/logs', { params: p }) }
