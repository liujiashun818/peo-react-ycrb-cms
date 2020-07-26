import React from 'react'
import PropTypes from 'prop-types';
import {routerRedux} from 'dva/router';
import {connect} from 'dva';
import {Jt} from '../../../utils';
import { routerPath } from '../../../constants';
import RoleListCmpt from '../../../components/users/roleList';

function RoleList({
	location, 
	dispatch,
	role
}, context) {
	const listProps = {
		loading: role.loading,
		dataSource: role.roles,
		dataScopes: role.dataScopes,
		addRole: () => {
			context.router.push(routerPath.ROLE_EDIT);
		},
		deleteRole: (id) => {
			dispatch({
				type: 'role/effect:delete:role',
				payload: id
			});
		}
	};
	return (
		<div className='content-inner'>
            <RoleListCmpt {...listProps}/>
        </div>
	);
}

RoleList.contextTypes = {
    router: PropTypes.object.isRequired
}

function mapStateToProps ({role}) {
  return {role};
}

export default connect(mapStateToProps)(RoleList)