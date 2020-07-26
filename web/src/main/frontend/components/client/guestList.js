import React from 'react';
import { Table ,Popconfirm} from 'antd'
import { Link } from 'dva/router'
import VisibleWrap from '../ui/visibleWrap'
import { fieldMap } from '../../constants';

class GuestList extends React.Component{
	constructor(props){
		super(props)
	}
	getColumns(){
		const {onDelete} = this.props
		return [
			{
			      title: fieldMap.GUEST_CONTENT,
  				  key: 'content',
			      dataIndex: 'content',
                  width:550
			},
			{
				title: fieldMap.ENVIRONMENT,
				key: 'environment',
				dataIndex: 'environment',
                width:240,
				render:(text,record) => {
					return (
						<span>
							{record.deviceModel?<p>手机型号：{record.deviceModel}</p>:null}
							{(record.deviceSize)?<p>分辨率：{record.deviceSize}</p>:null}
							{(record.deviceType)?<p>系统类型：{record.deviceType}</p>:null}
							{(record.osversion)?<p>系统版本：{record.osversion}</p>:null}
							{(record.version)?<p>App版本：{record.version}</p>:null}
							{(record.ip)?<p>IP地址：{record.ip}</p>:null}
							{(record.netType)?<p>网络：{record.netType}</p>:null}
						</span>
					)
				}
			},
			{
				title: fieldMap.CONTACT,
				key: 'contactInfo',
				dataIndex: 'contactInfo',
                width:200
			},
			{
				title: fieldMap.GUEST_TIME,
				key: 'createAt',
				dataIndex: 'createAt',
                width:200,
				render:(text,record) =>{
					let date = new Date(text)
					return <span>{text?date.format('yyyy-MM-dd HH:mm:ss'):''}</span>
				}
			},
			{
				title:fieldMap.ACTION,
				key:'operation',
				render:(text,record) =>{
					return (
					<p>
						<VisibleWrap permis="edit:delete">
							<Popconfirm title={'确定要删除吗？'} style={{
				              marginRight: 4
				            }} onConfirm={()=>onDelete(record.id)}>
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
					pagination={pagination}
					bordered
					columns={this.getColumns()}
					onChange={onPageChange}
					dataSource={dataSource}
					rowKey={record => record.id}
				></Table>
			</div>
		)
	}
}

export default GuestList
