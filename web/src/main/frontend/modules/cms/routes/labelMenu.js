import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { Modal,Button } from 'antd'
import LabelList from '../../../components/articles/labelList'
import LabelEdit from '../../../components/articles/labelEdit'

 class LabelMenu extends React.Component{
    constructor(props){
        super(props)
        const { dispatch} = this.props;
        const {pagination} = this.props.labelMenu
    }
     showModal = () => {
         this.props.dispatch({
             type:'labelMenu/updateState',
             payload:{
                 visible: true,
                 record:{}
             }
         })
     }
     handleOk = (data) => {
         data.actionType = data.id?'update':'add';
         const {pagination} = this.props.labelMenu;
         const payload = {pagination,...data,delFlag:0,callback:this.tableRefresh}
         this.props.dispatch({
             type:'labelMenu/edit',
             payload:payload
         })
         this.state.form.resetFields();
     }
     handleCancel = () => {
         this.state.form.resetFields();
         this.props.dispatch({
             type:'labelMenu/updateState',
             payload:{
                 visible: false,
                 record:{}
             }
         })
     }
     onPageChange = (page)=>{
        const { dispatch} = this.props;
        dispatch({
            type: 'labelMenu/pageChange',
            payload: {
                ...page
            }
        })
    }
     edit = (data)=>{
         this.props.dispatch({
             type:'labelMenu/updateState',
             payload:{
                 visible: true,
                 record:data
             }
         })
     }
     deleteItem=(id)=>{
         const {pagination} = this.props.labelMenu;
         const payload = {pagination,id:id,callback:this.tableRefresh}
         this.props.dispatch({
             type:'labelMenu/deleteItem',
             payload:payload
         })
     }
     tableRefresh = ()=>{
         this.props.dispatch({
             type:'labelMenu/query'
         })
     }
     saveFormRef = (form) => {
         this.setState({form})
     }
    render(){
        const {pagination,list,visible,record,catgs,catgId,loading} = this.props.labelMenu;
        return(
            <div style={{background:'#fff',padding:'24px'}}>
                <div style={{overflow:'hidden',marginBottom:'15px'}}>
                    <Button style={{float:'right'}} type="primary" onClick={this.showModal}>新增</Button>
                </div>
                <LabelList
                    onPageChange={this.onPageChange}
                    pagination={pagination}
                    dataSource={list}
                    edit = {this.edit}
                    loading = {loading}
                    deleteItem = {this.deleteItem}
                />
                <Modal
                    visible={visible}
                    title="新增标签"
                    onCancel={this.handleCancel}
                    footer = {null}
                    destroyOnClose={true}
                >
                    <LabelEdit
                        ref={this.saveFormRef}
                        handleOk={this.handleOk}
                        data={record}
                        handleCancel={this.handleCancel}
                        catgs={catgs}
                    />
                </Modal>
            </div>
        )
    }
 }

 LabelMenu.contextTypes = {
    router: PropTypes.object.isRequired
}
function mapStateToProps ({ labelMenu}, context) {
    return {labelMenu}
}

export default connect(mapStateToProps)(LabelMenu)
