import React from 'react'
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import { message } from 'antd'
import { Jt, Immutable } from '../../../utils'
import { routerPath } from '../../../constants'
import CommentList from '../../../components/comment/commentList'
import Search from '../../../components/comment/search'
import SensModal from '../../../components/comment/sensModal'
import ReplyModal from '../../../components/comment/replyModal'

function Comment({ location, dispatch,comment }, context) {
	const listProps = {
		dataSource : comment.list,
        pagination: comment.pagination,
        selectedRowKeys:comment.delflagKeys,
        loading:comment.loading,
        delFlag:location.query.delFlag || 0,
        onPageChange(page) {
            const query = location.query
            dispatch(routerRedux.push({
                pathname: routerPath.COMMENT,
                query: {
                    ...query,
                    pageNumber: page.current,
                    pageSize: page.pageSize ,
                }
            }))
        },
        onSelectedDelflag(delflagKeys){
            dispatch({
                type:'comment/putInState',
                payload:{
                    delflagKeys:delflagKeys
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
        // onChangeDelflag(delFlag,id,type){
        //     let query = location.query
        //     if(type == 'onoff'){
        //         if(delFlag == 0){ //如果上线则下线 其他情况则上线
        //             delFlag = 1
        //         }else{
        //            delFlag = 0
        //         }
        //     }else if(type == 'decline'){ //如果点不通过则待审核
        //         delFlag = 4
        //     }
        //     dispatch({
        //         type:'comment/updateProps',
        //         payload:{
        //             params:{
        //                id,
        //                delFlag
        //             },
        //             query
        //         }
        //     })
        // },
        onChangeDelflag2(delFlag,id,type){
            const query = location.query
            dispatch({
                type:'comment/batchOnOff2',
                payload:{
                    ...query,
                    commentId:id,
                    "actionStatus":0
                }
            })
        },
        onChangeDelflag(delFlag,id,type){

                // if(type == 'onoff'){
                //     if(delFlag == 0){ //如果上线则下线 其他情况则上线
                //         delFlag = 1
                //     }else{
                //        delFlag = 0
                //     }
                // }else if(type == 'decline'){ //如果点不通过则待审核
                //     delFlag = 4
                // }

                // dispatch({
                //             type:'comment/updateProps',
                //             payload:{
                //                 params:{
                //                    id,
                //                    delFlag
                //                 },
                //                 query
                //             }
                //         })
            const query = location.query
                dispatch({
                    type:'comment/batchOnOff',
                    payload:{
                        ...query,
                        commentIds:id,
                        updateDelFlag:(delFlag==0?0:1)
                    }
                })
        },
        saveLikes(record){
            let query = location.query
            dispatch({
                type:'comment/updateProps',
                payload:{
                    params:{
                        id:record.id,
                        likes:record.likes
                    },
                    query
                }
            })
        },
        replyHandle(record){
            dispatch({
                type:'comment/replyShow',
                payload: record
            });
        },
        handleSubmitReply(record, type) {
            let query = location.query;
            dispatch({
                type:'comment/addReply',
                payload:{
                    params:{
                        content: type === 'admin' ? record.replyAdminValue : record.replyValue,
                        parentId: record.id,
                        isAdmin: type === 'admin',
                        userName: type === 'admin' ? '管理员' : record.replyName,
                        sysCode:record.sysCode,
                        articleId:record.articleId
                    },
                    query
                }
            })
        }
	}
    const searchProps = {
        delFlag:location.query.delFlag || 0,
        content:location.query.content,
        onSearch(data){
            const query = location.query
             dispatch(routerRedux.push({
                pathname: routerPath.COMMENT,
                query: {
                    'pageSize': 10,
                    ...query,
                    ...data,
                    'pageNumber': 1,
                }
            }))
        },
        onDelFlagChange(param){
            const query = location.query
             dispatch(routerRedux.push({
                pathname: routerPath.COMMENT,
                query: {
                    'pageSize': 10,
                    ...query,
                    'delFlag':param.target.value,
                    'pageNumber': 1,
                }
            }))
        },
        batchMenuClick(menu){
            const query = location.query;
            const delFlag = (location.query.delFlag || 0)
            let delflagKeysStr = comment.delflagKeys
            if(!delflagKeysStr || delflagKeysStr.length===0){
                message.error('请选择要操作的条目');
            }
            else if(menu.key=='on'&&delFlag==0){
                message.error('选项已上线');
            }
            else if(menu.key=='off'&&delFlag==1){
                message.error('选项已下线');
            }
            else{
                delflagKeysStr = delflagKeysStr.join(",")
                dispatch({
                    type:'comment/batchOnOff',
                    payload:{
                        ...query,
                        commentIds:delflagKeysStr,
                        updateDelFlag:(menu.key=='on'?1:0)
                    }
                })
                dispatch({
                    type:'comment/putInState',
                    payload:{
                        delflagKeys:[]
                    }
                })
                delflagKeysStr = ''
            }
        }
    }
    const sensModalProps = {
        visible:comment.visible || false,
        sensWord:comment.sensWord || '',
        showSensModal(){
            dispatch({
                type:'comment/getSensWord',
                payload:{
                    visible:true
                }
            })
        },
        handleOk(param){
            if(comment.shouldPostSensWord){
                dispatch({
                    type:'comment/saveSensWord',
                    payload:{
                        ...param
                    }
                })
            }else{
                dispatch({
                    type:'comment/updateSensWord',
                    payload:{
                        ...param,
                        id:1,
                        level:1
                    }
                })
            }
        },
        handleCancel(){
           dispatch({
                type:'comment/putInState',
                payload:{
                    visible:false
                }
            })
        }
    };
    const replyModalProps = {
        visible: comment.replyModalVisible,
        currentItem:comment.currentItem,
        onCancel(){
            dispatch({
                type: 'comment/replyHide',
            });
        },
        replyOk(replyInfo){
            dispatch({
                type: 'comment/replyAdmin',
                payload: {
                    replyInfo,
                }
            });
        }
    };
    return (
        <div className="content-inner">
            <SensModal {...sensModalProps}></SensModal>
            <Search {...searchProps}></Search>
            <CommentList {...listProps} ></CommentList>
            <ReplyModal {...replyModalProps}/>
        </div>
    )
}

Comment.contextTypes = {
    router: PropTypes.object.isRequired
}

function mapStateToProps ({ comment }) {
  return { comment }
}

export default connect(mapStateToProps)(Comment)
