import React from 'react';
import { Button ,Popconfirm, Form, Input, Row, Col} from 'antd'
import { Link } from 'dva/router'
import VisibleWrap from '../ui/visibleWrap'

const FormItem = Form.Item
const formItemLayout = {
  labelCol: {
    span: 6
  },
  wrapperCol: {
    span: 6
  }
}
class LoadingImgsToolBar extends React.Component{
	constructor(props){
		super(props)
	}
	onSubmit(){
		const {validateFields,getFieldsValue} = this.props.form
		const {handleSearch} = this.props
	    validateFields((errors) => {
	      if (errors) {
	        return
	      }
	      const data = {
	        ...getFieldsValue(),
	      }
	      for(let i in data){
              if( data[i].replace(/\s/gi,'').length ==0) {
                  data[i] = ''
              }
          }
	      handleSearch(data)
	    })
	}
	render(){
		const {queryName} = this.props
		const {getFieldDecorator} = this.props.form
		return (
			<Row>
				<Col span={24}>
					<Form>
						<Col span={12}>
							<Col span={20}>
								<FormItem label='联系人' {...{labelCol:{span:6},wrapperCol:{span:17}}}>
							        {getFieldDecorator('name', {
							          initialValue: queryName || '',
							        })(<Input/>)}
								</FormItem>
							</Col>
							<Col span={4}>
								<FormItem {...formItemLayout}>
									<Button type="primary" onClick={() => this.onSubmit()}>查询</Button>
								</FormItem>
							</Col>
						</Col>
					</Form>
				</Col>
			</Row>
		)
	}
}

export default Form.create()(LoadingImgsToolBar)
