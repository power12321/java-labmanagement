<template>
  <router-view />
</template>

<script setup>
import useSettingsStore from '@/store/modules/settings'
import { handleThemeStyle } from '@/utils/theme'

onMounted(() => {
  nextTick(() => {
    // 初始化主题样式
    handleThemeStyle(useSettingsStore().theme)
  })
})
</script>
<style>
.dialog-footer{
  display: flex;
  justify-content: flex-end;
}

.el-date-editor{
  width: 100%;
}
</style>
