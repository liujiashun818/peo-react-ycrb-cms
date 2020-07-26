import React from 'react';
import { connect } from 'dva';
import { Button, Form, Input, Col, Row, Radio, Select, Upload, Icon, Modal, message } from 'antd'
import { Link } from 'dva/router'
import ImgUploader from '../form/imgUploader'
import VisibleWrap from '../ui/visibleWrap'
import { getClientUploadState } from '../../modules/cms/services/article';

/*import {Select} from "antd/lib/select";*/
//测试上传视频
import { UPLOAD_FILE, urlPath } from '../../constants';
import styles from "../form/mediaUploader.less";
import MediaPlayer from '../form/mediaPlayer'
import {
	Jt
} from '../../utils';

const { TextArea } = Input;
const FormItem = Form.Item
const RadioGroup = Radio.Group
const formItemLayout = {
	labelCol: {
		span: 6
	},
	wrapperCol: {
		span: 6
	}
}
class LoadingImgsEditForm extends React.Component {
	constructor(props) {
		super(props)

		this.state = {
			previewImage: '',
	        previewVisible: false,
		}
	}


	componentDidMount() {

	}
	componentWillUnmount() {

	}
	handlePreview = (file) => {
    	this.setState({
	      previewImage: file.url || file.thumbUrl,
	      previewVisible: true,
	    });
	}
	handleCancel = () => this.setState({ previewVisible: false })
	render() {
		const { newslistDetail, goBack, form: {getFieldDecorator} } = this.props;
		const {previewVisible, previewImage} = this.state;
		//测试传值

		var fileList = [];
		if (newslistDetail.medias) {
			newslistDetail.medias.split(',').forEach((item, index) => {
				fileList.push({url: item, uid: index, name: item, status: 'done'})
			});
		}

		return (
			<Form>
				<FormItem label='标题' {...formItemLayout}>
					{getFieldDecorator('title', {
						initialValue: newslistDetail.title || '',
					})(<Input />)
					}
				</FormItem>

				<FormItem label='内容' {...formItemLayout}>
					{getFieldDecorator('content', {
						initialValue: newslistDetail.content || '',
					})(<TextArea rows={4} />)
					}
				</FormItem>

				<FormItem label='图片' {...formItemLayout}>
					{getFieldDecorator('medias', {
						initialValue: newslistDetail.img || '',
					})(
						<div className="clearfix">
					        <Upload
					          listType="picture-card"
					          fileList={fileList}
					          onPreview={this.handlePreview}
					          onChange={this.handleChange}
					        >
					        </Upload>
					        <Modal visible={previewVisible} footer={null} onCancel={this.handleCancel}>
					          <img alt="example" style={{ width: '100%' }} src={previewImage} />
					        </Modal>
					    </div>
					)
					}
				</FormItem>

				<FormItem label='联系人' {...formItemLayout}>
					{getFieldDecorator('name', {
						initialValue: newslistDetail.name || '',
					})(<Input />)
					}
				</FormItem>
				<FormItem label='联系方式' {...formItemLayout}>
					{getFieldDecorator('contactInfo', {
						initialValue: newslistDetail.contactInfo || '',
					})(<Input />)
					}
				</FormItem>
				<FormItem label='IP' {...formItemLayout}>
 					{getFieldDecorator('IP', {
 						initialValue: newslistDetail.ip || '',
 					})(<Input />)
 					}
 				</FormItem>
 				<FormItem label='地理位置' {...formItemLayout}>
 					{getFieldDecorator('address', {
 						initialValue: newslistDetail.address || '',
 					})(<Input />)
 					}
 				</FormItem>
				{/* <FormItem label='备注' {...formItemLayout}>

					{getFieldDecorator('remarks', {
						initialValue: newslistDetail.remarks || '',
					})(<TextArea rows={4} />)
					}
				</FormItem> */}
				<FormItem {...{ labelCol: { span: 6 }, wrapperCol: { span: 10 } }} style={{ textAlign: 'right' }}>
					<Button size="large" onClick={goBack}>
						返回
			        </Button>
				</FormItem>
			</Form>
		)
	}
}

export default Form.create()(LoadingImgsEditForm)
