import React from 'react';
import { Table ,Popconfirm} from 'antd'
import { Link } from 'dva/router'
import VisibleWrap from '../ui/visibleWrap'
import { fieldMap } from '../../constants'

class LoadingImgsList extends React.Component{
	constructor(props){
		super(props)
	}
	getColumns(){
		const {onDelete,onChangeDelflag} = this.props
		return [
			{
			      title: fieldMap.NAME,
			      dataIndex: 'title',
			      key: 'title',
			      width: 100,
			      render: (text,record) => 
						<Link to={{pathname:`/news/list/edit`, query: {id: record.id}}} >
						{record.title}
						</Link>
						// <a href="">{record.title}</a>
			},
			{
				title: fieldMap.CONTENT,
				key: 'content',
				dataIndex: 'content'
			},
			{
				title: fieldMap.CONTACTOR,
				key: 'name',
				dataIndex: 'name',
				width: 100,
			},
			{
				title:fieldMap.CONTACT,
				key:'contactInfo',
				dataIndex:'contactInfo',
				width: 120,
			},
			{
				title: 'IP',
				key:'ip',
				dataIndex:'ip',
				width: 120,
			},
			{
				title: '地理位置',
				key:'address',
				dataIndex:'address',
				width: 120,
			},
			// {
			// 	title:fieldMap.REMARK,
			// 	key:'remarks',
			// 	dataIndex:'remarks',
			// 	width: 150,
			// },
			{
				title:fieldMap.ACTION,
				key:'operation',
				width: 100,
				render:(text,record) =>{
					return (
					<p>
				            <Link to={{pathname:`/news/list/edit`, query: {id: record.id}}} style={{
				              marginRight: 4 }}>
				              查看
				            </Link>
							<Popconfirm title={'确定要删除吗？'} style={{
				              marginRight: 4
				            }} onConfirm={()=>onDelete(record.id)}>
				              <a style={{marginRight: 4}}>删除</a>
				            </Popconfirm>
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
					pagination={pagination}
					bordered
					onChange={onPageChange}
					columns={this.getColumns()}
					dataSource={dataSource}
					rowKey={record => record.id}
				></Table>
			</div>
		)
	}
}

export default LoadingImgsList
