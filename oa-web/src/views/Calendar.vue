<template>
  <div class="page"><div class="page-head"><div><h2 class="page-title">日程会议</h2><p class="page-sub">会议室台账与预约，提交时由后端做时间段冲突检测</p></div><el-button type="primary" :icon="Plus" @click="dlg = true">预约会议</el-button></div>
    <el-row :gutter="16"><el-col :span="7"><div class="card card-pad"><div class="c-head"><b>会议室</b></div>
      <div class="room" v-for="r in rooms" :key="r.id"><div class="rname"><el-icon><OfficeBuilding /></el-icon>{{ r.name }}</div><div class="rmeta">{{ r.capacity }} 人 · {{ r.location }}</div><div class="requip">{{ r.equipment }}</div></div>
    </div></el-col><el-col :span="17"><div class="card card-pad"><div class="c-head"><b>近期日程</b><el-tag effect="plain" size="small">{{ events.length }} 场</el-tag></div>
      <el-table :data="events" border stripe><el-table-column prop="title" label="会议主题" min-width="210" /><el-table-column prop="roomName" label="会议室" width="140" />
        <el-table-column label="时间" width="250"><template #default="{ row }">{{ row.startTime }} ~ {{ row.endTime.slice(11) }}</template></el-table-column>
        <el-table-column prop="organizerName" label="组织人" width="100" /><el-table-column prop="attendees" label="参会人" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="80"><template #default="{ row }"><el-button link type="danger" @click="del(row)">取消</el-button></template></el-table-column>
      </el-table>
      <el-alert class="mt16" type="warning" :closable="false" title="冲突检测演示：选择 11-09 10:00-11:00 的 5F 会议室 A 再次预约，后端将返回「会议室在该时段已被占用」。" />
    </div></el-col></el-row>
    <el-dialog v-model="dlg" title="预约会议" width="540px"><el-form :model="form" label-width="90px">
      <el-form-item label="会议主题"><el-input v-model="form.title" /></el-form-item>
      <el-form-item label="会议室"><el-select v-model="form.roomId" style="width:100%"><el-option v-for="r in rooms" :key="r.id" :label="r.name + '（' + r.capacity + '人）'" :value="r.id" /></el-select></el-form-item>
      <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm" style="width:100%" /></el-form-item>
      <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm" style="width:100%" /></el-form-item>
      <el-form-item label="参会人"><el-input v-model="form.attendees" placeholder="多个姓名用逗号分隔" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
    </el-form><template #footer><el-button @click="dlg = false">取消</el-button><el-button type="primary" @click="save">提交预约</el-button></template></el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { calApi } from '@/api'
const rooms = ref([]), events = ref([]), dlg = ref(false)
const form = reactive({ title: '', roomId: null, startTime: '', endTime: '', attendees: '', remark: '' })
async function load() { const d = await calApi.events({}); rooms.value = d.rooms || []; events.value = d.events || [] }
async function save() { await calApi.save(form); ElMessage.success('预约成功'); dlg.value = false; load() }
async function del(row) { await ElMessageBox.confirm('取消会议「' + row.title + '」？', '提示', { type: 'warning' }); await calApi.del(row.id); load() }
onMounted(load)
</script>
<style scoped>.c-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; font-size: 14px; } .room { padding: 12px; border: 1px solid #eef1f6; border-radius: 9px; margin-bottom: 10px; } .rname { display: flex; align-items: center; gap: 8px; font-weight: 600; font-size: 13px; } .rmeta { font-size: 11px; color: #8b94a6; margin: 6px 0 3px; } .requip { font-size: 11px; color: #a4adbd; }</style>
