import React from 'react';
import { Button ,Popconfirm, Form, Input, Row, Col, DatePicker} from 'antd'
import { Link } from 'dva/router'
import moment from 'moment';

const RangePicker = DatePicker.RangePicker;
const FormItem = Form.Item
const formItemLayout = {
  labelCol: {
    span: 6
  },
  wrapperCol: {
    span: 6
  }
}
class GuestToolBar extends React.Component{
	constructor(props){
		super(props)
	}
	onSubmit(){
		const {validateFields,getFieldsValue} = this.props.form
		const {handleSearch} = this.props
		const dateFormat = 'YYYY-MM-DD';
	    validateFields((errors) => {
	      if (errors) {
	        return
	      }
	      const data = {
	        ...getFieldsValue(),
	      }
  	      if(data.date && data.date.length !== 0){
	        data.beginTime = data.date[0].format(dateFormat)
	        data.endTime = data.date[1].format(dateFormat)
	      }else{
	      	data.beginTime = ''
	      	data.endTime =''
	      }
	      delete data.date;
	      handleSearch(data)
	    })
	}
	render(){
		const {queryContent,beginTime,endTime} = this.props
		const {getFieldDecorator} = this.props.form
		const dateFormat = 'YYYY-MM-DD';
		return (
			<Row>
				<Col span={24}>
					<Form>
						<Col span={20}>
							<Col span={7}>
								<FormItem label='内容' {...{labelCol:{span:6},wrapperCol:{span:17}}}>
							        {getFieldDecorator('content', {
							          initialValue: queryContent || '',
							        })(<Input/>)}
								</FormItem>
							</Col>
					      	<Col span={13}>
			  			      <FormItem label='起止日期' labelCol={{span: 6}} wrapperCol={{span: 17}}>
							        {getFieldDecorator('date', {
							            initialValue: beginTime?[moment(beginTime, dateFormat), moment(endTime, dateFormat)]:null
							        })(<RangePicker format={dateFormat}/>)}
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

export default Form.create()(GuestToolBar)