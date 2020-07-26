import React from 'react'
import PropTypes from 'prop-types';
import {routerRedux} from 'dva/router';
import {connect} from 'dva';
import {message,Button} from 'antd';
import {Message} from  'antd'
import {Jt, Immutable} from '../../../utils';
import {routerPath} from '../../../constants';
import AskRecordList from '../../../components/ask/askList';
import ArtCommentModal from '../../../components/articles/artCommentModal';
import AskTree from '../../../components/ask/tree';
import SearchBar  from  '../../../components/ask/askSearch'
let formData = ''
// componentDidMount () {

// }
function AskList({
                         location,
                         dispatch,
                         app,
                        askList,article
                     }, context) {
    const handleSubmitReply = (values) => {
        dispatch({
            type:'article/addComment',
            payload:{
                ...values,
                "sysCode":'ask'
            }
        })
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
        onChangeDelflag2(delFlag,id,type){
            const query = location.query
            dispatch({
                type:'article/batchOnOff2',
                payload:{
                    ...query,
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
                        articleId:record.articleId,
                        isAdmin: isAdmin,
                        userName: isAdmin ? '管理员' : record.replyName,
                        "sysCode":'ask'
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
    const selectItem = (id)=>{

        dispatch({type: 'askList/query', payload: {govId:id,current:1,pageSize:askList.pagination.pageSize }})
    }

    const saveOrder = ()=>{
        let params = askList.list.map((item)=>{
            return {"id":item.id,'orderID':item.orderID?item.orderID:0}
        });
        params.callback = (res)=>{
            if(res){
                Message.success('操作成功');
            } else{
                Message.error('操作失败');
            }
        }
        dispatch({type: 'askList/saveOrder', payload:params});
       // dispatch({type: 'askList/query', payload: {govId:askList.query.govId,current:askList.pagination.current,pageSize:askList.pagination.pageSize }})
    }
    const goEdit =(id,gid)=>{

        dispatch(routerRedux.push({
            pathname: routerPath.ASKEDIT,
            query: {
               id:id,
               govId:askList.query.govId,
                gid:gid
            }
        }));
    }
    const onRecommond = (id)=>{
        if(!id){
            message.error('请选择推荐栏目');
            return ;
        }
        if(askList.selectedRows.length==0){
            message.error('请至少选择一项');
            return ;
        }
        dispatch({type: 'askList/recmmond',payload:{id,ids:askList.selectedRows,callback:(res)=>{
                   if(res){
                       Message.success('操作成功');
                   } else{
                       Message.error('操作失败');
                   }
                }}});
    }
    const  handleSearch = (values)=>{
        if(values.publishDate&&Array.isArray(values.publishDate)){
            values.startTime = new Date(values.publishDate[0]).toLocaleDateString().replace(/\//gi,'-');
            values.endTime = new Date(values.publishDate[1]).toLocaleDateString().replace(/\//gi,'-');
            delete  values.publishDate
        }
        dispatch({type: 'askList/query', payload: {...askList.query,...values,current:1,pageSize:askList.pagination.pageSize}})
    }

    const listProps = {
        cfg: app.cfg,
        loading:askList.loading,
        dataSource: askList.list,
        pagination: askList.pagination,
        viewComment: (record) => {
            dispatch({type: 'article/reducer:update', payload: {viewComment: true, curArt: record}});
            dispatch({ type: 'article/queryComment'});
        },
        changeItem:(id,e)=>{
            askList.list.map((item,index)=>{
                if(item.id==id){
                    item.orderID = e;
                }
                return item;
            });
        },
        onOnline:(id)=>{
            dispatch({type: 'askList/online',payload:id});
        },
        onOffline:(id)=>{
            dispatch({type: 'askList/offline',payload:id});
        },
        settop:(id)=>{
            dispatch({type: 'askList/settop',payload:id});
        },
        cancelTop:(id)=>{
            dispatch({type: 'askList/cancelTop',payload:id});
        },
        onEdit:(id,gid)=>{
            // console.log(document.getElementById('container').scrollTop,'哈哈哈');
            let latoutNode=document.getElementById('container').scrollTop;
            window.sessionStorage.setItem('sessionUserId',latoutNode);
            // for(var i in document.body){
            //     console.log(i,document.body[i])
            // }
            // console.log(document.documentElement)
            // var sTop = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop;
            // console.log(sTop);
            // console.log(document.body.clientHeight);
            goEdit(id,gid);
        },
        onSelected:(ids)=>{
            dispatch({type: 'askList/selectRow',payload:ids});
            // dispatch({type: 'askList/recmmond',payload:id});
        },
        onPageChange(page, filter, sorter) {
            let query =askList.query;
            query.current = page.current;
            query.pageSize = page.pageSize;
            dispatch({type: 'askList/query', payload: query})

        },
    };


    return (
        <div className='content-inner' style={{overflow:'hidden'}}>

            <div style={{float:'left',width:'15%'}}>
                <div style={{width:'100%',height:"500px",overflow:'auto'}}>
                    <AskTree
                        onSelect = {selectItem}
                        formData = {askList.menus}
                        // categoryId = {searchProps.query.categoryId}
                        // searchProps = {searchProps.onSearch}
                    />
                </div>
            </div>
            <div style={{float:'left',width:'85%'}}>
                <SearchBar saveOrder={saveOrder} onRecommond={onRecommond} recommendList={askList.recommendList} handleSearch={handleSearch.bind(this)}></SearchBar>
                <AskRecordList { ...listProps }/>
                <ArtCommentModal {...acmProps} listProps={commentListProps}
                                 searchProps={commentSearchProps}
                                 handleSubmitReply={handleSubmitReply}/>
                {/*<PushModal { ...pmProps }/>*/}
                {/*<CiteArtModal {...camProps}/>*/}
                {/*<EditArtModal {...eamProps}/>*/}
                {/*<ArtCommentModal {...acmProps} listProps={commentListProps}*/}
                                 {/*searchProps={commentSearchProps}*/}
                                 {/*handleSubmitReply={handleSubmitReply}/>*/}
            </div>


        </div>
    )
}

AskList.contextTypes = {
    router: PropTypes.object.isRequired
}

function mapStateToProps({app, askList,article,comment}) {
    return {app, askList,article}
}

export default connect(mapStateToProps)(AskList)
