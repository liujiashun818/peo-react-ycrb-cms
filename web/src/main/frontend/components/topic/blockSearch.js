import React from 'react'
import PropTypes from 'prop-types';
import { Table, Popconfirm, Row, Col, Form, Select, Radio, Input, InputNumber, Button } from 'antd';
import { Link } from 'dva/router';
import { routerPath } from '../../constants';
import styles from './blockSearch.less';

class BlockSearch extends React.Component {
	constructor(props) {
		super(props);
	}

	getKwTypeTpl() {
		let {query, form:{getFieldDecorator}} = this.props;
		return getFieldDecorator('kwType', {
			initialValue: query.kwType || 'title'
		})(
			<Select size="large" className="kwType">
				<Select.Option value="title">标题</Select.Option>
	            <Select.Option value="authors">作者</Select.Option>
			</Select>
		)
	}

	getDelFlag(query) {
		let delFlag = query.delFlag;
		if(typeof delFlag === 'undefined' || delFlag === null) {
			return '0';
		}
		else {
			return delFlag + '';
		}
	}

	searchHandle() {
		const {onSearch, form:{getFieldsValue}} = this.props;
		let values = getFieldsValue();
		values[values.kwType] = values.keyword;
		delete values.kwType;
		delete values.keyword;
		onSearch(values);
	}

	delFlagChg(e) {
		const {onSearch, form:{getFieldsValue}} = this.props;
		const delFlag = e.target.value;
		let values = getFieldsValue();
		values[values.kwType] = values.keyword;
		delete values.kwType;
		delete values.keyword;
		values.delFlag = delFlag;
		onSearch(values);
	}

	render() {
		let {query, updateBlock, form:{getFieldDecorator}} = this.props;

		return (
			<Form className={styles.blockSearch}>
				<Row>
					<Col span={10}>
						<Form.Item
            			>
	                        {
	                            getFieldDecorator('delFlag', {
	                                initialValue: this.getDelFlag(query)
	                            })(
	                                <Radio.Group onChange={this.delFlagChg.bind(this)}>
	                                    <Radio.Button value="0">上线</Radio.Button>
	                                    <Radio.Button value="2">审核</Radio.Button>
	                                    <Radio.Button value="1">下线</Radio.Button>
	                                </Radio.Group>
	                            )
	                        }
           				 </Form.Item>
					</Col>
					<Col span={10}>
						<Form.Item
	                        label="关键词"
	                        labelCol={{span: 6}}
	                        wrapperCol={{span: 16}}
	                    >
	                        {
	                            getFieldDecorator('keyword', {
	                                initialValue: query.keyword || ''
	                            })(
	                                <Input size="large" addonBefore={this.getKwTypeTpl()} />
	                            )
	                        }
	                    </Form.Item>
					</Col>
					<Col span={4} className="op-col">
						<Button type="primary" onClick={this.searchHandle.bind(this)}>搜索</Button>
						<Button onClick={updateBlock}>编辑</Button>
					</Col>
				</Row>
			</Form>
		);
	}
}

export default Form.create()(BlockSearch);