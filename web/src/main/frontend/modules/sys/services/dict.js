import { request } from '../../../utils'

export async function query (params) {
  return request('/api/sys/dict', {
    method: 'get',
    data:params
  })
}
export async function getItem (id) {
  return request(`/api/sys/dict/${id}`, {
    method: 'get',
  })
}
export async function deleteItem (id) {
  return request(`/api/sys/dict/${id}`, {
    method: 'delete',
  })
}
export async function create (params) {
  return request(`/api/sys/dict/`, {
    method: 'post',
    data:JSON.stringify(params)
  })
}
export async function update (params) {
  return request(`/api/sys/dict/`, {
    method: 'patch',
    data:JSON.stringify(params)
  })
}
export async function typeList () {
  return request(`/api/sys/dict/types`, {
    method: 'get',
  })
}