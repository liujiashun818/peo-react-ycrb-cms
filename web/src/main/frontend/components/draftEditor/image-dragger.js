import React, {Component} from 'react';
import {Modal, Upload, Icon, Form, Input, Row, Col} from 'antd';
import {urlPath, IMG_UPLOAD_TYEPS} from '../../constants';
import styles from './image-dragger.less';
import classnames from 'classnames';

const Dragger = Upload.Dragger;
const FormItem = Form.Item;

class ImageDragger extends Component {
    constructor(props){
        super(props);
        this.state = {
            mv:false,
            batchList:[],
            fileList:[]
        }
    }

    hideM(){
        this.setState({
            mv:false
        })
    }

    showM(){
        this.setState({
            mv:true
        })
    }

    batchAdd(){
        const {form:{getFieldsValue}, onChange} = this.props;
        const values = {...getFieldsValue()};
        const me = this;
        values.batchList&&values.batchList.map((item,index)=>{
            setTimeout(function(){
                onChange(item.imgSrc, item.height, item.width, "img");
            })
        })
        this.setState({
            mv:false,
        })
        setTimeout(function(){
            me.setState({
                batchList:[],
                fileList:[]
            })
        })
    }

    deleteItem(index){
        const {batchList,fileList} = this.state;
        batchList.splice(index,1)
        fileList.splice(index,1)
        this.setState({
            batchList,
            fileList
        })
    }

    renderBatchList(){
        const {batchList} = this.state;
        const {form: {getFieldDecorator} } = this.props;
        const formItemLayout = {
            labelCol: {
                span: 8
            },
            wrapperCol: {
                span: 16
            }
        };
        return (
            batchList.map((item,index)=>{
                return (
                    <Row key={index} gutter={6} align="middle" type="flex">
                        <Col span={15}>
                            <FormItem style={{'margin':'0'}} >
                                {getFieldDecorator(`batchList[${index}].imgSrc`, {
                                    initialValue: item.imgSrc
                                })(
                                    <p style={{'wordBreak':'break-all','lineHeight': '16px'}}>
                                        <Icon type="paper-clip" style={{'paddingRight':'5px'}}/>
                                        {item.name}
                                    </p>
                                )}
                            </FormItem>
                        </Col>
                        <Col span={4}>
                            <FormItem style={{'margin':'0'}} label="高" {...formItemLayout}>
                                {getFieldDecorator(`batchList[${index}].height`, {
                                    initialValue: item.height
                                })(
                                    <Input placeholder="Height" size="small"/>
                                )}
                            </FormItem>
                        </Col>
                        <Col span={4}>
                            <FormItem style={{'margin':'0'}} label="宽" {...formItemLayout}>
                                {getFieldDecorator(`batchList[${index}].width`, {
                                    initialValue: item.width
                                })(
                                    <Input placeholder="Width" size="small"/>
                                )}
                            </FormItem>
                        </Col>
                        <Col span={1}>
                            <a>
                                <Icon
                                    type="close"
                                    onClick={this.deleteItem.bind(this,index)}
                                    style={{'display': 'block','lineHeight':'1px'}}
                                />
                            </a>
                        </Col>
                    </Row>
                )
            })
        )
    }

    handleBatchUploadChange({fileList}) {
		const batchList = [];
		fileList.forEach(file => {
			if(file.status === 'done') {
				if(file.response && file.response.code == -1) {
					message.error(file.response.msg);
				}
				else {
                    const obj = {
                        name:file.name,
                        imgSrc:file.response.data.fileUrl,
                        height: this.props.config.defaultSize.height,
                        width: this.props.config.defaultSize.width,
                    }
					batchList.push(obj);
				}

			}
        });
        this.setState({
            batchList,
            fileList
        })
    }

    handleRemove(arg){
    }

    render(){
        const {
            config: { icon, className, title },
            expanded,
            onExpandEvent,
            translations
        } = this.props;
        const {mv, batchList} = this.state;

        const modalProps = {
			visible: mv,
			title: '批量添加',
			onCancel: this.hideM.bind(this),
			onOk: this.batchAdd.bind(this)
        };
        return (
            <div
                className="rdw-image-wrapper"
            >
                <div className="rdw-option-wrapper" onClick={this.showM.bind(this)} title={title || translations['components.controls.image.image']}>
                    <img src={icon} alt=""/>
                </div>
                <Modal {...modalProps} className="batch-upload-modal">
                    <div className={styles.drag_wrap}>
                        <Dragger
                            accept="image/*"
                            multiple={true}
                            showUploadList={false}
                            action={urlPath.UPLOAD_FILE}
                            onChange={this.handleBatchUploadChange.bind(this)}
                            fileList={this.state.fileList}
                        >
                            <p className="ant-upload-drag-icon">
                                <Icon type="inbox"/>
                            </p>
                        </Dragger>
                        <Form style={{'maxHeight':'300px','overflowY':'auto','overflowX':'hidden'}}>
                            {this.renderBatchList()}
                        </Form>
                    </div>
                </Modal>
            </div>
        )
    }
}

export default Form.create()(ImageDragger);
