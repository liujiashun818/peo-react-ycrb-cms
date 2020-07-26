import { request } from '../../../utils'

export async function getKeywords (params) {
  return request('/api/app/keywords/', {
    method: 'get',
    data: params
  })
}
export async function addKeywords (params) {
  return request('/api/app/keywords/', {
    method: 'post',
    data: params
  })
}
export async function deleteKeywords (params) {
  return request(`/api/app/keywords/${params.id}`, {
    method: 'delete',
    data: params
  })
}
// export async function getFilterKeywords (params) {
//   return request(`/api/app/keywords/${params.content}`, {
//     method: 'get',
//     data: params
//   })
// }