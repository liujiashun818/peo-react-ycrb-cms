import React from 'react';
import { Table,Popconfirm  } from 'antd'

class CheatList extends React.Component{
    constructor(props){
        super(props)
    }
    getColumns(){
        let {cheatPagination,loading} = this.props;
        return [
            {
                title: '序号',
                key: 'num',
                width: 200,
                render:(text,record,index) =>{
                    // return <span>1</span>
                    return <span>{(cheatPagination.current-1)+index+1}</span>
                }
            },
            {
                title: 'ip 地址',
                key: 'ip',
                dataIndex: 'ip',
                width: 200
            },
            {
                title: '访问次数(48小时以内)',
                key: 'hits',
                dataIndex:'hits',
                width:200
            }
        ]
    }
    render(){
        let {cheatPagination,cheatList,cheatOnPageChange,loading} = this.props;
        return (
            <div>
                <Table
                    pagination={cheatPagination}
                    columns={this.getColumns()}
                    dataSource={cheatList}
                    loading={loading}
                    onChange={cheatOnPageChange}
                    rowKey={record => record.id}
                    bordered
                />
            </div>
        )
    }
}
export default CheatList
