import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'dva'

function VisibleWrap({children, location, dispatch, currentPermission, permis, replace=''}) {
	const prefix = location.pathname.substr(1).replace(/\//g,':');
	permis = prefix + ':' +permis;
	const isInArr = currentPermission.findIndex(function(value, index, arr) {
		const re =new RegExp("^" + value + "\\b","g");
		return re.test(permis);
	});
	const isVisible = (isInArr >= 0) ? true : false;
	return (
    	<span>
    	{isVisible ? children : replace }
	 	</span>
	)
}

VisibleWrap.propTypes = {
  children: PropTypes.element.isRequired,
}

export default connect(({app},ownProps) => ({...ownProps,currentPermission:app.currentPermission,location:app.location}))(VisibleWrap)
