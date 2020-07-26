import { request } from '../../../utils'

export async function query (params) {
  return request('/api/activityCode/showActivityCode', {
    method: 'get',
    data:params
  })
}


export async function queryActivity (params) {
  return request('/api/activityCode/showActivityCode', {
    method: 'get'
  })
}

export async function createActivity (params) {
  return request('/api/activityCode/activityAdd', {
    method: 'post',
    data: JSON.stringify(params)
  })
}

export async function updateActivity (params) {
  return request('/api/activityCode/showActivityCode', {
    method: 'get',
    data: JSON.stringify(params)
  })
}

export async function deleteActivity (params) {
  return request(`/api/activityCode/${id}`, {
    method: 'delete'
  })
}