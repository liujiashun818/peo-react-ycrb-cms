import React from 'react'
import PropTypes from 'prop-types';
import { Modal, Popconfirm, Form, Input, InputNumber, Radio, Button } from 'antd';
import InputStat from '../form/inputStat';

const formItemLayout = {
	labelCol: {
		span: 4
	},
	wrapperCol: {
		span: 16
	}
};

class BlockEditModal extends React.Component {
	constructor(props) {
		super(props);
	}

	onOk() {
		let {action, topic, curBlock, onSave, form:{validateFields}} = this.props;
		validateFields((errors, values) => {
			if(errors) {
				return;
			}
			if(action === 'update') {
				values.id = curBlock.id;
			}
			values.parentId = topic.id;
			onSave(values);
		});
	}

	render() {
		let {id, action, curBlock, visible, onCancel, form:{getFieldDecorator}} = this.props;

		let modalProps = {
			key: id,
			visible,
			title: '区块编辑',
			onCancel,
			onOk: this.onOk.bind(this)
		};
		return (
			<Modal {...modalProps}>
				<Form horizontal>
					<Form.Item 
						{...formItemLayout}
						label="标题"
					>
						{
							getFieldDecorator('title', {
								initialValue: action === 'update' ? curBlock.title : '',
								rules: [
									{required: true, message: '请输入区块名称'}
								]
							})(
								<Input />
							)
						}
					</Form.Item>
					<Form.Item
						{...formItemLayout}
						label="权重"
					>
						{
							getFieldDecorator('weight', {
								initialValue: action === 'update' ? curBlock.weight : 0
							})(
								<InputNumber min={0}/>
							)
						}
					</Form.Item>
				</Form>
			</Modal>
		);
	}
}

export default Form.create()(BlockEditModal);