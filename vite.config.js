import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from "@tailwindcss/vite";
import path from 'node:path';
import { viteStaticCopy } from 'vite-plugin-static-copy';

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(), 
    tailwindcss(),
    viteStaticCopy({
      targets: [
        {
          src: 'index.html',
          dest: '',
          rename: 'register.html'
        },
        {
          src: 'index.html',
          dest: '',
          rename: 'contact.html'
        },
        {
          src: 'index.html',
          dest: '',
          rename: 'guia.html'
        },
        {
          src: 'public/sitemap.xml',
          dest: '',
          rename: 'sitemap.xml'
        }
      ]
    })
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 5199,
    // You can also set it to open the browser automatically
    open: true,
    // Uncomment to allow external access (from other devices on your network)
    // host: '0.0.0.0',
  },
})
