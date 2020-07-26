import React from 'react'
import PropTypes from 'prop-types';
import { Modal, Popconfirm, Form, Input, InputNumber, Radio, Button,Select } from 'antd';
import { Link } from 'dva/router';
import { routerPath } from '../../constants';
import ImgUploader from '../form/imgUploader';
import VisibleWrap from '../ui/visibleWrap'
import FSelect from "../form/f-select";

const formItemLayout = {
	labelCol: {
		span: 4
	},
	wrapperCol: {
		span: 16
	}
};

class EditArtModal extends React.Component {
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

	toThumbnail(imageUrl='') {
		const fileList = [];
		if(imageUrl) {
			const images = imageUrl.split(',');
			images.forEach((image, index) => {
				fileList.push({
					name: image,
					url: image,
					uid: index
				});
			});
		}
		return {fileList};
	}

	fromThumbnail(fileList=[]) {
		const urls = []
		for(let i = 0, len = fileList.length; i < len; i++) {
			urls.push(fileList[i].url)
		}
		return urls.join(',');
	}

	getEnterLink(record, source) {
		console.log("ree",record)
		const type = record.type;
		let isReference = record.isReference
		if(type === 'live') {
			return {pathname: routerPath.LIVE_EDIT, query: {id: record.articleId, action: 'edit', source}};
		}
		else if(type === 'subject') {
			return {pathname: routerPath.TOPIC_EDIT, query: {id: record.articleId, action: 'update', source}};
		}
		else {
			return {pathname: routerPath.ARTICLE_EDIT, query: {id: record.articleId, action: 'edit', source,isReference}};
		}
	}

	onOk() {
		const {art, onSave, form:{validateFields}} = this.props;
		validateFields((errors, values) => {
			if(errors) {
				return;
			}
			values.imageUrl = this.fromThumbnail(values.thumbnail.fileList);
			delete values.thumbnail;
			onSave({...art, ...values});
		});
	}

	render() {
		const {art, visible, source, onCancel, onHide, form:{getFieldDecorator}} = this.props;
		const modalProps = {
			visible,
			width: '50%',
			title: '文章编辑',
			onCancel,
			footer: [
				<VisibleWrap permis="edit:modify" key="ok">
					<Button size="large" type="primary" onClick={this.onOk.bind(this)} style={{'marginRight':'10px'}}>保存</Button>
				</VisibleWrap>,
				<Button key="cancel" size="large" onClick={onCancel}>返回</Button>
			]
		};

		return (
			<Modal {...modalProps}>
				<Form>
					<Form.Item
						{...formItemLayout}
						label="列表标题"
					>
						{
							getFieldDecorator('listTitle', {
								initialValue: art.listTitle || ''
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
								initialValue: art.weight
							})(
								<InputNumber min={0}/>
							)
						}
					</Form.Item>
					<Form.Item
						label="缩略图"
						{...formItemLayout}
					>
						{
							getFieldDecorator('thumbnail', {
								initialValue: this.toThumbnail(art.imageUrl)
							})(
								<ImgUploader />
							)
						}
					</Form.Item>
					<Form.Item
						wrapperCol={{span: formItemLayout.wrapperCol.span, offset: formItemLayout.labelCol.span}}
					>
					<VisibleWrap permis="view">
						<Link to={this.getEnterLink(art, source)} onClick={onHide}>进入原文</Link>
					</VisibleWrap>
					</Form.Item>
				</Form>
			</Modal>
		);
	}
}
export default Form.create()(EditArtModal);
