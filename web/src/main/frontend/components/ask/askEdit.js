import React from 'react'
import PropTypes from 'prop-types'
import { Link } from 'dva/router'
import {Input, Button, Form, Select, DatePicker, Upload, Modal, Icon} from 'antd'
import ImgUploader from '../form/imgUploader'
import moment from 'moment'
import {urlPath} from "../../constants";
const RangePicker = DatePicker.RangePicker;
const FormItem = Form.Item
const Option = Select.Option;
let scrollTop = 0;
const formItemLayout = {
    labelCol: {
        span: 6
    },
    wrapperCol: {
        span: 16
    }
}
class AskEdit extends React.Component{
    constructor(props){
        super(props);
        console.log(this.props.currentInfo.replyTime,'props的值');
        this.state={
            previewVisible: false,
            previewImage: '',
            fileList:[],
            imgUrl:this.props.currentInfo.headImage,

            previewVisible1: false,
            previewImage1: '',
            fileList1:[],
            imgUrl1:this.props.currentInfo.shareLogo,


            previewVisible2: false,
            previewImage2: '',
            fileList2:[],
            imgUrl2:this.props.currentInfo.attachment,

            visible:false,
        }
    }
    submitSearch(){
        const {validateFields,getFieldsValue} = this.props.form

        const {save} = this.props;


        // console.log(this.props,'这是保存中的那个值')

        validateFields((errors,values) => {
            console.log(values,'values')
            if (errors) {
                return
            }
            const data = {
                ...getFieldsValue(),
            }
            data.headImage  = this.state.imgUrl;
            data.shareLogo  = this.state.imgUrl1;
            data.attachment  = this.state.imgUrl2;
            for(let i in data) {
                if(!data[i]) {
                    delete data[i];
                }
            }
            
           let data1  = Object.assign({},this.props.currentInfo,data);
            if(save){
                save(data1)
            }

        })
    }
    goback(){
        this.props.goBack();
    }

    handleRemove = ()=>{
        const currentInfo = this.props.currentInfo
        currentInfo.headImage = '';
        this.setState({ fileList:[],imgUrl:""})
    }
    handlePreview = (file) => {
        this.setState({
            previewImage: file.url || file.thumbUrl,
            previewVisible: true,
        });
    }
    handleChange = ({ file,fileList }) => {

        if(file.response&&file.response.code==0){
            this.setState({ imgUrl:file.response.data.fileUrl})
        } else {

        }
        this.setState({ 'fileList':[...fileList]})

    }
    handleCancel = () => this.setState({ previewVisible: false })




    handleRemove1 = ()=>{
        const currentInfo = this.props.currentInfo
        currentInfo.shareLogo = '';
        this.setState({ fileList1:[],imgUrl1:""})
    }
    handlePreview1 = (file) => {
        this.setState({
            previewImage1: file.url || file.thumbUrl,
            previewVisible1: true,
        });
    }
    handleChange1 = ({ file,fileList }) => {
        this.setState({ fileList1:fileList})
        if(file.response&&file.response.code==0){
            this.setState({ imgUrl1:file.response.data.fileUrl})
        }
    }
    handleCancel1 = () => this.setState({ previewVisible1: false })




    handleRemove2 = ()=>{
        const currentInfo = this.props.currentInfo
        currentInfo.attachment = '';
        this.setState({ fileList2:[],imgUrl2:""})
    }
    handlePreview2 = (file) => {
        this.setState({
            previewImage2: file.url || file.thumbUrl,
            previewVisible2: true,
        });
    }
    handleChange2 = ({ file,fileList }) => {
        this.setState({ fileList2:fileList})
        if(file.response&&file.response.code==0){
            this.setState({ imgUrl2:file.response.data.fileUrl})
        }
    }
    handleCancel2 = () => this.setState({ previewVisible2: false })


    render(){
        const {getFieldDecorator} = this.props.form
        const fileList =  this.props.currentInfo&&this.props.currentInfo.headImage?[{uid: '-1',url:this.props.currentInfo.headImage}]:this.state.fileList;
        const fileList1 =  this.props.currentInfo&&this.props.currentInfo.shareLogo?[{uid: '-1',url:this.props.currentInfo.shareLogo}]:this.state.fileList1;
        const fileList2 =  this.props.currentInfo&&this.props.currentInfo.attachment?[{uid: '-1',url:this.props.currentInfo.attachment}]:this.state.fileList2;



        return (
            <Form >
                {getFieldDecorator('id', {
                    initialValue: this.props.id,
                })(
                    <Input type={'hidden'}/>
                )}
                {getFieldDecorator('governmentId', {
                    initialValue: this.props.govId,
                })(
                    <Input type={'hidden'}/>
                )}
                <FormItem required={true} label='留言领域' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('domainId', {
                        initialValue: this.props.currentInfo.domainId+'',
                        rules: [{ required: true, message: '留言领域不能为空' }],
                    })(
                        <Select
                            showSearch
                            placeholder="请选择"
                            optionFilterProp="children"
                            filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                            <Option key={-1} value=''>请选择</Option>
                            {
                                this.props.domainList.map((item,index)=>{
                                    return (
                                        <Option key={item.id} value={item.id+''}>{item.name}</Option>
                                    )
                                })
                            }


                        </Select>
                    )}
                </FormItem>

                <FormItem required={true}  label='留言类型' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('typeId', {
                        initialValue: this.props.currentInfo.typeId+'',
                        rules: [{ required: true, message: '留言类型不能为空' }],
                    })(
                        <Select
                            placeholder="请选择"
                        >
                            <Option key={-1} value=''>请选择</Option>
                            {
                                this.props.typeList.map((item,index)=>{
                                    return (
                                        <Option key={item.id} value={item.id+''}>{item.name}</Option>
                                    )
                                })
                            }
                        </Select>
                    )}
                </FormItem>
                <FormItem label='原始标题' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('originalTitle', {
                        initialValue:this.props.currentInfo.originalTitle,
                    })(
                       <Input readOnly={true} disabled={true} />
                    )}
                </FormItem>
                <FormItem  required={true} label='移动端标题' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('title', {
                        initialValue: this.props.currentInfo.title,
                        rules: [{ required: true, message: '移动端标题不能为空' }],
                    })(
                        <Input/>
                    )}
                </FormItem>


                <FormItem label='正文留言' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('questionContent', {
                        initialValue: this.props.currentInfo.questionContent,
                        rules: [{ required: true, message: '正文留言不能为空' }],
                    })(
                        <Input readOnly={true} disabled={true} type={'textarea'} autosize={false}  rows={20}/>
                    )}
                </FormItem>

                <FormItem label='真实留言姓名' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('userName', {
                        initialValue: this.props.currentInfo.userName,
                    })(
                        <Input readOnly={true}  disabled={true} />
                    )}
                </FormItem>

                <FormItem label='办理机构' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('organization', {
                        initialValue: this.props.currentInfo.organization,
                    })(
                        <Input   />
                    )}
                </FormItem>

                <FormItem label='回复正文' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('replyContent', {
                        initialValue: this.props.currentInfo.replyContent,
                        rules: [
                            {
                                validator: (rule, value, callback) => {
                                    if (this.props.form.getFieldValue('status')=='3'&&( value == undefined || value == null || value.replace(/\s/gi, '').length == 0)){
                                        callback('回复正文');
                                        return false;
                                    }
                                    callback();
                                }
                            }
                        ],
                    })(
                        <Input  type={'textarea'} autosize={false}  rows={10}/>
                    )}
                </FormItem>

                <FormItem label='回复时间' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('replyTime', {
                        initialValue:this.props.currentInfo.replyTime?moment(this.props.currentInfo.replyTime, 'YYYY-MM-DD hh:mm:ss'):''
                    })(
                        <DatePicker
                            showTime={true}
                            placeholder="请选择"
                            format={'YYYY-MM-DD hh:mm:ss'}
                        />
                    )}
                </FormItem>

                <FormItem label='留言状态' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('status', {
                        initialValue:  this.props.currentInfo.status+'',
                    })(
                        <Select
                            placeholder="请选择"
                        >
                            <Option key={'0'} value={'0'}>未审核</Option>
                            <Option key={'1'} value={'1'}>已审核</Option>
                            <Option key={'2'} value={'2'}>处理中</Option>

                            <Option key={'3'} value={'3'}>已回复</Option>
                            <Option key={'4'} value={'4'}>审核未通过</Option>
                        </Select>
                    )}
                </FormItem>

                <FormItem label='真实评论数' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('commentsNum', {
                        initialValue: this.props.currentInfo.commentsNum,
                    })(
                        <Input disabled={true}  readOnly={true} />
                    )}
                </FormItem>

                <FormItem label='评论显示数' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('falsecommentsNum', {
                        initialValue: this.props.currentInfo.falsecommentsNum,
                    })(
                        <Input  />
                    )}
                </FormItem>

                <FormItem label='真实点赞数' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('zanNum', {
                        initialValue: this.props.currentInfo.zanNum,
                    })(
                        <Input readOnly={true}  disabled={true} />
                    )}
                </FormItem>
                <FormItem label='点赞显示数' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('falseZanNum', {
                        initialValue:this.props.currentInfo.falseZanNum,
                    })(
                        <Input  />
                    )}
                </FormItem>
                <FormItem
                    label="头图"
                    {...formItemLayout}
                >
                    {
                        getFieldDecorator('headImage', {
                            initialValue:{fileList: this.props.currentInfo.headImage ? [{fileList:this.props.currentInfo.headImage, url: this.props.currentInfo.headImage, uid: -1}] : null}
                        })(

                        <div className="clearfix">
                            <Upload
                            action={urlPath.UPLOAD_FILE}
                            listType="picture-card"
                            fileList={fileList}
                            onPreview={this.handlePreview}
                            onChange={this.handleChange}
                            onRemove={this.handleRemove}
                            >
                            {fileList.length >= 1 ? null :  <div>
                                <Icon type="plus"/>
                                <div className="ant-upload-text">{this.props.tip || '上传'}</div>
                            </div>}
                            </Upload>
                            <Modal visible={this.state.previewVisible} footer={null} onCancel={this.handleCancel}>
                            <img alt="example" style={{ width: '100%' }} src={this.state.previewImage} />
                            </Modal>
                        </div>
                        )
                    }
                </FormItem>
                <FormItem
                    label="分享图标"
                    {...formItemLayout}
                >
                    {
                        getFieldDecorator('shareLogo', {
                            initialValue:{fileList: this.props.currentInfo.shareLogo ? [{fileList:this.props.currentInfo.shareLogo, url: this.props.currentInfo.shareLogo, uid: -1}] : null}
                        })(
                            <div className="clearfix">
                                <Upload
                                    action={urlPath.UPLOAD_FILE}
                                    listType="picture-card"
                                    fileList={fileList1}
                                    onPreview={this.handlePreview1}
                                    onChange={this.handleChange1}
                                    onRemove={this.handleRemove1}
                                >
                                    {fileList1.length >= 1 ? null :  <div>
                                        <Icon type="plus"/>
                                        <div className="ant-upload-text">{this.props.tip || '上传'}</div>
                                    </div>}
                                </Upload>
                                <Modal visible={this.state.previewVisible1} footer={null} onCancel={this.handleCancel1}>
                                    <img alt="example" style={{ width: '100%' }} src={this.state.previewImage1} />
                                </Modal>
                            </div>
                        )
                    }
                </FormItem>
                <FormItem
                    label="用户提问附件"
                    {...formItemLayout}
                >
                    {
                        getFieldDecorator('attachment', {
                            initialValue:{fileList: this.props.currentInfo.attachment ? [{fileList:this.props.currentInfo.attachment, url: this.props.currentInfo.attachment, uid: -1}] : null}
                        })(
                            <div className="clearfix">
                                <Upload
                                    action={urlPath.UPLOAD_FILE}
                                    listType="picture-card"
                                    fileList={fileList2}
                                    onPreview={this.handlePreview2}
                                    onChange={this.handleChange2}
                                    onRemove={this.handleRemove2}
                                >
                                    {fileList2.length >= 1 ? null :  <div>
                                        <Icon type="plus"/>
                                        <div className="ant-upload-text">{this.props.tip || '上传'}</div>
                                    </div>}
                                </Upload>
                                <Modal visible={this.state.previewVisible2} footer={null} onCancel={this.handleCancel2}>
                                    <img alt="example" style={{ width: '100%' }} src={this.state.previewImage2} />
                                </Modal>
                            </div>
                        )
                    }
                </FormItem>
                <FormItem label='提问人真实姓名' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('realUserName', {
                        initialValue: this.props.currentInfo.realUserName,
                    })(
                        <Input readOnly={true} disabled={true} />
                    )}
                </FormItem>

                <FormItem label='提问人联系手机号' {...formItemLayout} style={{'display':'inlineBlock'}}>
                    {getFieldDecorator('userPhone', {
                        initialValue: this.props.currentInfo.userPhone,
                    })(
                        <Input readOnly={true} disabled={true} />
                    )}
                </FormItem>

                <FormItem colon={false}  label=' ' {...formItemLayout}>
                    <Button onClick={this.submitSearch.bind(this)} type={'primary'}>保存</Button>
                    &nbsp;
                    <Button onClick={this.goback.bind(this)} >取消</Button>
                </FormItem>
            </Form>
        )
    }
}

export default Form.create()(AskEdit)
