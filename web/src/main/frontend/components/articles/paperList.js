import React from 'react';
import { connect } from 'dva';
import {
    message,
    Form,
    Input,
    Button,
    Row,
    Col,
    DatePicker,
    Table,
    Popconfirm,
    Select
} from 'antd';
import { withRouter, Link } from 'dva/router';
import moment from 'moment';
import {Jt} from '../../utils';
import UEditor from '../form/ueditor';
import styles from './paperList.less';
import VisibleWrap from '../ui/visibleWrap';
import {urlPath} from '../../constants';  //从这里引进来接口

const FormItem = Form.Item;
const Option = Select.Option;
const { RangePicker } = DatePicker;

const Jt_V = Jt.validate;
message.config({
    duration: 4.5
}); 

const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 18 }
};

const dateFormat = 'YYYY-MM-DD HH:mm:ss';

class PaperList extends React.Component {
    constructor(props) {
        super(props);
    }   

    goEditPage = (record) => {
        const { createArt } = this.props
        createArt(record.id)
    }

    handleStatusChange = (record) => {
        const { statusChange } = this.props
        statusChange(record, () => {
            message.success('操作成功')
        })
    }

    handleSearch = e => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (err) return console.log(err)
            const { searchPaperList } = this.props
            let { date } = values
            if (date && date.length) {
                values.startTime = moment(date[0]).format('YYYY-MM-DD') + ' 00:00:00'
                values.endTime = moment(date[1]).format('YYYY-MM-DD') + ' 23:59:59'
            } else {
                values.startTime = undefined
                values.endTime = undefined
            }

            if (values.delFlag === 'null') {
                values.delFlag = undefined
            }
        
            console.log('Received values of form: ', values);

            searchPaperList(values)
        });
    };
    
    render () {
        const { form: { getFieldDecorator } } = this.props
        const handleStatusChange = this.handleStatusChange.bind(this)

        const columns = [
            {
              title: 'ID',
              width: 100,
              key: 'docCode',
              dataIndex: 'docCode',
            },
            {
              title: '标题',
              key: 'title',
              dataIndex: 'title',
            },
            {
              title: '日期',
              width: 100,
              key: 'docTimeStr',
              dataIndex: 'docTimeStr',
            },
            {
                title: '版号',
                width: 100,
                key: 'pageNum',
                dataIndex: 'pageNum',
            },
            {
                title: '报纸状态',
                width: 100,
                dataIndex: 'status',
                key: 'status',
                render: (text, record) => {
                    return record.delFlag===1?'已下线':'已上线'
                }
            },
            {
                title: '修改时间',
                width: 150,
                key: 'updateAtStr',
                dataIndex: 'updateAtStr',
            },
            {
                title: '操作',
                width: 100,
                dataIndex: 'action',
                render: (text, record) => {
                    const word = record.delFlag===1?'上线':'下线'
                    const title = `确定要${word}吗？`
                    return (
                        <div>
                            <a onClick={this.goEditPage.bind(this, record)}>编辑</a>
                            <Popconfirm title={title}
						            onConfirm={()=>handleStatusChange(record)}>
                                <a style={{marginLeft: '20px'}}>{record.delFlag===1?'上线':'下线'}</a>
                            </Popconfirm>
                        </div>
                    )
                }
            },
        ];

        const { paperListData , paginationPaper, onPaperPageChange } = this.props
        // console.log('paperListData: ', paperListData)
        console.log(paginationPaper)
        return (
            <div className={styles.wrapper}>
                <Form onSubmit={this.handleSearch}>
                    <Row gutter={24}>
                        <Col span={8}>
                            <FormItem label="ID" {...formItemLayout}>
                                {
                                    getFieldDecorator('docCode')(
                                        <Input />
                                    )
                                }
                            </FormItem>
                        </Col>
                        <Col span={8}>
                            <FormItem label="标题" {...formItemLayout}>
                                {
                                    getFieldDecorator('title')(
                                        <Input />
                                    )
                                }
                            </FormItem>
                        </Col>
                        <Col span={8}>
                            <FormItem label="正文" {...formItemLayout}>
                                {
                                    getFieldDecorator('content')(
                                        <Input />
                                    )
                                }
                            </FormItem>
                        </Col>
                    </Row>
                    <Row gutter={24}>
                        <Col span={8}>
                            <FormItem label="版号" {...formItemLayout}>
                                {
                                    getFieldDecorator('pageNum')(
                                        <Input />
                                    )
                                }
                            </FormItem>
                        </Col>
                        <Col span={8}>
                            <FormItem label="日期" {...formItemLayout}>
                                {
                                    getFieldDecorator('date')(
                                        <RangePicker />
                                    )
                                }
                            </FormItem>
                        </Col>
                        <Col span={5}>
                            <FormItem label="报纸状态" {...{
                                    labelCol: { span: 10 },
                                    wrapperCol: { span: 14 }
                                }}>
                                {
                                    getFieldDecorator('delFlag', {
                                        initialValue: 'null'
                                    })(
                                        <Select style={{ width: 100 }}>
                                            <Option value="null">全部</Option>
                                            <Option value="0">已上线</Option>
                                            <Option value="1">已下线</Option>
                                        </Select>
                                    )
                                }
                            </FormItem>
                        </Col>
                        <Col span={3}>
                            <FormItem>
                                <Button type="primary"  htmlType="submit">查询</Button>
                            </FormItem>
                        </Col>
                    </Row>
                </Form>
                <Table
                    rowKey={record => record.id}
                    columns={columns}
                    dataSource={paperListData.list}
                    bordered
                    onChange={onPaperPageChange}
                    pagination={paginationPaper}
                />
            </div>
        )
    }
}

function mapStateToProps({ article },context) {
    return {
        paperListData: article.paperListData,
        paginationPaper: article.paginationPaper
    }
}

export default connect(mapStateToProps)(Form.create()(PaperList))
