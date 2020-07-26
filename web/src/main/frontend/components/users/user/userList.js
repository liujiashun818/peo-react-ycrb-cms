import React from 'react'
import PropTypes from 'prop-types'
import { Table, Popconfirm, Switch, Input, Button, Row, Col } from 'antd'
import { Link } from 'dva/router'
import VisibleWrap from '../../ui/visibleWrap'
import { fieldMap } from '../../../constants'

class List extends React.Component{
	constructor(props){
		super(props)
	}
	getColumns(){
		const {onDelete} = this.props
		return [
			{
				title: fieldMap.USERNAME,
				key: 'username',
				dataIndex: 'username'
			},
			{
				title: fieldMap.FULLNAME,
				key: 'name',
				dataIndex: 'name'
			},
			{
				title: fieldMap.REMARK,
				key: 'remark',
				dataIndex: 'remark',
                render: (text, record) =>(
                    text&&text!='null'?text:''
                )
			},
			{
				title: fieldMap.ACTION,
				key: 'operation',
				render:(text,record) =>{
					return (<p>
						<VisibleWrap permis="view">
							<Link to={{pathname:`/users/user/userEdit`,query:{id:record.id,type:'update'}}} style={{marginRight: 4}}>修改</Link>
						</VisibleWrap>
						<VisibleWrap permis="edit:delete">
							<Popconfirm title={'确定要删除吗？'} onConfirm={()=>onDelete(record.id)}>
								<a>删除</a>
							</Popconfirm>
						</VisibleWrap>
		            </p>
		            )
				}
			},
		]
	}
	render(){
		const {pagination,dataSource,onPageChange,addUser} = this.props
		return (
			<div>
				<VisibleWrap permis="edit:modify">
					<Button type="primary" style={{'marginBottom':10}} onClick={addUser}>
						用户添加
					</Button>
				</VisibleWrap>
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
