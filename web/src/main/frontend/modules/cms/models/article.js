import { parse } from 'qs'
import { message } from 'antd';
import { getDict } from '../../../services/app'
import { createPushInfo } from '../../../services/clientPush'
import { queryCatgs, queryCatgModel } from '../services/category'
import {
    createArt,
    removeArt,
    updateArt,
    updateArts,
    queryArts,
    getUploadState,
    queryArt,
    onOffArt,
    onOffArts,
    setPosition,
    markSensitiveWord,
    queryLabel,
    getPaperDetail,
    updatePaperDetail,
    getPaperList,
    deletePaper,
    downlinePaper,
    onlinePaper,
    addComment,
    onOffArtfromRemove,
    onbachOffArtfromRemove,
    getArticlePreviewUrl,
    fixPublishTime,
    advancePublish,
    cancelPublish,
    cancelBatchPublish,
    batchAdvancePublish,
} from '../services/article'
import {query, update, batchOnOff, addReply, batchOnOff2} from '../services/comment'
import { getPublicCmsCategoryList, postSyncArticle } from '../services/syncArticle'
import { routerPath } from '../../../constants'
import { Jt } from '../../../utils'

function formatTreeData(data = []) {
  for(let i = 0; i < data.length; i++) {
    data[i].key = data[i].value = data[i].id + ''
    if(!Jt.array.isEmpty(data[i].child)) {
      data[i].children = data[i].child
      delete data[i].child
      formatTreeData(data[i].children);
    }
  }
  return data;
}
function setChildren({ parent, ...restItem }) {
  let treeItem = {};
  if (parent) {
    treeItem = {
      ...parent,
      children: {
        ...restItem,
      },
    };
    return setChildren(treeItem);
  }
  treeItem = {
    ...restItem,
  };
  return treeItem;
}

function transformTree({ children, ...restItem }, arr = []) {
  const index = arr.findIndex(item => item.id === restItem.id);
  if (index !== -1) {
    if (children && arr[index].children) {
      // const nextIndex = arr[index].children
      // .findIndex(next => next.id === children.id)
      transformTree(children, arr[index].children);
    } else {
      arr[index].children = [
        { ...children },
      ];
    }
  } else {
    arr.push({
      ...restItem,
    });
  }
}
function generateTreeData(arr, publicCmsCategoryList) {
  const result = [];
  arr.map(cate => {
    const cateItem = setChildren(cate, publicCmsCategoryList);
    transformTree(cateItem, result);
  });
  return result
}
function getfileList(imgUrl){
    let imgArr = [],fileList=[]
    if(imgUrl) {
        imgArr = imgUrl.indexOf(',') > -1 ? imgUrl.split(',') : [imgUrl];
        imgArr.forEach(function (item, i) {
            fileList[i] = {
                uid: i,
                url: item
            }
        })
    }
    return {fileList,imgArr}
}

export default {
    namespace: 'article',

    state: {
        // 是否正在加载中
        loading: false,
        //是否正在保存
        saveLoading:false,
        // 检索条件
        query: {},
        // 文章列表
        arts: [],
        // 当前文章
        curArt: {},
        description:'',
        sensitiveWordsContent:'',
        labelList:[],
        formData: [],
        //文章详情新增状态
        artNewStatus:{
            threeFlag:true,
            viewType:'normal',
            isTailor:true,
            fileList:[],
            imgArr:[],
            fileList_two:[],
            thumbnailArr:[],
            fileList_three:[],
            thumbnailArr_three:[],
            fileList_four:[],
            thumbnailArr_four:[],
            fileList_five:[],
            thumbnailArr_five:[],
            isVoice: true,
            isShowTopImg: false
        },
        visible:false,
        sensitiveWordsVisible:false,
        // 推送弹框是否可见
        pushModalVisible: false,
        previewModalVisible: false,
        previewModalUrl:"",
        publishModal:false,
        publishId:"",
        // 选中的文章集合
        selArts: [],
        // 文章权重变更集合
        wgtChgObj: {},
        // 分页信息
        pagination: {
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            pageSize: 10,
            current: 1,
            total: null
        },
        paginationPaper: {
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            pageSize: 10,
            current: 1,
            total: null
        },
        searchPaperData: {},
        isShowListTitle: false,
        surveyNum: 0,
        albumNum: 0,
        videoNum: 0,
        audioNum: 0,
        mediaType: '',
        viewComment: false,  // 查看用户破评论
        commentList: [],
        commentPagination: {
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            pageSize: 10,
            current: 1,
            total: null
        },
        commentDelflagKeys: [],
        commentLoading: false,
        commentDelFlag: 0,  //0上线 1下线 2审核 4审核未通过
        commentContent: '',
        publicCmsCategoryList: [],
        selectCategory: null,
        paperData: {}, // 报纸详情数据
        paperListData: {}, // 报纸列表数据
        modelId: ''
    },

    subscriptions: {
        setup ({ dispatch, history }) {
            window.Mydispatch = dispatch;
            history.listen(location => {
                if(location.pathname === routerPath.ARTICLE_LIST) {
                    // console.log('setup')
                    // 获取文章列表
                    if(!location.query.categoryId){
                        dispatch({type: 'app/firstLogined', payload: {
                            fn:dispatch,
                            data:{type: 'reducer:update',payload:{modelId:''}}
                        }});
                   
                    }
                   
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:arts',payload:{location}}
                    }});
                    //  获取tree
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type:'effect:query:catgs',payload:{categoryId:location.query.categoryId}}
                    }});
                }
                else if(location.pathname === routerPath.ARTICLE_EDIT) {
                    // 获取文章详情
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:art',payload:{location}}
                    }});
                    dispatch({type: 'app/firstLogined', payload: {
                            fn:dispatch,
                            data:{type:'effect:query:label'}
                        }});
                   // dispatch({type:'effect:query:label'})
                }
                else if(location.pathname === routerPath.ARTICLE_EDIT_PAPER) {
                    // 获取报纸详情
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:paper:art',payload:{location}}
                    }});
                }
            });
        }
    },

    effects: {
        *queryPaperList ({ payload }, { call, put, select }) {
            // console.log('queryPaperList: ', payload)
            const { pagination, searchPaperData } = yield select(({article}) => {return {pagination: article.paginationPaper, searchPaperData:article.searchPaperData}});
            let query = {}
            if (payload.params) {
                query = payload.params
            } else {
                query = searchPaperData
            }

            let queryAll = {
                ...query,
                pageNumber: payload.params?1:pagination.current,
                pageSize: pagination.pageSize
            };
            const { data, code } = yield call (getPaperList, { ...queryAll})
            if (code === 0) {
                // console.log(data)
                yield put({
                    type: 'reducer:update',
                    payload: {
                        paperListData: data,
                        searchPaperData: query,
                        paginationPaper: {
                            ...pagination,
                            pageSize: data.pager.pageSize,
                            current: data.pager.pageNumber,
                            total:data.pager.recordCount
                        }
                    }
                })
            }
        },
        *'effect:paper:art'({payload}, {call, put, take, select}){
            const { data, code } = yield call (getPaperDetail, {id: payload.location.query.id})
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        paperData: data
                    }
                })
            }
        },
        *'effect:paper:update'({ payload }, { call, put }) {
            const { code } = yield call(updatePaperDetail, payload.params)
            if(code === 0) {
                payload.success && payload.success()
            }
        },
        *'effect:paper:pagechange'({ payload }, { call, put }) {
            yield put({
                type: 'reducer:update',
                payload: {
                    paginationPaper: payload.page
                }
            })
            yield put({
                type: 'queryPaperList',
                payload: {
                    paginationPaper: payload.page
                }
            })
        },
        *'effect:paperlist:stauts'({ payload }, { call, put }) {
            if (payload.delFlag === 0) {
                const { code } = yield call(downlinePaper, payload.id)
                if(code === 0) {
                    yield put({
                        type: 'queryPaperList',
                        payload
                    })
                    payload.success && payload.success()
                }
            } else {
                const { code } = yield call(onlinePaper, payload.id)
                if(code === 0) {
                    yield put({
                        type: 'queryPaperList',
                        payload
                    })
                    payload.success && payload.success()
                }
            }

            yield put({
                type: 'reducer:update',
                payload: {}
            })
        },
        *'effect:modelid:art'({ payload }, { call, put, select}) {
           
            const modelId = payload.modelId
          
            sessionStorage.setItem("modelId",modelId)
            if (modelId == 7) {
                const {pagination} = yield select(({article}) => {return {pagination: article.paginationPaper}});
                let query = {
                    pageNumber: pagination.current,
                    pageSize: pagination.pageSize
                };
                const { data, code } = yield call (getPaperList, { ...query })
                if (code === 0) {
                    // console.log(data)

                    yield put({
                        type: 'reducer:update',
                        payload: {
                            modelId,
                            searchPaperData: {},
                            paperListData: data
                        }
                    })
                }
            } else {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        modelId
                    }
                })
            }
        },
        *'effect:query:label'({payload}, {call, put, take, select}){
            const {data} = yield call (queryLabel)
           
            yield put({
                type:'reducer:update',
                payload: {
                    labelList:data
                }
            })
        },
        *'effect:query:arts'({payload}, {call, put, take, select}) {
            let cms = yield select(({cms}) => cms);
            
            if(!cms.catgId) {
                
                yield take('cms/reducer:query:catgs:success');
                cms = yield select(({cms}) => cms);
            }
           
            const {catgId, catgs} = cms;
            const {pagination} = yield select(({article}) => {return {pagination: article.pagination};});
           
            let query_o = yield select(({cms}) => cms.naq);
            const query_n = payload.location.query;
            let query = {
                pageNumber: 1,
                pageSize: pagination.pageSize
            };
            if(Jt.object.isEmpty(query_o)) {
                query_o = {
                    categoryId: catgId + '',
                    block: '2',
                    delFlag: '0'
                };
            }
            // console.log('query_o: ',query_o)
            // console.log('query_n: ',query_n)
            if (!query_o.block || !query_o.delFlag) {
                query_o = {...query_o, block: '2', delFlag: '0'}
            }
            if(!Jt.object.isEmpty(query_n)) {
                query = {...query, ...query_n};
            }
            else {
                query = {...query, ...query_o, categoryId:catgId+''};
            }
           
            const categoryId = query.categoryId ? query.categoryId : catgId;
           
            const catgModelId = Jt.catg.getCatg(catgs, categoryId)?Jt.catg.getCatg(catgs, categoryId).modelId:'';
           
            yield put({
                type: 'cms/effect:update:state',
                payload: {
                    naq: query,
                    catgId: categoryId,
                    catgModelId
                }
            });
            yield put({
                type: 'reducer:update',
                payload: {loading: true}
            });
            const modelId = sessionStorage.modelId

            if (modelId == 7) {
                const {pagination} = yield select(({article}) => {return {pagination: article.paginationPaper}});
                // console.log('pagination:', pagination)
                // 获取报纸列表
                const { data, code } = yield call (getPaperList, { ...pagination })
                if (code === 0) {
                    yield put({
                        type: 'reducer:update',
                        payload: {
                            paginationPaper: {
                                ...pagination,
                                current: 1,
                                total:data.pager.recordCount
                            },
                            searchPaperData: {},
                            modelId,
                            paperListData: data
                        }
                    })
                }
            } else {
               
                // 获取文章列表
                const {code, data} = yield call(queryArts, query);
                if(code === 0) {
                    yield put({
                        type: 'reducer:update',
                        payload: {
                            loading: false,
                            arts: data.list || [],
                            wgtChgObj: {},
                            selArts: [],
                            curArt: {doc_original_flag:true, isShowListTitle: true},
                            artNewStatus:{
                                threeFlag:true,
                                viewType:'normal',
                                isTailor:true,
                                fileList:[],
                                imgArr:[],
                                fileList_two:[],
                                thumbnailArr:[],
                                fileList_three:[],
                                thumbnailArr_three:[],
                                isVoice:true,
                                isShowTopImg: false,
                                fileList_four:[],
                                thumbnailArr_four:[],
                                fileList_five:[],
                                thumbnailArr_five:[],
                            },
                            pagination: {
                                ...pagination,
                                total: data.pager.recordCount,
                                current: data.pager.pageNumber,
                                pageSize: data.pager.pageSize
                            }
                        }
                    });
                }
            }
        },
        *'effect:markSensitiveWord'({payload}, {call, put, take, select}) {
            const {curArt,articleData={}} = yield select(({article}) => {return {curArt:article.curArt,articleData:article.curArt.articleData}});
            const {code,data:{isHaveSensitiveWords,sensitiveWordsContent}} = yield call(markSensitiveWord,{sensitiveWordsContent:payload.content})
            let data2;
            if(payload.id){
                 data2 = yield call(getArticlePreviewUrl, payload.id);
            }
           
            if(code==0){
                yield put({
                    type:'reducer:update',
                    payload:{
                        curArt:{
                            ...curArt,
                            articleData:{...articleData,content:payload.editorContent},
                            title:payload.title,
                            authors:payload.authors,
                            source:payload.source
                            },
                        sensitiveWordsContent:sensitiveWordsContent,
                        visible:true,
                        sensitiveWordsVisible:isHaveSensitiveWords,
                        previewModalUrl:data2&&data2.data?data2.data:''
                    }
                })
            }
        },
        *'effect:query:art'({ payload }, { call, put, take, select }) {
            let cms = yield select(({cms}) => cms);
            if(!cms.catgId) {
                yield take('cms/reducer:query:catgs:success');
                cms = yield select(({cms}) => cms);
            }
            const {catgId, treeCatgId, catgModelId, catgs} = cms;
            const query = payload.location.query;
            const publishDate = new Date().format('yyyy-MM-dd hh:mm:ss');
            if(query.action === 'edit') {
                const { code, data={} } = yield call(queryArt, { id: query.id })
                if(code === 0) {
                    const type = data.type;
                    let surveyNum = 0;
                    let albumNum = 0;
                    let videoNum = 0;
                    let audioNum = 0;
                    if(type === 'common' && !Jt.array.isEmpty(data.votes)) {
                        surveyNum = 1;
                    }

                    if(type === 'image') {
                        albumNum = 1;
                    }
                    else if(type === 'video') {
                        videoNum = 1;
                    }
                    else if(type === 'audio') {
                        audioNum = 1;
                    }
                    data.publishDate = data.publishDate || publishDate;
                    data.articleData = data.articleData || {};
                    data.fields = Jt.field.combine(data.fieldGroups);
                    const metas = data.metas || [];
                    const metaObj = {};
                    metas.forEach(meta => {
                        metaObj[meta.fieldCode] = meta.fieldValue;
                });
                    data.metas = metaObj;
                    const {artNewStatus} = yield select(({article}) => {return {artNewStatus: article.artNewStatus};});
                    let fileList_three = [],thumbnailArr_three=[],fileList_four=[],fileList_five = [],thumbnailArr_five=[]
                    if(data.articleData &&data.articleData.imageJson &&data.articleData.imageJson.length){
                        data.articleData.imageJson.forEach(function(item,i){
                             fileList_three.push({url:item.realImage,uid:i});
                             thumbnailArr_three.push(item.realImage);
                        })
                    }
                    let modelId  = yield select(({article}) => article.modelId);
                    if((data.type=='video' || data.type=='common'||data.type=='help'||modelId=='8')&& !!data.moreVideos){
                        const moreVideos = data.moreVideos===""?[]:JSON.parse(data.moreVideos);
                        moreVideos.forEach(function(item,i){
                            fileList_four[i] = item.video;
                            fileList_five.push({uid:i,url:item.image})
                            thumbnailArr_five.push(item.image)
                        })
                    }

                    Object.assign(
                            artNewStatus,
                            {

                                    threeFlag : ((data.type=='video'||data.type=="audio")?false:true)
                            },
                            {isTailor:data.isTailor,
                            isVoice:data.isVoice,
                            isShowTopImg: data.isShowTopImg,
                            viewType:data.viewType,
                            fileList_three,
                            thumbnailArr_three,
                            fileList_four,
                            fileList_five,
                            thumbnailArr_five
                            }
                     );
                     console.log("aaaaaaaaaaaaaaaaa",query)
                    yield put({
                        type: 'reducer:update',
                        payload:{
                            curArt: data,
                            isShowListTitle: true,
                            surveyNum,
                            albumNum,
                            videoNum,
                            audioNum,
                            isReference:query.isReference
                        }
                    })
                }
            }
            else {
                let modelId  = yield select(({article}) => article.modelId);
                const art = {categoryId: treeCatgId ? treeCatgId : catgId,doc_original_flag:1, type: modelId!='8'?'common':'help', delFlag: '2', block: '2', publishDate, articleData: {}, metas: {}};
                if(catgModelId) {
                    const { code, data={} } = yield call(queryCatgModel, { id: catgModelId });
                    if(code === 0) {
                        art.fields = Jt.field.combine(data.fieldGroups);
                    }
                }
                if(query.source === 'subject') {
                    art.inSubject = true;
                    art.categoryId = query.blockId;
                }
                yield put({
                    type: 'reducer:update',
                    payload: {
                        curArt: art,
                        isShowListTitle: false,
                        surveyNum: 0,
                        albumNum: 0,
                        videoNum: 0,
                        audioNum: 0,

                    }
                });
            }
        },
        *'effect:update:arts'({ payload }, { call, put }) {
            const { code } = yield call(updateArts, payload.arts)
            if(code === 0) {
                yield put({
                    type: 'effect:query:arts',
                    payload
                })
            }
        },
        *'effect:update:art'({ payload }, { call, put }) {
            const { code } = yield call(updateArt, payload.art)
            if(code === 0) {
                payload.success && payload.success()
            }
        },
        *'effect:create:art'({ payload }, { call, put }) {
            yield put({ type: 'reducer:updateloading', payload:{saveLoading: payload.loading} })
            try {
                const { code } = yield call(createArt, payload.art)
                if(code === 0) {
                    payload.success && payload.success()
                    yield put({ type: 'reducer:updateloading', payload:{saveLoading: false} })
                }
            }catch (e) {
                message.error(e.message)
                yield put({ type: 'reducer:updateloading', payload:{saveLoading: false} })
            }

        },
        *'effect:delete:art'({ payload }, { call, put }) {
            //const { code } = yield call(removeArt, {id: payload.id})
            // if(code === 0) {
            //     payload.success && payload.success()
            // }
        
            const { code } = yield call(removeArt, payload.art)
            if(code === 0) {
                yield put({
                    type: 'effect:query:arts',
                    payload
                })
            }
        },
        *'effect:show:pushModal'({ payload }, { call, put }) {
            yield put({ type: 'reducer:update', payload: { pushModalVisible: true } })
            yield put({ type: 'reducer:update:curArt', payload })
        },
        *'effect:hide:pushModal'({ payload }, { call, put }) {
            yield put({
                type: 'reducer:update',
                payload: {
                    pushModalVisible: false,
                    curArt: {}
                }
            })
        },
        *'effect:show:syncModal'({ payload }, { call, put }) {
            yield put({ type: 'reducer:update', payload: { syncModalVisible: true } })
            yield put({ type: 'reducer:update:curArt', payload })
        },
        *'effect:hide:syncModal'({ payload }, { call, put }) {
            yield put({
                type: 'reducer:update',
                payload: {
                    syncModalVisible: false
                }
            })
        },
        *'effect:create:pushInfo'({ payload }, { call, put }) {
            const { code } = yield call(createPushInfo, payload.pushInfo)
            if(code === 0) {
                yield put({
                    type: 'effect:hide:pushModal'
                })
            }
        },
        *'effect:onOff:art'({ payload }, { call, put }) {
            const { code } = yield call(onOffArt, payload.art)
            if(code === 0) {
                yield put({
                    type: 'effect:query:arts',
                    payload
                })
            }
        },
        *'effect:onOff:arts'({ payload }, { call, put }) {
            const { code } = yield call(onOffArts, {articleIds: payload.articleIds})
            if(code === 0) {
                message.success(`${payload.tips}修改成功！可通过${payload.tips}标签搜索查看`)
                delete payload.tips
                yield put({
                    type: 'effect:query:arts',
                    payload
                })
            }
        },
        *'effect:update:wgtChgObj'({ payload }, { call, put }) {
            yield put({
                type: 'reducer:update:wgtChgObj',
                payload
            })
        },
        *'effect:update:position'({ payload }, { call, put }) {
            payload.id = Number(payload.id);
            payload.position = Number(payload.position);
            const { code, msg } = yield call(setPosition, {id:payload.id,position:payload.position})
            if(code === 0) {
                yield put({
                    type: 'effect:query:arts',
                    payload:{location:{query: payload.query}}
                })
            }
            if(code === -1) {
                message.error( msg || '设置失败，固定位置不能大于该分类下文章总数！')
            }
        },
        *'effect:update:selArts'({ payload }, { call, put }) {
            yield put({
                type: 'reducer:update:selArts',
                payload
            })
        },
        *'effect:update:state'({payload}, {call, put}) {
            if(payload.curArt && payload.curArt.content){
                payload.curArt.articleData.content = payload.curArt.content.html;
            }
            yield put({
                type: 'reducer:update',
                payload
            });
        },
        *'effect:update:paperstate'({payload}, {call, put}) {
            console.log('effect:update:paperstate')
            if(payload.paperData && payload.paperData.content){
                payload.paperData.content = payload.paperData.content.html;
            }
            yield put({
                type: 'reducer:update',
                payload: {
                    paperData: payload.paperData
                }
            });
        },
        *'effect:update:artAttrs'({payload}, {call, put, select}) {
            const curArt = yield select(({article}) => article.curArt);
            yield put({
                type: 'reducer:update',
                payload: {
                    curArt: {...curArt, ...payload}
                }
            });
        },
        *'effect:update:artCatg'({payload}, {call, put, select}) {
            const {catgs, curArt} = yield select(({cms, article}) => {return {catgs: cms.catgs, curArt: article.curArt}});
            const categoryId = payload.categoryId;
            const catgModelId = Jt.catg.getCatg(catgs, categoryId) ? Jt.catg.getCatg(catgs, categoryId).modelId : null;
            if(catgModelId) {
                const { code, data={} } = yield call(queryCatgModel, { id: catgModelId });
                if(code === 0) {
                    curArt.fields = Jt.field.combine(data.fieldGroups);
                }
            }
            else {
                curArt.fields = [];
            }
            const newCurArt = payload.curArt;
            if(newCurArt.content && newCurArt.content.html) {
                newCurArt.articleData.content = newCurArt.content.html;
            }

            yield put({
                type: 'reducer:update',
                payload: {
                    curArt: {...newCurArt, categoryId}
                }
            });
        },
        *'effect:getUploadState'({payload}, {call, put, select}) {
            const {code,data} =yield call(getUploadState,payload.fileUrls)
            if(code==0){
                payload.resolve(data)
            }

        },
        *'effect:query:catgs'({payload}, {call, put, select}) {
         
            const {code, data} = yield call(queryCatgs);
            
            let catgModelId = payload.categoryId?payload.categoryId:'';
            let stemp = Jt.tree.format(data?data:[])
            let tid ;
            if(catgModelId){
                tid = Jt.catg.getCatg(stemp,catgModelId);
            }
           
            if(code === 0) {
                yield put({
                    type: 'cms/reducer:query:catgs:success',
                    payload:data?data:[]
                });
               
                yield put({
                    type: 'reducer:query:catgsModelId:success',
                    payload:tid&&tid.modelId?tid.modelId:""
                });
              
            }
        },
        *queryComment({ payload }, { call, put, select }){
            yield put({type:'reducer:update', payload: {commentLoading: true}})
            let content = yield select( ({ article }) => article.commentContent )
            let delFlag = yield select( ({ article }) => article.commentDelFlag )
            let pageSize = yield select( ({ article }) => article.commentPagination.pageSize )
            let pageNumber = yield select( ({ article }) => article.commentPagination.current )
            let articleId = yield select( ({ article }) => article.curArt.articleId )
            let params = Object.assign({
                'pageNumber': pageNumber,
                'pageSize': pageSize,
                'delFlag': delFlag,
                'content': content,
                'articleId': articleId
            }, payload);
            let pagination = yield select( ({ article }) => article.commentPagination ) //对应namespace 的 comment 的 state
            const data = yield call(query, parse(params))
            if (data) {
                pagination.total = data.data.pager.recordCount
                pagination.current = data.data.pager.pageNumber
                pagination.pageSize = parseInt(params.pageSize)
                yield put({
                  type: 'querySuccess',
                  payload: {
                    commentList: data.data.list,
                    commentPagination: pagination,
                    commentDelFlag: params.delFlag
                  }
                })
              }
        },
        *batchOnOff({payload},{call,put,select}) {
            yield put({type:'reducer:update', payload: {commentLoading: true}});
            let delFlag = yield select( ({ article }) => article.commentDelFlag )
            let pageNumber = yield select( ({ article }) => article.commentPagination.current )
            let pageSize = yield select( ({ article }) => article.commentPagination.pageSize )
            let content = yield select( ({ article }) => article.commentContent )
            payload.delFlag = delFlag;
            payload.pageNumber = pageNumber;
            payload.pageSize = pageSize;
            payload.content = content;
            const data = yield call(batchOnOff, parse(payload))
            delete payload.commentIds
            yield put({
                type: 'queryComment',
                payload
            });
        },
        *batchOnOff2({payload},{call,put,select}){
            yield put({type:'reducer:update', payload: {commentLoading: true}});
            let delFlag = yield select( ({ article }) => article.commentDelFlag )
            let pageNumber = yield select( ({ article }) => article.commentPagination.current )
            let pageSize = yield select( ({ article }) => article.commentPagination.pageSize )
            let content = yield select( ({ article }) => article.commentContent )
            payload.delFlag = delFlag;
            payload.pageNumber = pageNumber;
            payload.pageSize = pageSize;
            payload.content = content;
            const data = yield call(batchOnOff2, parse(payload))
            delete payload.commentIds
            yield put({
                type: 'queryComment',
                payload
            });



            // yield put({type: 'update:state', payload: {loading: true}});
            // let delFlag = yield select( ({ comment }) => comment.delFlag )
            // payload.delFlag = delFlag
            // const data = yield call(batchOnOff2, parse(payload))
            // delete payload.commentIds
            // yield put({
            //     type: 'queryComment',
            //     payload
            // })
        },
        *updateProps({payload},{call,put,select}){
            let params = payload.params
            const data = yield call(update, parse(params)) //改
            // let delFlag = yield select( ({ article }) => article.commentDelFlag )
            // let pageNumber = yield select( ({ article }) => article.commentPagination.current )
            // let pageSize = yield select( ({ article }) => article.commentPagination.pageSize )
            // let content = yield select( ({ article }) => article.commentContent )
            let query = payload.query;
                // query.content = content,
                // query.delFlag = delFlag,
                // query.pageSize = pageSize,
                // query.pageNumber = pageNumber
            yield put({
                type: 'queryComment',
                payload:{
                    ...query
                }
            })
        },
        *addReply({payload},{call,put,select}) {
            let params = payload.params;
            const data = yield call(addReply, parse(params)) //改
            yield put({
                type: 'queryComment'
            })
        },
        *addComment({payload},{call,put,select}) {
            payload.articleId = yield select( ({ article }) => article.curArt.articleId );
            if(!payload.sysCode){
                payload.sysCode = yield select( ({ article }) => article.curArt.sysCode );
            } else {

            }
            const data = yield call(addComment, parse(payload)) //改
            if (data) {
                yield put({
                    type: 'queryComment'
                })
            }
        },
        *getPublicCmsCategoryList({payload},{call,put,select}) {
            let params = payload.params;
            const res = yield call(getPublicCmsCategoryList, parse(params)) // 获取入住系统栏目列表
            if (res && res.data) {
              const categoryList = res.data.categoryList;
              const publicCmsCategoryList = generateTreeData(categoryList, publicCmsCategoryList)
              yield put({
                  type: 'reducer:query:publicCmsCategoryList:success',
                  payload: { publicCmsCategoryList }
              })
            }
        },
        *'effect:update:postSyncArticle'({ payload }, { call, put }) {
            const params = payload;
            const res = yield call(postSyncArticle, parse(params)) // 同步文章到入住系统
            if (res && res.msg === 'ok') {
              message.success('同步成功！')
              yield put({ type: 'reducer:update', payload: { syncModalVisible: false } })
            }
        },
        *'effect:restore:art'({ payload }, { call, put }) {
            const { code } = yield call(onOffArtfromRemove, payload.art)
            if(code === 0) {
                yield put({
                    type: 'effect:query:arts',
                    payload
                })
            }
        },
        *'effect:batchoff:arts'({ payload }, { call, put }) {
           
            const { code } = yield call(onbachOffArtfromRemove, payload)
            if(code === 0) {
                yield put({
                    type: 'effect:query:arts',
                    payload
                })
            }
        },
        *'effect:show:preivewModal'({ payload }, { call, put }) {
           
            const {code,data} = yield call(getArticlePreviewUrl, payload.id);
            
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        previewModalVisible: true,
                        previewModalUrl:data
                        
                    }
                })
            }
           
        },
        *'effect:hide:preivewModal'({ payload }, { call, put }) {
           
          
            
            // if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        previewModalVisible: false,
                        previewModalUrl:''
                        
                    }
                })
            // }
           
        },
        *'effect:show:publishModal'({ payload }, { call, put }) {
           
           
                yield put({
                    type: 'reducer:update',
                    payload: {
                        publishModal: true,
                        publishId:payload.id
                    }
                })
           
           
        },
        *'effect:hide:publishModal'({ payload }, { call, put }) {
           
           
            yield put({
                type: 'reducer:update',
                payload: {
                    publishModal: false,
                    publishId:''
                }
            })
       
       
        },
        *'effect:confirm:publishModal'({ payload }, { call, put,select }) {
           
            const {code,data} = yield call(fixPublishTime, payload);
            if(code==0){
                yield put({
                    type: 'reducer:update',
                    payload: {
                        publishModal: false,
                        publishId:''
                    }
                })
                yield put({
                    type: 'effect:query:arts',
                    payload
                })
            }
            
       
       
        },
        *'effect:advPublish:art'({ payload }, { call, put }) {
            const { code } = yield call(advancePublish, payload.art)
            if(code === 0) {
                yield put({
                    type: 'effect:query:arts',
                    payload
                })
            }
        },
        *'effect:cancelPublish:art'({ payload }, { call, put }) {
            const { code } = yield call(cancelPublish, payload.art)
            if(code === 0) {
                yield put({
                    type: 'effect:query:arts',
                    payload
                })
            }
        },
        *'effect:cancelPublish:arts'({ payload }, { call, put }) {
            const { code } = yield call(cancelBatchPublish, payload)
            if(code === 0) {
                yield put({
                    type: 'effect:query:arts',
                    payload
                })
            }
        },
        *'effect:advPublish:arts'({ payload }, { call, put }) {
            const { code } = yield call(batchAdvancePublish, payload)
            if(code === 0) {
                yield put({
                    type: 'effect:query:arts',
                    payload
                })
            }
        },
    },

    reducers: {
        'reducer:updateloading'(state, { payload }){
            return { ...state, ...payload }
        },
        'reducer:update'(state, { payload }) {
            const imageUrl = (payload.curArt&&payload.curArt.imageUrl)?payload.curArt.imageUrl:'';
            const realImageUrl = (payload.curArt&&payload.curArt.realImageUrl)?payload.curArt.realImageUrl:'';
            let artNewStatus = {}
            if(imageUrl){
                const imgObj = getfileList(imageUrl)
                artNewStatus = Object.assign(
                    state.artNewStatus,
                    {fileList:imgObj.fileList,imgArr:imgObj.imgArr}
                )
                if(realImageUrl){
                    const imgObj_two = getfileList(realImageUrl)
                    artNewStatus = Object.assign(
                        artNewStatus,
                        {fileList_two:imgObj_two.fileList,thumbnailArr:imgObj_two.imgArr}
                    )
                }
                return { ...state, ...payload,artNewStatus}
            }
            return { ...state, ...payload }
        },
        'reducer:update:curArt'(state, { payload }) {
            const arts = state.arts;
            let curArt = {};
            if(payload) {
                for(let i = 0, len = arts.length; i < len; i++) {
                    if(arts[i].id == payload) {
                        curArt = arts[i]
                        break
                    }
                }
            }
            return { ...state, curArt }
        },
        'reducer:update:wgtChgObj'(state, { payload }) {
            let wgtChgObj = {}
            if(payload) {
                wgtChgObj = { ...state.wgtChgObj, ...payload }
            }
            return { ...state, wgtChgObj }
        },
        'reducer:update:selArts'(state, { payload }) {
            const selArts = payload || []
            return { ...state, selArts }
        },
        'reducer:update:mediaType'(state, { payload }) {
            const mediaType = payload || ''
            return { ...state, mediaType }
        },
        'reducer:update:pagination'(state, { payload }) {
            const commentPagination = Object.assign(state.commentPagination, payload);
            return Object.assign(state, commentPagination);
        },
        'reducer:query:catgs:success'(state, {payload}) {
            // const catgs = Jt.tree.format(payload);
            // const catgId = Jt.catg.getInitId(catgs);
            return {...state, ...payload};
        },
        'querySuccess'(state, {payload}){
            if(!!payload.commentList && payload.commentList.length>0){
                formatTreeData(payload.commentList)

            }else if(!payload.commentList){
                payload.commentList = []
            }
            return {...state, ...payload, commentLoading: false}
        },
        'putInState'(state, {payload}) {
            return {...state, ...payload}
        },
        'reducer:query:publicCmsCategoryList:success'(state, {payload}) {
            // const catgs = Jt.tree.format(payload);
            // const catgId = Jt.catg.getInitId(catgs);
            return {...state, ...payload};
        },
        'reducer:update:selectPublicCmsCategory'(state, { payload }) {
            const selectCategory = payload || null
            return { ...state, selectCategory }
        },
        'reducer:initcreate'(state){
            console.log("curtyyy",state)
            return {...state,artNewStatus:{
                    threeFlag:true,
                    viewType:'normal',
                    isTailor:true,
                    fileList:[],
                    imgArr:[],
                    fileList_two:[],
                    thumbnailArr:[],
                    fileList_three:[],
                    thumbnailArr_three:[],
                    fileList_four:[],
                    thumbnailArr_four:[],
                    fileList_five:[],
                    thumbnailArr_five:[],
                    isVoice:true,
                    isShowTopImg: false,
                    categoryId:sessionStorage.getItem("catId"),
                },}
        },
        'reducer:initcreateHelp'(state){
            return {...state,artNewStatus:{
                    threeFlag:true,
                    viewType:'banner',
                    isTailor:true,
                    fileList:[],
                    imgArr:[],
                    fileList_two:[],
                    thumbnailArr:[],
                    fileList_three:[],
                    thumbnailArr_three:[],
                    fileList_four:[],
                    thumbnailArr_four:[],
                    fileList_five:[],
                    thumbnailArr_five:[],
                    isVoice:true,
                    isShowTopImg: false,
                    categoryId:sessionStorage.getItem("catId"),
                },}
        },
        'reducer:query:catgsModelId:success'(state, {payload}) {
            
            
            return {...state, "modelId":payload};
        }
    }
}
