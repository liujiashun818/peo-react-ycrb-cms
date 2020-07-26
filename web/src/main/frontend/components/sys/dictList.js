import React from 'react'
import PropTypes from 'prop-types'
import { Table, Popconfirm, Switch, Input, Button, Row, Col } from 'antd'
import { Link } from 'dva/router'
import VisibleWrap from '../ui/visibleWrap'
import { fieldMap } from '../../constants';

class List extends React.Component{
	constructor(props){
		super(props)
	}
	getColumns(){
		const {onDelete} = this.props
		return [
			{
				title: fieldMap.KEY,
				key: 'value',
				dataIndex: 'value'
			},
			{
				title: fieldMap.LABEL,
				key: 'label',
				dataIndex: 'label'
			},
			{
				title: fieldMap.TYPE,
				key: 'type',
				dataIndex: 'type'
			},
			{
				title: fieldMap.DESCRIBE,
				key: 'description',
				dataIndex: 'description'
			},
			{
				title: fieldMap.ACTION,
				key:'operation',
				render:(text,record) =>{
					return (<p>
						<VisibleWrap permis="view">
							<Link to={{pathname:`/sys/dict/dictEdit`,query:{id:record.id,type:'update'}}} style={{marginRight: 4}}>修改</Link>
						</VisibleWrap>
						<VisibleWrap permis="edit:delete">
							<Popconfirm title={'确定要删除吗？'} onConfirm={()=>onDelete(record.id)}>
								<a>删除</a>
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

export default List