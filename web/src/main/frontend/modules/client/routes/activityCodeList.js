import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router'
import { routerPath } from '../../../constants'
import { Jt, Immutable } from '../../../utils'
import ActivityCodeList from '../../../components/client/activityCodeList';

function ActivityList({ location, dispatch, activityCode }) {
  const activityTreeProps = {
    dataSource : activityCode.list,
    pagination : activityCode.pagination,
    onPageChange(page){
      const query = location.query
      dispatch(routerRedux.push({
          pathname: routerPath.ACTIVITYCODE,
          query: {
            ...query,
            pageNumber : page.current,
            pageSize : page.pageSize
          }
      }))
    },
    onDelete:function(id){
      const query = location.query
      dispatch({
        type: 'activityCode/deleteActivity',
        payload: {
          data:{
            id:id
          },
          callback:function(){
                dispatch(routerRedux.push({
                  pathname: routerPath.ACTIVITYCODE,
                  query
              }))
          }
        }
      })
    }
  }
  return (
    <div className='content-inner'>
      <ActivityCodeList {...activityTreeProps}/>
    </div>
  );
}
function mapStateToProps ({ activityCode }) {
  return { activityCode }
}

export default connect(mapStateToProps)(ActivityList)
