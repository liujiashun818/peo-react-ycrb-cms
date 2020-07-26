import React from 'react'
import PropTypes from 'prop-types';
import {connect} from 'dva';
import {Form, Popconfirm, InputNumber} from 'antd';
import {routerPath} from '../../../constants';
import Edit from '../../../components/live/liveEdit';

function LiveEdit({
	location,
	dispatch,
	cms,
	live
}, context) {
	const editProps = {
		catgs: live.catgs,
		live: live.base,
		hosts: live.hosts,
		guests: live.guests,
		saveUser: (user, success) => {
			if(user.id) {
				dispatch({
					type: 'live/effect:update:liveUser',
					payload: {user, success}
				})
			}
			else {
				dispatch({
					type: 'live/effect:create:liveUser',
					payload: {user, success}
				})
			}
		},
		deleteUser: (user,success) => {
			dispatch({
				type: 'live/effect:delete:liveUser',
				payload: {user, success}
			})
		},
		deleteLive: (id) => {
			dispatch({
				type: 'cms/effect:delete:art',
				payload: {
					id,
					success: () => {
						context.router.goBack();
					}
				}
			});
		},
		saveLive: (live) => {
			let success = () => {
				context.router.push({
					pathname: routerPath.ARTICLE_LIST,
					query: {
						...cms.naq,
						categoryId: live.categoryId,
						block: live.block,
						delFlag: live.delFlag
					}
				});
			};
			const source = location.query.source;
			if(source && source === 'subject') {
				success = () => {
					context.router.goBack();
				};
			}
			const action = live.id ? 'update' : 'create';
			const type = `live/effect:${action}:live`;
			dispatch({
				type,
				payload: {
					live,
					success
				}
			});
		},
		goBack: () => {
			context.router.goBack();
		},
		updateLiveAttrs: (live) => {
			dispatch({
				type: 'live/effect:update:liveAttrs',
				payload: live
			});
		}
	}
	return (
		<div className="content-inner">
			<Edit {...editProps}/>
		</div>
	)
}

LiveEdit.contextTypes = {
    router: PropTypes.object.isRequired
}

function mapStateToProps({ cms, live }) {
	return { cms, live }
}

export default connect(mapStateToProps)(LiveEdit)