<template>
  <div class="page"><div class="page-head"><div><h2 class="page-title">个人中心</h2><p class="page-sub">账号信息、角色权限与密码维护</p></div></div>
    <el-row :gutter="16"><el-col :span="8"><div class="card card-pad" style="text-align:center">
      <div class="big-avatar">{{ store.displayName.slice(0,1) }}</div><h3>{{ store.displayName }}</h3><p>{{ user.deptName }} · {{ user.postName }}</p>
      <el-tag v-for="r in store.roleNames" :key="r" effect="plain" style="margin:4px">{{ r }}</el-tag>
    </div></el-col><el-col :span="16"><div class="card card-pad">
      <div class="c-head"><b>基本信息</b></div><el-descriptions :column="2" border><el-descriptions-item label="登录账号">{{ user.username }}</el-descriptions-item><el-descriptions-item label="姓名">{{ user.realName }}</el-descriptions-item><el-descriptions-item label="手机号">{{ user.phone }}</el-descriptions-item><el-descriptions-item label="邮箱">{{ user.email }}</el-descriptions-item><el-descriptions-item label="入职日期">{{ user.hireDate }}</el-descriptions-item><el-descriptions-item label="账号状态">正常</el-descriptions-item></el-descriptions>
      <el-divider content-position="left">修改密码</el-divider><el-form :model="pwd" label-width="90px" style="max-width:420px"><el-form-item label="原密码"><el-input v-model="pwd.oldPassword" type="password" show-password /></el-form-item><el-form-item label="新密码"><el-input v-model="pwd.newPassword" type="password" show-password /></el-form-item><el-button type="primary" @click="change">保存修改</el-button></el-form>
    </div></el-col></el-row></div>
</template>
<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'
import { useUserStore } from '@/store/user'
const store = useUserStore(), user = computed(() => store.user)
const pwd = reactive({ oldPassword: '', newPassword: '' })
async function change() { await authApi.password(pwd); ElMessage.success('密码已修改，请重新登录'); pwd.oldPassword = ''; pwd.newPassword = '' }
</script>
<style scoped>.big-avatar { width: 78px; height: 78px; border-radius: 50%; background: linear-gradient(135deg,#4a83ff,#2f6bff); color: #fff; font-size: 32px; font-weight: 700; display: flex; align-items: center; justify-content: center; margin: 14px auto; } .c-head { margin-bottom: 14px; font-size: 14px; } p { color: #8b94a6; font-size: 12px; }</style>
