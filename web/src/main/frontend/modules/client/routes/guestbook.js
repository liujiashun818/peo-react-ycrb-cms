import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { routerRedux } from 'dva/router'
import { routerPath } from '../../../constants'
import { Jt, Immutable } from '../../../utils'
import GuestList from '../../../components/client/guestList'
import GuestToolBar from '../../../components/client/guestToolBar'

function Guestbook({ location, dispatch, guestbook },context) {
	const listProps = {
		dataSource : guestbook.list,
		pagination: guestbook.pagination,
	    onPageChange(page){
	        const query = location.query
	        dispatch(routerRedux.push({
	            pathname: routerPath.GUESTBOOK,
	            query: {
	                ...query,
	                pageNumber: page.current,
	                pageSize: page.pageSize,
	            }
	        }))
    	},
	    onDelete:function(id){
	      const query = location.query
	      dispatch({
	        type: 'guestbook/deleteItem',
	        payload: {
	          data:{
	            id:id,
	          },	          
	          callback:function(){
  	          	dispatch(routerRedux.push({
	                pathname: routerPath.GUESTBOOK,
	                query
            	}))
	          }
	        }
	      })
	    }		
	}
	const toolBarProps = {
		queryContent : location.query.content || '',
		beginTime: location.query.beginTime || '', 
    	endTime: location.query.endTime || '', 
	    handleSearch(data){
	      const query = location.query
	      const queryData = {
	        pageSize:10,
	        ...query,
	        ...data, //用搜索词覆盖原来query的搜索词
	        pageNumber:1, //搜索后一定是从第一页开始
	      }
	      dispatch(routerRedux.push({
	          pathname: routerPath.GUESTBOOK,
	          query: queryData
	      }))
	    }
	}
	  return (
	    <div className='content-inner'>
			<GuestToolBar {...toolBarProps}></GuestToolBar>
		 	<GuestList {...listProps}></GuestList>
	    </div>
	  );
}
Guestbook.contextTypes = {
    router: PropTypes.object.isRequired
}
function mapStateToProps ({ guestbook }) {
  return { guestbook }
}

export default connect(mapStateToProps)(Guestbook)