import React from 'react'
import PropTypes from 'prop-types';
import { Form, Input, Radio, Modal, Col,Row,Button, Select, TreeSelect,DatePicker } from 'antd';
import moment from 'moment';
import { Link } from 'dva/router';

const FormItem = Form.Item
const RangePicker = DatePicker.RangePicker;
const dateFormat = 'YYYY-MM-DD';
const formItemLayout = {
    labelCol: {
        span: 6
    },
    wrapperCol: {
        span: 7
    }
}
function onChangeTime(dates, dateStrings) {
}

const ActivityCodeEdit = ({
                              list,
                              item,
                              createActivity,
                              updateActivity,
                              backToList,
                              form: {
                                  getFieldDecorator,
                                  validateFields,
                                  getFieldsValue
                              }
                          }) => {
    function submitForm(){
        validateFields((errors) => {
            if (errors) {
                return
            }
            const data = {
                ...getFieldsValue(),
            }
            if(data.activityCodeTime && data.activityCodeTime.length !== 0) {
                data.beginTime = data.activityCodeTime[0].format(dateFormat);
                data.endTime = data.activityCodeTime[1].format(dateFormat);
            }
            delete data.activityCodeTime;
            createActivity(data)
        })
    }
    return (
        <Form>
            <FormItem label='标题' hasFeedback {...formItemLayout}>
                {getFieldDecorator('title', {
                    initialValue: item.title,
                    rules: [
                        {
                            required: true,
                            message: '标题未填写'
                        }
                    ]
                })(<Input />)}
            </FormItem>
            <FormItem label='摘要' hasFeedback {...formItemLayout}>
                {getFieldDecorator('digest', {
                    initialValue: item.digest,
                })(<Input type="textarea" rows={5}/>)}
            </FormItem>
            <FormItem label='邀请码前缀：'  {...formItemLayout}>
                {getFieldDecorator('prefix', {
                    initialValue: item.prefix,
                    rules: [
                        {
                            required: true,
                            message: '邀请码前缀格式错误，请输入两位小写字母',
                            pattern: /^[a-z]{2}$/g
                        }
                    ]
                })(<Input placeholder="请输入两位小写字母"  maxLength="2"/>)}
            </FormItem>
            <FormItem label='有效期时间范围：'  {...formItemLayout}>
                {getFieldDecorator('activityCodeTime', {
                    initialValue: item.beginTime ? [moment(item.beginTime, dateFormat), moment(item.endTimes)] : null,
                    rules: [
                        {
                            required: true,
                            message: '有效期未填写'
                        }
                    ]
                })(<RangePicker  onChangeTime={onChangeTime}
                />)}
            </FormItem>

            <FormItem label='计数上限' hasFeedback {...formItemLayout}>
                {getFieldDecorator('maxNumber', {
                    initialValue: item.maxNumber,
                    rules: [
                        {
                            required: true,
                            message: '计数上限未填写'
                        }
                    ]
                })(<Input />)}
            </FormItem>
            <FormItem label='生成邀请码数量' hasFeedback {...formItemLayout}>
                {getFieldDecorator('codeNumber', {
                    initialValue: item.codeNumber,
                    rules: [
                        {
                            required: true,
                            message: '邀请码数量未填写'
                        }
                    ]
                })(<Input />)}
            </FormItem>
            <FormItem wrapperCol={{span: 6, offset: 8}}>
                <Button type="primary" size="large"  onClick={submitForm}>创建</Button>
                <Button style={{ marginLeft: 8 }} size="large">
                    <a onClick={backToList}>返回</a>
                </Button>
            </FormItem>
        </Form>
    )
}



export default Form.create()(ActivityCodeEdit)
