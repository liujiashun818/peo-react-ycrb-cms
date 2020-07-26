import React from 'react';
import {Button, Form, Input, Radio, Select, Upload, Icon, message, Modal, TreeSelect} from 'antd'
import VisibleWrap from '../ui/visibleWrap'

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
        span: 16
    }
}
class LabelEdit extends React.Component{
    constructor(props){
        super(props)
    }
    submitForm = ()=>{
        const {handleOk} = this.props;
        const {validateFields,getFieldsValue} = this.props.form
        validateFields((errors) => {
            if (errors) {
                return
            }
            const data = {
                ...this.props.data,...getFieldsValue()
            }
            if(data.name.indexOf(" ")>-1){
                message.error('标签名不能含空格')
                return
            }
            handleOk(data)
        })
    }
    render(){
        const {getFieldDecorator,resetFields} = this.props.form;
        const {data,handleCancel} = this.props;
        return (
            <Form>
                <FormItem label='标签名称' {...formItemLayout1}>
                    {getFieldDecorator('name', {
                        initialValue: data.name || '',
                        rules: [
                            {
                                required: true,
                                message: '标签名称未填写'
                            }
                        ]
                    })(<Input style={{width:"300px"}} placeholder="为了APP端更好的显示效果，请您输入2-8个字符" />)}
                </FormItem>

                <FormItem label="备注" {...formItemLayout}>
                    {getFieldDecorator('description', {
                        initialValue: data.description || '',
                    })(
                        <textarea style={{resize:"none",width:"300px"}} rows="4" ></textarea>)}
                </FormItem>
                <div style={{borderTop:'1px solid #e8e8e8',paddingTop: '10px',textAlign: 'right',borderRadius: '0 0 4px 4px'}}>
                    <Button key="submit" type="primary" onClick={this.submitForm}>
                        保存
                    </Button>
                    <Button style={{marginLeft:'8px'}} key="back" onClick={()=>{resetFields();handleCancel()}}>返回</Button>
                </div>
            </Form>
        )
    }
}

export default Form.create()(LabelEdit)
