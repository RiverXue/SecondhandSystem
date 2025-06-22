import axios from 'axios'
import {ElMessage} from 'element-plus'
import {useUserStore} from '../store/user'
import router from '../router'

const request = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
    timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        const userStore = useUserStore()
        // 添加Authorization头
        if (userStore.accessToken && config.headers.Authorization !== null) {
            config.headers.Authorization = `Bearer ${userStore.accessToken}`
            console.log('添加Authorization头:', config.headers.Authorization);
            console.log('当前accessToken:', userStore.accessToken);
            console.log('当前请求URL:', config.url);
        } else {
            console.log('未找到accessToken或已显式禁用，不添加Authorization头');
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response) => {
        return response
    },
    async (error) => {
        const userStore = useUserStore()
        const originalRequest = error.config

        // 处理403权限错误
        if (error.response && error.response.status === 403) {
            console.log('403错误详情:', error.response);
            console.log('403响应数据:', error.response.data);
            console.log('请求URL:', originalRequest.url);
            
            // 避免logout请求本身触发403时的无限循环
            if (!originalRequest.url.includes('/logout')) {
                await userStore.logout();
                ElMessage.error('权限不足，请重新登录');
                router.push('/login');
            }
            return Promise.reject(error);
        }

        // 处理401未授权错误
        if (error.response?.status === 401) {
            await userStore.logout();
            ElMessage.error('登录已过期，请重新登录');
            router.push('/login');
            return Promise.reject(error);
        }

        // 统一错误提示
        ElMessage.error(error.response?.data?.message || '请求失败')
        return Promise.reject(error)
    }
)

export default request