import React from 'react';
import PropTypes from 'prop-types';
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
	      handleSearch(data)
	    })
	}
	LinkToLoadingImgsEdit = () => {
		this.context.router.push({
			pathname: '/client/loadingImgs/loadingImgsEdit',
			query: {type: 'add'}
		})
	}
	render(){
		const {queryName} = this.props
		
		const {getFieldDecorator} = this.props.form
		return (
			<Row>
				<Col span={24}>
					<Form>
						<Col span={4}>
							<FormItem {...{labelCol:{span:6},wrapperCol:{span:6}}}>
								<VisibleWrap permis="edit:modify">
									<Button onClick={this.LinkToLoadingImgsEdit}>
										添加开屏图
		{/* <Link to={{pathname:`/client/loadingImgs/loadingImgsEdit`, query: {type: 'add'}}}>添加开屏图</Link>*/}
									</Button>
								</VisibleWrap>
							</FormItem>
						</Col>
						<Col span={12}>
							<Col span={20}>
								<FormItem label='名称' {...{labelCol:{span:6},wrapperCol:{span:17}}}>
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
LoadingImgsToolBar.contextTypes = {
  router: PropTypes.object.isRequired,
};
export default Form.create()(LoadingImgsToolBar)