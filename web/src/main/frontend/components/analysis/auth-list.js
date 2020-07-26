import React from 'react'
import PropTypes from 'prop-types';
import {Table} from 'antd';
import {fieldMap} from '../../constants';

function List({
	loading,
	dataSource
}) {
	const columns = [{
		title: fieldMap.AUTHORS,
		dataIndex: 'authors',
		key: 'authors',
		width: 250
	},{
		title: fieldMap.ARTICAL_COUNT,
		dataIndex: 'articleCount',
		key: 'articleCount',
		width: 150
	},{
		title: fieldMap.COMMENT,
		dataIndex: 'commentsCount',
		key: 'commentsCount',
		width: 150,
	},{
		title: fieldMap.HITS_COUNT,
		dataIndex: 'hitsCount',
		key: 'hitsCount',
		width: 150
	},{
		title: fieldMap.LAST_UPDATE,
		dataIndex: 'updateTime',
		key: 'updateTime',
		width: 200
	}];

	return (
		<Table
			simple
			bordered
			scroll={{x: 900}}
			columns={columns}
			loading={loading}
			dataSource={dataSource}
			rowKey={record => record.authors}
			pagination={false}
		/>
	);
}

export default List;