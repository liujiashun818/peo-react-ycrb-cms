import React from 'react'
import PropTypes from 'prop-types';
import moment from 'moment';
import {Form, Input, DatePicker, Button, Row, Col} from 'antd';

const FormItem = Form.Item;

const dateFormat = 'YYYY-MM-DD';

const layout = {
	labelCol: {
		span: 6
	},
	wrapperCol: {
		span: 16
	}
};

function Search({
	searchHandle,
	form: {
		getFieldDecorator,
		getFieldsValue
	}
}) {

	function onSearch() {
		const values = getFieldsValue();
		if(values.updateTime && values.updateTime.length !== 0) {
			values.beginTime = values.updateTime[0].format(dateFormat);
			values.endTime = values.updateTime[1].format(dateFormat);
		}
		delete values.updateTime;
		for(let i in values) {
			if(!values[i]) {
				delete values[i];
			}
		}
		searchHandle(values);
	}

	return (
		<Form>
			<Row>
				<Col span={8}>
					<FormItem label="作者" {...layout}>
					{
						getFieldDecorator('authors', {

						})(<Input/>)
					}
					</FormItem>
				</Col>
				<Col span={8}>
					<Form.Item label="更新时间" {...layout}>
					{
						getFieldDecorator('updateTime', {

						})(
							<DatePicker.RangePicker format={dateFormat} />
						)
					}
					</Form.Item>
				</Col>
				<Col span={2}>
					<Button type="primary" onClick={onSearch}>搜索</Button>
				</Col>
			</Row>
		</Form>
	)
}

export default Form.create()(Search);