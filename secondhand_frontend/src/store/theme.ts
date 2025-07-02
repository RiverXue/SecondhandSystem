import { defineStore } from 'pinia';

interface ThemeState {
  isDark: boolean;
}

export const useThemeStore = defineStore('theme', {
  state: (): ThemeState => ({
    isDark: localStorage.getItem('theme') === 'dark'
  }),

  created() {
    document.documentElement.classList.toggle('dark', this.isDark);
  },

  actions: {
    toggleTheme() {
      this.isDark = !this.isDark;
      localStorage.setItem('theme', this.isDark ? 'dark' : 'light');
      document.documentElement.classList.toggle('dark', this.isDark);
    }
  }
});