import React from 'react'
import PropTypes from 'prop-types';
import moment from 'moment';
import {
    Form,
    Button,
    Row,
    Col,
    Radio,
    Input,
    TreeSelect,
    Select,
    DatePicker
} from 'antd';
import {
    Jt
} from '../../utils';
import {
    blocks,
    blocks2,
    delFlags
} from '../../constants'
import styles from './search.less';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const RadioButton = Radio.Button;
const InputGroup = Input.Group;
const Option = Select.Option;
const RangePicker = DatePicker.RangePicker;

const dateFormat = 'YYYY-MM-DD HH:mm:ss';

const layout = {
    labelCol: {
        span: 6
    },
    wrapperCol: {
        span: 16
    }
};

const artTypes = [
    {value: 'live', label: '直播'},
    {value: 'subject', label: '专题'}
];

class Search extends React.Component {
    constructor(props) {
        super(props);
    }

    getParam = (name)=>{
        let param = null;
        location.hash.split('?')[1].split("&").forEach((item,i)=>{
            if(item.indexOf(name)>-1){
                param = item.split("=")[1]
            }
        })
        return param
    }
    getQuery() {
        const {getdata,listType} =  this.props;
        const data = this.props.form.getFieldsValue();
        data.categoryId = this.getParam('categoryId')?this.getParam('categoryId'):sessionStorage.CID
        if(!Jt.array.isEmpty(data.publishDate)) {
            if(listType==5){
                data.fixedBeginTime = data.publishDate[0].format(dateFormat);
                data.fixedEndTime = data.publishDate[1].format(dateFormat);
            } else {
                data.beginTime = data.publishDate[0].format(dateFormat);
                data.endTime = data.publishDate[1].format(dateFormat);
            }
           
        }
        delete data.publishDate;
        if(!Jt.array.isEmpty(data.deleteDate)) {
            data.deleteBeginTime = data.deleteDate[0].format(dateFormat);
            data.deleteEndTime = data.deleteDate[1].format(dateFormat);
        }
        delete data.deleteDate;
        if(data.keyword) {
            data[data.kwType] = data.keyword;
        }
        const type = data.type;
        if(type) {
            if(type === 'live' || type === 'subject') {
                data.sysCode = type;
                delete data.type;
            }
            else if(type === 'all') {
                delete data.type;
            }
        }

        for(let i in data) {
            if(!data[i]) {
                delete data[i];
            }
        }
        getdata(data);
        return data;
    }

    searchHandle() {
        this.props.onSearch(this.getQuery());
    }
    resetFields () {
        this.props.form.resetFields()
    }
    resetHandle() {
        const {onSearch, form:{setFieldsValue,getFieldsValue},getdata, query} = this.props;
        setFieldsValue({
            'categoryId': query.categoryId,
            'block': "2",
          // 'delFlag': "0",
            'type': 'all',
            'kwType': 'title',
            'keyword': undefined,
            'publishDate': undefined
        });
        getdata({
            'categoryId': query.categoryId,
            'block': "2",
           // 'delFlag': "0",
            'type': undefined,
            'kwType': 'title',
            'keyword': undefined,
            'publishDate': undefined
        });
        onSearch({kwType: 'title', block: '2', delFlag: query.delFlag, categoryId:query.categoryId});
    }

    catgChg(value, label) {
        const data = this.getQuery();
        data.categoryId = value;
        this.props.onSearch(data);
    }

    blockChg(e) {
        const data = this.getQuery();
        console.log(data)
        data.block = e.target.value;
        this.props.onSearch(data);
    }

    delFlagChg(e) {
        const data = this.getQuery();
        data.delFlag = e.target.value;
        this.props.onSearch(data);
    }

    typeChg(value) {
        const data = this.getQuery();
        if(value === 'live' || value === 'subject') {
            data.sysCode = value;
            delete data.type;
        }
        else if(value === 'all') {
            delete data.type;
            delete data.sysCode;
        }
        else {
            data.type = value;
            delete data.sysCode;
        }
        this.props.onSearch(data);
    }

    getArtTypes() {
        const types = [
            {value: 'all', label: '全部'},
            ...this.props.artTypes,
            ...artTypes
        ];
        return (
            <Select onChange={this.typeChg.bind(this)}>
            {
                types.map((item, index) => {
                    return <Option key={index} value={item.value}>{item.label}</Option>
                })
            }
            </Select>
        );
    }

    getKwPrefix() {
        const {query: {kwType}, form: {getFieldDecorator}} = this.props;
        return getFieldDecorator('kwType', {
            initialValue: kwType || 'title'
        })(
            <Select size="large" className="kw-prefix">
                <Option value="title">标题</Option>
                <Option value="authors">作者</Option>
                <Option value="content">正文</Option>
            </Select>
        )
    }
    componentDidMount () {
        this.getQuery();
        window.$searchThis = this
    }
    render() {
        const {
            catgs,
            query,
            listType,
            form: {
                getFieldDecorator
            }
        } = this.props;
        let modelId = this.props.modelId
        return (
            <div>
                {
                    <Form className={styles.form}>
                        <Row>
                            <Col span={8}>
                                <FormItem wrapperCol={{offset: layout.labelCol.span}}>
                                {
                                    getFieldDecorator('block', {
                                        initialValue: query.block || '2'
                                    })(
                                        <RadioGroup onChange={this.blockChg.bind(this)}>
                                            {
                                                  modelId == '6'|| modelId == '8'?
                                                  blocks2.map((item, index) => {
                                                      return <RadioButton key={index} value={item.value}>{item.label}</RadioButton>
                                                  })
                                                  :
                                                blocks.map((item, index) => {
                                                    return <RadioButton key={index} value={item.value}>{item.label}</RadioButton>
                                                })
                                            }
                                        </RadioGroup>
                                    )
                                }
                                </FormItem>
                            </Col>
                            <Col span={12}>
                                <FormItem wrapperCol={{offset: layout.labelCol.span}}>
                                {
                                    getFieldDecorator('delFlag', {
                                        initialValue: query.delFlag || '0'
                                    })(
                                        <RadioGroup onChange={this.delFlagChg.bind(this)}>
                                        {
                                            delFlags.map((item, index) => {
                                                return <RadioButton key={index} value={item.value}>{item.label}</RadioButton>
                                            })
                                        }
                                        </RadioGroup>
                                    )
                                }
                                </FormItem>
                            </Col>
                        </Row>
                        <Row>
                            <Col span={7}>
                                <FormItem label="类型" {...layout}>
                                {
                                    getFieldDecorator('type', {
                                        initialValue: query.type || query.sysCode || 'all'
                                    })(this.getArtTypes())
                                }
                                </FormItem>
                            </Col>
                            <Col span={7}>
                                <FormItem label="关键词" {...layout}>
                                {
                                    getFieldDecorator('keyword', {
                                        initialValue: query[query.kwType] || ''
                                    })(
                                        <Input size="large" addonBefore={this.getKwPrefix()}/>
                                    )
                                }
                                </FormItem>
                            </Col>
                            <Col span={8}>
                            {
                                listType==5?
                                <FormItem label="定时发布时间" {...layout}>
                                {
                                    getFieldDecorator('publishDate', {
                                        initialValue: query.beginTime ? [moment(query.beginTime, dateFormat), moment(query.endTimes)] : null
                                    })(<RangePicker showTime format={dateFormat}/>)
                                }
                                </FormItem>
                                :
                                <FormItem label="发布时间" {...layout}>
                                {
                                    getFieldDecorator('publishDate', {
                                        initialValue: query.beginTime ? [moment(query.beginTime, dateFormat), moment(query.endTimes)] : null
                                    })(<RangePicker showTime format={dateFormat}/>)
                                }
                                </FormItem>
                            }
                                
                            </Col>
                            
                           
                        </Row>
                        <Row>
                        {
                                listType==3?
                                <Col span={8}>
                                <FormItem label="删除时间" {...layout}>
                                {
                                    getFieldDecorator('deleteDate', {
                                        initialValue: query.deleteBeginTime ? [moment(query.deleteBeginTime, dateFormat), moment(query.deleteEndTime)] : null
                                    })(<RangePicker showTime format={dateFormat}/>)
                                }
                                </FormItem>
                                </Col>
                                :''
                            }
                        </Row>
                        <FormItem className="btn-item">
                            <Button type="primary" onClick={this.searchHandle.bind(this)}>搜索</Button>
                            <Button className="clear-btn" onClick={this.resetHandle.bind(this)}>清除</Button>
                        </FormItem>
                    </Form>
                }
            </div>
        );
    }
}

export default Form.create()(Search);
