import React from 'react'
import PropTypes from 'prop-types';
import {Row, Col, Form, TreeSelect, Select, Input, Button} from 'antd';
import styles from './citeArtSearch.less';

const FormItem = Form.Item;
const Option = Select.Option;

const layout = {
	labelCol: {span: 6},
	wrapperCol: {span: 16}
};

class CiteArtSearch extends React.Component {
	constructor(props) {
		super(props);
	}

	componentWillReceiveProps(nextProps) {
		if(!nextProps.visible) {
			this.reset();
		}
	}

	getKwTypeTpl() {
		const {form:{getFieldDecorator}} = this.props;
		return getFieldDecorator('kwType', {
			initialValue: 'title'
		})(
			<Select size="large" className="kwType">
				<Option value="title">标题</Option>
	            <Option value="authors">作者</Option>
			</Select>
		)
	}

	getQuery() {
		const {form: {getFieldsValue}} = this.props;
		let values = getFieldsValue();
		if(values.keyword) {
			values[values.kwType] = values.keyword;
		}
		delete values.kwType;
		delete values.keyword;
		return values
	}

	catgChg(value, label) {
        const data = this.getQuery();
        data.categoryId = value;
        this.props.onSearch(data);
    }

	searchHandle() {
		const {onSearch} = this.props;
		onSearch(this.getQuery());
	}

	reset() {
		const {form:{resetFields}} = this.props;
		resetFields();
	}

	render() {
		const {catgs, form:{getFieldDecorator}} = this.props;

		return (
			<Form className={styles.citeArtSearch}>
				<Row>
					<Col span={10}>
           				<FormItem label="栏目" {...layout}>
           				{
           					getFieldDecorator('categoryId', {
           						initialValue: ''
           					})(
           						<TreeSelect
									allowClear
									showSearch
									treeNodeFilterProp="label"
	                                dropdownStyle={{maxHeight: 300, overflow: 'auto'}}
	                                treeData={catgs}
	                                onChange={this.catgChg.bind(this)}
								/>
           					)
           				}
           				</FormItem>
					</Col>
					<Col span={10}>
						<FormItem label="关键词" {...layout}>
                        {
                            getFieldDecorator('keyword', {
                                initialValue: ''
                            })(
                                <Input size="large" addonBefore={this.getKwTypeTpl()} />
                            )
                        }
	                    </FormItem>
					</Col>
					<Col span={4} className="op-col">
						<Button type="primary" onClick={this.searchHandle.bind(this)}>搜索</Button>
					</Col>
				</Row>
			</Form>
		);
	}
}

export default Form.create()(CiteArtSearch);
