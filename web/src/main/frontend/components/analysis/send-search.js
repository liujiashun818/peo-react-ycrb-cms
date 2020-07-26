import React from 'react'
import PropTypes from 'prop-types'
import moment from 'moment';
import {Input, Button, Row, Col, Form, Select, Checkbox, DatePicker} from 'antd'

const FormItem = Form.Item;
const Option = Select.Option;
const RangePicker = DatePicker.RangePicker;

const layout = {
    labelCol: {
        span: 6
    },
    wrapperCol: {
        span: 16
    }
};

const dateFormat = 'YYYY-MM-DD';

function Search({query,searchHandle,form: {
                        getFieldDecorator,
                        getFieldsValue
                    }
                }) {
    let {beginTime,endTime,userName} = query;

    function onSearch() {
        const values = getFieldsValue();
        if(values.date && values.date.length !== 0) {
            values.beginTime = values.date[0].format(dateFormat);
            values.endTime = values.date[1].format(dateFormat);
        }
        delete values.date;
        for(let i in values) {
            if(!values[i]) {
                delete values[i];
            }
        }
        searchHandle(values);
    }

    return (
        <Form>
            <Row>
                <Col span={5}>
                    <FormItem label="用户名" {...layout}>
                        {
                            getFieldDecorator('userName', {
                                initialValue: userName,
                            })(
                                <Input/>
                            )
                        }
                    </FormItem>
                </Col>

                <Col span={6}>
                    <FormItem label="起止时间" {...layout}>
                        {
                            getFieldDecorator('date', {
                                initialValue: beginTime ? [moment(beginTime, dateFormat), moment(endTime, dateFormat)]:null
                            })(
                                <RangePicker format={dateFormat}/>
                            )
                        }
                    </FormItem>
                </Col>
                <Col span={4}>
                    <FormItem>
                        <Button type="primary" onClick={onSearch}>查询</Button>
                    </FormItem>
                </Col>
            </Row>
        </Form>
    );
}

export default Form.create()(Search);
