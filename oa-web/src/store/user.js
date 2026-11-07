import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('oa_token') || '',
    user: JSON.parse(localStorage.getItem('oa_user') || '{}'),
    roles: JSON.parse(localStorage.getItem('oa_roles') || '[]'),
    roleNames: JSON.parse(localStorage.getItem('oa_roleNames') || '[]'),
    perms: JSON.parse(localStorage.getItem('oa_perms') || '[]'),
    menus: JSON.parse(localStorage.getItem('oa_menus') || '[]')
  }),
  getters: {
    logged: s => !!s.token,
    isAdmin: s => s.roles.includes('ADMIN'),
    displayName: s => s.user.realName || '未登录'
  },
  actions: {
    setLogin(data) {
      this.token = data.token
      this.user = data.user
      this.roles = data.roles
      this.roleNames = data.roleNames || []
      this.perms = data.perms
      this.menus = data.menus
      localStorage.setItem('oa_token', data.token)
      localStorage.setItem('oa_user', JSON.stringify(data.user))
      localStorage.setItem('oa_roles', JSON.stringify(data.roles))
      localStorage.setItem('oa_roleNames', JSON.stringify(data.roleNames || []))
      localStorage.setItem('oa_perms', JSON.stringify(data.perms))
      localStorage.setItem('oa_menus', JSON.stringify(data.menus))
    },
    setInfo(data) {
      this.user = data.user
      this.roles = data.roles
      this.roleNames = data.roleNames || []
      this.perms = data.perms
      this.menus = data.menus
      localStorage.setItem('oa_user', JSON.stringify(data.user))
      localStorage.setItem('oa_roles', JSON.stringify(data.roles))
      localStorage.setItem('oa_roleNames', JSON.stringify(data.roleNames || []))
      localStorage.setItem('oa_perms', JSON.stringify(data.perms))
      localStorage.setItem('oa_menus', JSON.stringify(data.menus))
    },
    has(perm) { return this.perms.includes('*:*') || this.perms.includes(perm) },
    logout() {
      this.token = ''; this.user = {}; this.roles = []; this.perms = []; this.menus = []
      for (const k of ['oa_token', 'oa_user', 'oa_roles', 'oa_roleNames', 'oa_perms', 'oa_menus']) {
        localStorage.removeItem(k)
      }
    }
  }
})
