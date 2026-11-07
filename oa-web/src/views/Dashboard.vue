<template>
  <div class="page" v-loading="loading">
    <div class="page-head">
      <div>
        <h2 class="page-title">早上好，{{ store.displayName }} 👋</h2>
        <p class="page-sub">今天是 {{ today }}，这里是你的工作台概览 · 数据来自后端实时接口</p>
      </div>
      <div>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
        <el-button type="primary" :icon="EditPen" @click="$router.push('/approval/mine')">发起审批</el-button>
      </div>
    </div>

    <el-row :gutter="16" class="mb16">
      <el-col :span="6" v-for="s in stats" :key="s.label">
        <div class="stat-card">
          <div class="stat-label">{{ s.label }}</div>
          <div class="stat-value" :style="{ color: s.color }">{{ s.value }}</div>
          <div class="stat-foot">{{ s.foot }}</div>
          <div class="stat-icon" :style="{ background: s.bg, color: s.color }"><el-icon><component :is="s.icon" /></el-icon></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mb16">
      <el-col :span="14"><div class="card card-pad">
        <div class="c-head"><b>近 5 个工作日考勤趋势</b><span class="c-sub">att_checkin 实时聚合</span></div>
        <div ref="lineEl" class="chart"></div>
      </div></el-col>
      <el-col :span="10"><div class="card card-pad">
        <div class="c-head"><b>任务状态分布</b><span class="c-sub">prj_task 分组统计</span></div>
        <div ref="pieEl" class="chart"></div>
      </div></el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="10"><div class="card card-pad">
        <div class="c-head"><b>我的待办</b><el-link type="primary" :underline="false" @click="$router.push('/approval/todo')">查看全部 →</el-link></div>
        <div v-for="(t, i) in todos" :key="i" class="todo-item">
          <span class="dot" :style="{ background: t.type === 'APPROVAL' ? '#2f6bff' : '#f7a13c' }"></span>
          <span class="todo-title">{{ t.title }}</span>
          <el-tag size="small" :type="t.type === 'APPROVAL' ? 'primary' : 'warning'" effect="plain">{{ t.type === 'APPROVAL' ? '审批' : '任务' }}</el-tag>
        </div>
        <el-empty v-if="!todos.length" description="暂无待办" :image-size="60" />
      </div></el-col>
      <el-col :span="8"><div class="card card-pad">
        <div class="c-head"><b>部门人员分布</b></div>
        <div ref="barEl" class="chart" style="height:230px"></div>
      </div></el-col>
      <el-col :span="6"><div class="card card-pad">
        <div class="c-head"><b>快捷入口</b></div>
        <div class="quick" v-for="q in quicks" :key="q.t" @click="$router.push(q.p)">
          <el-icon :style="{ color: q.c }"><component :is="q.i" /></el-icon><span>{{ q.t }}</span><el-icon><ArrowRight /></el-icon>
        </div>
      </div></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { Refresh, EditPen } from '@element-plus/icons-vue'
import { dashboardApi } from '@/api'
import { useUserStore } from '@/store/user'

const store = useUserStore()
const loading = ref(false)
const stats = ref([]), todos = ref([])
const lineEl = ref(), pieEl = ref(), barEl = ref()
let charts = []
const today = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
const quicks = [
  { t: '打卡签到', p: '/attendance', i: 'AlarmClock', c: '#2f6bff' },
  { t: '发起请假', p: '/approval/mine', i: 'Stamp', c: '#f7a13c' },
  { t: '上传文档', p: '/doc', i: 'FolderOpened', c: '#20b58a' },
  { t: '项目看板', p: '/work/board', i: 'Grid', c: '#8b64e8' },
  { t: '预约会议', p: '/calendar', i: 'Calendar', c: '#e8547c' }
]

function draw(data) {
  charts.forEach(c => c.dispose()); charts = []
  if (lineEl.value) {
    const c = echarts.init(lineEl.value); charts.push(c)
    const rows = data.attendanceTrend || []
    c.setOption({
      grid: { left: 42, right: 18, top: 32, bottom: 30 },
      tooltip: { trigger: 'axis' },
      legend: { right: 0, top: 0, itemWidth: 10, textStyle: { fontSize: 11, color: '#7b8496' } },
      xAxis: { type: 'category', data: rows.map(r => String(r.label).slice(5)), axisLine: { lineStyle: { color: '#dfe5ef' } }, axisLabel: { color: '#8b94a6', fontSize: 11 } },
      yAxis: { type: 'value', splitLine: { lineStyle: { color: '#eef1f6' } }, axisLabel: { color: '#8b94a6', fontSize: 11 } },
      series: [
        { name: '打卡人数', type: 'bar', data: rows.map(r => r.cnt), barWidth: 18, itemStyle: { color: '#2f6bff', borderRadius: [5, 5, 0, 0] } },
        { name: '迟到人数', type: 'line', smooth: true, data: rows.map(r => r.late), itemStyle: { color: '#f7a13c' }, lineStyle: { width: 2 } }
      ]
    })
  }
  if (pieEl.value) {
    const c = echarts.init(pieEl.value); charts.push(c)
    const labelMap = { TODO: '待办', DOING: '进行中', REVIEW: '待验收', DONE: '已完成' }
    const colors = { TODO: '#9aa7bd', DOING: '#2f6bff', REVIEW: '#f7a13c', DONE: '#20b58a' }
    const rows = (data.taskDistribution || []).map(r => ({ name: labelMap[r.label] || r.label, value: r.cnt, itemStyle: { color: colors[r.label] || '#ccc' } }))
    c.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 0, itemWidth: 9, textStyle: { fontSize: 11, color: '#7b8496' } },
      series: [{ type: 'pie', radius: ['46%', '68%'], center: ['50%', '44%'], data: rows, label: { show: false }, itemStyle: { borderColor: '#fff', borderWidth: 2 } }]
    })
  }
  if (barEl.value) {
    const c = echarts.init(barEl.value); charts.push(c)
    const rows = (data.deptStaff || []).slice(0, 6).reverse()
    c.setOption({
      grid: { left: 78, right: 20, top: 8, bottom: 20 },
      xAxis: { type: 'value', splitLine: { lineStyle: { color: '#eef1f6' } }, axisLabel: { color: '#9aa3b2', fontSize: 10 } },
      yAxis: { type: 'category', data: rows.map(r => r.label), axisLabel: { color: '#5b6478', fontSize: 11 }, axisLine: { show: false }, axisTick: { show: false } },
      series: [{ type: 'bar', data: rows.map(r => r.cnt), barWidth: 11, itemStyle: { color: '#5d8bff', borderRadius: [0, 6, 6, 0] } }]
    })
  }
}

async function load() {
  loading.value = true
  try {
    const d = await dashboardApi.overview()
    const s = d.stats || {}
    stats.value = [
      { label: '在职员工', value: s.staff ?? 0, foot: `覆盖 ${s.depts ?? 0} 个部门`, color: '#2f6bff', bg: '#eef3ff', icon: 'User' },
      { label: '进行中审批', value: s.runningApprovals ?? 0, foot: `待我处理 ${s.myTodo ?? 0} 条`, color: '#f7a13c', bg: '#fff5e8', icon: 'Stamp' },
      { label: '进行中项目', value: s.activeProjects ?? 0, foot: `未完成任务 ${s.openTasks ?? 0} 项`, color: '#20b58a', bg: '#e9f9f4', icon: 'Grid' },
      { label: '今日打卡', value: s.todayCheckins ?? 0, foot: `本月迟到 ${s.monthLate ?? 0} 人次`, color: '#8b64e8', bg: '#f3eeff', icon: 'AlarmClock' }
    ]
    todos.value = d.todos || []
    await nextTick(); draw(d)
  } catch (e) { ElMessage.error('工作台数据加载失败') }
  finally { loading.value = false }
}
onMounted(load)
onBeforeUnmount(() => charts.forEach(c => c.dispose()))
</script>

<style scoped>
.c-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; font-size: 14px; }
.c-sub { font-size: 11px; color: #a4adbd; font-weight: 400; }
.todo-item { display: flex; align-items: center; gap: 8px; padding: 11px 2px; border-bottom: 1px dashed #eef1f6; font-size: 12px; }
.todo-title { flex: 1; color: #4b5568; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.quick { display: flex; align-items: center; gap: 10px; padding: 12px 4px; border-bottom: 1px dashed #eef1f6; font-size: 13px; cursor: pointer; }
.quick:hover { color: #2f6bff; }
.quick span { flex: 1; }
</style>
