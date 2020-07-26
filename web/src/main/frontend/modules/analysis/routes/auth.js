import React from 'react'
import PropTypes from 'prop-types';
import {connect} from 'dva';
import {routerPath} from '../../../constants';
import Search from '../../../components/analysis/auth-search';
import List from '../../../components/analysis/auth-list';

function Auth({
	location, 
	dispatch, 
	anls_auth: {
		loading,
		list
	}
}, context) {
	
	const searchProps = {
		searchHandle: (query) => {
			dispatch({
				type: 'anls_auth/effect:query:list',
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
        	<Search {...searchProps} />
        	<List {...listProps} />
    	</div>
	);
}

Auth.contextTypes = {
    router: PropTypes.object.isRequired
};

function mapStateToProps({anls_auth}) {
    return {
        anls_auth
    };
}

export default connect(mapStateToProps)(Auth);
