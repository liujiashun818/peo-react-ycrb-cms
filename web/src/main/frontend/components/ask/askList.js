import React from 'react';
import {Table,Input, Popconfirm, InputNumber, Tag,Select} from 'antd';
import {Link} from 'dva/router';
import {routerPath} from '../../constants';
import VisibleWrap from '../ui/visibleWrap';
// var defaultScrollTop = 59;
// let scrollTop = 0
let defaultScrollTop = 0;
class List extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
          
        };
        // this.onScrollHandle = this.onScrollHandle.bind(this);
    }
   
componentDidMount() {
   
   let containers=window.sessionStorage.getItem('sessionUserId');
//    console.log(containers,'刷新滚动条');
   document.getElementById('container').scrollTop = containers;
    // console.log(latoutNode,'刷新滚动条')
    // }  
  }
    getColumns() {
        let {onOnline,onOffline,settop,cancelTop,onEdit,viewComment} = this.props
        const columns = [
            {
                title: '排序',
                dataIndex: 'orderID',
                key: 'orderID',
                width: 100,
                render: (text, record) => {
                    let num =record.orderID?record.orderID:0
                    return (
                        <span> <InputNumber key={record.id} onChange={this.props.changeItem.bind(this,record.id)} defaultValue={num}></InputNumber></span>
                        // <InputNumber onChange={this.props.changeItem.bind(this,record.id)} defaultValue={num}></InputNumber>
                    );
                }
            },
            {
                title: '提问标题',
                dataIndex: 'title',
                key: 'title',
                width: 100,
                render: (text, record) => {

                    return (
                        <a href="javascript:;"  onClick={()=>{onEdit(record.id,record.governmentId)}}>{text}</a>
                    );
                }
            },
            {
                title: '领域',
                dataIndex: 'askDomainName',
                key: 'askDomainName',
                width: 100,
                render: (text, record) => {
                    return (<span>{text}</span>);
                }
            },
            {
                title: '类型',
                dataIndex: 'askTypeName',
                key: 'askTypeName',
                width: 100,
                sorter: true,
                render: (text, record) => {

                    return (<span>{text}</span>);
                }
            },
            {
                title: '提问机构名称',
                dataIndex: 'askGovernmentName',
                key: 'askGovernmentName',
                width: 100,
                render: (text, record) => {
                    const defaultValue = text?String(text):"0";
                    return (<span>{text}</span>);
                }
            },
            {
                title: '真实姓名',
                dataIndex: 'userName',
                key: 'userName',
                width: 80,
                render: (text, record) => {
                    const defaultValue = text?String(text):"0";
                    return (<span>{text}</span>);
                }
            },
            {
                title: '状态',
                dataIndex: 'status',
                key: 'status',
                width: 80,
                render: (text, record) => {
                    if(text==0){
                        return('未审核')
                    }  else if(text==1){
                        return('已审核')
                    } else if(text==2){
                        return('处理中')
                    } else if(text==3){
                        return('已回复')
                    } else if(text==4){
                        return('审核未通过')
                    }

                }
            },
            {
                title: '研发ID',
                dataIndex: 'tid',
                key: 'tid',
                width: 80,
                render: (text, record) => {
                    return (<span>{text}</span>);

                }
            },
            {
                title: '提问时间',
                dataIndex: 'questionTime',
                key: 'questionTime',
                width: 150
            },

            {
                title: '操作',
                key: 'action',
                width: 200,
                render: (text, record) => (
                    <p>
                        <span>
                            <a href="javascript:;" onClick={()=>{onEdit(record.id,record.governmentId)}}>修改</a>
                            <span className="ant-divider"/>
                        </span>
                        {
                            record.publishStatus==1?(
                                <span>
                                     <Popconfirm title="确定下线吗？" onConfirm={() => onOffline(record.id)}><a>下线</a></Popconfirm>
                                <span className="ant-divider"/>
                                </span>
                            ):(
                                <span>
                                     <Popconfirm title="确定上线吗？" onConfirm={() => onOnline(record.id)}><a>上线</a></Popconfirm>
                                <span className="ant-divider"/>
                                </span>
                            )
                        }


                        {
                            record.isHeadline==1?(
                                <span>
                                     <Popconfirm title="确定取消头条吗？" onConfirm={() => cancelTop(record.id)}><a>取消头条</a></Popconfirm>
                                <span className="ant-divider"/>
                                </span>
                            ):(
                                <span>
                                     <Popconfirm title="确定置为头条吗？" onConfirm={() => settop(record.id)}><a>置为头条</a></Popconfirm>
                                <span className="ant-divider"/>
                                </span>
                            )
                        }

                        <span>
                            <a onClick={()=>{viewComment(record)}}>评论</a>
                        </span>

                    </p>
                )
            }
        ];
        return columns
    }

    render() {
        const {
            loading,
            dataSource,
            pagination,
            selArts,
            onPageChange,
            selectArt
        } = this.props;

        // const selectedRowKeys = selArts.map((art) => art.id);
        //
        // const rowSelection = {
        //     selectedRowKeys,
        //     onChange: (ids, arts) => {
        //         selectArt(arts);
        //     }
        // };
        const rowSelection = {
            onChange: (selectedRowKeys, selectedRows) => {
                if(this.props.onSelected){
                    this.props.onSelected(selectedRowKeys);
                }
            }

        };


        return (
            <Table 
                simple
                bordered
                columns={this.getColumns()}
                rowSelection={rowSelection}
                dataSource={dataSource}
                loading={loading}
                onChange={onPageChange}
                pagination={pagination}
                rowKey={record => record.id}
                // rowSelection={rowSelection}
            />
        );
    }
}
export default List;
