import React from 'react'
import PropTypes from 'prop-types';
import {Table} from 'antd';
import {fieldMap} from '../../constants';

class List extends React.Component {
	constructor(props) {
		super(props);
	}

	getColumns() {
		return [{
			title: fieldMap.PARENTNAME,
			dataIndex: 'parentName',
			key: 'parentName',
			width: 200
		},{
			title: fieldMap.CATG_NAME,
			dataIndex: 'categoryName',
			key: 'categoryName',
			width: 200
		},{
			title: fieldMap.ARTICAL_COUNT,
			dataIndex: 'articleCount',
			key: 'articleCount',
			width: 100
		},{
			title: fieldMap.COMMENT,
			dataIndex: 'commentsCount',
			key: 'commentsCount',
			width: 100
		},{
			title: fieldMap.HITSCOUNT,
			dataIndex: 'hitsCount',
			key: 'hitsCount',
			width: 100
		},{
			title: fieldMap.LAST_UPDATE,
			dataIndex: 'updateTime',
			key: 'updateTime',
			width: 200
		},{
			title: fieldMap.OFFICE_NAME,
			dataIndex: 'officeName',
			key: 'officeName',
			width: 200
		}];
	}

	render() {
		const {loading, dataSource} = this.props;
		return (
			<Table
				simple
				bordered
				scroll={{x: 1100}}
				columns={this.getColumns()}
				loading={loading}
				dataSource={dataSource}
				rowKey={record => record.categoryId}
				pagination={false}
			/>

		);
	}
}

export default List;