import {defineStore} from 'pinia'
import {getUserInfo, login, logout, register, updateUserInfo} from '../api/user'

export const useUserStore = defineStore('user', {
    state: () => ({
        userInfo: null as any,
        accessToken: localStorage.getItem('accessToken') || null
    }),
    actions: {
        async login(userData: { username: string; password: string }) {
            try {
                const res = await login(userData)
                if (res.data.code === 200) {
                    this.accessToken = res.data.data.accessToken
                    localStorage.setItem('accessToken', this.accessToken!)
                    await this.fetchUserInfo()
                    return res.data
                }
                return res.data
            } catch (error) {
                console.error('登录失败:', error)
                throw error
            }
        },

        async register(userData: { username: string; password: string; phone?: string }) {
            try {
                const res = await register(userData)
                return res.data
            } catch (error) {
                console.error('注册失败:', error)
                throw error
            }
        },

        async fetchUserInfo() {
            try {
                console.log('开始获取用户信息')
                const res = await getUserInfo()
                console.log('用户信息请求响应:', res)
                if (res.data.code === 200) {
                    this.userInfo = res.data.data.user
                    console.log('用户信息更新成功:', this.userInfo)
                } else {
                    console.error('获取用户信息失败:', res.data.message)
                    this.userInfo = null
                }
            } catch (error) {
                console.error('获取用户信息请求异常:', error)
                this.userInfo = null
                throw error
            }
        },

        async updateUserInfo(userData: { nickname?: string; avatar?: string; phone?: string }) {
            try {
                const res = await updateUserInfo(userData)
                if (res.data.code === 200) {
                    await this.fetchUserInfo()
                }
                return res.data
            } catch (error) {
                console.error('更新用户信息失败:', error)
                throw error
            }
        },

        async logout() {
            try {
                await logout()
            } finally {
                this.userInfo = null
                this.accessToken = null
                localStorage.removeItem('accessToken')
            }
        },

        clearUserInfo() {
            this.userInfo = null
        }
    }
})