<template>
  <div class="login">
    <div class="left">
      <div class="left-inner">
        <div class="logo-line"><span class="logo">云</span><span class="logo-name">云枢 OA</span></div>
        <h1>让协作更简单<br />让组织更高效</h1>
        <p class="lead">面向中小企业的云原生协同办公平台，覆盖组织权限、考勤、审批、文档、任务与会议全流程。</p>
        <div class="feat-grid">
          <div v-for="f in feats" :key="f.t" class="feat">
            <el-icon><component :is="f.i" /></el-icon>
            <div><b>{{ f.t }}</b><span>{{ f.d }}</span></div>
          </div>
        </div>
        <div class="tech-line">SpringBoot 3 · MyBatis · Vue 3 · Vue Router 4 · Element Plus 2</div>
      </div>
      <div class="circles"><i></i><i></i><i></i></div>
    </div>
    <div class="right">
      <div class="box">
        <div class="box-title">欢迎登录</div>
        <div class="box-sub">请使用企业内部账号登录协同办公平台</div>
        <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="submit">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入账号" :prefix-icon="User" clearable />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" placeholder="请输入密码" :prefix-icon="Lock" type="password" show-password />
          </el-form-item>
          <div class="row"><el-checkbox v-model="remember">记住账号</el-checkbox><span class="tip">忘记密码请联系管理员</span></div>
          <el-button type="primary" class="submit" :loading="loading" @click="submit">登 录</el-button>
        </el-form>
        <div class="demo">
          <div class="demo-title">演示账号 <span>（密码统一为 123456）</span></div>
          <div class="demo-tags">
            <el-tag v-for="a in accounts" :key="a.u" effect="plain" class="acc" @click="pick(a)">{{ a.n }} · {{ a.u }}</el-tag>
          </div>
        </div>
        <div class="box-foot">© 2026 云枢科技　·　<a href="https://github.com/clawgeek/oa-cloud-collab" target="_blank">GitHub 开源地址</a></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { authApi } from '@/api'
import { useUserStore } from '@/store/user'

const router = useRouter(), route = useRoute(), store = useUserStore()
const formRef = ref(), loading = ref(false), remember = ref(true)
const form = reactive({ username: '', password: '' })
const rules = { username: [{ required: true, message: '请输入账号', trigger: 'blur' }], password: [{ required: true, message: '请输入密码', trigger: 'blur' }] }
const feats = [
  { i: 'OfficeBuilding', t: '组织权限', d: '部门 · 岗位 · RBAC' },
  { i: 'Stamp', t: '流程审批', d: '请假 · 报销 · 出差' },
  { i: 'FolderOpened', t: '云文档', d: '共享 · 版本 · 下载' },
  { i: 'Grid', t: '任务协作', d: '项目看板 · 评论' },
  { i: 'AlarmClock', t: '考勤打卡', d: '迟到判定 · 月报' },
  { i: 'Calendar', t: '日程会议', d: '会议室冲突检测' }
]
const accounts = [
  { u: 'admin', n: '系统管理员' }, { u: 'hr', n: '人事专员' },
  { u: 'devlead', n: '研发总监' }, { u: 'zhangwei', n: '普通员工' }
]
function pick(a) { form.username = a.u; form.password = '123456' }
async function submit() {
  await formRef.value.validate(async ok => {
    if (!ok) return
    loading.value = true
    try {
      const data = await authApi.login(form)
      store.setLogin(data)
      if (remember.value) localStorage.setItem('oa_last_user', form.username)
      ElMessage.success('登录成功，欢迎回来，' + data.user.realName)
      router.push(route.query.redirect || '/dashboard')
    } finally { loading.value = false }
  })
}
onMounted(() => { form.username = localStorage.getItem('oa_last_user') || 'admin'; form.password = '123456' })
</script>

<style scoped>
.login { height: 100vh; display: flex; background: #fff; }
.left { flex: 1.15; position: relative; overflow: hidden; background: linear-gradient(145deg,#12224b 0%,#1b3b82 55%,#2f6bff 100%); display: flex; align-items: center; }
.left-inner { position: relative; z-index: 2; padding: 0 8% ; color: #fff; }
.logo-line { display: flex; align-items: center; gap: 12px; margin-bottom: 68px; }
.logo { width: 42px; height: 42px; border-radius: 11px; background: rgba(255,255,255,.16); border: 1px solid rgba(255,255,255,.28); display: flex; align-items: center; justify-content: center; font-size: 21px; font-weight: 700; }
.logo-name { font-size: 21px; font-weight: 700; letter-spacing: 2px; }
h1 { font-size: 44px; line-height: 1.35; margin: 0 0 20px; font-weight: 700; letter-spacing: 1px; }
.lead { font-size: 15px; line-height: 1.9; color: #c7d7ff; max-width: 470px; margin: 0 0 42px; }
.feat-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 18px 34px; max-width: 500px; }
.feat { display: flex; gap: 11px; align-items: flex-start; }
.feat .el-icon { font-size: 19px; margin-top: 2px; color: #8fb6ff; }
.feat b { display: block; font-size: 14px; font-weight: 600; }
.feat span { display: block; font-size: 11px; color: #a9c1f5; margin-top: 3px; }
.tech-line { margin-top: 54px; font-size: 12px; color: #8ba7e0; letter-spacing: .4px; }
.circles i { position: absolute; border: 1px solid rgba(255,255,255,.09); border-radius: 50%; }
.circles i:nth-child(1) { width: 620px; height: 620px; right: -190px; top: -180px; }
.circles i:nth-child(2) { width: 420px; height: 420px; right: -60px; bottom: -170px; }
.circles i:nth-child(3) { width: 260px; height: 260px; left: -110px; bottom: 20px; }
.right { flex: 1; display: flex; align-items: center; justify-content: center; }
.box { width: 380px; }
.box-title { font-size: 28px; font-weight: 700; color: #172033; }
.box-sub { font-size: 13px; color: #8b94a6; margin: 9px 0 34px; }
.row { display: flex; justify-content: space-between; align-items: center; margin: -6px 0 18px; font-size: 12px; }
.tip { color: #a4adbd; }
.submit { width: 100%; height: 44px; font-size: 15px; letter-spacing: 4px; background: linear-gradient(90deg,#2f6bff,#4a83ff); border: none; }
.demo { margin-top: 30px; padding: 16px; border-radius: 10px; background: #f6f8fc; border: 1px solid #e9edf5; }
.demo-title { font-size: 12px; color: #4b5568; font-weight: 600; margin-bottom: 11px; }
.demo-title span { color: #9aa3b2; font-weight: 400; }
.demo-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.acc { cursor: pointer; }
.box-foot { text-align: center; margin-top: 34px; font-size: 11px; color: #a4adbd; }
.box-foot a { color: #2f6bff; text-decoration: none; }
</style>
