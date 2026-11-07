<template>
  <div class="page">
    <div class="page-head"><div><h2 class="page-title">云文档</h2><p class="page-sub">部门共享盘、文件上传落盘、版本与下载计数</p></div>
      <div><el-button :icon="FolderAdd" @click="addFolder">新建文件夹</el-button><el-upload :show-file-list="false" :before-upload="doUpload" accept="*"><el-button type="primary" :icon="UploadFilled">上传文件</el-button></el-upload></div></div>
    <el-row :gutter="16"><el-col :span="5"><div class="card card-pad">
      <div class="c-head"><b>文件夹</b></div>
      <div class="folder" :class="{ on: folderId === null }" @click="folderId = null; load()"><el-icon><Files /></el-icon>全部文件 <em>{{ allCount }}</em></div>
      <div v-for="f in folders" :key="f.id" class="folder" :class="{ on: folderId === f.id }" @click="folderId = f.id; load()"><el-icon><Folder /></el-icon>{{ f.name }}<el-icon class="del" @click.stop="delFolder(f)"><Delete /></el-icon></div>
    </div></el-col><el-col :span="19"><div class="card card-pad">
      <div class="c-head"><b>{{ folderName }}</b><div class="toolbar" style="margin:0"><el-input v-model="kw" placeholder="搜索文件名" style="width:200px" clearable @keyup.enter="load" /><el-button @click="load">搜索</el-button></div></div>
      <el-table :data="rows" border stripe>
        <el-table-column label="文件名" min-width="280"><template #default="{ row }"><span class="fn"><span class="ext" :class="color(row.ext)">{{ row.ext.toUpperCase() }}</span>{{ row.name }}</span></template></el-table-column>
        <el-table-column label="大小" width="110"><template #default="{ row }">{{ size(row.fileSize) }}</template></el-table-column>
        <el-table-column label="版本" width="80"><template #default="{ row }"><el-tag size="small" effect="plain">v{{ row.version }}</el-tag></template></el-table-column>
        <el-table-column prop="uploaderName" label="上传人" width="110" /><el-table-column prop="downloadCount" label="下载次数" width="100" />
        <el-table-column prop="createTime" label="上传时间" width="170" />
        <el-table-column label="操作" width="130" fixed="right"><template #default="{ row }"><el-button link type="primary" @click="download(row)">下载</el-button><el-button link type="danger" @click="del(row)">删除</el-button></template></el-table-column>
      </el-table>
      <div class="pager"><el-pagination background layout="prev, pager, next" :total="total" v-model:current-page="page" :page-size="20" @current-change="load" /></div>
    </div></el-col></el-row>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { FolderAdd, UploadFilled } from '@element-plus/icons-vue'
import { docApi, authApi } from '@/api'
const folders = ref([]), rows = ref([]), total = ref(0), page = ref(1), kw = ref(''), folderId = ref(null), allCount = ref(0)
const folderName = computed(() => folderId.value === null ? '全部文件' : (folders.value.find(f => f.id === folderId.value)?.name || '文件夹'))
function color(ext) { return { pdf: 'r', docx: 'b', xlsx: 'g', pptx: 'o', zip: 'p', md: 'b' }[ext] || 'k' }
function size(n) { n = Number(n || 0); return n > 1048576 ? (n / 1048576).toFixed(2) + ' MB' : (n / 1024).toFixed(1) + ' KB' }
async function load() { const r = await docApi.files({ folderId: folderId.value, kw: kw.value, page: page.value, size: 20 }); rows.value = r.rows || []; total.value = r.total || 0; if (folderId.value === null) allCount.value = total.value; folders.value = await docApi.folders() }
async function doUpload(file) { await docApi.upload(folderId.value || 0, file); ElMessage.success('上传成功：' + file.name); load(); return false }
async function addFolder() { const { value } = await ElMessageBox.prompt('请输入文件夹名称', '新建文件夹'); if (value) { await docApi.addFolder({ parentId: 0, name: value }); ElMessage.success('已创建'); load() } }
async function delFolder(f) { await ElMessageBox.confirm('删除文件夹「' + f.name + '」？', '提示', { type: 'warning' }); await docApi.delFolder(f.id); if (folderId.value === f.id) folderId.value = null; load() }
async function del(row) { await ElMessageBox.confirm('删除文件「' + row.name + '」？', '提示', { type: 'warning' }); await docApi.del(row.id); load() }
function download(row) { const a = document.createElement('a'); a.href = docApi.downloadUrl(row.id); a.download = row.name; a.click(); ElMessage.success('开始下载：' + row.name); setTimeout(load, 600) }
onMounted(load)
</script>
<style scoped>.c-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; font-size: 14px; } .folder { display: flex; align-items: center; gap: 9px; padding: 10px 9px; border-radius: 7px; cursor: pointer; font-size: 13px; } .folder:hover { background: #f6f8fc; } .folder.on { background: #eef3ff; color: #2f6bff; font-weight: 600; } .folder em { margin-left: auto; font-style: normal; color: #a4adbd; font-size: 11px; } .del { margin-left: auto; color: #c0c6d2; } .fn { display: flex; align-items: center; gap: 10px; } .ext { font-size: 9px; font-weight: 700; color: #fff; padding: 3px 5px; border-radius: 4px; min-width: 34px; text-align: center; } .ext.r { background: #e85454; } .ext.b { background: #2f6bff; } .ext.g { background: #20b58a; } .ext.o { background: #f7a13c; } .ext.p { background: #8b64e8; } .ext.k { background: #7b8496; } .pager { display: flex; justify-content: flex-end; margin-top: 16px; }</style>
