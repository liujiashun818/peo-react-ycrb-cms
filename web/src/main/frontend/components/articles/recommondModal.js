import { Modal, Button, Form, DatePicker, Input, Layout, Menu, Icon, Row, Col, Table, Tag ,message } from 'antd';
import { connect } from 'dva';
import { queryCatgs2 } from '../../modules/cms/services/category';
import { queryArts2 } from '../../modules/cms/services/article';
import { Jt } from '../../utils';
import Tree from '../articles/tree';
import {artTypes as types} from '../../constants';
import styles from './list.less';
const dateFormat = 'YYYY-MM-DD HH:mm:ss'

const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const FormItem = Form.Item;
const { Header, Content, Footer, Sider } = Layout;
const { SubMenu } = Menu;



const layout_r = {
    labelCol: {
        span: 6
    },
    wrapperCol: {
        span: 18
    }
}

class RecommondModal extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            loading: false,
            query: {
                total: 0,
                pageNumber: 1,
                pageSize: 10,
                kwType: 'title',
                beginTime: '',
                endTime: '',
                title: '',
                categoryId: "1",
                delFlag: "0"
            },
            selectedRowKeys:this.props.selectedRowKeys?this.props.selectedRowKeys:[],
            selectRows:this.props.selectRows?this.props.selectRows:[],
            getCatgsList: [],
            artsData: [],
            previewModalVisible:false,
        }
        this.onPageChange = this.onPageChange.bind(this)
        this.onSizeChange = this.onSizeChange.bind(this)
        this.onDateChange = this.onDateChange.bind(this)
        this.onTreeSelect = this.onTreeSelect.bind(this)
        this.searchHandle = this.searchHandle.bind(this)
        this.getColumns = this.getColumns.bind(this)
    }
    componentWillReceiveProps(nextProps){
        this.setState({'selectedRowKeys':nextProps.selectedRowKeys})
    }
    preview(record){
        let {dispatch}  = this.props
        dispatch({type: 'article/effect:show:preivewModal',payload:{id:record.articleId}})
    }
    getColumns(){
        const {checkedHandle}=this.props
        return [
            {
                title: '标题',
                dataIndex: 'title',
                key: 'title',
                render: (text,record) => <a onClick={()=>this.preview(record)} href="javascript:;">{text}</a>,
            }, {
                title: '类型',
                dataIndex: 'type',
                key: 'type',
                render: (text, record) => {
                    if (!text) {
                        return '';
                    }
                    let tags = [text];
                    if (record.isReference) {
                        tags.push('cite');
                    }
                    return (
                        <div className="tag-td">
                            {
                                tags.map((tag, index) => {
                                    if (types[tag]) {
                                        return <Tag key={index} color={types[tag].color}>{types[tag].label}</Tag>
                                    } else {
                                        return <Tag key={index}>{tag}</Tag>
                                    }
                                })
                            }
                        </div>
                    );
                }

            }, {
                title: '发布时间',
                dataIndex: 'publishDate',
                key: 'publishDate',
            }, 
            // {
            //     title: '操作',
            //     key: 'action',
            //     render: record => {
            //         return (<span>
            //             <a onClick={()=>{checkedHandle(record)}}>选用</a>
            //         </span>
            //         )},
            // }
        ]
    }
    getQuery() {
        const data = this.props.form.getFieldsValue();
        if(!Jt.array.isEmpty(data.publishDate)) {
            data.beginTime = data.publishDate[0].format(dateFormat);
            data.endTime = data.publishDate[1].format(dateFormat);
        }
        delete data.publishDate;
        for(let i in data) {
            if(!data[i]) {
                delete data[i];
            }
        }
        return data;
    }

    showModal = () => {
        this.setState({
            visible: true,
        });
    }
    searchHandle(data) {
        let query = Object.assign(this.state.query, {
            beginTime: this.getQuery().beginTime,
            endTime: this.getQuery().endTime,
            title:  this.getQuery().title,
            pageNumber: 1,
            categoryId: data.categoryId !== undefined ? data.categoryId[0] : this.state.query.categoryId
        })
        this.setState({ query: query }, function () {
            this.getArts()
        })
    }

    resetHandle(data) {
        const {form:{setFieldsValue}} = this.props;
        setFieldsValue({
            'title': undefined,
            'publishDate': undefined
        });
        this.searchHandle(data)
    }
    async getCatgs() {
        if(this.props.visible) {
            //传入当前关注状态
            const res = await queryCatgs2()
            if (res && res.code === 0) {
                const catgs = Jt.tree.format(res.data);
                this.setState({ getCatgsList: catgs });
            }
        }
    }

    async getArts() {
        if(this.props.visible) {
            //传入当前关注状态
            const res = await queryArts2({
                pageNumber: this.state.query.pageNumber,
                pageSize: this.state.query.pageSize,
                beginTime: this.state.query.beginTime,
                endTime: this.state.query.endTime,
                title: this.state.query.title,
                categoryId: this.state.query.categoryId,
                delFlag: this.state.query.delFlag
            })
            if (res && res.code === 0) {
                this.setState({
                    artsData: res.data.list,
                    query: {
                        total: res.data.pager.recordCount,
                        pageNumber: res.data.pager.pageNumber,
                        pageSize: res.data.pager.pageSize,
                        beginTime: this.state.query.beginTime,
                        endTime: this.state.query.endTime,
                        title: this.state.query.title,
                        categoryId: this.state.query.categoryId,
                        delFlag: "0"
                    }
                })
            }
        }
    }
    // 当前页改变时
    onPageChange(page, pageSize) {
        let query = Object.assign(this.state.query, { pageNumber: page })
        this.setState({ query: query }, function () {
            this.getArts()
        })
    }

    onSizeChange(current, size) {
        let query = Object.assign(this.state.query, { pageSize: size })
        this.setState({ query: query })
        this.getArts()
    }


    onDateChange(date, dateString) {
        let query = Object.assign(this.state.query, { beginTime: dateString[0], endTime: dateString[0]})
        this.setState({ query: query })
        this.getArts()
    }
    onTreeSelect(selectedKeys, e) {
        // console.log('selectedKeys', selectedKeys)
    }

    componentDidMount() {
        this.getCatgs()
        this.getArts()
    }
    // componentDidUpdate(){
    //     this.getCatgs()
    //     this.getArts()
    // }
    componentWillUnmount(){ 
        //重写组件的setState方法，直接返回空
        this.setState = (state,callback)=>{
          return;
        }
    }
    onSelectChange = (selectedRowKeys,selectedRows) => {
        if(selectedRowKeys.length>5){
            message.error("最多选择5个")
            return false
        }
        console.log("-------",selectedRowKeys,selectedRows)
        let c = [...(this.state.selectRows?this.state.selectRows:[]),...selectedRows];
        c = c.filter((item)=>{
               
                return selectedRowKeys.indexOf(item.id)!=-1
        });
        this.setState({selectedRowKeys, selectRows:c });
     }
     checkedHandle1(data){
        const { handleCancel, checkedHandle,form: { getFieldDecorator },visible } = this.props;
        let mp = {}
        let arr = data.filter((item,index)=>{
            if(mp[item.id]){
                return false
            } else {
                mp[item.id] = item;
                return true
            }
        })
        checkedHandle(arr)
     }
     previewHandleCancel(){
        let {dispatch}  = this.props
        dispatch({type: 'article/effect:hide:preivewModal'})
    }
    render() {
        let { loading,query:{categoryId}} = this.state;
        const { handleCancel, checkedHandle,article,form: { getFieldDecorator },visible } = this.props;
        const rowSelection = {
            selectedRowKeys:this.state.selectedRowKeys,
            onChange: this.onSelectChange,
            getCheckboxProps:(record)=>{
                return record
            }
          };
        return (
            <div>
                <Modal
                    visible={visible}
                    title="添加相关文章"
                    width="60%"
                    onCancel={handleCancel}
                    footer={[
                        <Button key="ok" onClick={this.checkedHandle1.bind(this,this.state.selectRows)}>添加</Button>,
                        <Button key="back" onClick={handleCancel}>关闭</Button>,
                    ]}
                >
                    <Layout style={{ padding: '24px 0', background: '#fff' }}>
                        <Sider width={200} style={{ background: '#fff' }}>
                            <Tree
                                catgs={this.state.getCatgsList}
                                formData={this.state.query}
                                searchProps={this.searchHandle}
                                categoryId = {categoryId}
                                changeCatgId = {()=>{}}
                            />
                        </Sider>
                        <Content style={{ padding: '0 24px', minHeight: 0 }}>
                            <div>
                                <Form>
                                    <Row gutter={16}>
                                        <Col className="gutter-row" span={6}>
                                            <Form.Item label='标题' labelCol={{ span: 10 }} wrapperCol={{ span: 14 }}>
                                                {getFieldDecorator('title', {
                                                    initialValue: '',
                                                })(<Input />)}
                                            </Form.Item>
                                        </Col>
                                        <Col className="gutter-row" span={12}>

                                            <Form.Item label="发布时间" {...layout_r}>
                                                {
                                                    getFieldDecorator('publishDate', {
                                                        initialValue: ""
                                                    })(
                                                        <RangePicker />
                                                    )
                                                }
                                            </Form.Item>
                                        </Col>
                                        <Col className="gutter-row" span={6}>
                                            <FormItem className="btn-item">
                                                <Button type="primary" onClick={this.searchHandle}>查询</Button>&nbsp;
                                                <Button className="clear-btn" onClick={this.resetHandle.bind(this)}>重置</Button>
                                            </FormItem>
                                        </Col>
                                    </Row>
                                </Form>
                                <Table
                                    rowSelection={rowSelection} 
                                    columns={this.getColumns()}
                                    dataSource={this.state.artsData}
                                    rowKey={record => record.id}
                                    pagination={{ current: this.state.query.pageNumber, pageSize: this.state.query.pageSize, total: this.state.query.total, showSizeChanger: true, showQuickJumper: true, onChange: this.onPageChange, onShowSizeChange: this.onSizeChange }}
                                />
                            </div>
                        </Content>
                    </Layout>
                </Modal>
                <Modal
                    title={'预览'}
                    visible={article.previewModalVisible}
                    closable={false}
                    footer={[
                        <Button key="close" onClick={this.previewHandleCancel.bind(this)}>确定</Button>,
                    ]}
                >
                   <div className={styles.detailModal}>
                            <div
                                className={styles.detailContent}
                               
                            >
                                
                                <iframe src={article.previewModalUrl} frameBorder={0} style={{'width':'100%','height':'100%'}}></iframe>  
                            </div>
                        </div>
                </Modal>
            </div>
        );
    }
}
function mapStateToProps({ app, cms, article }, context) {
    console.log("aax",cms,article)
    return { app, cms, article }
}

export default connect(mapStateToProps)(Form.create()(RecommondModal))
