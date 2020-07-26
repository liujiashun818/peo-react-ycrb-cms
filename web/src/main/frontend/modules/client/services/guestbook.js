import { request } from '../../../utils'

export async function query (params) {
  return request('/api/guestbook', {
    method: 'get',
    data: params
  })
}


export async function deleteItem (id) {
  return request(`/api/guestbook/${id}`, {
    method: 'delete',
  })
}