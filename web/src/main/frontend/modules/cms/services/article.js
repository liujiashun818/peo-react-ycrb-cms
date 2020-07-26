import { request } from '../../../utils'

export async function deletePaper(id) {
    return request(`/api/cms/paper/${id}`, {
        method: 'delete'
    })
} 

export async function onlinePaper(id) {
    return request(`/api/cms/paper/online/${id}`, {
        method: 'get'
    })
} 

export async function downlinePaper(id) {
    return request(`/api/cms/paper/downline/${id}`, {
        method: 'get'
    })
} 

export async function getPaperList(params) {
    return request(`/api/cms/paper/searchPage`, {
        method: 'post',
        data: JSON.stringify(params)
    })
} 

export async function getPaperDetail(params) {
    return request(`/api/cms/paper/${params.id}`, {
        method: 'get'
    })
} 

export async function updatePaperDetail(params) {
    return request(`/api/cms/paper/update`, {
        method: 'post',
        data: params
    })
}

export async function queryArts(params) {
    return request('/api/cms/article/searchPage', {
        method: 'get',
        data: params
    })
}

export function queryArts2(params) {
    return request('/api/cms/article/searchPage', {
        method: 'get',
        data: params
    })
}

export async function queryCiteArts(params) {
    return request('/api/cms/article/referPage', {
        method: 'get',
        data: params
    });
}

export async function queryArt(params) {
    return request(`/api/cms/article/${params.id}`, {
        method: 'get'
    })
}

export async function createArt(params) {
    return request('/api/cms/article', {
        method: 'post',
        data: JSON.stringify(params)
    })
}

export async function removeArt(params) {
    return request(`/api/cms/article/${params.id}`, {
        method: 'delete'
    })
}

export async function updateArt(params) {
    return request('/api/cms/article', {
        method: 'patch',
        data: JSON.stringify(params)
    })
}

export async function updateArts(params) {
    return request('/api/cms/article/batchUpdate', {
        method: 'post',
        data: JSON.stringify(params)
    })
}

export async function onOffArt(params) {
    return request(`/api/cms/article/onOff/${params.id}`, {
        method: 'get'
    })
}

export async function onOffArts(params) {
    return request(`/api/cms/article/batchOnOff?articleIds=${params.articleIds}`, {
        method: 'post'
    })
}

export async function citeArts(params) {
    const block = params.block?`&block=${params.block}`:''
    return request(`/api/cms/article/batchSave?categoryId=${params.categoryId}${block}`, {
        method: 'post',
        data: JSON.stringify(params.list)
    })
}
export async function setPosition (params) {
    return request('/api/cms/article/setPosition', {
        method: 'get',
        data: params
    })
}
export async function markSensitiveWord (params) {
    return request('/api/comment/sensitiveWords/replaceSensitiveWordContent', {
        method: 'post',
        data: params
    })
}
export async function queryLabel (params) {
    return request('/api/cms/tags/queryAll', {
        method: 'get',
    })
}
export async function getUploadState (params) {
    return request('/api/upload/checkUploadStatus', {
        method: 'post',
        data: {fileUrls:params}
    })
}
export function getClientUploadState (params) {
    return request('/api/upload/checkUploadStatus', {
        method: 'post',
        data: {fileUrls:params}
    })
}
export async function addComment(params) {
    return request('api/comment/comments/addComment', {
        method: 'post',
        data: JSON.stringify(params)
    })
}

export async function queryGaArts(params) {
    return request('/api/cms/article/getPubliccmsArticle', {
        method: 'get',
        data: params
    });
}

export async function citeGaArts(params) {
    return request('/api/cms/article/savePubliccmsArticle', {
        method: 'post',
        data: params
    });
}



export async function onOffArtfromRemove(params) {
    return request(`/api/cms/article/OffLine/${params.id}`, {
        method: 'post'
    })
}

export async function onbachOffArtfromRemove(params) {
    return request(`/api/cms/article/batchOffLine?articleIds=${params.articleIds}`, {
        method: 'post'
       
    })
}

export async function getArticlePreviewUrl(id) {
    return request(`/api/cms/article/getArticlePreviewUrl?id=${id}`, {
        method: 'post'
       
    })
}


export async function fixPublishTime(param) {
    return request(`/api/cms/article/fixedPublish`, {
        method: 'post',
        data: JSON.stringify(param)
    })
}
export async function advancePublish(param) {
    return request(`/api/cms/article/advancePublish/${param.id}`, {
        method: 'get',
       
    })
}

export async function cancelPublish(param) {
    return request(`/api/cms/article/cancelPublish/${param.id}`, {
        method: 'get',
       
    })
}


export async function cancelBatchPublish(param) {
    return request(`/api/cms/article/batchCancelPublish?articleIds=${param.articleIds}`, {
        method: 'post'
       
    })
}

export async function batchAdvancePublish(param) {
    return request(`/api/cms/article/batchAdvancePublish?articleIds=${param.articleIds}`, {
        method: 'post'
       
    })
}