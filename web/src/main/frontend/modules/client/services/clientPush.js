import { request } from '../../../utils'

export async function queryPush (params) {
  return request('/api/client/push', {
    method: 'get',
    data: params
  })
}
export async function deletePush (params) {
  return request('/api/client/push', {
    method: 'patch',
    data:  JSON.stringify(params)
  })
}
