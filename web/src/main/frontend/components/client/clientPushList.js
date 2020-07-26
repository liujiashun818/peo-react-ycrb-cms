import React from 'react';
import { Table ,Popconfirm} from 'antd'
import VisibleWrap from '../ui/visibleWrap'
import { fieldMap } from '../../constants'

class clientPushList extends React.Component{
	constructor(props){
		super(props)
	}
	getColumns(){
		const {onDelete} = this.props
		return [
			{
				title: fieldMap.TITLE,
				key: 'title',
				dataIndex: 'title'
			},
			{
				title: fieldMap.CONTENT,
				key: 'description',
				dataIndex: 'description'
			},
			{
				title: fieldMap.PUSH_TIME,
				key:'createAt',
				dataIndex:'createAt',
				render:(text,record) =>{
					let date = new Date(text)
					return <span>{text?date.format('yyyy-MM-dd HH:mm:ss'):''}</span>
				}
			},
			{
				title:fieldMap.PUSH_PLATFORM,
				key:'platform',
				dataIndex:'platform',
				render:(text,record) =>{
					if(record.platform === 0){
						return <span>全部</span>
					}else if(record.platform === 1){
						return <span>iOS</span>
					}else if(record.platform === 2){
						return <span>Android</span>
					}else{
						return null
					}
				}
			},
            {
                title:fieldMap.PUSH_STATUS,
                key:'status',
                dataIndex:'status',
                render:(text) =>{
                    if(text){
                        return <span>成功</span>
                    }else{
                        return <span>失败</span>
                    }
                }
            },
			{
				title:fieldMap.ACTION,
				key:'operation',
				render:(text,record) =>{
					return (
					<p>
						<VisibleWrap permis="edit:delete">
							<Popconfirm title={'确定要删除吗？'} onConfirm={()=>onDelete(record.delFlag,record.id)} style={{
				              marginRight: 4
				            }}>
				              <a style={{marginRight: 4}}>删除</a>
				            </Popconfirm>
			            </VisibleWrap>
		            </p>
		            )
				}
			}
		]
	}
	render(){
		const {pagination,dataSource,onPageChange} = this.props
		return (
			<div>
				<Table
					scroll={{ x: 1200 }}
					pagination={pagination}
					onChange={onPageChange}
					bordered
					columns={this.getColumns()}
					dataSource={dataSource}
					rowKey={record => record.id}
				></Table>
			</div>
		)
	}
}

export default clientPushList
