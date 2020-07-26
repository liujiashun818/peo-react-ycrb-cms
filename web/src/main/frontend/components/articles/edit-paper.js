import React from 'react';
import { connect } from 'dva';
import {
    message,
    Form,
    Input,
    Button,
    Row,
    Col,
    Icon,
    Upload,
    Modal
} from 'antd';
import moment from 'moment';
import {Jt} from '../../utils';
import UEditor from '../form/ueditor';
import styles from './edit-paper.less';
import VisibleWrap from '../ui/visibleWrap';
import {urlPath} from '../../constants';  //从这里引进来接口

const FormItem = Form.Item;
const confirm = Modal.confirm;

const Jt_V = Jt.validate;
message.config({
    duration: 4.5
}); 

const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 18 }
};

const dateFormat = 'YYYY-MM-DD HH:mm:ss';

class EditPaper extends React.Component {
    constructor(props) {
        super(props);
       
        this.state = {
            dataList: []
        }
    }   

    componentWillReceiveProps (nextProps) {
       if (nextProps.data.attachmentList) {
            const dataList = nextProps.data.attachmentList
            dataList.map((item, index) => {
                item.key = index
            })
            this.setState({ dataList })
        }
    }

    // componentWillUnmount () {
    //     const { dispatch } = this.props
    //     dispatch({
    //         type: 'reducer:update',
    //         payload: {
    //             paperData: {}
    //         }
    //     })
    // }

    EditChange(html) {
        const { updateState, data, form: { setFieldsValue } } = this.props
        
        if (!html) html = 'null'

        setFieldsValue({
            content: { html }
        })

        updateState({
            paperData:{
                ...data,
                content: { html }
            }
        })
    }

    showDeleteConfirm (file) {
        const _this = this
        confirm({
          title: '提示',
          content: '图片删除后将在APP端无法显示，请谨慎操作！',
          okText: '确定',
          okType: 'danger',
          cancelText: '取消',
          onOk() {
            const { dataList } =  _this.state
            dataList.map((item, index) => {
                if (item.key === file.key) {
                    dataList.splice(index, 1);
                }
            })

            _this.setState({
                dataList
            })
          },
          onCancel() {
            console.log('Cancel');
          }
        })
    }

    textContentChange = (editer) => {      
        this.uediterNodes = editer
        this.uediterNodes.addListener('contentChange', () => {
            this.EditChange(this.uediterNodes.getContent())
        })
    }

    handleInputChange = (e, key) => {
        const attachmentname = e.target.value;
		const dataList = this.state.dataList;
		dataList.map(item => {
			if(item.key == key) {
				item.attachmentname = attachmentname;
			}
        })

        this.setState({ dataList })
    }

    handleUploadChange = (info, key) => {
        let dataList = this.state.dataList;
		if(info.file.status === 'done') {
            console.log(info, key)
			if(info.file.response && info.file.response.code == 0 && info.file.response.data) {
				const file = info.file.response.data.fileUrl;
				dataList.map(item => {
					if(item.key == key) {
						item.attachmenturl = file;
					}
				})
            }
            this.setState({ dataList });
		}
    }

    goBack = () => {
        history.back()
    }

    handleSubmit = (e) => {
        e.preventDefault()
        this.props.form.validateFields((err, values) => {
            if (!err) {
                const { data, dispatch } = this.props
                values.attachmentList = this.state.dataList
                values.id = data.id
                values.mobileTitle = values.title
                values.content = values.content.html
                
                console.log('Received values of form: ', values)
                // dispatch({
                //     type: 'reducer:update',
                //     payload: {
                //         paperData: {}
                //     }
                // })

                this.props.updatePaper({
                    params: values,
                    success: () => {
                        message.success('保存成功')
                        // UE.getEditor('ueditorPaper').destroy();
                        // history.back()
                        window.location.href = `/cms.html#/cms/articles/list?categoryId=${sessionStorage.catId}&kwType=title`
                    }
                })
            }
        });
    }
    
    render () {
        const { form: { getFieldDecorator }, data} = this.props
        console.log('data::',data)
        return (
            <div className={styles.wrapper}>
                <Form onSubmit={this.handleSubmit}>
                    <Row>
                        <Col span={10}>
                            <FormItem label="标题" {...formItemLayout}>
                                {
                                    getFieldDecorator('title', {
                                        initialValue: data.title || ''
                                    })(
                                        <Input placeholder="请输入标题" />
                                    )
                                }
                            </FormItem>
                        </Col>
                        <Col span={10}>
                            <FormItem label="作者" {...formItemLayout}>
                                {
                                    getFieldDecorator('editor', {
                                        initialValue: data.editor || ''
                                    })(
                                        <Input />
                                    )
                                }
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={10}>
                            <FormItem label="肩标题1" {...formItemLayout}>
                                {
                                    getFieldDecorator('introtitle', {
                                        initialValue: data.introtitle || ''
                                    })(
                                        <Input />
                                    )
                                }
                            </FormItem>
                        </Col>
                        <Col span={10}>
                            <FormItem label="副标题1" {...formItemLayout}>
                                {
                                    getFieldDecorator('subTitle', {
                                        initialValue: data.subTitle || ''
                                    })(
                                        <Input />
                                    )
                                }
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={10}>
                            <FormItem label="肩标题2" {...formItemLayout}>
                                {
                                    getFieldDecorator('introtitle1', {
                                        initialValue: data.introtitle1 || ''
                                    })(
                                        <Input />
                                    )
                                }
                            </FormItem>
                        </Col>
                        <Col span={10}>
                            <FormItem label="副标题2" {...formItemLayout}>
                                {
                                    getFieldDecorator('subTitle1', {
                                        initialValue: data.subTitle1 || ''
                                    })(
                                        <Input />
                                    )
                                }
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={10}>
                            <FormItem label="肩标题3" {...formItemLayout}>
                                {
                                    getFieldDecorator('introtitle2', {
                                        initialValue: data.introtitle2 || ''
                                    })(
                                        <Input />
                                    )
                                }
                            </FormItem>
                        </Col>
                        <Col span={10}>
                            <FormItem label="副标题3" {...formItemLayout}>
                                {
                                    getFieldDecorator('subTitle2', {
                                        initialValue: data.subTitle2 || ''
                                    })(
                                        <Input />
                                    )
                                }
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={10}>
                            <FormItem label="上版日期" {...formItemLayout}>
                                {
                                    getFieldDecorator('docTimeStr', {
                                        initialValue: data.docTimeStr || ''
                                    })(
                                        <div>
                                        {data.docTimeStr}
                                        </div>
                                    )
                                }
                            </FormItem>
                        </Col>
                        <Col span={10}>
                            <FormItem label="版号" {...formItemLayout}>
                                {
                                    getFieldDecorator('pageNum', {
                                        initialValue: data.pageNum || ''
                                    })(
                                        <div>
                                        {data.pageNum}
                                        </div>
                                    )
                                }
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <FormItem>
                            {
                                data.content && getFieldDecorator('content', {
                                    initialValue: {
                                        html: data.content || '' ,
                                        innerChange: false
                                    }
                                })(<UEditor
                                    _id="ueditorPaper"
                                    name="content"
                                    afterInit={this.textContentChange}
                                    afterInitw={(editer) => { this.editerNode = editer; }}
                                    initialContent={data.content || ''}
                                    width={'100%'}
                                    height={600}
                                  />)
                            }
                        </FormItem>
                    </Row>
                    {
                        this.state.dataList.length?
                        <Row>
                            <Col>
                                <div className={styles.header}>
                                    正文页以外的图片
                                </div>
                                <div className={styles.picList}>
                                    {
                                        this.state.dataList.map((file, index) => {
                                            const imageUrl = file.attachmenturl;
                                            return (
                                                <div key={file.key} className="album-item">
                                                    <Row type="flex" justify="space-between" align="top">
                                                        <Col span={4}>
                                                            <Upload
                                                                className="album-uploader"
                                                                name="file"
                                                                showUploadList={false}
                                                                action={urlPath.UPLOAD_FILE}
                                                                onChange={info => this.handleUploadChange(info, file.key)}
                                                            >
                                                            {
                                                                imageUrl
                                                                ? <div className="album-image-wrap"><img src={imageUrl} className="album-img"/></div>
                                                                : <Icon type="plus" className="album-uploader-trigger"/>
                                                            }
                                                            </Upload>
                                                        </Col>
                                
                                                        <Col span={16}>
                                                            <Input 
                                                                type="textarea" 
                                                                rows={5} 
                                                                value={file.attachmentname}
                                                                onChange={(e) => this.handleInputChange(e, file.key)}
                                                            />
                                                        </Col>

                                                        <Col span={2}>
                                                            <Icon style={{marginTop:'40px'}} type="delete" className={styles.deleteBtn} onClick={this.showDeleteConfirm.bind(this, file)} />
                                                        </Col>
                                                    </Row>
                                                </div>
                                            );
                                        })
                                    }
                                </div>
                            </Col>
                        </Row>
                        :''
                    }
                    <Row type="flex" justify="center">
                        <Col>
                            <FormItem>
                                <Button htmlType="submit" type="primary">保存</Button>
                                <Button style={{marginLeft: '30px'}} onClick={this.goBack}>取消</Button>
                            </FormItem>
                        </Col>
                    </Row>
                </Form>
            </div>
        )
    }
}

function mapStateToProps({ article ,cms},context) {
    return {
        artTags: cms.artTags
    }
}

export default connect(mapStateToProps)(Form.create()(EditPaper))