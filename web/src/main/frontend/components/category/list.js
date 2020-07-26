import React from 'react'
import PropTypes from 'prop-types'
import { Table, Popconfirm, Switch, Input, InputNumber, Button, Row, Col } from 'antd'
import { Link } from 'dva/router'
import EditableCell from '../ui/editableCell'
import styles from './list.less'
import VisibleWrap from '../ui/visibleWrap'
import { fieldMap } from '../../constants'

class List extends React.Component {
	constructor(props) {
		super(props)
		let dataSource = props.dataSource
		this.state = {
			sortObj: {},
			expandedRowKeys: dataSource.length > 0 ? [dataSource[0].id] : []
		}
	}

	componentWillReceiveProps(nextProps) {
		if(nextProps.dataSource.length > 0 && this.state.expandedRowKeys.length === 0) {
			this.setState({
				expandedRowKeys: [nextProps.dataSource[0].id]
			})
		}
	}

	expandedRowsChange(keys) {
		this.setState({
			expandedRowKeys: keys
		})
	}

	switchCommentStrategy(item) {
		const obj = {...item, isAutoOnline: !item.isAutoOnline};
		delete obj.children;
		this.props.updateCatg(obj)
	}

	sortOnChange(id, sort) {
		let sortObj = this.state.sortObj
		sortObj[id] = sort
		this.setState({
			sortObj: sortObj
		})
	}

	saveSort() {
		const sortObj = this.state.sortObj
		const data = []
		for(let key in this.state.sortObj) {
			data.push({
				id: key,
				sort: sortObj[key]
			})
		}
		if(data.length > 0) {
			this.props.updateCatgs(data)
		}
	}

	getColumns() {
		let {catgModels, onOffCatg, deleteCatg} = this.props
		let {sortObj} = this.state;
		return [
			{
				title: fieldMap.CATG_NAME,
				key: 'name',
				dataIndex: 'name'
			},
			{
				title: fieldMap.OFFICE_NAME,
				key: 'officeName',
				dataIndex: 'officeName',
				width: 130
			},
			{
				title: fieldMap.MODEL,
				key: 'modelId',
				dataIndex: 'modelId',
				width: 100,
				render: (text, record) => {
					for(let i = 0; i < catgModels.length; i++) {
						if(text === catgModels[i].id) {
							return catgModels[i].name;
						}
					}
					return text;
				}
			},
			{
				title: fieldMap.AUTO_ONLINE,
				key: 'isAutoOnline',
				dataIndex: 'isAutoOnline',
				width: 100,
				render: (text, record) => {
					return <Switch
							checked={text}
							checkedChildren="是"
							unCheckedChildren="否"
							onChange={() => this.switchCommentStrategy(record)}
						   />
				}
			},
			{
				title: fieldMap.SORT,
				key: 'sort',
				dataIndex: 'sort',
				width: 100,
				render: (text, record) => {
					let value = text;
					if(typeof sortObj[record.id] !== 'undefined') {
						value = sortObj[record.id];
					}
					return <InputNumber
								value={value}
								onChange={(sort) => this.sortOnChange(record.id, sort)}
						   />
				}
			},
			{
				title: fieldMap.DELFLAG,
				key: 'delFlag',
				dataIndex: 'delFlag',
				width: 100,
				render: (text, record) => {
					return <Switch
							checked={text === 0 ? true : false}
							checkedChildren="上线"
							unCheckedChildren="下线"
							onChange={() => onOffCatg(record,(text === 0 ? true : false)) }
						   />
				}
			},
			{
				title: fieldMap.ACTION,
				key: 'operation',
				render: (text, record) => (
					<p>
						{
							record.parentId !== 0 ?
							<span>
								<VisibleWrap permis="view">
									<Link to={{pathname: '/cms/category/edit', query: {id: record.id, type: 'updateCatg'}}}>修改</Link>
								</VisibleWrap>
								<span className="ant-divider" />
								<VisibleWrap permis="edit:delete">
									<Popconfirm title='确定要删除吗？' onConfirm={() => deleteCatg(record.id)}>
							            <a>删除</a>
							        </Popconfirm>
						        </VisibleWrap>
								<span className="ant-divider" />
							</span> : null
						}
						<Link to={{pathname: '/cms/category/edit', query: {id: record.id, type: 'addSubCatg'}}}>添加下级栏目</Link>
					</p>
				)
			}
		]
	}

	render() {
		let {loading, dataSource} = this.props
		let {expandedRowKeys} = this.state
		return (
			<div>
				<Table
					bordered
					scroll={{ x: 1200 }}
					columns={this.getColumns()}
					dataSource={dataSource}
					loading={loading}
					rowKey={record => record.id}
					pagination={false}
					className={styles.table}
					expandedRowKeys={expandedRowKeys}
					onExpandedRowsChange={(keys) => this.expandedRowsChange(keys)}
				/>
				<Row gutter={24}>
					<Col>
						<Button type="primary" size="large" onClick={this.saveSort.bind(this)}>保存排序</Button>
					</Col>
				</Row>
			</div>
		)
	}
}

export default List
