import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { routerRedux } from 'dva/router'
import { routerPath } from '../../../constants'
import { Jt, Immutable } from '../../../utils'
import ClientPushList from '../../../components/client/clientPushList'

function ClientPush({ location, dispatch, client },context) {
  const listProps = {
    dataSource : client.pushList,
    pagination: client.pagination,
    onPageChange(page){
      const query = location.query
      dispatch(routerRedux.push({
          pathname: routerPath.CLIENT_PUSH,
          query: {
              pageNumber: page.current,
              pageSize: page.pageSize ,
          }
      }))
    },
    onDelete(delFlag,id){
      const query = location.query
      const pager = {
        pageNumber:1,
        pageSize:10,
        ...query
      }
      dispatch({
        type:'client/deletePush',
        payload:{
          params:{
            id,
            delFlag:3,
          },
          pager,
        }
      })
    }
  }
  return (
    <div className='content-inner'>
      <ClientPushList {...listProps}></ClientPushList>
    </div>
  );
}
ClientPush.contextTypes = {
    router: PropTypes.object.isRequired
}
function mapStateToProps ({ client }) {
  return { client }
}

export default connect(mapStateToProps)(ClientPush)