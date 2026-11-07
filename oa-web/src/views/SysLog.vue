<template>
  <div class="page"><div class="page-head"><div><h2 class="page-title">系统审计日志</h2><p class="page-sub">基于 Spring AOP 自动记录所有写操作：操作人、模块、URI、IP 与耗时</p></div></div>
    <div class="card card-pad"><div class="toolbar"><el-input v-model="kw" placeholder="操作人 / 操作" style="width:220px" clearable @keyup.enter="load(1)" /><el-select v-model="module" placeholder="全部模块" style="width:160px" clearable><el-option v-for="m in ['Auth','Org','Approval','Notice','Doc','Work','Calendar','Log']" :key="m" :label="m" :value="m" /></el-select><el-button type="primary" @click="load(1)">查询</el-button><span class="spacer"></span><span class="total">共 {{ total }} 条</span></div>
      <el-table :data="rows" border stripe><el-table-column prop="id" label="ID" width="70" /><el-table-column prop="userName" label="操作人" width="110" /><el-table-column prop="module" label="模块" width="110"><template #default="{ row }"><el-tag size="small" effect="plain">{{ row.module }}</el-tag></template></el-table-column>
        <el-table-column prop="operation" label="操作" width="120" /><el-table-column prop="requestUri" label="请求地址" min-width="230" /><el-table-column prop="ip" label="IP" width="130" />
        <el-table-column label="耗时" width="100"><template #default="{ row }"><span :style="{ color: row.costMs > 100 ? '#f7a13c' : '#20b58a' }">{{ row.costMs }} ms</span></template></el-table-column>
        <el-table-column label="结果" width="90"><template #default="{ row }"><el-tag size="small" :type="row.success === 1 ? 'success' : 'danger'" effect="plain">{{ row.success === 1 ? '成功' : '失败' }}</el-tag></template></el-table-column>
        <el-table-column prop="createTime" label="时间" width="170" /></el-table>
      <div class="pager"><el-pagination background layout="prev, pager, next" :total="total" v-model:current-page="page" :page-size="10" @current-change="load()" /></div>
    </div></div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { logApi } from '@/api'
const rows = ref([]), total = ref(0), page = ref(1), kw = ref(''), module = ref('')
async function load(p) { if (p) page.value = p; const r = await logApi.page({ kw: kw.value, module: module.value, page: page.value, size: 10 }); rows.value = r.rows || []; total.value = r.total || 0 }
onMounted(() => load())
</script>
<style scoped>.toolbar .spacer { flex: 1; } .total { font-size: 12px; color: #8b94a6; } .pager { display: flex; justify-content: flex-end; margin-top: 16px; }</style>
