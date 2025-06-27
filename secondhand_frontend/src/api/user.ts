import request from "./request.ts"

/**
 * 用户相关API接口
 * 包含登录、注册、个人信息管理等功能
 */

// 登录接口
/**
 * 用户登录接口
 * @param {Object} data - 登录参数
 * @param {string} data.username - 用户名
 * @param {string} data.password - 用户密码
 * @returns {Promise<Object>} 登录结果，包含token和用户信息
 */
export function login(data: { username: string; password: string }) {
    return request({
        url: '/user/login',
        method: 'POST',
        data
    })
}

// 注册接口
/**
 * 用户注册接口
 * @param {Object} data - 注册参数
 * @param {string} data.username - 用户名
 * @param {string} data.password - 用户密码
 * @param {string} [data.phone] - 可选手机号
 * @returns {Promise<Object>} 注册结果
 */
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
        console.error('获取用户信息失败:', error)
        throw error
    })
}

/**
 * 根据用户ID获取用户信息
 * @param {number} userId - 用户ID
 * @returns {Promise<Object>} 用户信息
 */
export function getUserById(userId: number) {
    return request({
        url: `/user/${userId}`,
        method: 'GET'
    }).then(response => {
        console.log('用户信息响应:', response)
        return response
    }).catch(error => {
        console.error('获取用户信息失败:', error)
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