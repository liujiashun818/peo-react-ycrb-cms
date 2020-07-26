import { request } from '../../../utils';

export async function getPublicCmsCategoryList(params) {
  return request('/api/cms/article/getPublicCmsCategoryList/', {
    method: 'get',
    data: params,
  });
}
export async function postSyncArticle({ articleId, syncCategoryId }) {
  return request(`/api/cms/article/syncArticle?articleId=${articleId}&syncCategoryId=${syncCategoryId}`, {
    method: 'post',
  });
}
