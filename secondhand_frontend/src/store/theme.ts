import { defineStore } from 'pinia';

/**
 * 主题状态接口定义
 * @interface ThemeState
 * @property {boolean} isDark - 是否为暗黑模式
 */
interface ThemeState {
  isDark: boolean;
}

/**
 * 主题状态管理Store
 * 负责应用的主题切换和状态持久化
 */
export const useThemeStore = defineStore('theme', {
  state: (): ThemeState => ({
    /**
     * 是否启用暗黑模式
     * 从localStorage读取初始值，默认为false(亮色模式)
     */
    isDark: localStorage.getItem('theme') === 'dark'
  }),

  /**
   * Store初始化钩子
   * 在Store创建时执行，用于初始化主题状态到DOM
   */
  created() {
    // 根据isDark状态切换HTML元素的dark类
    document.documentElement.classList.toggle('dark', this.isDark);
  },

  actions: {
    /**
     * 切换主题模式
     * 切换isDark状态，更新localStorage，并同步到DOM
     */
    toggleTheme() {
      // 切换暗黑模式状态
      this.isDark = !this.isDark;
      // 将主题状态保存到localStorage，实现持久化
      localStorage.setItem('theme', this.isDark ? 'dark' : 'light');
      // 更新HTML元素的dark类，应用主题样式
      document.documentElement.classList.toggle('dark', this.isDark);
    }
  }
});