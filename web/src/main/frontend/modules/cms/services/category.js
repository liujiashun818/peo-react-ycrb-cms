import { request } from '../../../utils'

export async function queryCatgs(params) {
    return request('/api/cms/category/tree', {
        method: 'get'
    })
}

export function queryCatgs2(params) {
    return request('/api/cms/category/tree', {
        method: 'get'
    })
}

export async function queryCatg(params) {
    return request(`/api/cms/category/${params.id}`, {
        method: 'get'
    })
}

export async function createCatg(params) {
    return request('/api/cms/category', {
        method: 'post',
        data: JSON.stringify(params)
    })
}

export async function removeCatg(params) {
    return request(`/api/cms/category/${params.id}`, {
        method: 'delete'
    })
}

export async function updateCatg(params) {
    return request('/api/cms/category', {
        method: 'patch',
        data: JSON.stringify(params)
    })
}

export async function updateCatgs(params) {
    return request('/api/cms/category/batchUpdate', {
        method: 'post',
        data: JSON.stringify(params)
    })
}

export async function onOffCatg(params) {
    return request(`/api/cms/category/onOff/${params.id}`, {
        method: 'get'
    })
}

export async function queryCatgModels() {
    return request('/api/cms/category/model/all', {
        method: 'get'
    });
}

export async function queryCatgModel(params) {
    return request(`/api/cms/category/model/${params.id}`, {
        method: 'get'
    });
}
