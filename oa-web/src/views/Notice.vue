<template>
  <div class="page">
    <div class="page-head"><div><h2 class="page-title">公告通知</h2><p class="page-sub">公司制度、人事通知与会议公告，支持置顶与已读回执</p></div><el-button type="primary" :icon="Plus" @click="open()">发布公告</el-button></div>
    <div class="card card-pad">
      <div class="toolbar"><el-input v-model="kw" placeholder="搜索标题" style="width:220px" clearable @keyup.enter="load(1)" /><el-select v-model="category" placeholder="全部分类" style="width:150px" clearable><el-option v-for="c in ['公告','制度','人事','财务','会议','培训']" :key="c" :label="c" :value="c" /></el-select><el-button type="primary" @click="load(1)">查询</el-button><span class="spacer"></span><el-tag effect="plain">未读 {{ unread }}</el-tag></div>
      <el-table :data="rows" border stripe @row-click="view">
        <el-table-column label="标题" min-width="330"><template #default="{ row }"><span class="ntitle"><el-tag v-if="row.pinned" type="danger" size="small" effect="dark">置顶</el-tag>{{ row.title }}<span class="new" v-if="!row.isRead">NEW</span></span></template></el-table-column>
        <el-table-column prop="category" label="分类" width="100"><template #default="{ row }"><el-tag size="small" effect="plain">{{ row.category }}</el-tag></template></el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="110" /><el-table-column prop="viewCount" label="阅读量" width="90" />
        <el-table-column prop="publishTime" label="发布时间" width="170" /><el-table-column label="操作" width="90"><template #default="{ row }"><el-button link type="primary" @click.stop="view(row)">查看</el-button></template></el-table-column>
      </el-table>
      <div class="pager"><el-pagination background layout="prev, pager, next" :total="total" v-model:current-page="page" :page-size="10" @current-change="load()" /></div>
    </div>
    <el-dialog v-model="detailDlg" :title="cur.title" width="640px"><div class="meta">{{ cur.category }} · {{ cur.publisherName }} · {{ cur.publishTime }}</div><div class="content">{{ cur.content }}</div></el-dialog>
    <el-dialog v-model="dlg" title="发布公告" width="560px"><el-form :model="form" label-width="80px"><el-form-item label="标题"><el-input v-model="form.title" /></el-form-item><el-form-item label="分类"><el-select v-model="form.category" style="width:100%"><el-option v-for="c in ['公告','制度','人事','财务','会议','培训']" :key="c" :label="c" :value="c" /></el-select></el-form-item><el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item><el-form-item label="置顶"><el-switch v-model="form.pinned" :active-value="1" :inactive-value="0" /></el-form-item></el-form><template #footer><el-button @click="dlg = false">取消</el-button><el-button type="primary" @click="save">发布</el-button></template></el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { noticeApi } from '@/api'
const rows = ref([]), total = ref(0), page = ref(1), kw = ref(''), category = ref(''), unread = ref(0)
const detailDlg = ref(false), dlg = ref(false), cur = ref({}), form = reactive({ title: '', content: '', category: '公告', pinned: 0 })
async function load(p) { if (p) page.value = p; const r = await noticeApi.list({ kw: kw.value, category: category.value, page: page.value, size: 10 }); rows.value = r.rows || []; total.value = r.total || 0; unread.value = await noticeApi.unread() }
async function view(row) { cur.value = await noticeApi.detail(row.id); detailDlg.value = true; load() }
function open() { Object.assign(form, { title: '', content: '', category: '公告', pinned: 0 }); dlg.value = true }
async function save() { await noticeApi.save(form); ElMessage.success('发布成功'); dlg.value = false; load() }
onMounted(() => load())
</script>
<style scoped>.toolbar .spacer { flex: 1; } .ntitle { cursor: pointer; display: inline-flex; align-items: center; gap: 8px; } .ntitle:hover { color: #2f6bff; } .new { color: #f56c6c; font-size: 10px; font-weight: 700; } .pager { display: flex; justify-content: flex-end; margin-top: 16px; } .meta { font-size: 12px; color: #98a1b2; margin-bottom: 16px; } .content { font-size: 14px; line-height: 2; color: #374151; white-space: pre-wrap; }</style>
