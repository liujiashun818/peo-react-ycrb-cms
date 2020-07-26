import React from 'react'
import PropTypes from 'prop-types'
// import { Form, Input, InputNumber, Radio, Modal } from 'antd'
import { Form, Input, Button, Radio, Modal } from 'antd'
import InputStat from '../form/inputStat'
const FormItem = Form.Item

const formItemLayout = {
    labelCol: {
        span: 4
    },
    wrapperCol: {
        span: 18
    }
}

const PushModal = ({
    visible,
    curArt={},
    onOk,
    onCancel,
    form: {
        getFieldDecorator,
        validateFields,
        getFieldsValue
    }
}) => {
    function handleOk () {
        validateFields((errors) => {
            if (errors) {
                return
            }
            Modal.confirm({
                content: '确认推送？',
                onOk: () => {
                    const data = {
                        ...getFieldsValue(),
                        articleId: curArt.id
                    }
                    onOk(data)
                }
            })
        })
    }

    const modalOpts = {
        key: curArt.id,
        title: '推送',
        visible,
        width: 600,
        onOk: handleOk,
        onCancel,
        footer:[
            <Button key='1' type="primary" style={{ marginRight: '10px' }} size="large" onClick={handleOk}>保存</Button>,
            <Button key='0' size="large" onClick={onCancel}>返回</Button>
        ],
        wrapClassName: 'vertical-center-modal'
    }

    return (
        <Modal {...modalOpts}>
            <Form horizontal>
                <FormItem
                    label="注意"
                    {...formItemLayout}
                >
                    <span>为了能够顺利推送，请限制标题+内容的总字数小于68个。</span>
                </FormItem>
                <FormItem
                    label='推送平台'
                    {...formItemLayout}
                >
                    {
                        getFieldDecorator('platform', {
                            initialValue: '0'
                        })(
                            <Radio.Group>
                                <Radio value="0">全部</Radio>
                                <Radio value="1">IOS</Radio>
                                <Radio value="2">Android</Radio>
                            </Radio.Group>
                        )
                    }
                </FormItem>
                <FormItem
                    label="标题"
                    hasFeedback
                    {...formItemLayout}
                >
                    {
                        getFieldDecorator('title', {
                            initialValue: curArt.title,
                            rules: [
                                { required: true, message: '请输入标题（为了APP端更好的显示效果，请结合具体文章将标题控制在8-22个字符内）'},
                                { validator: (rule, value, callback)=>{
                                        if (value.replace(/\s/gi,'').length == 0) {
                                            callback('请输入标题（为了APP端更好的显示效果，请结合具体文章将标题控制在8-22个字符内）');
                                            return;
                                        }
                                        callback();
                                    } }
                            ]
                        })(
                            <InputStat type="text"/>
                        )
                    }
                </FormItem>
                <FormItem
                    label="内容"
                    hasFeedback
                    {...formItemLayout}
                >
                    {
                        getFieldDecorator('description', {
                            initialValue: '',
                            rules: [
                               { required: true, message: '请输入内容'},
                                { validator: (rule, value, callback)=>{
                                        if (value.replace(/\s/gi,'').length == 0) {
                                            callback('请输入内容');
                                            return;
                                        }
                                        callback();
                                    } }
                            ]
                        })(
                            <InputStat type="textarea" />
                        )
                    }
                </FormItem>
            </Form>
        </Modal>
    )
}

PushModal.propTypes = {
  visible: PropTypes.any,
  form: PropTypes.object,
  curArt: PropTypes.object,
  onOk: PropTypes.func,
  onCancel: PropTypes.func
}

export default Form.create()(PushModal)
