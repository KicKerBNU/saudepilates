import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from "@tailwindcss/vite";
import path from 'node:path';
import fs from 'node:fs';
import { viteStaticCopy } from 'vite-plugin-static-copy';
import { compression } from 'vite-plugin-compression2';

// https://vite.dev/config/
export default defineConfig({
  base: '/',
  plugins: [
    vue(), 
    tailwindcss(),
    compression({ algorithm: 'gzip', threshold: 1024 }),
    compression({ algorithm: 'brotliCompress', threshold: 1024 }),
    {
      name: 'generate-spa-html-files',
      apply: 'build',
      enforce: 'post',
      closeBundle() {
        const routeFiles = [
          'pricing.html', 
          'contact.html',
          'guia.html',
          'register.html',
          'privacy.html'
        ];
        
        const indexContent = fs.readFileSync('dist/index.html', 'utf-8');
        
        routeFiles.forEach(file => {
          fs.writeFileSync(`dist/${file}`, indexContent);
          console.log(`Created ${file}`);
        });
      }
    },
    viteStaticCopy({
      targets: [
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
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'vue-vendor': ['vue', 'vue-router', 'pinia', '@vueuse/head'],
          'firebase-core': ['firebase/app', 'firebase/auth'],
          'firebase-services': ['firebase/firestore', 'firebase/storage', 'firebase/analytics'],
          'i18n': ['vue-i18n'],
        }
      }
    }
  },
  server: {
    port: 5199,
    strictPort: false,
    open: true,
  },
  preview: {
    port: 4173,
    strictPort: false,
    open: true,
  },
})
