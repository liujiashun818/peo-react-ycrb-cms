import React from 'react'
import PropTypes from 'prop-types';
import {routerRedux} from 'dva/router';
import {connect} from 'dva';
import {Jt} from '../../../utils';
import { routerPath } from '../../../constants';
import RoleEditCmpt from '../../../components/users/roleEdit';

function RoleEdit({
	location, 
	dispatch,
	role
}, context) {
	const editProps = {
		offices: role.offices,
		dataScopes: role.dataScopes,
		menus: role.menus,
		role: role.role,
		onSave: (role) => {
			if(role.id) {
				dispatch({
					type: 'role/effect:update:role',
					payload: {
						role,
						success: () => {
							context.router.goBack();
						}
					}
				});
			}
			else {
				dispatch({
					type: 'role/effect:create:role',
					payload: {
						role,
						success: () => {
							context.router.goBack();
						}
					}
				});
			}
		},
		goBack: () => {
			context.router.push(routerPath.ROLE_LIST);
		}
	};
	return (
		<div className='content-inner'>
            <RoleEditCmpt {...editProps}/>
        </div>
	);
}

RoleEdit.contextTypes = {
    router: PropTypes.object.isRequired
}

function mapStateToProps ({role}) {
	return {role};
}

export default connect(mapStateToProps)(RoleEdit)