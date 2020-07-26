
import { request } from '../../../utils';

export async function queryLive(params) {
    return request(`/api/live/room/${params.id}`, {
        method: 'get'
    });
}

export async function createLive(params) {
    return request('/api/live/room/', {
        method: 'post',
        data: JSON.stringify(params)
    });
}

export async function updateLive(params) {
    return request('/api/live/room/', {
        method: 'patch',
        data: JSON.stringify(params)
    });
}

export async function createSpeak(params) {
    return request('/api/live/talk/speak', {
        method: 'post',
        data: JSON.stringify(params)
    });
}

export async function getLiveUsers(params) {
    return request('/api/live/user/', {
        method: 'get',
        data: params
    });
}

export async function getLiveUser(params) {
    return request(`/api/live/user/${params.id}`, {
        method: 'get'
    });
}

export async function removeLiveUser(params) {
    return request(`/api/live/user/${params.id}`, {
        method: 'delete'
    });
}

export async function createLiveUser(params) {
    return request('/api/live/user/', {
        method: 'post',
        data: JSON.stringify(params)
    });
}

export async function updateLiveUser(params) {
    return request('/api/live/user/', {
        method: 'patch',
        data: JSON.stringify(params)
    });
}
