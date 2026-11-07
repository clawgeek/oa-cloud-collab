<template>
  <div class="page">
    <div class="page-head"><div><h2 class="page-title">流程定义</h2><p class="page-sub">wf_process_def 中以 JSON 配置串行审批节点与办理人角色</p></div></div>
    <el-row :gutter="16"><el-col :span="12" v-for="d in defs" :key="d.id"><div class="card card-pad mb16">
      <div class="c-head"><b>{{ d.procName }}</b><el-tag size="small" effect="plain">{{ d.procKey }}</el-tag></div>
      <div class="flow"><template v-for="(n, i) in nodes(d.nodesJson)" :key="i"><div class="node"><span class="idx">{{ i + 1 }}</span><div><b>{{ n.name }}</b><small>办理角色 {{ n.assignee }}</small></div></div><span v-if="i < nodes(d.nodesJson).length - 1" class="arrow">→</span></template></div>
      <div class="json">nodes_json: {{ d.nodesJson }}</div>
    </div></el-col></el-row>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { approvalApi } from '@/api'
const defs = ref([])
function nodes(s) { try { return JSON.parse(s) } catch (e) { return [] } }
onMounted(async () => { defs.value = await approvalApi.defs() })
</script>
<style scoped>.c-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; font-size: 14px; } .flow { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; } .node { display: flex; align-items: center; gap: 9px; background: #f4f7ff; border: 1px solid #dfe8ff; border-radius: 9px; padding: 9px 12px; } .idx { width: 22px; height: 22px; border-radius: 50%; background: #2f6bff; color: #fff; font-size: 11px; display: flex; align-items: center; justify-content: center; } .node b { display: block; font-size: 12px; } .node small { font-size: 10px; color: #98a1b2; } .arrow { color: #98a1b2; } .json { margin-top: 16px; font-family: Consolas, monospace; font-size: 11px; color: #8b94a6; word-break: break-all; background: #f8fafc; padding: 9px; border-radius: 6px; }</style>
