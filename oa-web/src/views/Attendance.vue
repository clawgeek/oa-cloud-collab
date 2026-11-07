<template>
  <div class="page">
    <div class="page-head"><div><h2 class="page-title">考勤管理</h2><p class="page-sub">每日打卡、迟到早退自动判定、月度统计与假期余额</p></div></div>
    <el-row :gutter="16" class="mb16">
      <el-col :span="8"><div class="card card-pad clock-card">
        <div class="clock-date">{{ todayInfo.date }}</div>
        <div class="clock-time">{{ now }}</div>
        <div class="clock-hint">工作时间 09:00 - 18:00 · 定位：总部-5F WiFi</div>
        <el-button type="primary" size="large" round class="clock-btn" @click="doCheck">
          {{ !record ? '上班打卡' : (record.checkOut ? '今日已完成' : '下班打卡') }}
        </el-button>
        <div class="clock-status" v-if="record">
          <el-tag :type="record.inStatus === 'NORMAL' ? 'success' : 'danger'" effect="plain" size="small">上班 {{ record.checkIn }} {{ record.inStatus === 'NORMAL' ? '正常' : '迟到' }}</el-tag>
          <el-tag v-if="record.checkOut" :type="record.outStatus === 'NORMAL' ? 'success' : 'warning'" effect="plain" size="small">下班 {{ record.checkOut }} {{ record.outStatus === 'NORMAL' ? '正常' : '早退' }}</el-tag>
        </div>
      </div></el-col>
      <el-col :span="8"><div class="card card-pad">
        <div class="c-head"><b>我的假期余额</b><span class="c-sub">{{ year }} 年度</span></div>
        <div class="bal" v-for="b in balances" :key="b.k"><span>{{ b.k }}</span><b>{{ b.v }} 天</b><el-progress :percentage="b.p" :color="b.c" :show-text="false" /></div>
      </div></el-col>
      <el-col :span="8"><div class="card card-pad">
        <div class="c-head"><b>本月考勤概览</b></div>
        <div class="mini"><div><b>{{ summary.length }}</b><span>统计人数</span></div><div><b>{{ totalLate }}</b><span>迟到人次</span></div><div><b>{{ totalEarly }}</b><span>早退人次</span></div></div>
        <el-divider /><div class="hint">数据由 att_checkin 表按 check_date 聚合，后端 Application 层做迟到/早退判定。</div>
      </div></el-col>
    </el-row>
    <div class="card card-pad">
      <div class="c-head"><b>打卡明细</b><div class="toolbar" style="margin:0"><el-date-picker v-model="month" type="month" value-format="YYYY-MM" placeholder="选择月份" @change="loadRecords" /><el-input v-model="userId" placeholder="按工号筛选" style="width:150px" clearable @change="loadRecords" /></div></div>
      <el-table :data="records" border stripe max-height="330">
        <el-table-column prop="checkDate" label="日期" width="130" /><el-table-column prop="realName" label="姓名" width="110" />
        <el-table-column prop="deptName" label="部门" min-width="150" /><el-table-column prop="checkIn" label="上班时间" width="110" />
        <el-table-column label="上班状态" width="110"><template #default="{ row }"><el-tag :type="row.inStatus === 'NORMAL' ? 'success' : 'danger'" size="small" effect="plain">{{ row.inStatus === 'NORMAL' ? '正常' : '迟到' }}</el-tag></template></el-table-column>
        <el-table-column prop="checkOut" label="下班时间" width="110" /><el-table-column label="下班状态" width="110"><template #default="{ row }"><el-tag :type="row.outStatus === 'NORMAL' ? 'success' : 'warning'" size="small" effect="plain">{{ row.outStatus === 'EARLY' ? '早退' : '正常' }}</el-tag></template></el-table-column>
        <el-table-column prop="workHours" label="工时" width="90" /><el-table-column prop="location" label="打卡地点" min-width="150" />
      </el-table>
      <div class="pager"><el-pagination background layout="prev, pager, next" :total="total" v-model:current-page="page" :page-size="10" @current-change="loadRecords" /></div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { attApi } from '@/api'
const now = ref(''), month = ref('2026-11'), userId = ref(''), records = ref([]), total = ref(0), page = ref(1)
const todayInfo = ref({}), record = ref(null), balance = ref(null), summary = ref([])
let timer
const year = new Date().getFullYear()
const balances = computed(() => {
  const b = balance.value || {}
  const at = Number(b.annualTotal || 10), au = Number(b.annualUsed || 0)
  const ct = Number(b.compTotal || 0), cu = Number(b.compUsed || 0)
  return [
    { k: '年假剩余', v: at - au, p: at ? Math.round((at - au) / at * 100) : 0, c: '#2f6bff' },
    { k: '调休剩余', v: ct - cu, p: ct ? Math.round((ct - cu) / ct * 100) : 0, c: '#20b58a' }
  ]
})
const totalLate = computed(() => summary.value.reduce((s, r) => s + Number(r.late || 0), 0))
const totalEarly = computed(() => summary.value.reduce((s, r) => s + Number(r.early || 0), 0))
function tick() { now.value = new Date().toLocaleTimeString('zh-CN', { hour12: false }) }
async function load() {
  const t = await attApi.today(); todayInfo.value = t; record.value = t.record; balance.value = t.balance
  const r = await attApi.records({ month: month.value, userId: userId.value || null, page: page.value, size: 10 })
  records.value = r.rows || []; total.value = r.total || 0
  summary.value = await attApi.summary({ month: month.value })
}
function loadRecords() { page.value = 1; load() }
async function doCheck() {
  if (record.value?.checkOut) return ElMessage.info('今日已完成打卡')
  await attApi.checkin({ location: '总部-5F WiFi' }); ElMessage.success('打卡成功'); load()
}
onMounted(() => { tick(); timer = setInterval(tick, 1000); load() })
onBeforeUnmount(() => clearInterval(timer))
</script>
<style scoped>.c-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; font-size: 14px; } .c-sub { font-size: 11px; color: #a4adbd; } .clock-card { text-align: center; } .clock-date { color: #8b94a6; font-size: 13px; } .clock-time { font-size: 42px; font-weight: 700; letter-spacing: 1px; color: #172033; margin: 8px 0; } .clock-hint { font-size: 12px; color: #a4adbd; } .clock-btn { width: 190px; margin: 22px 0 12px; } .clock-status { display: flex; gap: 8px; justify-content: center; } .bal { margin-bottom: 18px; font-size: 13px; } .bal span { color: #6b7488; } .bal b { float: right; color: #172033; } .mini { display: flex; justify-content: space-around; text-align: center; padding: 14px 0; } .mini b { display: block; font-size: 24px; color: #2f6bff; } .mini span { font-size: 11px; color: #98a1b2; } .hint { font-size: 11px; color: #98a1b2; line-height: 1.7; } .pager { display: flex; justify-content: flex-end; margin-top: 14px; }</style>
