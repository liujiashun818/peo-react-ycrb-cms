import React from 'react'
import PropTypes from 'prop-types'
import { Table, Popconfirm, Switch, Input, Button, Row, Col, Form, Checkbox, TreeSelect } from 'antd'
import { Link } from 'dva/router'
import VisibleWrap from '../ui/visibleWrap'

const CheckboxGroup = Checkbox.Group;
const FormItem = Form.Item
const formItemLayout = {
  labelCol: {
    span: 6
  },
  wrapperCol: {
    span: 10
  }
}
class form extends React.Component{
	constructor(props){
		super(props)
		this.state = {
			confirmDirty: false,
			disabled:true,
		}
	}  
	handleConfirmBlur(e){
	    const value = e.target.value;
	    this.setState({ confirmDirty: this.state.confirmDirty || !!value });
  	}
	submitForm(){
		const {handleSubmit,currentItem={}} = this.props //BE19DB6FE03F0373BBC0D503EE4A8B7F05F4EBF607664792A6210688
		const {validateFields,getFieldsValue} = this.props.form
	    validateFields((errors) => {
	      if (errors) {
	        return
	      }
	      const data = {
	        ...getFieldsValue(),
	        id:currentItem.id
	      }
	      delete data.confirm
	      if(!data.password){
	      	delete data.password
	      }
	      if(!data.newPassword){
	      	delete data.newPassword
	      }
	      handleSubmit(data)
	    })
	}
	checkPassword(rule, value, callback){ //确认新密码
	    const form = this.props.form;
	    if (value && value !== form.getFieldValue('newPassword')) {
	      callback('两次输入密码不一致!');
	    }else {
	      callback();
	    }
	}
	checkConfirm(rule, value, callback){ //新密码
	    const form = this.props.form;
	    if (value && this.state.confirmDirty ) {
	      form.validateFields(['confirm'], { force: true });
	    }
	    callback();
	}	
	checkOldPassword(rule, value, callback){
		const {password} = this.props.currentItem
		const form = this.props.form
		if(!value){
			this.setState({ disabled: true });
			form.setFieldsValue({newPassword:''})
			form.setFieldsValue({confirm:''})
			callback()
		}
		else{
			this.setState({ disabled: false });
			callback()
		}
	}
	render(){
		const {currentItem={}} = this.props
		const {disabled} = this.state
		const {getFieldDecorator} = this.props.form
		return (
			<Form>	
		      <FormItem label='登录名' {...formItemLayout}>
		        {(
		        	<p>{currentItem.username}</p>
		        )}
		      </FormItem> 
		      <FormItem label='姓名' {...formItemLayout}>
		        {getFieldDecorator('name', {
		          initialValue: currentItem.name || '',
            	  rules: [	
		            {
		              required: true,
		              message: '姓名未填写'
		            }
		          ]
		        })(
		        	<Input />
		        )}
		      </FormItem> 
		      <FormItem label='原密码' {...formItemLayout} >
		        {getFieldDecorator('password', {
		          initialValue: '',
            	  rules: [	
		            {
		              required: false,
		              message: '请输入原密码'
		            }, 
		            {
		              validator: (rule, value, callback)=>{this.checkOldPassword(rule, value, callback)},
		            }
		          ]
		        })(
		        	<Input type="password"/>
		        )}
		      </FormItem>
		      <FormItem label='新密码' required={false} {...formItemLayout} hasFeedback>
		        {getFieldDecorator('newPassword', {
		          initialValue: '',
            	  rules: [	
		            {
		              required: !disabled,
		              message: '请输入新密码'
		            }, {
		              validator: (rule, value, callback)=>{this.checkConfirm(rule, value, callback)},
		            }
		          ]
		        })(
		        	<Input disabled={disabled} type="password"/>
		        )}
		      </FormItem>
		      <FormItem label='确认新密码' required={false} {...formItemLayout} hasFeedback>
		        {getFieldDecorator('confirm', {
		          initialValue: '',
            	  rules: [	
		            {
		              required: !disabled,
		              message: '请确认新密码'
		            }, {
		              validator: (rule, value, callback)=>{this.checkPassword(rule, value, callback)},
		            }
		          ]
		        })(
		        	<Input onBlur={(e)=>{this.handleConfirmBlur(e)}} disabled={disabled} type="password"/>
		        )}
		      </FormItem>	

  	      	  <FormItem wrapperCol={{span: 10, offset: 7}}>
  	      	  {/*<VisibleWrap permis="edit:modify">*/}
  	         	<Button type="primary" style={{ marginLeft: 8 }} size="large" onClick={()=>this.submitForm()}>保存</Button>
         	  {/*</VisibleWrap>*/}
		      </FormItem> 
			</Form>
		)
	}

}
export default Form.create()(form)
