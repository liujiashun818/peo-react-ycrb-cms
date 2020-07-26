import React from 'react'
import PropTypes from 'prop-types';
import { Modal, Popconfirm, Form, Input, Radio, Button } from 'antd';
import ImgUploader from '../form/imgUploader';
import styles from './userEditModal.less';
import VisibleWrap from '../ui/visibleWrap'

const formItemLayout = {
	labelCol: {
		span: 4
	},
	wrapperCol: {
		span: 16
	}
};

class UserEditModal extends React.Component {
	constructor(props) {
		super(props);
	}

	componentWillReceiveProps(nextProps) {
		if(!nextProps.visible) {
			this.reset();
		}
	}

	reset() {
		const {form:{resetFields}} = this.props;
		resetFields();
	}

	onDelete() {
		let {user, onCancel, deleteUser} = this.props;
		deleteUser(user, onCancel);
	}

	onSave() {
		let {user, onCancel, saveUser, form:{validateFields, getFieldsValue}} = this.props;
		validateFields(errors => {
			if(errors) {
				return;
			}
			let data = getFieldsValue();
			if(data.image && data.image.fileList && data.image.fileList.length > 0) {
				data.image = data.image.fileList[0].url;
			}
			else {
				delete data.image;
			}

			if(user.id) {
				data.id = user.id;
			}

			saveUser(data, onCancel);
		});
	}

	render() {
		let {form, user, visible, onCancel} = this.props;
		let {getFieldDecorator} = form;
		let modalProps = {
			visible,
			title: "人员编辑",
			footer: null,
			onCancel
		};
		return (
			<Modal {...modalProps} className={styles.userEditModal}>
				<Form>
					<Form.Item 
						{...formItemLayout}
						label="名称"
					>
						{
							getFieldDecorator('name', {
								initialValue: user.name || '',
								rules: [
									{required: true, message: '请输入姓名'}
								]
							})(
								<Input />
							)
						}
					</Form.Item>
					<Form.Item
						{...formItemLayout}
						label="角色"
					>
						{
							getFieldDecorator('role', {
								initialValue: user.role || 'host'
							})(
								<Radio.Group>
									<Radio value="host">主持人</Radio>
									<Radio value="guest">嘉宾</Radio>
								</Radio.Group>
							)
						}
					</Form.Item>
					<Form.Item
						{...formItemLayout}
						label="头像"
					>
						{
							getFieldDecorator('image', {
								initialValue: {fileList: user.image ? [{name:user.image, url: user.image, uid: -1}] : null}
							})(
								<ImgUploader single={true}/>
							)
						}
					</Form.Item>
					<Form.Item
						{...formItemLayout}
						label="简介"
					>
						{
							getFieldDecorator('description', {
								initialValue: user.description || ''
							})(
								<Input type="textarea" rows={4}/>
							)
						}
					</Form.Item>
					<Form.Item
						wrapperCol={{span:16, offset: 4}}
						className="btn-item"
					>
						<VisibleWrap permis="edit:modify">
							<Button type="primary" onClick={this.onSave.bind(this)}>确认</Button>
						</VisibleWrap>
						{
							user.id 
							?	
							<VisibleWrap permis="edit:delete">
								<Popconfirm title="确定删除吗？" onConfirm={this.onDelete.bind(this)}>
									<Button type="danger">删除</Button>
								</Popconfirm>	
							</VisibleWrap>
							: 	null
						}
						
						<Button onClick={onCancel}>取消</Button>
					</Form.Item>
				</Form>
			</Modal>
		);
	}
}

export default Form.create()(UserEditModal);