import {request} from "../../../utils";

export async function query (params) {
    return request('/api/cms/ask/searchAskQuestions', {
        method: 'post',
        data: params
    })
}
export  async function queryTree() {

    return request('/api/cms/ask/getAllLocalGov', {
        method: 'get'

    })
}

export  async function offline(id) {
    return request(`/api/cms/ask/downline/${id}`, {
        method: 'post',
        data: {}
    })
}


export  async function online(id) {
    return request(`/api/cms/ask/online/${id}`, {
        method: 'post',
        data: {}
    })
}


export  async function  settop(id) {
    return request(`/api/cms/ask/setTop/${id}`, {
        method: 'post',
        data: {}
    })
}


export  async function  cancelTop(id) {
    return request(`/api/cms/ask/cancelTop/${id}`, {
        method: 'post',
        data: {}
    })
}




export  async function  getDetail(id) {
    // console.log(id)
    return request(`/api/cms/ask/${id}`, {
        method: 'get'

    })
}

export async function queryRecommond() {

    return request('/api/cms/category/tree', {
        method: 'get'

    })
}
export async function saveRecommond(params) {

    return request(`/api/cms/ask/recommendToArticle/${params.id}?askIds=${params.ids}`, {
        method: 'post'

    })
}


export async function save1(params) {

    return request(`/api/cms/ask/save`, {
        method: 'post',
        data: params
    })
}
export  async function  saveOrder(params) {
    return request(`/api/cms/ask/saveSortAsk`, {
        method: 'post',
        data: params
    })
}


export async function queryComment(params) {
    return request('/api/comment/comments/', {
        method: 'get',
        data: params
    })
}
