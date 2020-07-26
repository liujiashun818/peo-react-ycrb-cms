import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, InputNumber, Radio, Modal, Button, TreeSelect, Icon } from 'antd'
import { Link } from 'dva/router'
import VisibleWrap from '../ui/visibleWrap'

const FormItem = Form.Item
const formItemLayout = {
  labelCol: {
    span: 6
  },
  wrapperCol: {
    span: 8
  }
}
class DictForm extends React.Component{
	constructor(props){
		super(props)
	}
	submitForm(){
		const {validateFields,getFieldsValue} = this.props.form
		const {handleSubmit} = this.props
	    validateFields((errors) => {
	      if (errors) {
	        return
	      }
	      const data = {
	        ...getFieldsValue(),
	      }
	      handleSubmit(data)
	    })
  	}
	render(){
		const {currentItem={},goBack} = this.props
		const {getFieldDecorator} = this.props.form
		return (
			<Form horizontal style={{marginTop:50}}>
		        <FormItem label='键值' hasFeedback {...formItemLayout}>
			        {getFieldDecorator('value', {
			          initialValue: currentItem.value,
			          rules: [
			            {
			              required: true,
			              message: '键值未填写'
			            }
			          ]
			        })(<Input />)}
		        </FormItem>
		        <FormItem label='标签' hasFeedback {...formItemLayout}>
			        {getFieldDecorator('label', {
			          initialValue: currentItem.label,
			        })(<Input />)}
		        </FormItem>
		        <FormItem label='类型' hasFeedback {...formItemLayout}>
			        {getFieldDecorator('type', {
			          initialValue: currentItem.type,
          	          rules: [
			            {
			              required: true,
			              message: '类型未填写'
			            }
			          ]
			        })(<Input />)}
		        </FormItem>
		        <FormItem label='描述' hasFeedback {...formItemLayout}>
			        {getFieldDecorator('description', {
			          initialValue: currentItem.description,
			        })(<Input />)}
		        </FormItem>
		        <FormItem {...{labelCol: {span: 6}, wrapperCol: {span: 12}}} style={{textAlign:'right',marginTop:30}}>
		        	<VisibleWrap permis="edit:modify">
				        <Button size="large" type="primary" style={{marginRight:10}} onClick={() => this.submitForm()}>
									保存
								</Button>
							</VisibleWrap>
			        <Button size="large" onClick={goBack}>
								返回
							</Button>
				</FormItem>
			</Form>
		)
	}
}

export default Form.create()(DictForm)