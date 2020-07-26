import React from 'react'
import PropTypes from 'prop-types'
import { Link } from 'dva/router'
import {Table, Popconfirm, Input, Button, Row, Col, Form, Select, DatePicker, TreeSelect, Tree} from 'antd'
import VisibleWrap from '../ui/visibleWrap'
import moment from "moment";
import {Jt} from "../../utils";
const TreeNode = Tree.TreeNode;
const RangePicker = DatePicker.RangePicker;
const FormItem = Form.Item

const formItemLayout = {
    labelCol: {
        span: 8
    },
    wrapperCol: {
        span: 16
    }
}
class SearchBar extends React.Component{
    constructor(props){
        super(props)
    }
    submitSearch(){
        const {validateFields,getFieldsValue} = this.props.form
        const {handleSearch} = this.props
        validateFields((errors) => {
            if (errors) {
                return
            }
            const data = {
                ...getFieldsValue(),
            }

            if(handleSearch){
                handleSearch(data)
            }

        })
    }
    saveOrder(){
        this.props.saveOrder();
    }
    onRecommond(){
        let p = this.props.form.getFieldValue('recmonnd')
        this.props.onRecommond(p);
    }
    loop(data) {
        return data.map((item) => {
            if (item.children && item.children.length) {
                return <TreeNode value={item.id+''} key={item.id+''} title={item.name}>{this.loop(item.children)}</TreeNode>;
            }
            return <TreeNode value={item.id+''} key={item.id} title={item.name} />;
        });
    }
    render(){
        const {typeData=[]} = this.props
        const {getFieldDecorator} = this.props.form
        let catgs  = this.props.recommendList.length>0?Jt.tree.format(this.props.recommendList):[];
        return (
            <Form >
                <Row>
                    <Col span={6}>

                            <FormItem label='问题标题' {...formItemLayout} style={{'display':'inlineBlock'}}>
                                {getFieldDecorator('title', {
                                    initialValue: '',
                                })(<Input />)}
                            </FormItem>
                    </Col>
                    <Col span={6}>
                        <FormItem label='问题内容' {...formItemLayout} style={{'display':'inlineBlock'}}>
                            {getFieldDecorator('questionContent', {
                                initialValue: '',
                            })(<Input />)}
                        </FormItem>
                    </Col>
                    <Col span={6}>

                        <FormItem label='回复内容' {...formItemLayout} style={{'display':'inlineBlock'}}>
                            {getFieldDecorator('replyContent', {
                                initialValue: '',
                            })(<Input />)}
                        </FormItem>
                    </Col>
                    <Col span={6}>

                        <FormItem label='回复机构' {...formItemLayout} style={{'display':'inlineBlock'}}>
                            {getFieldDecorator('organization', {
                                initialValue: '',
                            })(<Input />)}
                        </FormItem>
                        {/*<Button type="primary" onClick={() => this.submitSearch()}>查询</Button>*/}
                    </Col>
                </Row>
                <Row>
                    <Col span={6}>
                        <FormItem label='留言状态' {...formItemLayout} style={{'width':'100%'}}>
                            {getFieldDecorator('status', {
                                initialValue: '',
                            })(<Select>
                                <Select.Option key={1} value="1">已审核</Select.Option>
                                <Select.Option key={2} value={'2'}>处理中</Select.Option>
                                <Select.Option key={3} value={'3'}>已回复</Select.Option>
                                <Select.Option key={4} value={'4'}>审核未通过</Select.Option>
                            </Select>)}
                        </FormItem>
                    </Col>
                    <Col span={6}>
                        <Form.Item {...formItemLayout}
                            label="提问时间"
                        >

                            {
                                getFieldDecorator('publishDate', {
                                    initialValue:''
                                })(<RangePicker
                                    showTime={{ format: 'HH:mm' }}
                                    format="YYYY-MM-DD HH:mm"
                                    placeholder={['开始时间', '结束时间']}
                                />)
                            }
                        </Form.Item>
                    </Col>
                    <Col span={6} offset={4}>
                        <Form.Item {...formItemLayout}
                                   label="推荐到"
                        >
                        {getFieldDecorator('recmonnd', {
                            initialValue: '',
                        })(<TreeSelect
                                showSearch
                                treeNodeFilterProp="label"
                                placeholder=""
                                allowClear
                                style={{'width':'50%'}}
                                dropdownMatchSelectWidth ={false}

                        >

                            {this.loop(catgs)}
                        </TreeSelect>)}
                        &nbsp;
                            <Button htmlType={'button'} onClick={(this.onRecommond.bind(this))} type={'primary'}>推荐到</Button>
                        </Form.Item>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <Form.Item>
                            <Button htmlType={'button'} onClick={(this.submitSearch.bind(this))} type={'primary'}>查询</Button>
                            &nbsp;
                            <Button htmlType={'button'} onClick={(this.saveOrder.bind(this))} type={'primary'}>保存排序</Button>
                        </Form.Item>
                    </Col>
                </Row>
            </Form>
        )
    }
}

export default Form.create()(SearchBar)
