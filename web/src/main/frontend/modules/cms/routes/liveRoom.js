import React from 'react'
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { routerPath } from '../../../constants';
import Room from '../../../components/live/liveRoom';

function LiveRoom ({
	location,
	dispatch,
	cms,
	live
}, context) {
	let roomProps = {
		live: live.base,
		query:location.query,
		createSpeak: (speak, success) => {
			dispatch({
				type: 'article/effect:create:speak',
				payload: {
					speak,
					success
				}
			});
		},
		goBack() {
			context.router.goBack();
		  }
	};
	return (
		<div className="content-inner">
			<Room {...roomProps}/>
		</div>
	)
}

LiveRoom.contextTypes = {
    router: PropTypes.object.isRequired
};

function mapStateToProps({ cms, live }) {
	return { cms, live }
}

export default connect(mapStateToProps)(LiveRoom);