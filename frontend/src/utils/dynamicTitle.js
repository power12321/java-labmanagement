import store from '@/store'
import defaultSettings from '@/settings'
import useSettingsStore from '@/store/modules/settings'

/**
 * 动态修改标题
 */
export function useDynamicTitle() {
  const settingsStore = useSettingsStore();
  if (settingsStore.dynamicTitle) {
    document.title = 'Spring Cloud微服务架构实验室管理服务平台'
  } else {
    document.title = 'Spring Cloud微服务架构实验室管理服务平台'
  }
}