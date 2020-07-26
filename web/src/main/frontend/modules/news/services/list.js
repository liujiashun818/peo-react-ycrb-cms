import { request } from '../../../utils'
// 列表
export async function query (params) {
  return request('/api/cms/revelations/list/', {
    method: 'get',
    data: params
  })
}
// 新增
export async function create (params) {
  return request('/api/cms/revelations/save/', {
    method: 'post',
    data: JSON.stringify(params)
  })
}
// 删除 
export async function deleteItem (id) {
  return request(`/api/cms/revelations/${id}`, {
    method: 'delete',
  })
}
// 根据id查询
export async function getItemById (params) {
  return request('/api/cms/revelations/form/', {
    method: 'get',
    data: params
  })
}

// 根据联系人查询 
export async function getItemByName (params) {
  return request('/api/cms/revelations/listByName/', {
    method: 'get',
    data: params
  })
}
