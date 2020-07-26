import React from 'react';
import PropTypes from 'prop-types';
import {Table} from 'antd';
import {fieldMap} from '../../constants';
function List({
	loading,
	dataSource,
	pagination,
	onPageChange
}) {
	const columns = [{
		title: fieldMap.URI,
		key: 'requestUri',
		dataIndex: 'requestUri',
		width: 200
	},{
		title: fieldMap.METHOD,
		key: 'method',
		dataIndex: 'method',
		width: 150
	},{
		title: fieldMap.ACT_TIME,
		key: 'createDate',
		dataIndex: 'createDate',
		width: 200,
		render:(text,record) =>{
			let date = new Date(text)
			return <span>{text?date.format('yyyy-MM-dd HH:mm:ss'):''}</span>
		}
	}];

	function expandedRender(record) {
		return (
			<div>
				{record.userAgent?(<p>用户代理：{record.userAgent}</p>):''}
				{record.params?(<p>提交参数：{record.params}</p>):''}
				{record.exception?(<p>异常信息：{record.exception}</p>):''}
			</div>
		);
	}
	
	return (
		<Table
			bordered
			columns={columns}
			pagination={pagination}
			onChange={onPageChange}
			dataSource={dataSource}
			loading={loading}
			rowKey={record => record.id}
			expandedRowRender={expandedRender}
		/>
	);
}

export default List;