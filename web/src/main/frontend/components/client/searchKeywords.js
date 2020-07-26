import React from 'react';
import { Link } from 'dva/router'
import {
  Row,
  Table,
  Input, 
  Button,
  Popconfirm,
  Form,
  Modal
} from 'antd';

const {TextArea} = Input;

const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 }
};

class SearchKeywords extends React.Component {
	constructor(props){
		super(props);
		this.state = {
			isAddModal: true
		};
		this.curRecord = null;
	}
	handleShowEditModal(record) {
		this.curRecord = record;
		this.setState({isAddModal: false});
		this.props.handleShowModal();
	}
	handleShowAddModal() {
		this.curRecord = null;
		this.setState({isAddModal: true});
		this.props.handleShowModal();
	}
	hideModal() {
		this.props.handleHideModal();
	}
	handleSubmit() {
		const {handleSubmit, form: {validateFields}} = this.props;
		validateFields((err, values) => {
			if (!err) {
				handleSubmit(values, this.curRecord && this.curRecord.id);
			}
		})
	}
	render() {
		const {dataSource, handleDelete, showModal, handleHideModal, form: {getFieldDecorator}} = this.props;
		const {isAddModal} = this.state;
		const isMaxLength = dataSource.length >= 5 ? true : false;
		const columns = [
			{
			  title: '名称',
			  dataIndex: 'title'
			},
			{
			  title: '备注',
			  dataIndex: 'remarks',
			},
			{
			  title: '操作',
			  dataIndex: 'diyOptions',
			  width: 150,
			  render: (text, record, index) => {
			    return (
				  	<p>
				        <a style={{marginRight: 4}} onClick={this.handleShowEditModal.bind(this, record)}>修改</a>
						<Popconfirm title='确定要删除吗？'
						            onConfirm={()=>handleDelete(record.id)}>
		                    <a>删除</a>
			            </Popconfirm>
		            </p>
			    )
			  }
			}
		];
		return (
			<Row style={{backgroundColor: '#fff'}}>
				<Row style={{margin: '10px'}}>
					<Button type="primary" onClick={this.handleShowAddModal.bind(this)} disabled={isMaxLength}>增加搜索关键词</Button>
					{
					   isMaxLength &&
					   <span style={{marginLeft: 10, color: 'red'}}>搜索关键词只能添加5个</span>
					}
				</Row>
				<Row style={{margin: '0 10px'}}>
					<Table columns={columns}
					       rowKey={(record) => record.id}
					       bordered
					       dataSource={dataSource}/>
				</Row>
				{
					showModal &&
					<Modal
					  visible={showModal}
			          title={isAddModal ? '添加搜索关键词' : '编辑搜索关键词'}
			        //   okText="确认"
					//   cancelText="取消"
					footer={[
						<Button key='1' type="primary" style={{ marginRight: '10px' }} size="large" onClick={this.handleSubmit.bind(this)}>保存</Button>,
						<Button key='0' size="large" onClick={handleHideModal.bind(this)}>返回</Button>
					]}
			          onOk={this.handleSubmit.bind(this)}
			          onCancel={handleHideModal.bind(this)}
			        >
			        	<Form onSubmit={this.handleSubmit}>
				            <Form.Item
					          label="名称"
					          {...formItemLayout}
					        >
					        {
					          	getFieldDecorator('title', {
					          		    initialValue: isAddModal ? '' : this.curRecord.title,
							            rules: [{
							              required: true, message: '请输入搜索关键字名称',
							            }],
						          	})(
						            <Input />
					          	)
					        }
					        </Form.Item>
					        <Form.Item
					          label="备注"
					          {...formItemLayout}
					        >
					        {
					          	getFieldDecorator('remarks', {
					          		    initialValue: isAddModal ? '' : this.curRecord.remarks,
							            rules: [{
							            }],
						          	})(
						            <TextArea rows={4}/>
					          	)
					        }
					        </Form.Item>
					    </Form>
			        </Modal>
				}
			</Row>
		)
	}
}

export default Form.create()(SearchKeywords)