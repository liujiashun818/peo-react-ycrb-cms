import React from 'react'
import PropTypes from 'prop-types';
import { Modal, Form, Input } from 'antd';
import { routerPath } from '../../constants';
import VisibleWrap from '../ui/visibleWrap'

const formItemLayout = {
    labelCol: {
        span: 4
    },
    wrapperCol: {
        span: 16
    }
};

const ReplyModal =({
    currentItem={},
    visible,
    replyOk,
    onCancel,
    form: {
       getFieldDecorator,
       validateFields,
        getFieldsValue
    }
}) => {
    function handleOk() {
        validateFields((errors) => {
            if (errors) {
                return
            }
            const data = {
                        ...getFieldsValue(),
                        id: currentItem.id
                    }
            replyOk(data);
        })
    }

    const  modalProps = {
        key:currentItem.id,
        onOk:handleOk,
        visible,
        width: '50%',
        title: '管理员回复',
        onCancel,
        okText:"保存",
        cancelText:"返回",
        wrapClassName: 'vertical-center-modal'
    }
    return (
            <Modal {...modalProps}>
                <Form>
                    <Form.Item
                        {...formItemLayout}
                        label="管理员回复内容"
                    >
                        {
                            getFieldDecorator('adminReply', {
                                initialValue: currentItem.adminReply || ''
                            })(
                                <Input type="textarea" rows={5} placeholder="管理员回复内容"/>
                            )
                        }
                    </Form.Item>
                </Form>
            </Modal>
        );

}

ReplyModal.propTypes = {
  visible: PropTypes.any,
  form: PropTypes.object,
  currentItem: PropTypes.object,
  replyOk: PropTypes.func,
  onCancel: PropTypes.func
}

export default Form.create()(ReplyModal);
