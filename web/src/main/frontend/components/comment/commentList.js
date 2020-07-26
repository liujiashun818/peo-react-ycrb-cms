import React from 'react'
import PropTypes from 'prop-types'
import {
	Table,
	Popconfirm,
	Switch,
	Input,
	InputNumber,
	Button,
	Row,
	Col,
	Form,
	message
} from 'antd'
import VisibleWrap from '../ui/visibleWrap'
import { fieldMap } from '../../constants'

const { TextArea } = Input;
const formItemLayout = {
	labelCol: { span: 8 },
	wrapperCol: { span: 14 },
};

class List extends React.Component{
	static  defaultProps = {
        handleClickTitle: () => {}
    };
	constructor(props) {
		super(props);
		this.state = {
			dataSource: [],
			showReply: false,
			showAdminReply: false,
			replyValue: '',
			adminReplyValue: ''
		}
	}
	handleReply(index, type) {
		const {dataSource} = this.state;
		dataSource[index][type] = !dataSource[index][type]
		this.setState({dataSource: dataSource});
	}
	/**
		@params type 回复内容类型replyValue  adminReplyValue
	**/
	handleChangeInput(index, type, e) {
		const {dataSource} = this.state;
		dataSource[index][type] = e.target.value;
		this.setState({dataSource: dataSource});
	}
	handleSubmitReply(record, index, type) {
		const {handleSubmitReply} = this.props;
		if (type === 'normal') {
			if (!this.trim(record.replyValue)) {
				message.error('回复不能为空');
				return false;
			};
		} else if (type === 'admin') {
			if (!this.trim(record.replyAdminValue)) {
				message.error('回复不能为空');
				return false;
			};
		}
		handleSubmitReply(record, type);
	}
	trim(text = '') {
        text = text.replace(/&nbsp;/ig, "");
        text = text.replace(/&ensp;/ig, "");
        text = text.replace(/&emsp;/ig, "");
        text = text.replace(/&thinsp;/ig, "");
        text = text.replace(/&zwnj;/ig, "");
        text = text.replace(/&zwj;/ig, "");
        return text == null ?
            "" :
            (text + "").replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, "");
    }
	getColumns(){
		const {
			onChangeDelflag,
            onChangeDelflag2,
			saveLikes,
			replyHandle,
			handleClickTitle
		} = this.props;
		const {dataSource} = this.state;
		return [
			{
				title: fieldMap.ORG_TITLE,
				key: 'title',
				dataIndex: 'title',
				width:200,
                render:(text,record) => {
                    return <div style={{cursor: 'pointer'}} onClick={() => handleClickTitle(record)}>
                               <span dangerouslySetInnerHTML={{__html: text}} ></span>
                           </div>
                }
			},
			{
				title: fieldMap.COM_CONTENT,
				key: 'content',
				dataIndex: 'content',
				render:(text,record, index) => {
					return <div>
					           <span dangerouslySetInnerHTML={{__html: text}} ></span>
					           <Row style={{margin: '10px 0'}} type="flex" justify="start" gutter={12}>
					           		<Col>
					           		    <Button type="primary"
					           		            onClick={this.handleReply.bind(this, index, 'showReply')}>
					           		                回复
					           		    </Button>
					           		</Col>
					           		<Col>
					           		{
					           			!record.showReply &&
					           		    <Button type="primary"
					           		            onClick={this.handleReply.bind(this, index, 'showAdminReply')}>
					           		                管理员回复
					           		    </Button>
					           		}
					           		</Col>
					           </Row>
					           {
					           	    record.showReply  &&
					           		<Row>
					                    <Row>
						                    <TextArea value={record.replyValue}
			                                          onChange={this.handleChangeInput.bind(this, index, 'replyValue')}
			                                          defaultValue={record.replyValue}
			                                          rows={4} />
								            <Row style={{marginTop: '10px'}}>
								           		<Col span="19">
									           		<Row align="middle" type="flex">
									           			<Col span="8">评论人姓名:</Col>
									           			<Col span="14">
									           			    <Input
												                value={record.replyName}
												                defaultValue={record.replyName}
												                onChange={this.handleChangeInput.bind(this, index, 'replyName')}
												            />
												        </Col>
												    </Row>
								           		</Col>
								           		<Col span="5">
								           			<Button type="primary" onClick={this.handleSubmitReply.bind(this, record, index, 'normal')}>提交</Button>
								           		</Col>
								           </Row>
					                    </Row>
						            </Row>
					           }
					           {
						           record.showReply &&
						           <Row style={{margin: '10px 0'}} type="flex" justify="start" gutter={12}>
						           		<Col>
						           		    <Button type="primary"
						           		            onClick={this.handleReply.bind(this, index, 'showAdminReply')}>
						           		                管理员回复
						           		    </Button>
						           		</Col>
						           </Row>
					           }
					           {
					           		record.showAdminReply  &&
					           		<Row>
					                    <Row>
						                    <TextArea value={record.replyAdminValue}
			                                          onChange={this.handleChangeInput.bind(this, index, 'replyAdminValue')}
			                                          defaultValue={record.replyAdminValue}
			                                          rows={4} />
								            <Row style={{marginTop: '10px'}}>
								           		<Col span="19">
									           		<Row align="middle" type="flex">
									           			<Col span="8">评论人姓名:</Col>
									           			<Col span="14">
									           			    <Input
												                value='管理员'
												                disabled={true}
												                onChange={this.handleChangeInput.bind(this, index, 'replyAdminName')}
												            />
												        </Col>
												    </Row>
								           		</Col>
								           		<Col span="5">
								           			<Button type="primary" onClick={this.handleSubmitReply.bind(this, record, index, 'admin')}>提交</Button>
								           		</Col>
								           </Row>
					                    </Row>
						            </Row>
					           }
					       </div>
				}
			},
			{
				title: fieldMap.COM_USER,
				key: 'userName',
				width:120,
				dataIndex: 'userName'
			},
            // {
            //     title: fieldMap.COM_REPLY,
            //     key: 'adminReply',
            //     width:120,
            //     dataIndex: 'adminReply'
            // },
			{
				title:fieldMap.COM_TIME,
				key:'createAt',
				width:150,
				dataIndex:'createAt',
				render:(text,record) =>{
					let date = new Date(text)
					return <span>{text?date.format('yyyy-MM-dd HH:mm:ss'):''}</span>
				}
			},
			{
				title:fieldMap.AREA,
				width:100,
				key:'area',
				dataIndex:'area'
			},
			{
				title:fieldMap.LIKES,
				key:'likes',
				width:100,
				dataIndex:'likes',
				render:(text,record) =>{
					return <InputNumber
						defaultValue={record.likes?record.likes:0} onChange={(likes) => {
							record.likes = likes
						}}
					/>
				}
			},
			{
				title:fieldMap.ACTION,
				key:'operation',
				width:150,
				render:(text,record) =>{
					return (<p>
						{/*<VisibleWrap permis="edit:online">*/}
							<Popconfirm title={'确定要'+(record.delFlag==0?'下线':'上线')+'吗？'} onConfirm={()=>onChangeDelflag(record.delFlag,record.id,'onoff')} style={{
				              marginRight: 4
				            }}>
				              <a style={{marginRight: 4}}>{record.delFlag==0?'下线':'上线'}</a>
				            </Popconfirm>
			            {/*</VisibleWrap>*/}
			            {record.delFlag == 2?(
							<Popconfirm title={'确定要不通过审核吗？'} onConfirm={()=>onChangeDelflag2(record.delFlag,record.id,'decline')} style={{
				              marginRight: 4
				            }}>
				              <a style={{marginRight: 4}}>不通过</a>
				            </Popconfirm>
			            	):''}
			            <a onClick={() => saveLikes(record)}>保存赞</a>
                        {/* <VisibleWrap permis="view"><span style={{marginLeft: 4}}><a onClick={() => replyHandle(record)}>管理员回复</a></span></VisibleWrap> */}
		            </p>
		            )
				}
			}
		]
	}
	componentWillReceiveProps(nextProps) {
		if (nextProps.dataSource !== this.props.dataSource) {
			this.setState({dataSource: nextProps.dataSource});
		}
	}
	render(){
		let {loading, pagination,onPageChange,onSelectedDelflag,selectedRowKeys=[]} = this.props;
		const {dataSource} = this.state;
	    const rowSelection = {
	    	selectedRowKeys,
	        onChange: function(selectedRowKeys, selectedRows) {
	            onSelectedDelflag(selectedRowKeys)
	        }
    	}
		return (
		<div>
			<Table
				scroll={{ x: 1200 }}
				pagination={pagination}
				onChange={onPageChange}
				loading={loading}
				bordered
                rowKey={record => record.id}
				columns={this.getColumns()}
				dataSource={dataSource}
				rowSelection={rowSelection}
			></Table>
		</div>
		)
	}
}
export default Form.create()(List);
