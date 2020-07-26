import React from 'react'
import PropTypes from 'prop-types';
import moment from 'moment';
import {Form, TreeSelect, Input, DatePicker, Button, Row, Col} from 'antd';

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

class Search extends React.Component {
	constructor(props) {
		super(props);
	}

	onSearch() {
		const {searchHandle, form: {getFieldsValue}} = this.props;
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

	render() {
		const {catgs, offcs, form: {getFieldDecorator}} = this.props;
		return (
			<Form>
				<Row>
					<Col span={6}>
						<FormItem label="归属栏目" {...layout}>
						{
							getFieldDecorator('categoryId', {

							})(
								<TreeSelect
									allowClear
									showSearch
									treeNodeFilterProp="label"
	                                dropdownStyle={{maxHeight: 300, overflow: 'auto'}}
	                                treeData={catgs}
								/>
							)
						}
						</FormItem>
					</Col>
					<Col span={6}>
						<FormItem label="归属机构" {...layout}>
						{
							getFieldDecorator('officeId', {

							})(
								<TreeSelect
									allowClear
									showSearch
	                                treeNodeFilterProp="label"
	                                dropdownStyle={{maxHeight: 300, overflow: 'auto'}}
	                                treeData={offcs}
								/>
							)
						}
						</FormItem>
					</Col>
					<Col span={6}>
						<FormItem label="更新时间" {...layout}>
						{
							getFieldDecorator('updateTime', {

							})(
								<DatePicker.RangePicker format={dateFormat} />
							)
						}
						</FormItem>
					</Col>
					<Col span={2}>
						<Button type="primary" onClick={this.onSearch.bind(this)}>搜索</Button>
					</Col>
				</Row>
			</Form>
		);
	}
}

export default Form.create()(Search);
