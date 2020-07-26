import { request } from '../utils'

export async function queryPushInfo(params) {
    return request(`/api/client/push/${params.id}`, {
    	method: 'get'
    })
}

export async function createPushInfo(params) {
	return request('/api/client/push', {
		method: 'post',
		data: JSON.stringify(params)
	})
}