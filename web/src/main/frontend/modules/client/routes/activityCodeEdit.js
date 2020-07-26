import React from 'react'
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { routerRedux } from 'dva/router'
import { routerPath } from '../../../constants'
import { Jt, Immutable } from '../../../utils'
import ActivityCodeEdit from '../../../components/client/activityCodeEdit'


function ActivityEdit({ history, location, dispatch, activityCode }, context) {
	
    const { list, curActivity } = activityCode;

	const activityEditFormProps = {
		list,
        item:curActivity,
        createActivity(data) {
            dispatch({
                type: 'activityCode/createActivity',
                payload: {
                    code: data,
                    success: () => {
                        context.router.push('/client/activityCode')
                    }
                }
            })
        },
        updateActivity(data) {
            dispatch({
                type: 'activityCode/updateActivity',
                payload: {
                    code: data,
                    success: () => {
                        context.router.push('/client/activityCode')
                    }
                }
            })
        },
        backToList(){
          context.router.push('/client/activityCode')
        }
	}
  return (
    <div className='content-inner'>
      <ActivityCodeEdit {...activityEditFormProps}/>
    </div>
  );
}

ActivityEdit.contextTypes = {
    router: PropTypes.object.isRequired
}

ActivityEdit.propTypes = {
    location: PropTypes.object,
    dispath: PropTypes.func,
    activityCode: PropTypes.object
}

function mapStateToProps ({ activityCode }) {
  return { activityCode }
}

export default connect(mapStateToProps)(ActivityEdit)