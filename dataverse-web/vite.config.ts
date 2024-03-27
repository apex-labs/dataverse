import { resolve } from 'path'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueSetupExtend from 'unplugin-vue-setup-extend-plus/vite'
import { visualizer } from 'rollup-plugin-visualizer'
import Components from 'unplugin-vue-components/vite'
import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
// import legacy from '@vitejs/plugin-legacy'

const url = 'http://apexnexus-datavs.chinapex.com.cn'

// https://cn.vitejs.dev/guide/
export default defineConfig(async ({ command, mode }) => {
  return {
    base: '/',
    optimizeDeps: {
      include: ['ant-design-vue/es/locale/zh_CN', 'ant-design-vue/es/locale/en_US', 'lodash-es']
    },
    plugins: [
      vue(),
      vueJsx(),
      // name 可以写在 script 标签上
      vueSetupExtend({}),
      // use svg
      createSvgIconsPlugin({
        iconDirs: [resolve(process.cwd(), 'src/assets/icons')],
        symbolId: 'svg-icon-[name]'
      }),
      Components({
        resolvers: [
          AntDesignVueResolver({
            importStyle: mode === 'development' ? false : 'less' // css in js
          })
        ]
      }),
      // legacy({
      //   targets: ['defaults', 'not IE 11']
      // }),
      visualizer()
    ],
    resolve: {
      alias: {
        '@': resolve(__dirname, './src'),
        global: resolve(__dirname, './src')
      },
      extensions: ['.js', '.ts', '.jsx', '.json', '.vue']
    },
    server: {
      origin: 'http://127.0.0.1:8898', // 解决资源访问
      port: 8898,
      host: '0.0.0.0', // 如果将此设置为 0.0.0.0 或者 true 将监听所有地址，包括局域网和公网地址
      strictPort: true,
      open: false,
      hmr: true,
      cors: true,
      headers: {
        'Access-Control-Allow-Origin': '*'
      },
      proxy: {
        '/api': {
          target: url,
          rewrite: (path: string) => path.replace(/^\/api/, '/')
        },
        '/dvs-api': {
          target: 'http://10.25.19.208:18082',
          rewrite: (path: string) => path.replace(/^\/dvs-api/, '/')
        }
      }
    },
    preview: {
      port: 8898,
      proxy: {
        '/api': {
          target: url,
          rewrite: (path: string) => path.replace(/^\/api/, '/')
        },
        '/dvs-api': {
          target: 'http://10.25.19.208:18082',
          rewrite: (path: string) => path.replace(/^\/dvs-api/, '/')
        }
      }
    },
    build: {
      outDir: 'dist',
      targets: ['es2015'],
      minify: 'terser',
      terserOptions: {
        compress: {
          drop_console: true,
          drop_debugger: true
        }
      },
      // 规定触发警告的 chunk 大小
      chunkSizeWarningLimit: 2000,
      rollupOptions: {
        output: {
          // 最小化拆分包
          manualChunks(id: any) {
            if (id.includes('node_modules')) {
              return id.toString().split('node_modules/')[1].split('/')[0].toString()
            }
          },
          chunkFileNames: 'js/[name]-[hash].js', // 引入文件名的名称
          entryFileNames: 'js/[name]-[hash].js', // 包的入口文件名称
          assetFileNames: '[ext]/[name]-[hash].[ext]' // 资源文件像 字体，图片等
        }
      }
    }
  }
})
