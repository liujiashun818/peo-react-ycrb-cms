import React from 'react';
import { Table,Popconfirm  } from 'antd'

class LabelList extends React.Component{
    constructor(props){
        super(props)
    }
    getColumns(){
        const {edit,disabled,deleteItem} = this.props
        return [
            {
                title: '标签',
                dataIndex: 'name',
                key: 'name',
                width: 200,
            },
            {
                title: '文章数',
                key: 'articleCount',
                dataIndex: 'articleCount',
                width: 200,
                render: (text,record) => (
                    <span>
                        {text?text:0}
                    </span>
                )
            },
            {
                title: '操作',
                key: 'action',
                width: 200,
                render: (text,record) => (
                    <span>
                        <a onClick={()=>edit(record)}>编辑</a>
                        {Number(record.articleCount)>0?null:
                            <Popconfirm title="确认删除标签?" onConfirm={()=>deleteItem(record.id)} okText="确定" cancelText="取消">
                                <a style={{marginLeft:'10px'}}>删除</a>
                            </Popconfirm>
                                }
                    </span>
                )
            }
        ]
    }
    render(){
        let {pagination,dataSource,onPageChange,loading} = this.props;
        return (
            <div>
                <Table
                    pagination={pagination}
                    columns={this.getColumns()}
                    dataSource={dataSource}
                    loading={loading}
                    onChange={onPageChange}
                    rowKey={record => record.id}
                    bordered
                />
            </div>
        )
    }
}
export default LabelList
