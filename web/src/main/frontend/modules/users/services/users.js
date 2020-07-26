import { request } from '../../../utils'

export async function queryOffices (params) {
  return request('/api/users/office/tree', {
    method: 'get'
  })
}

export async function queryOffice (params) {
  return request(`/api/users/office/${params.id}`, {
    method: 'get'
  })
}

export async function createOffice (params) {
  return request('/api/users/office/add', {
    method: 'post',
    data: JSON.stringify(params)
  })
}

export async function updateOffice (params) {
  return request('/api/users/office/', {
    method: 'patch',
    data: JSON.stringify(params)
  })
}

export async function deleteOffice (params) {
  return request(`/api/users/office/${params.id}`, {
    method: 'delete'
  })
}

/*********************用户***********************/
export async function userList (params) {
  return request('/api/users/user/list', {
    method: 'get',
    data: params
  })
}

/*********************角色***********************/
export async function queryRoles(params) {
  return request('/api/users/role/listAll', {
    method: 'get'
  })
}

export async function queryRole(params) {
  return request(`/api/users/role/${params.id}`, {
    method: 'get'
  })
}

export async function createRole(params) {
  return request('/api/users/role/add', {
    method: 'post',
    data: JSON.stringify(params)
  })
}
export async function getUserById (id) {
  return request(`/api/users/user/${id}`, {
    method: 'get',
  })
}
export async function deleteItem (id) {
  return request(`/api/users/user/${id}`, {
    method: 'delete',
  })
}
export async function getRoleListAll (params) {
  return request(`/api/users/role/listAll`, {
    method: 'get',
    data:params
  })
}
export async function getOfficeTree (params) {
  return request(`/api/users/office/tree`, {
    method: 'get',
  })
}
export async function createUser (params) {
  return request(`/api/users/user/add`, {
    method: 'post',
    data:JSON.stringify(params)
  })
}
export async function updateUser (params) {
  return request(`/api/users/user/update`, {
    method: 'patch',
    data:JSON.stringify(params)
  })
}

export async function updateRole(params) {
  return request('/api/users/role/update', {
    method: 'patch',
    data: JSON.stringify(params)
  })
}

export async function deleteRole(params) {
  return request(`/api/users/role/${params.id}`, {
    method: 'delete'
  })
}
