import { request } from '../../../utils'

export async function query (params) {
    return request('/api/cms/tags/', {
        method: 'get',
        data: params
    })
}

export async function update (params) {
    return request('/api/cms/tags/', {
        method: 'patch',
        data: JSON.stringify(params)
    })
}

export async function create (params) {
    return request('/api/cms/tags/', {
        method: 'post',
        data: JSON.stringify(params)
    })
}

export async function deleteItem (id) {
    return request(`/api/cms/tags/${id}`, {
        method: 'delete',
    })
}

export async function getItem (id) {
    return request(`/api/cms/tags/${id}`, {
        method: 'get',
    })
}
