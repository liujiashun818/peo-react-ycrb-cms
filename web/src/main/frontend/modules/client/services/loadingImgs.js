import { request } from '../../../utils'

export async function query (params) {
  return request('/api/client/loadingImgs/', {
    method: 'get',
    data: params
  })
}

export async function update (params) {
  return request('/api/client/loadingImgs/', {
    method: 'patch',
    data: JSON.stringify(params)
  })
}

export async function create (params) {
  return request('/api/client/loadingImgs/', {
    method: 'post',
    data: JSON.stringify(params)
  })
}

export async function deleteItem (id) {
  return request(`/api/client/loadingImgs/${id}`, {
    method: 'delete',
  })
}

export async function getItem (id) {
  return request(`/api/client/loadingImgs/${id}`, {
    method: 'get',
  })
}

export async function onOff (id) {
  return request(`/api/client/loadingImgs/onOff/${id}`, {
      method: 'get',
  })
}