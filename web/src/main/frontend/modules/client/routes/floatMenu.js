import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { Modal,Button } from 'antd'
import FloatList from '../../../components/client/floatList'
import FloatEdit from '../../../components/client/floatEdit'
import VisibleWrap from '../../../components/ui/visibleWrap'

let num = 0;
 class FloatMenu extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            form:""
        }
        const { dispatch} = this.props;
        const {pagination} = this.props.floatMenu
    }
     showModal = () => {
        num--
         this.props.dispatch({
             type:'floatMenu/updateState',
             payload:{
                 visible: true,
                 record:{id:num}
             }
         })
     }
     handleOk = (data) => {
         const {pagination} = this.props.floatMenu;
         data.actionType = Number(data.id)>=0?'update':'add';
         const payload = {pagination,...data,callback:this.tableRefresh}
         this.props.dispatch({
             type:'floatMenu/edit',
             payload:payload
         })
         this.state.form.resetFields();
     }
     handleCancel = () => {
         this.state.form.resetFields();
         this.props.dispatch({
             type:'floatMenu/updateState',
             payload:{
                 visible: false,
                 record:{}
             }
         })
     }
     onPageChange =(page)=>{
        const { dispatch} = this.props;
        dispatch({
            type: 'floatMenu/pageChange',
            payload: {
                ...page
            }
        })
    }
     tableRefresh = ()=>{
         this.props.dispatch({
             type:'floatMenu/query'
         })
     }
     edit = (data)=>{
         this.props.dispatch({
             type:'floatMenu/updateState',
             payload:{
                 visible: true,
                 record:data
             }
         })
     }
     onOff = (id)=>{
         const {pagination} = this.props.floatMenu;
         const payload = {pagination,id:id,callback:this.tableRefresh}
         this.props.dispatch({
             type:'floatMenu/onOff',
             payload:payload
         })
     }
     deleteItem =(id)=>{
         const {pagination} = this.props.floatMenu;
         const payload = {pagination,id:id,callback:this.tableRefresh}
         this.props.dispatch({
             type:'floatMenu/deleteItem',
             payload:payload
         })
     }
     saveFormRef = (form) => {
        console.log(form)
        this.setState({form})
     }
    render(){
        const {pagination,list,submitLoading,visible,record,clientTree} = this.props.floatMenu;
        return(
            <div style={{background:'#fff',padding:'24px'}}>
                <div style={{overflow:'hidden',marginBottom:'15px'}}>
                    {/*<VisibleWrap permis="client:floatingImgs:edit">*/}
                        <Button style={{float:'right'}} type="primary" onClick={this.showModal}>新增</Button>
                    {/*</VisibleWrap>*/}
                </div>
                <FloatList
                    onPageChange={this.onPageChange}
                    pagination={pagination}
                    dataSource={list}
                    edit = {this.edit}
                    disabled = {this.disabled}
                    deleteItem = {this.deleteItem}
                    onOff = {this.onOff}
                />
                <Modal
                    visible={visible}
                    destroyOnClose={true}
                    title="新增浮动图"
                    onCancel={this.handleCancel}
                    footer = {null}
                >
                    <FloatEdit
                        key={record.id}
                        ref={this.saveFormRef}
                        handleOk={this.handleOk}
                        loading={submitLoading}
                        data={record}
                        handleCancel={this.handleCancel}
                        clientTree={clientTree}
                    />
                </Modal>
            </div>
        )
    }
 }

FloatMenu.contextTypes = {
    router: PropTypes.object.isRequired
}
function mapStateToProps ({ floatMenu}, context) {
    return {floatMenu}
}

export default connect(mapStateToProps)(FloatMenu)
