import { request } from '../../../utils'

export async function queryTopic(params) {
    return request(`/api/cms/subject/${params.id}`, {
        method: 'get'
    })
}

export async function createTopic(params) {
    return request('/api/cms/subject/category/subject', {
        method: 'post',
        data: JSON.stringify(params)
    })
}

export async function updateTopic(params) {
    return request('/api/cms/subject/category/subject', {
        method: 'patch',
        data: JSON.stringify(params)
    })
}

export async function removeTopic(params) {
    return request(`/api/cms/subject/${params.id}`, {
        method: 'delete'
    })
}

export async function queryBlocks(params) {
    return request('/api/cms/subject/blocks', {
        method: 'get',
        data: params
    })
}

export async function queryBlockArts(params) {
    return request('/api/cms/subject/articles', {
        method: 'get',
        data: params
    })
}

export async function createBlock(params) {
    return request('/api/cms/subject', {
        method: 'post',
        data: JSON.stringify(params)
    })
}

export async function updateBlock(params) {
    return request('/api/cms/subject', {
        method: 'patch',
        data: JSON.stringify(params)
    })
}

export async function removeBlock(params) {
    return request(`/api/cms/subject/block/${params.id}`, {
        method: 'delete'
    })
}

export async function citeArts(params) {
    return request('/api/cms/subject/articles', {
        method: 'POST',
        data: JSON.stringify(params)
    })
}

export async function addArt(params) {
    return request('', {
        method: 'POST',
        data: JSON.stringify(params)
    })
}