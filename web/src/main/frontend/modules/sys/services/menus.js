import { request } from '../../../utils'

export async function query (params) {
  return request('/api/sys/menu/tree', {
    method: 'get',
  })
}
export async function queryView (params) {
  return request('/api/sys/menu/treeView', {
    method: 'get',
  })
}

export async function create (params) {
  return request('/api/sys/menu/add', {
    method: 'post',
    data: JSON.stringify(params)
  })
}

export async function remove (params) {
  return request(`/api/sys/menu/${params.id}`, {
    method: 'delete'
  })
}

export async function update (params) {
  return request('/api/sys/menu/', {
    method: 'patch',
    data: JSON.stringify(params)
  })
}