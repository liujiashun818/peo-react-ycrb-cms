import { request } from '../../../utils'

export async function query (params) {
    return request('/api/client/floatingImgs', {
        method: 'get',
        data: params
    })
}

export async function update (params) {
    return request('/api/client/floatingImgs', {
        method: 'patch',
        data: JSON.stringify(params)
    })
}

export async function create (params) {
    return request('/api/client/floatingImgs', {
        method: 'post',
        data: JSON.stringify(params)
    })
}
export async function clientTree (params) {
    return request('/api/client/menu/tree', {
        method: 'get',
    })
}

export async function deleteItem (id) {
    return request(`/api/client/floatingImgs/${id}`, {
        method: 'delete',
    })
}

export async function onOff (id) {
    return request(`/api/client/floatingImgs/onOff/${id}`, {
        method: 'get',
    })
}
export async function getItem (id) {
    return request(`/api/client/floatingImgs/${id}`, {
        method: 'get',
    })
}
