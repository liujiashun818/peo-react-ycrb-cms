import { request } from '../../../utils'

export async function getUserInfo () {
  return request('/api/users/user/current', {
    method: 'get',
  })
}

export async function updateUserInfo (params) {
  return request('/api/users/user/currentUpdate', {
    method: 'patch',
    data:JSON.stringify(params)
  })
}