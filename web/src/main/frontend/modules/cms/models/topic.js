import { message } from 'antd';
import { getDict } from '../../../services/app';
import { queryCatgs } from '../services/category';
import { queryArts, updateArt, updateArts, onOffArt, onOffArts, removeArt } from '../services/article';
import {
    queryTopic, createTopic, updateTopic, removeTopic,
    queryBlocks, queryBlockArts, createBlock, updateBlock, removeBlock,
    importArts, addArt
} from '../services/topic';
import { routerPath } from '../../../constants';
import { Jt } from '../../../utils';

export default {
    namespace: 'topic',
    state: {
        // 栏目列表
        catgs: [],
        // 专题基本属性
        base: {},
        fileList:[],
        // 专题区块
        blocks: [],
        // 当前激活区块
        curBlock: {},
        // 区块操作类型
        blockOpType: 'add',
        // 区块文章列表是否正在加载中
        baLoading: false,
        // 区块文章列表分页信息
        baPag: {
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            pageSize: 10,
            current: 1,
            total: null
        },
        // 区块文章列表查询条件
        baQuery: {},
        // 区块文章列表选中的文章集合
        baSelArts: [],
        // 区块文章列表权重变更集合
        wgtChgObj: {},
        // 区块文章列表
        blockArts: [],
        // 区块编辑弹框是否可见
        bemVisible: false
    },

    subscriptions: {
        setup ({ dispatch, history }) {
            window.Mydispatch = dispatch;
            history.listen(location => {
                if(location.pathname === routerPath.TOPIC_EDIT) {
                    // 初始化专题编辑页面
                      dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:init:editPage',payload:{location}}
                      }});
                }
                else if(location.pathname === routerPath.TOPIC_DETAIL) {
                    // 获取专题详情页面
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:init:detailPage',payload:{location}}
                    }});
                }
            });
        }
    },

    effects: {
        *'effect:update:art'({payload}, {call, put}) {
            const {code} = yield call(updateArt, payload);
            if(code === 0) {
                yield put({
                    type: 'effect:hide:aem'
                });

                yield put({
                    type: 'effect:query:blockArts'
                });
            }
        },
        *'effect:onOff:art'({ payload }, { call, put }) {
            const { code } = yield call(onOffArt, payload);
            if(code === 0) {
                yield put({
                    type: 'effect:query:blockArts'
                });
            }
        },
        *'effect:onOff:arts'({ payload }, { call, put }) {
            yield put({
                type: 'reducer:loading',
                payload: {
                    baLoading: true
                }
            });

            const { code } = yield call(onOffArts, payload);
            if(code === 0) {
                message.success(`${payload.tips}修改成功！可通过${payload.tips}标签搜索查看`)
                delete payload.tips
                yield put({
                    type: 'effect:query:blockArts'
                });
                yield put({
                    type: 'reducer:loading',
                    payload: {
                        baLoading: true
                    }
                });
            }
            yield put({
                type: 'reducer:loading',
                payload: {
                    baLoading: false
                }
            });
        },
        *'effect:update:arts'({payload}, {call, put}) {
            const { code } = yield call(updateArts, payload.arts);
            if(code === 0) {
                yield put({
                    type: 'effect:init:detailPage',
                    payload: {
                        location: payload.location
                    }
                })
            }
        },
        *'effect:update:artsTopicWeight'({payload}, {call, put}) {
            const { code } = yield call(updateArts, payload.arts);
            if(code === 0) {
                yield put({
                    type: 'effect:query:blockArts',
                    payload: {
                    }
                })
            }
        },
        *'effect:delete:art'({payload}, {call, put}) {
            const { code } = yield call(removeArt, payload);
            if(code === 0) {
                yield put({
                    type: 'effect:query:blockArts',
                })
            }
        },
        *'effect:init:editPage'({payload}, {call, put, select}) {
            const res = yield call(queryCatgs);
            let catgs = [];
            let categoryId = yield select(({cms}) => cms.naq.categoryId);
            if(res.code === 0) {
                catgs = res.data;
                Jt.catg.format(catgs);
                if(!categoryId) {
                    categoryId = Jt.catg.getInitId(catgs);
                }
            }

            const query = payload.location.query;

            const obj = {
                catgs,
                base: {}
            };
            const publishDate = new Date().format('yyyy-MM-dd hh:mm:ss');
            if(query.action === 'update') {
                const res = yield call(queryTopic, {id: query.id});
                res.data.publishDate = res.data.publishDate || publishDate;
                if(res.code === 0) {
                    if(res.data.bannerUrl) {
                        res.data.bannerUrl = JSON.parse(res.data.bannerUrl)
                    }
                    obj.base = res.data;
                }
            }
            else if(query.action === 'create') {
                obj.base = {
                    showTitle: false,
                    showTop:"1",
                    pageType:"1",
                    article: {
                        categoryId: categoryId,
                        delFlag: '2',
                        block: '2',
                        viewType: 'normal',
                        publishDate,
                       
                    }
                }
            }

            yield put({
                type: 'reducer:update',
                payload: obj
            });
        },
        *'effect:init:detailPage'({payload}, {call, put, select}) {
            const {curBlock, base} = yield select(({topic}) => {return {base: topic.base, curBlock: topic.curBlock};});
            const {id, articleId, isReference} = payload.location.query;
            let obj = {curBlock, baQuery: {delFlag: 0}, blockArts: []};
            // 获取专题基本信息
            const res_1 = yield call(queryTopic, {id: isReference === 'true' ? id : id});
            if(res_1.code === 0) {
                obj.base = res_1.data;
            }
            // 获取专题对应区块
            const res_2 = yield call(queryBlocks, {id, articleId, isReference});
            if(res_2.code === 0) {
                obj.blocks = res_2.data || [];
                if((!curBlock.id || base.id !== obj.base.id) && obj.blocks.length > 0) {
                    obj.curBlock = obj.blocks[0];
                }
                else if(obj.blocks.length === 0) {
                    obj.curBlock = {};
                }
            }

            yield put({
                type: 'reducer:update',
                payload: obj
            });

            // 获取区块对应的文章列表
            if(!Jt.object.isEmpty(obj.curBlock)) {
                yield put({
                    type: 'effect:query:blockArts'
                });
            }
        },
        *'effect:create:topic'({ payload }, { call, put }) {
            const { code } = yield call(createTopic, payload.topic);
            if(code === 0) {
                payload.success && payload.success()
            }
        },
        *'effect:update:topic'({ payload }, { call, put }) {
            const { code } = yield call(updateTopic, payload.topic);
            if(code === 0) {
                payload.success && payload.success();
            }
        },
        *'effect:query:blockArts'({ payload }, { call, put, select }) {
            let {block, page, query} = yield select( ({ topic }) => {return {block: topic.curBlock, page:topic.baPag, query: topic.baQuery}});
            const { code, data } = yield call(queryBlockArts, {
                pageNumber: 1,
                pageSize: page.pageSize,
                categoryId: block.id,
                delFlag: 0,
                ...query,
                ...payload
            });
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        loading: false,
                        blockArts: data.list,
                        baSelArts: [],
                        baQuery: {...query, ...payload},
                        wgtChgObj: {},
                        baPag: {
                            ...page,
                            total: data.pager.recordCount,
                            current: data.pager.pageNumber,
                            pageSize: data.pager.pageSize
                        }
                    }
                });
            }
        },
        *'effect:create:block'({ payload }, { call, put, select }) {
            let blocks = yield select(({ topic }) => topic.blocks);
            const {code, data} = yield call(createBlock, payload.block);
            if(code === 0) {
                blocks.push(data);
                blocks = blocks.sort((a, b) => {
                    return b.weight - a.weight;
                });
                yield put({
                    type: 'reducer:update',
                    payload: {
                        bemVisible: false,
                        blocks,
                        curBlock: data,
                        blockArts: []
                    }
                });
            }
        },
        *'effect:update:block'({ payload }, { call, put, select }) {
            let blocks = yield select(({ topic }) => topic.blocks);
            const {code, data} = yield call(updateBlock, payload.block);
            if(code === 0) {
                for(let i = 0, len = blocks.length; i < len; i++) {
                    if(blocks[i].id == data.id) {
                        blocks.splice(i, 1);
                        break;
                    }
                }
                blocks.push(data);
                blocks = blocks.sort((a, b) => {
                    return b.weight - a.weight;
                });
                yield put({
                    type: 'reducer:update',
                    payload: {
                        bemVisible: false,
                        blocks,
                        curBlock: data
                    }
                });
            }
        },
        *'effect:delete:block'({ payload }, { call, put, select }) {
            const {id, location} = payload;
            const {code} = yield call(removeBlock, {id})
            if(code === 0) {
                const curBlock = yield select(({topic}) => topic.curBlock);
                if(curBlock.id == id) {
                    yield put({
                        type: 'effect:init:detailPage',
                        payload: {
                            location
                        }
                    });
                }
                else {
                    const blocks = yield select(({topic}) => topic.blocks);
                    for(let i = 0, len = blocks.length; i < len; i++) {
                        if(blocks[i].id == id) {
                            blocks.splice(i, i);
                            break;
                        }
                    }
                    yield put({
                        type: 'reducer:update',
                        payload: {
                            blocks
                        }
                    });
                }
            }
        },
        *'effect:switch:block'({ payload }, { call, put, select }) {
            yield put({ type: 'reducer:update', payload: { loading: true } });
            const blocks = yield select(({topic}) => topic.blocks);
            if(blocks.length > 0) {
                let curBlock = {};
                for(let i = 0, len = blocks.length; i < len; i++) {
                    if(blocks[i].id == payload.id) {
                        curBlock = blocks[i];
                        break;
                    }
                }
                yield put({
                    type: 'reducer:update',
                    payload: {
                        curBlock
                    }
                });
                yield put({
                    type: 'effect:query:blockArts'
                });
            }
        },
        *'effect:update:baSelArts'({payload}, {call, put, select}) {
            yield put({
                type: 'reducer:update',
                payload: {
                    baSelArts: payload
                }
            });
        },
        *'effect:update:wgtChgObj'({ payload }, { call, put }) {
            yield put({
                type: 'reducer:update:wgtChgObj',
                payload
            });
        },
        *'effect:show:bem'({ payload }, { call, put }) {
            yield put({type: 'reducer:update', payload:{bemVisible: true, blockOpType: payload.blockOpType}});
        },
        *'effect:hide:bem'({ payload }, { call, put }) {
            yield put({type: 'reducer:update', payload:{bemVisible: false}});
        }
    },

    reducers: {
        'reducer:loading'(state, { payload }) {
            return { ...state, ...payload }
        },
        'reducer:update'(state, { payload }) {
            return { ...state, ...payload }
        },
        'reducer:update:wgtChgObj'(state, { payload }) {
            let wgtChgObj = {}
            if(payload) {
                wgtChgObj = { ...state.wgtChgObj, ...payload }
            }
            return { ...state, wgtChgObj }
        }
    }
}
