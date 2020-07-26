import React from 'react'
import PropTypes from 'prop-types';
import {routerRedux} from 'dva/router';
import {connect} from 'dva';
// import {message, Row, Col} from 'antd';
import {Jt, Immutable} from '../../../utils';
import {routerPath} from '../../../constants';
import ArtSearch from '../../../components/articles/search';
import ArtToolbar from '../../../components/articles/toolbar';
import ArtList from '../../../components/articles/list';
import PushModal from '../../../components/articles/pushModal';
import SyncModal from '../../../components/articles/syncModal';
import CiteArtModal from '../../../components/cms/citeArtModal';
import EditArtModal from '../../../components/cms/editArtModal';
import ArtCommentModal from '../../../components/articles/artCommentModal';
import GovAffairsModal from '../../../components/cms/govAffairsModal';
import Tree from '../../../components/articles/tree';
import PaperList from '../../../components/articles/paperList';
import styles from '../../../components/articles/list.less';
import copy from 'copy-to-clipboard';
import {
    message,
    notification,
    Form,
    Input,
    InputNumber,
    Radio,
    Select,
    TreeSelect,
    DatePicker,
    Collapse,
    Button,
    Checkbox,
    Popconfirm,
    Row,
    Col,
    Switch,
    Icon,
    Upload,
    Modal,
    Carousel
} from 'antd';
import moment from 'moment';
let formData = ''
let previewVisible = false;
const formTailLayout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 8, },
  };
class PublishDateModal extends React.Component{
    constructor(props){
        super(props);
       
    }
    publishModalConfirm(){
        this.props.publishModalConfirm(this.props.form)
    }
    render(){
        return (
            <Modal
                    title={'定时发布'  }
                    visible={this.props.publishModal}
                    closable={false}
                    footer={[
                        <Button key="close" onClick={this.publishModalConfirm.bind(this)}>确定</Button>,
                        <Button key="cls" onClick={this.props.hidePublishModal}>取消</Button>,
                    ]}
                >
                   <Form.Item label="发布时间" {...formTailLayout}>
                        {
                        this.props.form.getFieldDecorator('fixedPubTime', {
                            initialValue:  '',
                            rules:[
                                {required:true,message:'请选择发布时间'}
                            ]
                        })(<DatePicker  showTime format={dateFormat} disabledDate={this.disabledDate} disabledTime={this.disabledDateTime}/>)
                        }
                    </Form.Item>
                </Modal>
           
        )
    }
}

const dateFormat = 'YYYY-MM-DD HH:mm:ss';

function ArticleList({
    location,
    dispatch,
    app,
    cms,
    article,
    comment
}, context) {
    const  hidePublishModal = (record)=>{
        dispatch({type: 'article/effect:hide:publishModal'})
    }
    const  publishModalConfirm = (form)=>{
        form.validateFieldsAndScroll((err, value) => {
            if(err){
                return 
            } else {
                dispatch({type: 'article/effect:confirm:publishModal',payload:{location,articleId:article.publishId,fixedPubTime:value.fixedPubTime.format(dateFormat)}})
            }
        });
     
    }
    const listProps = {
        cfg: app.cfg,
        listType: cms.naq.delFlag,
        wgtChgObj: article.wgtChgObj,
        loading: article.loading,
        selArts: article.selArts,
        artTypes: cms.artTypes,
        dataSource: article.arts,
        pagination: article.pagination,
        preview(record){
            
            dispatch({type: 'article/effect:show:preivewModal',payload:{id:record.articleId}})
        },
        showPublishModal(record){
            dispatch({type: 'article/effect:show:publishModal',payload:{id:record.id}})
        },
       
        onPageChange(page, filter, sorter) {
            const query = cms.naq;
        
            if (sorter.order === 'descend') {
                query.desc = sorter.field;
                delete query.asc;
            } else if (sorter.order === 'ascend') {
                query.asc = sorter.field;
                delete query.desc;
            }
            dispatch(routerRedux.push({
                pathname: routerPath.ARTICLE_LIST,
                query: {
                    ...query,
                    pageNumber: page.current,
                    pageSize: page.pageSize
                }
            }));
        },
        onLineArt(id){
            
            dispatch({
                type: 'article/effect:advPublish:art',
                payload: {
                    art: {
                        id
                    },
                    location
                }
            })
        },
        
        restore(id){
            dispatch({
                type: 'article/effect:restore:art',
                payload: {
                    art: {
                        id
                    },
                    location
                }
            })
        },
        offLineArt(id){
            dispatch({
                type: 'article/effect:cancelPublish:art',
                payload: {
                    art: {
                        id
                    },
                    location
                }
            })
        },
        showPushModal(art) {
            dispatch({type: 'article/effect:show:pushModal', payload: art.id})
        },
        hidePushModal() {
            dispatch({type: 'article/effect:hide:pushModal'})
        },
        showSyncModal(art) {
            dispatch({type: 'article/effect:show:syncModal', payload: art.id})
            dispatch({type: 'article/getPublicCmsCategoryList', payload: {
                params: {
                    articleId: art.id
                }
            }})
        },
        hideSyncModal() {
            dispatch({type: 'article/effect:hide:syncModal'})
        },
        showPreviewModal(id) {
            dispatch({type: 'article/effect:show:previewModal', payload: id})
        },
        hidePreviewModal() {
            dispatch({type: 'article/effect:hide:previewModal'})
        },
        onOffArt(id) {
            dispatch({
                type: 'article/effect:onOff:art',
                payload: {
                    art: {
                        id
                    },
                    location
                }
            })
        },
        updateWgtChgObj(obj) {
            dispatch({type: 'article/effect:update:wgtChgObj', payload: obj})
        },
        updatePosition(obj) {
            const query = cms.naq;
            dispatch({
                type: 'article/effect:update:position',
                payload:
                    {
                        ...obj,
                        query
                    }
            })
        },
        selectArt(selArts) {
            dispatch({type: 'article/reducer:update:selArts', payload: selArts})
        },
        deleteArt: (id) => {
            
            dispatch({
                type: 'article/effect:delete:art',
                payload: {
                    art: {
                        id
                    },
                    location
                    // success: () => {
                    //     context.router.push(routerPath.ARTICLE_LIST)
                    // }
                }
            })
            // dispatch({
            //     type: 'article/effect:delete:art',
            //     payload: {
            //         id,
            //         success: () => {
            //             context.router.push(routerPath.ARTICLE_LIST)
            //         }
            //     }
            // })
        },
        onEdit: (record) => {
            dispatch({
                type: 'cms/effect:toggle:eam',
                payload: {
                    art: record,
                    eamv: true
                }
            });
        },
        viewComment: (record) => {
            dispatch({type: 'article/reducer:update', payload: {viewComment: true, curArt: record}});
            dispatch({ type: 'article/queryComment'});
        }
    };
    const searchProps = {
        artTypes: cms.artTypes,
        query: cms.naq,
        catgs: cms.catgs,
        listType: cms.naq.delFlag,
        getdata(data){
            formData = data;
            // dispatch(routerRedux.push({pathname: routerPath.ARTICLE_LIST}))
        },
        onSearch: (query, from) => {
            const {desc, asc} = cms.naq;
            query = {
                ...query,
                desc,
                asc
            };

            // 从树点击时默认 上线与列表,清空搜索条件
            if (from === 'fromTree') {
                query = {
                    ...query,
                    block: '2',
                    delFlag: '0',
                    keyword: undefined,
                    title: undefined,
                    type: undefined,
                    beginTime: undefined,
                    endTime: undefined,
                    authors: undefined,
                    content:undefined,
                    deleteBeginTime:undefined,
                    deleteEndTime:undefined,
                    kwType: 'title'
                };
                window.$searchThis.resetFields()
            }

            dispatch(routerRedux.push({pathname: routerPath.ARTICLE_LIST, query}))
        }
    };

    const tbProps = {
        selArts: article.selArts,
        wgtChgObj: article.wgtChgObj,
        artTypes: cms.artTypes,
        listType: cms.naq.delFlag,
        createtype:article.modelId,
        saveWeight(arts) {
            dispatch({
                type: 'article/effect:update:arts',
                payload: {
                    arts,
                    location
                }
            })
        },
        relase(ids){
            let idarr =  ids.map((item)=>{
                return item.id;
            });
            idarr = idarr.join(',')
            dispatch({
                type: 'article/effect:advPublish:arts',
                payload: {
                    articleIds: idarr,
                    location,
                    
                }
            });
        },
        relaseCancel(ids){
            let idarr =  ids.map((item)=>{
                return item.id;
            });
            idarr = idarr.join(',')
            dispatch({
                type: 'article/effect:cancelPublish:arts',
                payload: {
                    articleIds: idarr,
                    location,
                    
                }
            });
        },
        restore(ids){
            let idarr =  ids.map((item)=>{
                return item.id;
            });
            idarr = idarr.join(',')
            dispatch({
                type: 'article/effect:batchoff:arts',
                payload: {
                    articleIds: idarr,
                    location,
                    
                }
            });
        },
        onOffArts(ids, tips) {
            dispatch({
                type: 'article/effect:onOff:arts',
                payload: {
                    articleIds: ids,
                    location,
                    tips
                }
            })
        },
        createArt(key) {
            dispatch({
                type: 'article/reducer:initcreate',
                payload: {

                }
            })
            if (key === 'normal') {
                context.router.push(routerPath.ARTICLE_EDIT)
            } else if (key === 'live') {
                context.router.push(routerPath.LIVE_EDIT)
            } else if (key === 'topic') {
                dispatch(routerRedux.push({
                    pathname: routerPath.TOPIC_EDIT,
                    query: {
                        action: 'create',
                        categoryId: cms.naq.categoryId
                    }
                }));
            } else if (key === 'cite') {
                dispatch({
                    type: 'cms/effect:toggle:cam',
                    payload: {
                        camv: true
                    }
                });
            } else if (key === 'govAffairs') {
                dispatch({
                    type: 'cms/effect:toggle:gam',
                    payload: {
                        gamv: true
                    }
                });
            } else if(key === 'help'){
                dispatch({
                    type: 'article/reducer:initcreateHelp',
                    payload: {
    
                    }
                });
                context.router.push(routerPath.ARTICLE_EDIT)
            }
        }
    };
    const paperProps = {
        createArt (id) {
            dispatch({
                type:'article/reducer:update',
                payload:{
                    paperData: {}
                }
            })
            context.router.push(routerPath.ARTICLE_EDIT_PAPER + `?id=${id}`)
        },
        statusChange ({delFlag, id}, success) {
            dispatch({
                type:'article/effect:paperlist:stauts',
                payload:{
                    delFlag, id , success
                }
            })
        },
        searchPaperList (params) {
            dispatch({
                type:'article/queryPaperList',
                payload: { params }
            })
        },
        onPaperPageChange: (page) => {
            dispatch({
                type: 'article/effect:paper:pagechange',
                payload: {
                    page
                }
            });
        },
    }
    const pmProps = {
        visible: article.pushModalVisible,
        curArt: article.curArt,
        onOk: (pushInfo) => {
            dispatch({type: 'article/effect:create:pushInfo', payload: {
                    pushInfo
                }});
        },
        onCancel: () => {
            dispatch({type: 'article/effect:hide:pushModal'});
        }
    };
    const syncModalProps = {
        visible: article.syncModalVisible,
        curArt: article.curArt,
        publicCmsCategoryList: article.publicCmsCategoryList,
        selectCategory: article.selectCategory,
        onOk: (syncInfo) => {
            dispatch({type: 'article/effect:update:postSyncArticle', payload: {
                    articleId: syncInfo.articleId,
                    syncCategoryId: syncInfo.selectCategory.id
                }});
        },
        onTreeSelect: (selectInfo) => {
            dispatch({type: 'article/reducer:update:selectPublicCmsCategory', payload: selectInfo ? selectInfo.props : null});
        },
        onCancel: () => {
            dispatch({type: 'article/effect:hide:syncModal'});
        }
    };

    const camProps = {
        catgs: cms.catgs,
        visible: cms.camv,
        loading: cms.cal,
        pagination: cms.cap,
        dataSource: cms.cas,
        artTypes: cms.artTypes,
        artTags: cms.artTags,
        catgId: cms.catgId,
        naq: cms.naq,
        onPageChange: (page) => {
            dispatch({
                type: 'cms/effect:query:cas',
                payload: {
                    ...cms.caq,
                    pageNumber: page.current,
                    pageSize: page.pageSize
                }
            });
        },
        onCancel: () => {
            dispatch({
                type: 'cms/effect:toggle:cam',
                payload: {
                    camv: false
                }
            });
        },
        onCite: (params) => {

            dispatch({
                type: 'cms/effect:cite:arts',
                payload: {
                    ...params,
                    location,
                    source: 'article'
                }
            });
        },
        onSearch: (params) => {
            dispatch({type: 'cms/effect:query:cas', payload: params});
        }
    };

    const eamProps = {
        visible: cms.eamv,
        art: cms.art,
        source: 'article',
        onCancel: () => {
            dispatch({
                type: 'cms/effect:toggle:eam',
                payload: {
                    eamv: false
                }
            });
        },
        onSave: (art) => {
            dispatch({
                type: 'cms/effect:update:citeArt',
                payload: {
                    art,
                    source: 'article',
                    location
                }
            });
        },
        onHide: () => {
            dispatch({
                type: 'cms/effect:toggle:eam',
                payload: {
                    eamv: false
                }
            });
        }
    };
    const acmProps = {
        visible: article.viewComment,
        onCancel: () => {
            dispatch({
                type: 'article/reducer:update',
                payload: {
                    viewComment: false
                }
            });
        },
        onHide: () => {
            dispatch({
                type: 'article/reducer:update',
                payload: {
                    viewComment: false
                }
            });
        }
    }
    const commentListProps = {
        dataSource : article.commentList,
        pagination: article.commentPagination,
        selectedRowKeys: article.commentDelflagKeys,
        loading: article.commentLoading,
        delFlag: article.commentDelFlag || 0,
        onPageChange(page) {
            dispatch({
                type: 'article/reducer:update:pagination',
                payload: {
                    current: page.current,
                    pageSize: page.pageSize
                }
            });
            dispatch({type: 'article/queryComment'})
        },
        onSelectedDelflag(delflagKeys) {
            dispatch({
                type:'article/putInState',
                payload:{
                    commentDelflagKeys: delflagKeys
                }
            })
        },
        handleClickTitle(record) {
            dispatch(routerRedux.push({
                pathname: `/cms/articles/edit`,
                query: {
                    action: 'edit',
                    id: record.articleId,
                }
            }));
        },
        onChangeDelflag(delFlag,id,type) {
            dispatch({
                type:'article/batchOnOff',
                payload:{
                    commentIds:id,
                }
            })
        },
        onChangeDelflag2(delFlag,id,type) {
            dispatch({
                type:'article/batchOnOff2',
                payload:{
                    commentId:id,
                    "actionStatus":0
                }
            })
        },
        saveLikes(record) {
            dispatch({
                type:'article/updateProps',
                payload:{
                    params:{
                        id: record.id,
                        likes: record.likes || 0
                    },
                    query: {
                        articleId: record.articleId
                    }
                }
            })
        },
        // replyHandle(record){
        //     dispatch({
        //         type:'comment/replyShow',
        //         payload: record
        //     });
        // },
        handleSubmitReply(record, type) {
            const isAdmin = type === 'admin';
            dispatch({
                type:'article/addReply',
                payload:{
                    params:{
                        content: isAdmin ? record.replyAdminValue : record.replyValue,
                        parentId: record.id,
                        isAdmin: isAdmin,
                        userName: isAdmin ? '管理员' : record.replyName
                    },
                    query: {
                        articleId: record.articleId
                    }
                }
            })
        }
    }
    const commentSearchProps = {
        delFlag: article.commentDelFlag || 0,
        content: article.commentContent,
        onSearch(data) {
            dispatch({
                type: 'article/reducer:update',
                payload: {
                    commentContent: data.content
                }
            });
            dispatch({
                type:'article/queryComment'
            });
        },
        onDelFlagChange(param){
            dispatch({
                type: 'article/reducer:update',
                payload: {
                    commentDelFlag: param.target.value
                }
            });
            dispatch({
                type:'article/queryComment'
            });
        },
        batchMenuClick(menu) {
            const delFlag = article.commentDelFlag || 0;
            let delflagKeysStr = article.commentDelflagKeys;
            if (!delflagKeysStr || delflagKeysStr.length === 0 ) {
                message.error('请选择要操作的条目');
            }
            else if (menu.key == 'on' && delFlag == 0) {
                message.error('选项已上线');
            }
            else if (menu.key == 'off' && delFlag == 1) {
                message.error('选项已下线');
            }
            else{
                delflagKeysStr = delflagKeysStr.join(",")
                dispatch({
                    type:'article/batchOnOff',
                    payload:{
                        commentIds: delflagKeysStr,
                    }
                })
                dispatch({
                    type:'article/putInState',
                    payload:{
                        commentDelflagKeys: []
                    }
                })
                delflagKeysStr = ''
            }
        }
    }
    const handleSubmitReply = (values) => {
        dispatch({
            type:'article/addComment',
            payload:{
                ...values
            }
        })
    }
    const gamProps = {
        visible: cms.gamv,
        loading: cms.gal,
        pagination: cms.gap,
        dataSource: cms.gas,
        catgId: cms.catgId,
        gaq: cms.gaq,
        onPageChange: (page) => {
            dispatch({
                type: 'cms/effect:query:gas',
                payload: {
                    ...cms.gaq,
                    pageNumber: page.current,
                    pageSize: page.pageSize
                }
            });
        },
        onCancel: () => {
            dispatch({
                type: 'cms/effect:toggle:gam',
                payload: {
                    gamv: false
                }
            });
        },
        onCiteGa: (params) => {
            dispatch({
                type: 'cms/effect:citeGa:arts',
                payload: {
                    ...params,
                    location,
                }
            });
        },
        onSearch: (params) => {
            dispatch({type: 'cms/effect:query:gas', payload: params});
        }
    }
    const changeCatgId = (CatgId) => {
        sessionStorage.setItem("catId",CatgId?CatgId:(searchProps.query.categoryId?searchProps.query.categoryId:"1"));
        dispatch({type: 'cms/effect:query:CatgId', payload: CatgId?CatgId:(searchProps.query.categoryId?searchProps.query.categoryId:"1")});
    }

    const changeModelId = (modelId) => {
        
        dispatch({type: 'article/effect:modelid:art', payload: { modelId }});
    }

    const tranfer = (data,id)=>{
        if(!data) return [];
        let obj ;
        return data.map((item,index)=>{
            obj = {...item}
            if(item.children){
                if(item.id == id){
                    obj.disabled = true;
                }
                obj.children = tranfer(item.children,id);
                return obj;
            } else {
                if(item.id == id){
                    obj.disabled = true;
                }
                return obj;
            }
        })
    }
    let cid = searchProps.query.categoryId
    // console.log("cid",cid)
    if(searchProps.catgs){
        searchProps.catgs = tranfer(searchProps.catgs,cid)
    }
    let arrs = []
    function loop(arr) {
        for (let item of arr) {
            if (item.children && item.children.length) {
                loop(item.children)
            } else {
                console.log('item>',item)
                arrs.push(item)
                break
            }
        }
    }
    if ((cid == 1 || !cid) && searchProps.catgs[0]) {
        // cid = searchProps.catgs[0].children[0].key
        // sessionStorage.CID = cid
        // sessionStorage.modelId = searchProps.catgs[0].children[0].modelId
        // console.log('catgs: ', cid)  
        loop(searchProps.catgs)
         sessionStorage.CID = cid = arrs[0].key      
         sessionStorage.modelId = arrs[0].modelId
    }
    const previewHandleCancel = ()=>{
        dispatch({type: 'article/effect:hide:preivewModal'})
    }

    const oncopy = (str)=>{
        if(copy(str)){
            console.log("复制成功");
            }else{
                console.log("复制失败")
            }
    }
    const disabledDate = (current) =>{
        // var today = moment().format('YYYY-MM-DD HH:mm:ss');
        // var last = moment().subtract('days', 1).format('YYYY-MM-DD HH:mm:ss')
        return current && current <= moment().startOf('day');
       // return currt ? currt< moment(last): moment(last);
    }
    const disabledDateTime =(da) =>{
        function range(start, end) {
            const result = [];
            for (let i = start; i < end; i++) {
                result.push(i);
            }
            return result;
        }

        return {
          disabledHours: () => moment(da).date()>new Date().getDate()?range(-1,-1):range(0 ,new Date().getHours()),
          disabledMinutes: () => moment(da).date()>new Date().getDate()||moment(da).hours()>new Date().getHours() ?range(-1,-1) :range(0,new Date().getMinutes()),
          disabledSeconds: () => moment(da).date()>new Date().getDate()||moment(da).hours()>new Date().getHours() ||moment(da).seconds()>new Date().getSeconds()?range(-1,-1) :range(0,new Date().getSeconds()),
        };
      }
    const getTitle = (str)=>{
        return <p>"预览" {str} <Button onClick={()=>{oncopy(str)}} >复制</Button></p>
    }
    const PublishDateModalComp = Form.create()(
        PublishDateModal
      )
      
    return (
        <Row className='content-inner' style={{overflow:'hidden'}}>
       
            <Col span={3} style={{width: '15%'}}>
                {/* <div style={{width:'100%',height:"100%"}}> */}
                <div style={{width:'100%',height:"100%",overflow:'auto'}}>
                    <Tree
                        catgs={searchProps.catgs}
                        changeCatgId={changeCatgId}
                        changeModelId={changeModelId}
                        formData = {formData}
                        categoryId = {cid}
                        searchProps = {searchProps.onSearch}
                    />
                </div>
            </Col>
            <Col span={21} style={{width: '85%',overflow:'hidden'}} >
                    {
                        article.modelId == 7?
                        <div>
                            <PaperList { ...paperProps }/>
                        </div>
                        :
                        <div>
                            <ArtSearch { ...searchProps } modelId={article.modelId}/>
                            <ArtToolbar { ...tbProps }/>
                            {
                                cid==1?'':
                                <ArtList { ...listProps } modelId={article.modelId}/>
                            }
                        </div>
                    }
                    <PushModal { ...pmProps }/>
                    <SyncModal { ...syncModalProps }/>
                    <CiteArtModal {...camProps}/>
                    <EditArtModal {...eamProps}/>
                    <ArtCommentModal {...acmProps} listProps={commentListProps}
                                                searchProps={commentSearchProps}
                                                handleSubmitReply={handleSubmitReply}/>
                    <GovAffairsModal {...gamProps}/>
                    {/* <Modal
                    title={getTitle(article.previewModalUrl)  }
                    visible={article.previewModalVisible}
                    closable={false}
                    footer={[
                        <Button key="close" onClick={previewHandleCancel}>确定</Button>,
                    ]}
                >
                   <div className={styles.detailModal}>
                            <div
                                className={styles.detailContent}
                               
                            >
                                
                                <iframe src={article.previewModalUrl} frameBorder={0} style={{'width':'100%','height':'100%'}}></iframe>  
                            </div>
                        </div>
                </Modal> */}
                <PublishDateModalComp publishModal={article.publishModal} publishModalConfirm={publishModalConfirm} hidePublishModal={hidePublishModal}/>
            </Col>
        </Row>
    )
}

ArticleList.contextTypes = {
    router: PropTypes.object.isRequired
}

function mapStateToProps({app, cms, article, comment}) {
    return {app, cms, article, comment}
}

export default connect(mapStateToProps)(ArticleList)
