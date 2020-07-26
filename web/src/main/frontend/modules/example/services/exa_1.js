import { request } from '../../../utils'

export async function query (params) {
  return request('/api/cms/article/searchPage', {
    method: 'get',
    data: params
  })
}