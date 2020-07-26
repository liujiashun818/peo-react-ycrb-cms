import React from 'react';
import {Button, Form, Input, Radio, Select, Upload, Icon, message, Modal, TreeSelect} from 'antd'
import {urlPath} from '../../constants'
import ImgUploader from '../form/imgUploader'
import VisibleWrap from '../ui/visibleWrap'
import LoadingImgModal from './loadingImgModal'
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
const formItemLayout1 = {
    labelCol: {
        span: 6
    },
    wrapperCol: {
        span: 8
    }
}
class FloatEdit extends React.Component{
    constructor(props){
        super(props)
        this.state={
            previewVisible: false,
            previewImage: '',
            type:this.props.data.type?this.props.data.type:"0",
            fileList:[],
            imgUrl:this.props.data.imgUrl?this.props.data.imgUrl:'',
            articleId:this.props.data.articleId?this.props.data.articleId:'',
            articleTitle:this.props.data.articleTitle?this.props.data.articleTitle:'',
            visible:false,
            modalKey: 1
        }
    }
    handleCancel = () => this.setState({ previewVisible: false })
    handleRemove = ()=>{
        const {data} = this.props;
        data.imgUrl = '';
        this.setState({ fileList:[],imgUrl:""})
    }
    handlePreview = (file) => {
        this.setState({
            previewImage: file.url || file.thumbUrl,
            previewVisible: true,
        });
    }
    handleChange = ({ file,fileList }) => {
      
        this.setState({ fileList})
        if(file.response&&file.response.code==0){
            this.setState({ imgUrl:file.response.data.fileUrl})
        } else if(file.response&&file.response.code<0){
            message.error(file.response.msg)
            fileList.splice(0,1)
            this.setState({ fileList})
        } else {
            
        }
    }
    CheckUrl = (str)=>{
        var RegUrl = new RegExp();
      //  RegUrl.compile("^[A-Za-z]+://[A-Za-z0-9-_]+\\.[A-Za-z0-9-_%&\?\/.=]+$");
        RegUrl.compile("^[a-zA-z]+://[^\\s]*$")
        if (!RegUrl.test(str)) {
            return false;
        }
        return true;
    }
    submitForm = ()=>{
        const {handleOk} = this.props;
        const {validateFields,getFieldsValue,resetFields} = this.props.form
        validateFields((errors) => {
            if (errors) {
                return
            }
            const data = {
                ...this.props.data,...getFieldsValue()
            }
            data.imgUrl = this.state.imgUrl;
            data.articleId = this.state.articleId;
            if(data.type==1 && !this.CheckUrl(data.redirectUrl)){
                message.error("请输入正确的url格式");
                return;
            }
            resetFields();
            handleOk(data)
        })
    }
    typeChange=(value)=>{
        let visible = false
        if(value!=2){
            visible = false
        }else{
            visible = true
        }
        let modalKey = this.state.modalKey+1
        this.setState({
            type:value,
            visible,
            modalKey: modalKey
        })
    }
    getLabel(type,data,clientTree){
        const {getFieldDecorator,resetFields} = this.props.form;
        if(type==2){
            return null
        }else if(type=='0'){
            return <FormItem label="栏目" {...formItemLayout1}>
                {getFieldDecorator('categoryId', {
                    initialValue: data.categoryId ? data.categoryId+'':'1'
                })(
                    <TreeSelect
                        allowClear
                        showSearch
                        showCheck
                        treeNodeFilterProp="label"
                        dropdownStyle={{maxHeight: 300, overflow: 'auto'}}
                        treeData={clientTree}
                    />
                )}
            </FormItem>
        }else if (type=='1'){
            return <FormItem label="URL" {...formItemLayout}>
                    {getFieldDecorator('redirectUrl', {
                        initialValue: data.redirectUrl || '',
                        rules: [
                            {
                                required: true,
                                message: 'Url未填写'
                            }
                        ]
                    })(
                        <Input type={'url'}/>)}
                </FormItem>
        }
    }
    checkedHandle = (data)=>{
        this.setState({
            articleId:data.id,
            articleTitle:data.title,
            visible:false
        })
    }
    checkedHandleCancel = ()=>{
        this.setState({
            visible:false
        })
    }
    render(){
        const {getFieldDecorator,resetFields} = this.props.form;
        const { previewVisible, previewImage,type,visible,articleTitle} = this.state;
        const fileList = this.props.data.imgUrl?[{uid: '-1',url:this.props.data.imgUrl}]:this.state.fileList
        const {data,loading,handleCancel,clientTree} = this.props;
        const uploadButton = (
            <div>
                <Icon type="plus" />
                <div className="ant-upload-text">Upload</div>
            </div>
        );
        return (
            <Form>
                <FormItem required={true} label='浮动图名称' {...formItemLayout}>
                    {getFieldDecorator('name', {
                        initialValue: data.name || '',
                        rules: [
                            {
                                validator: (rule, value, callback) => {
                                    if (value.replace(/\s/gi,'').length== 0) {
                                        callback('请输入浮动图名称');
                                        return;
                                    }
                                    callback();
                                }
                            }]
                    })(<Input/>)}
                </FormItem>
                <FormItem  required={true} label="浮动图" {...formItemLayout}>
                    {getFieldDecorator('imgUrl', {
                        initialValue: data.imgUrl || '',
                        rules: [
                            {
                                validator: (rule, value, callback) => {
                                    if (value.replace(/\s/gi,'').length== 0) {
                                        callback('请上传图片');
                                        return;
                                    }
                                    callback();
                                }
                            }]
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
                                {fileList.length >= 1 ? null : uploadButton}
                            </Upload>
                            <Modal visible={previewVisible} footer={null} onCancel={this.handleCancel}>
                                <img alt="example" style={{ width: '100%' }} src={previewImage} />
                            </Modal>
                        </div>
                    )}
                </FormItem>
                <FormItem label="首页是否展示" {...formItemLayout}>
                    {getFieldDecorator('isShow', {
                        initialValue: Number(data.isShow)?'1':'0',
                    })(
                        <Select>
                            <Select.Option key='1' value='1'>是</Select.Option>
                            <Select.Option key='0' value='0'>否</Select.Option>
                        </Select>)}
                </FormItem>
                <FormItem label="跳转方式" {...formItemLayout}>
                    {getFieldDecorator('type', {
                        initialValue: data.type || '0',
                    })(
                        <Select onChange={this.typeChange}>
                            <Select.Option key='0' value='0'>APP内栏目</Select.Option>
                            <Select.Option key='1' value='1'>URL跳转</Select.Option>
                            <Select.Option key='2' value='2'>新闻详情页</Select.Option>
                        </Select>)}
                </FormItem>
                {this.getLabel(type,data,clientTree)}
                {type=='2'?<FormItem label="新闻标题" {...formItemLayout}>
                        <div>{articleTitle}</div>
                </FormItem>:null}
                <LoadingImgModal
                    key={this.state.modalKey}
                    visible={visible}
                    checkedHandle={this.checkedHandle}
                    handleCancel={this.checkedHandleCancel}
                />
                <div style={{borderTop:'1px solid #e8e8e8',paddingTop: '10px',textAlign: 'right',borderRadius: '0 0 4px 4px'}}>
                    <Button key="submit" type="primary" loading={loading} onClick={this.submitForm}>
                        保存
                    </Button>
                    <Button style={{marginLeft:'8px'}} key="back" onClick={()=>{resetFields();handleCancel()}}>返回</Button>
                </div>
            </Form>
        )
    }
}

export default Form.create()(FloatEdit)
