import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  base: '/english-for-it-engineers/',
  plugins: [vue()],
  server: {
    host: true,
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
