import {getDict} from '../../../services/app';
import {queryCatgs} from '../services/category';
import {queryCiteArts, removeArt, updateArt, citeArts as citeArts_a, queryGaArts, citeGaArts} from '../services/article';
import {citeArts as citeArts_t} from '../services/topic';
import { Jt } from '../../../utils';
import { log } from 'util';

export default {
    namespace: 'cms',
    state: {
        // 栏目列表
        catgs: [],
        // 初始化栏目ID
        catgId: '',
        //点击栏目 id 后，新建文章时选中
        treeCatgId: '',
        // 初始化栏目模型ID
        catgModelId: '',
        // 文章类型列表
        artTypes: [],
        // 文章标签列表
        artTags: [],
        // 普通文章列表查询条件
        naq: {},
        // 引用文章列表
        cas: [],
        // 引用文章列表查询条件
        caq: {},
        // 引用文章列表分页信息
        cap: {
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            pageSize: 10,
            current: 1,
            total: null
        },
        // 引用文章列表是否正在加载中
        cal: false,
        // 引用文章弹框是否可见
        camv: false,
        // 编辑文章弹框是否可见
        eamv: false,
        // 当前文章
        art: {},
        gamv: false,
        gal: false,
        gap: {
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            pageSize: 10,
            current: 1,
            total: null
        },
        gas: []
    },

    subscriptions: {
        setup ({ dispatch, history }) {
            window.Mydispatch = dispatch;
            console.log("history",history)
            // 初始化
            dispatch({
                type: 'app/firstLogined', payload: {
                    fn: dispatch,
                    data: {type: 'effect:init'}
                }
            });
        }
    },

    effects: {
        *'effect:init'({payload}, {call, put}) {
            // 获取栏目列表
            yield put({type: 'effect:query:catgs'});
            // 获取文章类型
            yield put({type: 'effect:query:dict', payload: {attrName: 'artTypes', type: 'cms_article_type'}});
            // 获取文章标签
            yield put({type: 'effect:query:dict', payload: {attrName: 'artTags', type: 'cms_article_tag'}});
        },
        *'effect:query:catgs'({payload}, {call, put, select}) {
            
            const {code, data} = yield call(queryCatgs);
            if(code === 0) {
                yield put({
                    type: 'reducer:query:catgs:success',
                    payload: data
                });
            }
        },
        *'effect:query:dict'({payload}, {call, put}) {
            const data = yield call(getDict, {type: payload.type});
            if(data && data.length > 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        [`${payload.attrName}`]: data
                    }
                });
            }
        },
        *'effect:delete:art'({payload}, {call, put}) {
            const {code} = yield call(removeArt, {id: payload.id});
            if(code === 0) {
                payload.success && payload.success();
            }
        },
        *'effect:update:citeArt'({payload}, {call, put}) {
            const {code} = yield call(updateArt, payload.art);
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        eamv: false
                    }
                });
                if(payload.source === 'article') {
                    yield put({
                        type: 'article/effect:query:arts',
                        payload: {
                            location: payload.location
                        }
                    });
                }
                else if(payload.source === 'subject') {
                    yield put({
                        type: 'topic/effect:query:blockArts'
                    });
                }
            }
        },
        *'effect:cite:arts'({payload}, {call, put, select}) {
            if(payload.source === 'article') {
                const naq = yield select(({cms}) => cms.naq)
                const {code} = yield call(citeArts_a, {categoryId: naq.categoryId || '1', list: payload.list, block: naq.block || '2' });
                if(code === 0) {
                    yield put({
                        type: 'reducer:update',
                        payload: {camv: false}
                    });
                    yield put({
                        type: 'article/effect:query:arts',
                        payload: {
                            location: payload.location
                        }
                    });
                }
            }
            else if(payload.source === 'subject') {
                const {code} = yield call(citeArts_t, {categoryId: payload.categoryId, list: payload.list});
                if(code === 0) {
                    yield put({
                        type: 'reducer:update',
                        payload: {camv: false}
                    });
                    yield put({
                        type: 'topic/effect:query:blockArts'
                    });
                }
            }

        },
        *'effect:query:cas'({payload}, {call, put, select}) {
            yield put({
                type: 'reducer:update',
                payload: {
                    cal: true
                }
            });
            const {page} = yield select(({cms}) => {return {page: cms.cap};});
            const {code, data} = yield call(queryCiteArts, {
                pageNumber: 1,
                pageSize: page.pageSize,
                ...payload
            });
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        cas: data.list || [],
                        cal: false,
                        caq: payload,
                        cap: {
                            ...page,
                            total: data.pager.recordCount,
                            current: data.pager.pageNumber,
                            pageSize: data.pager.pageSize
                        }
                    }
                });
            }
        },
        *'effect:toggle:cam'({payload}, {call, put}) {
            const {camv, sysCode} = payload;
            yield put({
                type: 'reducer:update',
                payload: {
                    camv
                }
            });
            if(camv) {
                yield put({
                    type: 'effect:query:cas',
                    payload: {
                        sysCode
                    }
                });
            }
        },
        *'effect:toggle:eam'({payload}, {call, put}) {
            const {eamv, art={}} = payload;
            yield put({
                type: 'reducer:update',
                payload: {
                    eamv,
                    art
                }
            });
        },
        *'effect:update:state'({payload}, {call, put}) {
            yield put({
                type: 'reducer:update',
                payload
            });
        },
        *'effect:toggle:gam'({payload}, {call, put}) {
            const {gamv} = payload;
            yield put({
                type: 'reducer:update',
                payload: {
                    gamv
                }
            });
            if(gamv) {
                yield put({
                    type: 'effect:query:gas',
                    payload: {
                    }
                });
            }
        },
        *'effect:query:gas'({payload}, {call, put, select}) {
            yield put({
                type: 'reducer:update',
                payload: {
                    gal: true
                }
            });
            const {page} = yield select(({cms}) => {return {page: cms.gap};});
            const {code, data} = yield call(queryGaArts, {
                pageNumber: 1,
                pageSize: page.pageSize,
                ...payload
            });
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        gas: data.list || [],
                        gal: false,
                        gaq: payload,
                        gap: {
                            ...page,
                            total: data.pager.recordCount,
                            current: data.pager.pageNumber,
                            pageSize: data.pager.pageSize
                        }
                    }
                });
            }
        },
        *'effect:citeGa:arts'({payload}, {call, put, select}) {
            const naq = yield select(({cms}) => cms.naq)
            const {code} = yield call(citeGaArts, {categoryId: Number(naq.categoryId), articleIds: payload.articleIds, block: naq.block || '2'});
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {gamv: false}
                });
                yield put({
                    type: 'article/effect:query:arts',
                    payload: {
                        location: payload.location
                    }
                });
            }
        },
        *'effect:query:CatgId'({ payload }, { call, put }) {
            // return console.log(payload);

            yield put({
                type: 'reducer:update',
                payload: { treeCatgId: payload}
            });
        },
    },

    reducers: {
        'reducer:update'(state, {payload}) {
            return {...state, ...payload};
        },
        'reducer:query:catgs:success'(state, {payload}) {
            console.log("5",payload?Jt.tree.format(payload):[]);
            const catgs = payload?Jt.tree.format(payload):[];
            const catgId = payload?Jt.catg.getInitId(catgs):"";
            
            return {...state, catgs, catgId};
        }
       
    }
};
