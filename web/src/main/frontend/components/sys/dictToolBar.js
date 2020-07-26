import React from 'react'
import PropTypes from 'prop-types'
import { Link } from 'dva/router'
import { Table, Popconfirm, Input, Button, Row, Col, Form, Select } from 'antd'
import VisibleWrap from '../ui/visibleWrap'

const FormItem = Form.Item
const formItemLayout = {
  labelCol: {
    span: 5
  },
  wrapperCol: {
    span: 19
  }
}
class ToolBar extends React.Component{
	constructor(props){
		super(props)
	}
	submitSearch(){
		const {validateFields,getFieldsValue} = this.props.form
		const {handleSearch} = this.props
	    validateFields((errors) => {
	      if (errors) {
	        return
	      }
	      const data = {
	        ...getFieldsValue(),
	      }
	      handleSearch(data)
	    })
	}
	render(){
		const {typeData=[]} = this.props
		const {getFieldDecorator} = this.props.form
		return (
			<Form>
				<Row>
					<Col span={2} style={{'lineHeight':'32px','textAlign':'center'}}>
						<VisibleWrap permis="edit:modify">
							<Button size="large">
								<Link to={{pathname:`/sys/dict/dictEdit`,query:{type:'addDict'}}}>字典添加</Link>
							</Button>
						</VisibleWrap>
					</Col>
					<Col span={20} offset={2}>
						<Col span={9}>
						    <FormItem label='类型' {...formItemLayout} style={{'display':'inlineBlock'}}>
						        {getFieldDecorator('type', {
						          initialValue: '',
						        })(<Select 
						              allowClear 
						              showSearch
						              filterOption={(input, option) => option.props.value.toLowerCase().indexOf(input.toLowerCase()) >= 0}
						            >
						              {
						                typeData.map((item,index) => {
						                  return <Select.Option key={index} value={item.type}>{item.type}</Select.Option> 
						                })
						              }
						          </Select>)}
						    </FormItem> 
					    </Col>
						<Col span={9}>
						    <FormItem label='描述' {...formItemLayout} style={{'display':'inlineBlock'}}>
						        {getFieldDecorator('description', {
						          initialValue: '',
						        })(<Input />)}
						    </FormItem> 
					    </Col>
						<Col span={4} style={{'lineHeight':'32px','textAlign':'center'}}>
							<Button type="primary" onClick={() => this.submitSearch()}>查询</Button>
					    </Col>	
				    </Col>			    
			    </Row>
			</Form>
		)
	}
}

export default Form.create()(ToolBar)
