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
		function linkTypeText(linkType){
			if(linkType == 0) {
				return '空';
			}else if(linkType == 1){
				return '应用内部新闻ID'
			}else if(linkType == 2){
				return '触发外部浏览器wap地址'
			}else if(linkType == 3){
				return '内置浏览器wap地址'
			}
		}
		return [
			{
			      title: fieldMap.PIC,
			      dataIndex: 'imgUrl',
			      key: 'imgUrl',
			      width: 100,
			      render: (text,record) => <img width={90} src={record.type=='video'?record.videoCoverUrl:text} />
			},
			{
				title: fieldMap.SHOWTIMES,
				key: 'showtimes',
				dataIndex: 'showtimes'
			},
			{
				title: fieldMap.INTRO,
				key: 'intro',
				dataIndex: 'intro',
				render:(text,record) => {
					return (
						<span>
							{record.name?<p>名称：{record.name}</p>:null}
							{(record.height&&record.width)?<p>分辨率：{record.height
								+'*'+record.width}</p>:null}
							{(record.linkValue)?<p>链接值：{record.linkValue}</p>:null}
							{(record.linkType)?
								<p>链接类型：{
									linkTypeText(record.linkType)
								}</p>
								:
								<p>链接类型：空</p>
							}
						</span>
					)
				}
			},
			{
				title:fieldMap.DELFLAG,
				key:'delFlag',
				dataIndex:'delFlag',
				render:(text,record) =>{
					if(record.delFlag === 0){
						return <span>上线</span>
					}else{
						return <span>下线</span>
					}
				}
			},
			{
				title:fieldMap.ACTION,
				key:'operation',
				render:(text,record) =>{
					return (
					<p>
						<VisibleWrap permis="view">
				            <Link to={{pathname:`/client/loadingImgs/loadingImgsEdit`, query: {id: record.id, type: 'update'}}} style={{
				              marginRight: 4 }}>
				              修改
				            </Link>
			            </VisibleWrap>
			            <VisibleWrap permis="edit:online">
				            <Popconfirm title={'确定要'+(record.delFlag==0?'下线':'上线')+'吗？'} style={{
				              marginRight: 4
				            }} onConfirm={()=>onChangeDelflag(record.id,record.delFlag)}>
				              <a style={{marginRight: 4}}>{record.delFlag==0?'下线':'上线'}</a>
				            </Popconfirm>
			            </VisibleWrap>
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
