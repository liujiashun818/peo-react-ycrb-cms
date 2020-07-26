import React from 'react'
import PropTypes from 'prop-types';
import {routerRedux} from 'dva/router';
import {connect} from 'dva';
import AskTree from "../../../components/ask/tree";
import AskEditView from '../../../components/ask/askEdit'
import {Button,Message} from "antd";
import moment from  'moment';

function AskEdit({
                     location,
                     dispatch,
                     app,
                     askList
                 }, context) {
        const editProps = {
            typeList : askList.typeList,
            domainList : askList.domainList,
            currentInfo: askList.currentItem,
            id:location.query.id,
            govId:location.query.gid,
            save:(data)=>{
                console.log(data,'data')
              //  data.headImage = data.headImage&&data.headImage.fileList&&data.headImage.fileList[0]?data.headImage.fileList[0].fileList:'';
              //  data.shareLogo = data.shareLogo&&data.shareLogo.fileList&&data.shareLogo.fileList[0]?data.shareLogo.fileList[0].fileList:'';
              //  data.attachment = data.attachment&&data.attachment.fileList&&data.attachment.fileList[0]?data.attachment.fileList[0].fileList:'';
                data.replyTime = data.replyTime? moment(data.replyTime).format('YYYY-MM-DD HH:mm:ss'):'';
                // data.replyTime=moment(data.replyTime).format('YYYY-MM-DD HH:mm:ss');
                console.log('data.repleytime',{...data})
                dispatch({type: 'askList/save', payload: {...data,callback:(e)=>{
                            if(e){
                                Message.success("操作成功");
                                dispatch({type: 'askList/query', payload: {govId:location.query.govId,current:askList.pagination.current,pageSize:askList.pagination.pageSize }})
                                dispatch(routerRedux.goBack())
                            } else {
                                Message.error("操作失败")
                            }
                        }}});
            },
            goBack(){
                dispatch(routerRedux.goBack())
            }
        }
        const selectItem = (id)=>{
            console.log(location)
            if (location.pathname === '/ask/edit'){
                window.location.href = "#/ask/asks";
                // window.location.reload();
                // window.open()
            }
            dispatch({type: 'askList/query', payload: {govId:id,current:1,pageSize:askList.pagination.pageSize }})
        }
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

                    <AskEditView {...editProps}></AskEditView>
                </div>
            

            </div>
        )

}
function mapStateToProps({app, askList}) {
    return {app, askList}
}
export default connect(mapStateToProps)(AskEdit)
