import React from 'react';
import PropTypes from 'prop-types';
import {Table, Popconfirm, Row, Col, Button} from 'antd';
import {fieldMap} from '../../constants';
import VisibleWrap from '../ui/visibleWrap';
import styles from './fieldList.less';

class FieldList extends React.Component {
	constructor(props) {
		super(props);
	}

	showEditModal(curField) {
		this.props.updateState({
			femv: true,
			curField: {
				...curField
			}
		});
	}

	onDelete(id) {
		this.props.deleteHandle(id);
	}

	onBatchDelete(ids) {
		this.props.batchDeleteHandle(ids);
	}

	getColumns() {
		return [
			{
				title: '字段名称',
				dataIndex: 'name',
				key: 'name',
				width: 200
			},
			{
				title: '字段组',
				dataIndex: 'fieldGroupName',
				key: 'fieldGroupName',
				width: 200
			},
			{
				title: '别名',
				dataIndex: 'slug',
				key: 'slug',
				width: 200
			},
			{
				title: '类型',
				dataIndex: 'type',
				key: 'type',
				width: 100
			},
			{
				title: '操作',
				key: 'action',
				width: 150,
				render: (text, record) => {
					return (
						<p>
							<VisibleWrap permis="edit">
								<span>
									<a onClick={this.showEditModal.bind(this, record)}>编辑</a>
									<span className="ant-divider"/>
								</span>
							</VisibleWrap>
							<VisibleWrap permis="edit:delete">
								<Popconfirm title="确定删除吗？" onConfirm={this.onDelete.bind(this, record.id)}>
									<span>
										<a>删除</a>
									</span>
								</Popconfirm>
							</VisibleWrap>
						</p>
					);
				}
			}
		];
	}

	getRowSelection() {
		const {selectedIds, updateState} = this.props;
		return {
			selectedRowKeys: selectedIds,
			onChange: (selectedRowKeys) => {
				updateState({
					selectedIds: selectedRowKeys
				})
			}
		}
	}

	render() {
		const {
			loading,
			dataSource,
			pagination,
			onPageChange,
			selectedIds
		} = this.props;

		return (
			<div className={styles['field-list']}>
				<div className="toolbar">
					<VisibleWrap permis="edit:delete">
						<Popconfirm title="确定删除吗？" onConfirm={this.onBatchDelete.bind(this, selectedIds)}>
							<Button 
								disabled={selectedIds.length === 0}
							>
								批量删除
							</Button>
						</Popconfirm>
					</VisibleWrap>
				</div>
				<Table
					simple
					bordered
					loading={loading}
					scroll={{x: 1200}}
					columns={this.getColumns()}
					rowKey={record => record.id}
					rowSelection={this.getRowSelection()}
					dataSource={dataSource}
					pagination={pagination}
					onChange={onPageChange}
				/>
			</div>
		);
	}
}

export default FieldList;
