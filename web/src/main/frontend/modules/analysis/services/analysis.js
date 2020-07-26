import { request } from '../../../utils'

export async function queryAnlsLogs(params) {
    return request('/api/sys/log', {
        method: 'get',
        data: params
    });
}

export async function queryAnlsCatgs(params) {
    return request('/api/cms/stats/list', {
        method: 'get',
        data: params
    });
}

export async function queryAnlsAuths(params) {
    return request('/api/cms/stats/count', {
        method: 'get',
        data: params
    });
}

export async function queryUsers() {
    return request('/api/users/user/all', {
        method: 'get'
    });
}

export  async function  querySend(params) {
    return request('/api/cms/stats/articleList', {
        method: 'get',
        data: params
    });
}
