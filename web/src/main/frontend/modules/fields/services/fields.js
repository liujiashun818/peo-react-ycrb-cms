import { request } from '../../../utils'

export async function queryFieldGroups (params) {
    return request('/api/fields/field/group/', {
        method: 'get',
        data:params
    });
}

export async function queryFieldGroup (params) {
    return request(`/api/fields/field/group/${params.id}`, {
        method: 'get'
    });
}

export async function saveFieldGroup (params) {
    return request('/api/fields/field/group/', {
        method: 'post',
        data: JSON.stringify(params)
    });
}

export async function updateFieldGroup (params) {
    return request('/api/fields/field/group/', {
        method: 'post',
        data: JSON.stringify(params)
    });
}

export async function deleteFieldGroup (params) {
    return request(`/api/fields/field/group/${params.id}`, {
        method: 'delete'
    });
}

export async function batchDelete (params) {
    return request('/api/fields/field/group/batchDelete', {
        method: 'post',
        data: JSON.stringify(params.ids)
    });
}

export async function queryCategoryModels (params) {
    return request('/api/cms/category/model/all', {
        method: 'get'
    });
}
export async function queryFields (params) {
    return request('/api/fields/field/', {
        method: 'get',
        data:params
    });
}
export async function queryField (params) {
    return request(`/api/fields/field/${params.id}`, {
        method: 'get',
        data:params
    });
}
export async function checkFieldExist (params) {
    return request('/api/fields/field/exist', {
        method: 'get',
        data:params
    });
}

export async function deleteField(params) {
    return request(`/api/fields/field/${params.id}`, {
        method: 'delete'
    });
}

export async function deleteFields(params) {
    return request('/api/fields/field/batchDelete', {
        method: 'post',
        data: JSON.stringify(params.ids)
    });
}

export async function updateField(params) {
    return request('/api/fields/field/', {
        method: 'patch',
        data: JSON.stringify(params)
    });
}
