import React from 'react';
import PropTypes from 'prop-types';
import {routerRedux} from 'dva/router';
import {connect} from 'dva';
import {routerPath} from '../../../constants';
import Search from '../../../components/analysis/log-search';
import List from '../../../components/analysis/log-list';
import {Jt} from '../../../utils';

function Log({
	location,
	dispatch,
	anls_log: {
		loading,
		users,
		query,
		list,
		pagination
	}
}, context) {
	
	const searchProps = {
		users,
		query,
		searchHandle: (params) => {
			dispatch(routerRedux.push({
				pathname: routerPath.ANLS_LOG,
				query: params
			}));
		}
	};

	const listProps = {
		loading,
		dataSource: list,
		pagination,
		onPageChange: (page) => {
			dispatch(routerRedux.push({
				pathname: routerPath.ANLS_LOG,
				query: {
					...query,
					pageNumber: page.current,
					pageSize: page.pageSize
				}
			}))
		}
	};

	return (
		<div className='content-inner'>
			<Search {...searchProps}/>
			<List {...listProps}/>
		</div>
	);
}

Log.contextTypes = {
	router: PropTypes.object.isRequired
};

function mapStateToProps({anls_log}) {
	return {
		anls_log
	};
}

export default connect(mapStateToProps)(Log);