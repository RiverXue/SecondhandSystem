import {createApp} from 'vue'
import './style.css'
import App from './App.vue'
// 导入默认主题的CSS样式，保证正常显示
import 'element-plus/dist/index.css'
// 导入组件库
import ElementPlus from 'element-plus'
// 导入路由
import router from './router/index'
// 导入pinia
import {createPinia} from "pinia"


// 创建vue应用
const app = createApp(App)

// 全局使用ElementPlus组件库
app.use(ElementPlus)

// 全局使用路由
app.use(router)

// 全局使用pinia
app.use(createPinia())

// 启动Vue，挂载到#app元素上，即index.html中的<div id="app"></div>
app.mount('#app')
