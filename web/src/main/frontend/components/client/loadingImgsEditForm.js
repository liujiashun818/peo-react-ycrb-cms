import React from 'react';
import { connect } from 'dva';
import { Button, Form, Input, Col, Row, Radio, Select, Upload, Icon, Modal,message } from 'antd'
import { Link } from 'dva/router'
import ImgUploader from '../form/imgUploader'
import VisibleWrap from '../ui/visibleWrap'
import LoadingImgModal from './loadingImgModal'
import { getClientUploadState } from '../../modules/cms/services/article';

/*import {Select} from "antd/lib/select";*/
//测试上传视频
import { UPLOAD_FILE, urlPath } from '../../constants';
import styles from "../form/mediaUploader.less";
import MediaPlayer from '../form/mediaPlayer'
import {
    Jt
} from '../../utils';

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
			previewVisible: false,
			previewImage: '',
			previewVideo: '',
			previewUrl: '',
			fileList: this.props.currentItem.imgUrl ? [{ uid: 1, url: this.props.currentItem.imgUrl }] : [],
			fileList2: this.props.currentItem.videoUrl ? [{key:this.props.currentItem.videoUrl, uid: 2, url: this.props.currentItem.videoUrl, name: this.props.currentItem.videoUrl }] : [],
			fileList3: this.props.currentItem.videoCoverUrl ? [{ uid: 1, url: this.props.currentItem.videoCoverUrl }] : [],
		}
	}
	handleCancel = () => this.setState({ previewVisible: false })
	handlePreview = (file) => {
		this.setState({
			previewImage: file.url || file.thumbUrl,
			previewVisible: true,
		});
	}
	handleChange = (files) => {
		const { form, updateState, currentItem } = this.props;
		form.setFieldsValue({
			imgUrl: files.fileList,
		});
		updateState({
			currentItem:{
				...currentItem,
				videoCoverUrl: files.fileList[0].url,
			}
		})
		// const {updateState,currentItem}=this.props
		// let fileList = info.fileList;
		// fileList = fileList.slice(-1);
		// fileList = fileList.map((file,index) => {
			
		// 	if (file.response&&file.response.code === 0) {
			
		// 		file.url = file.response.data.fileUrl;
        //         updateState({
        //             currentItem:{
        //                 ...currentItem,videoCoverUrl: file.url
        //             }
		// 		})
		// 		file.key  = index
		// 		return true
		// 	}else if (file.response&&file.response.code === -2) {
		// 		message.error(file.response.msg)
		// 		return false
		// 	}else if (file.response&&file.response.code === -1) {
		// 		message.error(file.response.msg)
		// 		return false
		// 	} else {
		// 		return true
		// 	}
			
		// });
		// this.setState({ fileList3: fileList });
	}
	handleChange2 = (info) => {
        const {updateState,currentItem}=this.props
		let fileList = info.fileList;
		fileList = fileList.slice(-1);
		fileList = (fileList?fileList:[]).filter((file,index) => {
			if (file.response && file.response.code === 0) {
				file.url = file.response.data[0].fileUrl;
				updateState({
                    currentItem:{
                        ...currentItem,videoUrl: file.url
                    }
				})
				file.key = index
				this.getVideoUrl(file.url);
				return true;
			}else if (file.response &&file.response.code === -2) {
				message.error(file.response.msg);
				return false;
			}else if (file.response &&file.response.code === -1) {
				message.error(file.response.msg)
				return false;
			} else {
				return true
			}
			
		});
		
		this.setState({ fileList2: fileList });
	}
	 /* 预览视频url处理 */
	 async getVideoUrl(url) {
        const { updateState, currentItem } = this.props
        //传入当前关注状态
        if(!!url) {
            const res = await getClientUploadState(url)
			
            if (res && res.code === 0 && res.data.status) {
                this.setState({
                 previewUrl: res.data.hdUrlList[0]
                })
                // console.log('tag', this.state.previewUrl)
                // updateState({
                //     currentItem: {
                //         ...currentItem, videoUrl: res.data.hdUrlList[0]
                //     }
                // })
            }
        }
	}
	
	 async checkVideoUrl(url) {
        const { updateState, currentItem } = this.props
        //传入当前关注状态
        if(!!url) {
            const res = await getClientUploadState(url)
			
            if (res && res.code === 0 && res.data.status) {
                this.setState({
                 previewUrl: res.data.hdUrlList[0]
				})
				return res.data.hdUrlList[0]
                // console.log('tag', this.state.previewUrl)
                // updateState({
                //     currentItem: {
                //         ...currentItem, videoUrl: res.data.hdUrlList[0]
                //     }
                // })
            } else{
				message.error("视频转码尚未完成，请稍后");
				return false
			}
        } else{
			return false
		}
	}
	componentDidMount() {
        if(this.state.fileList2 && this.state.fileList2[0] && this.state.fileList2[0].url) {
            this.getVideoUrl(this.state.fileList2[0].url)
        }
	}
	componentWillUnmount(){
        //重写组件的setState方法，直接返回空
        this.setState = (state,callback)=>{
          return;
        };
    }
	toThumbnail() {
		let imageUrl = this.props.currentItem.imgUrl
		if (!imageUrl) {
			return {}
		}
		let urls = imageUrl.split(',')
		let fileList = []
		for (let i = 0, len = urls.length; i < len; i++) {
			fileList.push({
				uid: -(i + 1),
				name: urls[i],
				url: urls[i],
				key:i
			})
		}
		return { fileList }
	}
	toThumbnail2() {
		let imageUrl = this.props.currentItem.videoCoverUrl
		if (!imageUrl) {
			return {}
		}
		let urls = imageUrl.split(',')
		let fileList = []
		for (let i = 0, len = urls.length; i < len; i++) {
			fileList.push({
				uid: -(i + 1),
				name: urls[i],
				url: urls[i],
				key:i
			})
		}
		return { fileList }
	}
	imageOnChange = (files) => {
		const { form, updateState, currentItem } = this.props;
		form.setFieldsValue({
			imgUrl: files.fileList,
		});
		updateState({
			currentItem:{
				...currentItem,
				imgUrl: files.fileList[0].url,
			}
		})
	}
	attrChange(e, attr) {
		const { updateState, currentItem } = this.props;
		const { validateFields, getFieldsValue } = this.props.form
		const data = {...getFieldsValue()}

		if(attr === 'name') {
			updateState({
				currentItem:{
					...currentItem,
					name: e.target.value,
				}
			})
		}else if(attr === "width") {
			updateState({
				currentItem:{
					...currentItem,
					width: e.target.value,
				}
			})
		} else if(attr === "height") {
			updateState({
				currentItem:{
					...currentItem,
					height: e.target.value,
				}
			})
		}
	}
	selectChage = (value)=>{
		const { updateState } = this.props;
        updateState({
			selectType: value
        })
	}
	checkedHandle = (data) => {
		const {updateState,currentItem}=this.props
		updateState({
            currentItem:{
                ...currentItem,
                articleTitle: data.title,
                articleId: data.id
            },
            checkVisible:false
		})
	}
    close = ()=>{
        const {updateState}=this.props
        updateState({
            checkVisible:false
        })
    }
    CheckUrl = (str)=>{
	    if(!str) return true
        var RegUrl = new RegExp();
        RegUrl.compile("^[A-Za-z]+://[A-Za-z0-9-_]+\\.[A-Za-z0-9-_%&\?\/.=]+$");
        if (!RegUrl.test(str)) {
            return false;
        }
        return true;
    }
    clickLinkType(e) {
        let { updateLinkType,handleSubmit, currentItem,num,checkVisible } = this.props
        const { getFieldsValue } = this.props.form;
        num++;
        let showInput=false,checkVisibleFlag = false;
        if (e.target.value == '1') {
            showInput=true;
            checkVisibleFlag = true;
        } else {
            showInput=false;
            checkVisibleFlag = false;
        }
		const data = {...getFieldsValue()}
        if (data.type == 'image') {
            let imgUrl = ''
            if (!!data.imgUrl) {
                if (!!data.imgUrl.fileList && data.imgUrl.fileList.length > 0) {
                    imgUrl = data.imgUrl.fileList[0].url
                }
            }
            data.imgUrl = imgUrl
            data.videoCoverUrl = ""
            data.videoUrl = ""
        } else if (data.type == 'video') {
            data.imgUrl = ''
            data.videoCoverUrl = currentItem.videoCoverUrl;
            data.videoUrl = currentItem.videoUrl;
        }
        data.id = currentItem.id?currentItem.id:"";
        data.linkType = e.target.value
        data.showInput = showInput;
        data.num = num;
        data.checkVisible = checkVisibleFlag;
        updateLinkType(data)
    }
     submitForm() {
        const { handleSubmit,currentItem} = this.props
        const { validateFields, getFieldsValue } = this.props.form
        validateFields(async(errors) => {
		
            if (errors) {
                return
            }
            const data = {
                ...getFieldsValue(),
			}
			console.log("-----------",getFieldsValue())
		//	return 
            if (data.type == 'image') {
                let imgUrl = ''
                if (!!data.imgUrl) {
                    if (!!data.imgUrl.fileList && data.imgUrl.fileList.length > 0) {
                        imgUrl = data.imgUrl.fileList[0].url
                    }
                }
                data.imgUrl = imgUrl
                data.videoCoverUrl = ""
                data.videoUrl = ""
            } else if (data.type == 'video') {
                data.imgUrl = ''
                data.videoCoverUrl = currentItem.videoCoverUrl
                data.videoUrl = currentItem.videoUrl
            }
            if(data.linkType !='1'){
                if(!this.CheckUrl(data.linkValue)){
                    message.error("请输入正确的url格式")
                    return
                }
            }
            data.articleId = currentItem.articleId
			data.articleTitle = currentItem.articleTitle
			if(data.type == 'video'){
				let r = await this.checkVideoUrl(this.state.fileList2[0].url)
				if(r){
					handleSubmit(data)
				}
			} else {
				handleSubmit(data)
			}
           
        })
    }
	render() {
		const { currentItem, goBack, showInput = false ,selectType,checkVisible} = this.props
		const { getFieldDecorator } = this.props.form
		console.log("this.props.currentItem.videoCoverUrl",this.props.currentItem.videoCoverUrl)
		//测试传值
		let typeInit = 'image'
		let coverImg = ''
		const { previewVisible, previewImage, videoUrl, fileList } = this.state;
		const uploadButton = (
			<div>
				<Icon type="plus" />
				<div className="ant-upload-text">Upload</div>
			</div>
		);

		const props = {
			action: urlPath.UPLOAD_FILE,
			onChange({ file, fileList }) {
				if (file.status !== 'uploading') {
				}
			}
		};
		const props2 = {
			action: urlPath.UPLOAD_FILES,
			    onChange: this.handleChange2
		};
		const mediaPlayerProps = {
			type: 'video',
			url: 'http://medias-source.oss-cn-beijing.aliyuncs.com/test/mp4Files/20181010/cdrb_2_20150352b92859-3f9a-41aa-a6aa-3137835584fe.mp4'
		}

		
		return (
			<Form>
				<FormItem required={true} label='名称' {...formItemLayout}>
					{getFieldDecorator('name', {
						initialValue: currentItem.name || '',
                        rules: [
                            {required:true,message:'请输入名称'}
                            ]
					})(<Input onBlur={value => this.attrChange(value, 'name') } />)
				}
				</FormItem>
				<FormItem label="类型" {...formItemLayout}>
					{getFieldDecorator('type', {
						initialValue: selectType,
					})(
						<Select onChange={this.selectChage}>
							<Select.Option key='image' value='image'>图片</Select.Option>
							<Select.Option key='video' value='video'>视频</Select.Option>
						</Select>)}
				</FormItem>
				{selectType == 'image' ?
					<FormItem required={true} label="图片" {...formItemLayout}>
						{
							getFieldDecorator('imgUrl', {
								initialValue: { ...this.toThumbnail() || undefined},
                                rules: [
                                    {
                                        validator: (rule, value, callback) => {
										
                                            if(selectType == 'image'){
                                                if (Object.keys(value).length== 0) {
                                                    callback('请上传图片');
                                                    return;
                                                } else {
                                                    callback();
                                                }
                                            }

                                            callback();
                                        }
                                    }]
							})(
								<ImgUploader onChange={this.imageOnChange} previewFlag={true} single={true} />
							)
						}
					</FormItem>
					:
					<div>
						<FormItem required={true} label="视频" {...formItemLayout}>
							{
								getFieldDecorator('videoUrl', {
                                    initialValue: this.props.currentItem.videoUrl,
                                    rules: [
                                        {required:selectType == 'video',message:'请上传视频'}
                                        // {
                                        //     validator: (rule, value, callback) => {
                                        //         if(selectType == 'video'){
                                        //             if (!value) {
                                        //                 callback('请上传视频');
                                        //                 return;
                                        //             } else {
                                        //                 callback();
                                        //             }
                                        //         }
                                        //
                                        //         callback();
                                        //     }
                                        // }]
                                    ]
								})(
								    <div>
										<Upload action={urlPath.UPLOAD_FILES}
                                onChange={this.handleChange2}

                                fileList={this.state.fileList2}>
											<Button>
												<Icon type="upload" /> Upload
									        </Button>
										</Upload>  
                                    <MediaPlayer nourl={()=>{this.checkVideoUrl(this.state.fileList2[0].url)}} url={this.state.previewUrl} /></div>


								)}

						</FormItem>
						<FormItem label="视频封面" {...formItemLayout}>
							{
								getFieldDecorator('videoCoverUrl', {
									initialValue:{ ...this.toThumbnail2() || undefined},
								})(
									<ImgUploader onChange={this.handleChange} previewFlag={true} single={true} />
									// <div>
									// 	<Upload
									// 		action={urlPath.UPLOAD_FILE}
									// 		listType="picture-card"
									// 		onPreview={this.handlePreview}
									// 		onChange={this.handleChange}
									// 		fileList={this.state.fileList3}
									// 	>
									// 		{this.state.fileList3.length >= 1 ? null : uploadButton}
									// 	</Upload>
									// 	<Modal visible={previewVisible} footer={null} onCancel={this.handleCancel}>
									// 		<img alt="example" style={{ width: '100%' }} src={previewImage} />
									// 	</Modal>
									// </div>
								)
							}
						</FormItem>
					</div>
				}
				<Row>
					<Col span={7} offset={4}>
						<FormItem label='分辨率' {...{ labelCol: { span: 7 }, wrapperCol: { span: 17 } }}>
							{getFieldDecorator('width', {
								initialValue: currentItem.width || '',
							})(<Input placeholder="宽" onBlur={value => this.attrChange(value,'width') } />)}
						</FormItem>
					</Col>
					<Col span={1}>
						<p style={{ 'textAlign': 'center', 'lineHeight': '30px' }}>*</p>
					</Col>
					<Col span={7}>
						<FormItem {...{ wrapperCol: { span: 17 } }}>
							{getFieldDecorator('height', {
								initialValue: currentItem.height || '',
							})(<Input placeholder="高" onBlur={value => this.attrChange(value, 'height') } />)}
						</FormItem>
					</Col>
				</Row>
				<FormItem
					{...{ labelCol: { span: 6 }, wrapperCol: { span: 17 } }}
					label="链接类型"
				>
					{getFieldDecorator('linkType', { initialValue: currentItem.linkType || '0' })(
						<RadioGroup onChange={(e) => this.clickLinkType(e)}>
							<Radio value='0'>空</Radio>
							<Radio value='1'>应用内部新闻ID</Radio>
							<Radio value='2'>触发外部浏览器wap地址</Radio>
							<Radio value='3'>内置浏览器wap地址</Radio>
						</RadioGroup>
					)}
				</FormItem>
                <LoadingImgModal
                    visible={checkVisible}
                    checkedHandle={this.checkedHandle}
                    handleCancel={this.close}
                />
				{!showInput ?
					<FormItem label='链接值' {...formItemLayout}>
						{getFieldDecorator('linkValue', {
							initialValue: currentItem.linkValue || '',
						})(<Input />)}
					</FormItem>:null
				}
				{Jt.string.isEmpty(currentItem.articleTitle)&&!showInput || currentItem.linkType!=1 ? null : (
					<Row>
						<Col span={6} ><p style={{textAlign: "right", height: "30px"}}>新闻标题：</p></Col>
						<Col span={6} ><span>{currentItem.articleTitle}</span></Col>
					</Row>
				)
				}

				<FormItem label='显示时长' {...formItemLayout}>
					{getFieldDecorator('showtimes', {
						initialValue: currentItem.showtimes || '',
					})(<Input style={{ 'width': '70%' }} />)}
					<span className="ant-form-text" style={{ 'marginLeft': '10px' }}>秒</span>
				</FormItem>
				<FormItem {...{ labelCol: { span: 6 }, wrapperCol: { span: 10 } }} style={{ textAlign: 'right' }}>
					<VisibleWrap permis="edit:modify">
						{/* <Button type="primary" style={{ marginLeft: 8 }} size="large" onClick={() => this.submitForm()}>保存</Button> */}
						<Button type="primary" style={{ marginRight: '10px' }} size="large" onClick={() => this.submitForm()}>保存</Button>
					</VisibleWrap>
					<Button size="large" onClick={goBack}>
						返回
			        </Button>
				</FormItem>
			</Form>
		)
	}
}

export default Form.create()(LoadingImgsEditForm)
