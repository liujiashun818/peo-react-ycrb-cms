import { request } from '../utils'

export async function login (params) {
    return request('/auth/login', {
        method: 'post',
        data: JSON.stringify(params)
    });
}

export async function logout (params) {
    return request('/auth/logout', {
        method: 'get',
        data: params
    });
}

export async function userInfo (params) {
    return request('/api/users/user/current', {
        method: 'get',
        data: params
    });
}

export async function currentPermission (params) {
    return request('/api/users/user/currentPermission', {
        method: 'get',
        data: params
    });
}

export async function getMenu (params) {
    return request('/api/sys/menu/treeView', {
        method: 'get',
        data: params
    });
}

export async function getDict(params) {
    return request('/api/sys/dict/listData', {
        method: 'get',
        data: params
    });
}

export async function getOrgTree(params) {
    return request('/api/service/orgtree/treeView', {
        method: 'get',
        data: params
    });

}

// 获取媒体文件列表
export async function getMediaList(params) {
    return request('/api/file/media/info/', {
        method: 'get',
        data: params
    })
}

// 获取系统全局配置参数
export async function queryCfg() {
    return request('/api/front', {
        method: 'get'
    });
}

// 获取栏目模型
export async function getCatgModels() {
    return request('/api/cms/category/model/all', {
        method: 'get'
    });
}
