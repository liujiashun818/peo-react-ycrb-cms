import { request } from '../../../utils'

export async function query(params) {
    return request('/api/comment/comments/', {
        method: 'get',
        data: params
    })
}
export async function getSensData(id) {
    return request(`/api/comment/sensitiveWords/${id}`, {
        method: 'get',
    })
}
export async function updateSensData(params) {
    return request(`/api/comment/sensitiveWords/`, {
        method: 'patch',
        data: JSON.stringify(params)
    })
}
export async function saveSensData(params) {
    return request(`/api/comment/sensitiveWords/`, {
        method: 'post',
        data: JSON.stringify(params)
    })
}
export async function update(params) {
    return request('/api/comment/comments/', {
        method: 'patch',
        data: JSON.stringify(params)
    })
}
export async function batchOnOff(params) {
    return request(`/api/comment/comments/batchOnOff?commentIds=${params.commentIds}`, {
        method: 'post',
    })
    // return request(`/api/comment/comments/batchOnOff?commentIds=${params.commentIds}&updateDelFlag=${params.updateDelFlag}`, {
    //     method: 'post',
    // })
}
export async function batchOnOff2(params) {
    return request(`/api/comment/comments/auditComment`, {
        method: 'post',
        data: JSON.stringify(params)
    })
    // return request(`/api/comment/comments/batchOnOff?commentIds=${params.commentIds}&updateDelFlag=${params.updateDelFlag}`, {
    //     method: 'post',
    // })
}
export async function addReply(params) {
    return request('/api/comment/comments/addReplyComment', {
        method: 'post',
        data: JSON.stringify(params)
    })
}
