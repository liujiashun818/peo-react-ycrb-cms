import React from 'react'
import PropTypes from 'prop-types'
import { Table, Popconfirm, Switch, Input, Button, Row, Col, Form, Checkbox, TreeSelect } from 'antd'
import { Link } from 'dva/router'
import { Jt, Immutable } from '../../../utils'
import VisibleWrap from '../../ui/visibleWrap'

function formatListData(data = []) {
  for(let i = 0; i < data.length; i++) {
    data[i].key = data[i].value = data[i].id
    data[i].label = data[i].name
  }
  return data;
}
function formatTreeData(data = []) {
  for(let i = 0; i < data.length; i++) {
    data[i].key = data[i].value = data[i].id +''
    data[i].label = data[i].name //为了selectTreeFilter准备
    if(!Jt.array.isEmpty(data[i].child)) {
      data[i].children = data[i].child
      delete data[i].child
      formatTreeData(data[i].children);
    }
  }
  return data;
}

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
	}
	submitForm(){
		const {handleSubmit} = this.props
		const {validateFields,getFieldsValue} = this.props.form
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
		const {currentItem,goBack,roleListAll,officeTree,type='add'} = this.props
		const {getFieldDecorator} = this.props.form
		const roleListAllData = formatListData(Immutable.fromJS(roleListAll).toJS())
		const officeTreeData = formatTreeData(Immutable.fromJS(officeTree).toJS())
		return (
			<Form>
		      <FormItem label='所属部门' {...formItemLayout}>
		        {getFieldDecorator('officeId', {
		          initialValue: currentItem.officeId?currentItem.officeId+'' : '',
                  rules: [
		            {
		              required: true,
		              message: '所属部门未填写'
		            }
		          ]
		        })(
		        	<TreeSelect
									
		              allowClear
		              showSearch
		              treeNodeFilterProp="label"
		              dropdownStyle={{maxHeight:300,overflow:'auto'}}
		              treeData={officeTreeData}
			        />
		        )}
		      </FormItem>
		      <FormItem label='登录名' {...formItemLayout}>
		        {getFieldDecorator('username', {
		          initialValue: currentItem.username || '',
               	  rules: [
		            {
		              required: true,
		              message: '登录名未填写'
		            }
		          ]
		        })(
		        	<Input  />
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
		        	<Input  />
		        )}
		      </FormItem>
		      <FormItem label='密码' {...formItemLayout}>
		        {getFieldDecorator('password', {
		          initialValue: currentItem.password || '',
              	  rules: [
		           {
		              required: (type=='add'?true:false),
		              message: '密码未填写'
		           }
		          ]
		        })(<Input type='password' />)
		       }
		      </FormItem>
		      <FormItem label='用户角色' {...formItemLayout}>
		        {getFieldDecorator('roleIds', {
		          initialValue: currentItem.roleIds || []
		        })(
		        	<CheckboxGroup options={roleListAllData} />
		        )}
		      </FormItem>
		      <FormItem label='备注' {...formItemLayout}>
		        {getFieldDecorator('remark', {
		          initialValue: currentItem.remark&&currentItem.remark!='null' ?currentItem.remark: ''
		        })(
		        	<Input type="textarea" rows={5}/>
		        )}
		      </FormItem>
  	      	  <FormItem wrapperCol={{span: 10, offset: 9}}>
							<VisibleWrap permis="edit:modify">
  	         		<Button type="primary" style={{ marginRight: 8 }} size="large" onClick={()=>this.submitForm()}>保存</Button>
							</VisibleWrap>
							<Button size="large" onClick={goBack}>
								返回
							</Button>
		      </FormItem>
			</Form>
		)
	}

}
export default Form.create()(form)
