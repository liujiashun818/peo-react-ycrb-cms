import { request } from '../../../utils'

export async function query (params) {
  return request('/api/client/menu/tree', {
    method: 'get',
    // data: params
  })
}

export async function queryView (params) {
  return request('/api/client/menu/treeView', {
    method: 'get',
    // data: params
  })
}

export async function create (params) {
  return request('/api/client/menu', {
    method: 'post',
    data: JSON.stringify(params)
  })
}


export async function update (params) {
  return request('/api/client/menu', {
    method: 'patch',
    data: JSON.stringify(params)
  })
}

export async function tree (params) {
  return request('/api/sys/dict/listData', {
    method: 'get',
    data: params
  })
}

export async function categoryTree (params) {
  return request('/api/cms/category/tree', {
    method: 'get',
    data: params
  })
}
