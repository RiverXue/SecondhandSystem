import request from "./request.ts"

// 登录接口
export function login(data: { username: string; password: string }) {
    return request({
        url: '/user/login',
        method: 'POST',
        data
    })
}

// 注册接口
export function register(data: { username: string; password: string; phone?: string }) {
    return request({
        url: '/user/register',
        method: 'POST',
        data
    })
}

// 获取当前用户信息
export function getUserInfo() {
    return request({
        url: '/user/info',
        method: 'GET'
    }).then(response => {
        console.log('用户信息响应:', response)
        return response
    }).catch(error => {
        console.error('用户信息请求失败:', error)
        throw error
    })
}

// 更新用户信息
export function updateUserInfo(data: { nickname?: string; avatar?: string; phone?: string }) {
    return request({
        url: '/user/update',
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        data
    })
}

// 刷新令牌
export function refreshToken(refreshToken: string) {
    return request({
        url: '/user/refresh-token',
        method: 'POST',
        params: { refreshToken },
        headers: { Authorization: null }
    })
}

// 登出
export function logout() {
    return request({
        url: '/user/logout',
        method: 'POST'
    })
}