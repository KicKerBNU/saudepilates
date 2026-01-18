import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from "@tailwindcss/vite";
import path from 'node:path';
import fs from 'node:fs';
import { viteStaticCopy } from 'vite-plugin-static-copy';

// https://vite.dev/config/
export default defineConfig({
  base: '/',
  plugins: [
    vue(), 
    tailwindcss(),
    {
      name: 'generate-spa-html-files',
      apply: 'build',
      enforce: 'post',
      closeBundle() {
        // Create HTML files for SPA routes
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
  server: {
    port: 5199,
    strictPort: false,
    // You can also set it to open the browser automatically
    open: true,
    // Uncomment to allow external access (from other devices on your network)
    // host: '0.0.0.0',
  },
  preview: {
    port: 4173,
    strictPort: false,
    open: true,
  },
})
