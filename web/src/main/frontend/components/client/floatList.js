import React from 'react';
import { Table,Popconfirm } from 'antd'

class FloatList extends React.Component{
    constructor(props){
        super(props)
    }
    getColumns(){
        const {edit,disabled,deleteItem,start,onOff} = this.props
        return [
            {
                title: '浮动图名称',
                dataIndex: 'name',
                key: 'name',
                width: 200,
            },
            {
                title: '图片',
                key: 'imgUrl',
                dataIndex: 'imgUrl',
                width: 200,
                render: (text) => <img width={50} src={text} />
            },
            {
                title: '首页是否显示',
                dataIndex: 'isShow',
                key: 'isShow',
                width: 200,
                render: (text) => <span>{Number(text)?'是':'否'}</span>
            },
            {
                title: '跳转方式',
                dataIndex: 'type',
                key: 'type',
                width: 200,
                render: (text,record) =>{
                    if(text==0){
                        return <span>APP内栏目</span>
                    }else if(text==1){
                        return <span>跳转URL</span>
                    }else if(text==2){
                        return <span>新闻详情页</span>
                    }
                }
            },
            {
                title: '状态',
                dataIndex: 'delFlag',
                key: 'delFlag',
                width: 200,
                render: (text,record) => <span>{Number(text)?'禁用':'启用'}</span>
            },
            {
                title: '操作',
                key: 'action',
                width: 200,
                render: (text,record) => {
                    const str = Number(record.delFlag)?'启用':'禁用';
                    return(
                        <span>
                            <a onClick={()=>edit(record)}>编辑</a>
                            <a onClick={()=>onOff(record.id)} style={{marginLeft:'10px'}}>{str}</a>
                            <Popconfirm title="确定删除?" onConfirm={()=>deleteItem(record.id)} okText="确定" cancelText="取消">
                                <a style={{marginLeft:'10px'}}>删除</a>
                            </Popconfirm>
                        </span>
                    )
                }
            }
        ]
    }
    render(){
        let {pagination,dataSource,onPageChange} = this.props;
        return (
            <div>
                <Table
                    pagination={pagination}
                    columns={this.getColumns()}
                    dataSource={dataSource}
                    onChange={onPageChange}
                    rowKey={record => record.id}
                    bordered
                />
            </div>
        )
    }
}
export default FloatList
