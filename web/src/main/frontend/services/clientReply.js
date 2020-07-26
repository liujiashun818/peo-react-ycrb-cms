import { request } from '../utils'

export async function queryReplyInfo(params) {
    return request(`/api/comment/comments/${params.id}`, {
        method: 'get'
    })
}

export async function createReplyInfo(params) {
    return request('/api/comment/comments/', {
        method: 'patch',
        data: JSON.stringify(params)
    })
}
