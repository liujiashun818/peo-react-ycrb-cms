import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'dva';
import {Jt} from '../../../utils';
import {routerPath} from '../../../constants';
import Search from '../../../components/analysis/catg-search';
import List from '../../../components/analysis/catg-list';

function Catg({
	location,
	dispatch,
	anls_catg: {
		loading,
		catgs,
		offcs,
		list
	}
}, context) {
	const searchProps = {
		catgs,
		offcs,
		searchHandle: (query) => {
			dispatch({
				type: 'anls_catg/effect:query:list',
				payload: query
			});
		}
	};

	const listProps = {
		loading,
		dataSource: list
	};
	return (
		<div className="content-inner">
			<Search {...searchProps}/>
			<List {...listProps}/>
		</div>
	);
}

Catg.contextTypes = {
	router: PropTypes.object.isRequired
};

function mapStateToProps({anls_catg}) {
	return {
		anls_catg
	};
}

export default connect(mapStateToProps)(Catg);
