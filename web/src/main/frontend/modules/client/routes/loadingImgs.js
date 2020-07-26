import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { routerRedux } from 'dva/router'
import { routerPath } from '../../../constants'
import { Jt, Immutable } from '../../../utils'
import LoadingImgsList from '../../../components/client/loadingImgsList'
import LoadingImgsToolBar from '../../../components/client/loadingImgsToolBar'

function LoadingImgs({ location, dispatch, loadingImgs },context) {
	const listProps = {
		dataSource : loadingImgs.list,
		pagination: loadingImgs.pagination,
	    onPageChange(page){
	        const query = location.query
	        dispatch(routerRedux.push({
	            pathname: routerPath.LOADINGIMGS,
	            query: {
	                ...query,
	                pageNumber: page.current,
	                pageSize: page.pageSize,
	            }
	        }))
    	},
	    onChangeDelflag:function(id,delFlag){
    	  const query = location.query
	      if(delFlag==0){
	        delFlag=1
	      }else{
	        delFlag=0
	      }
	      dispatch({
	        type: 'loadingImgs/onOff',
	        payload: {
						id:id,
	          // data:{
	          //   delFlag:delFlag
	          // },
	          callback:function(){
  	          	dispatch(routerRedux.push({
	                pathname: routerPath.LOADINGIMGS,
	                query
            	}))
	          }
	        }
	      })
	    },
	    onDelete:function(id,delFlag){
	      const query = location.query
	      dispatch({
	        type: 'loadingImgs/deleteItem',
	        payload: {
	          data:{
	            id:id,
	          },	          
	          callback:function(){
  	          	dispatch(routerRedux.push({
	                pathname: routerPath.LOADINGIMGS,
	                query
            	}))
	          }
	        }
	      })
	    }
	}
	const toolBarProps = {
		queryName : location.query.name || '',
		handleSearch(data){
			const query = location.query
			const queryData = {
				pageSize:10,
				...query,
				...data, //用搜索词覆盖原来query的搜索词
				pageNumber:1, //搜索后一定是从第一页开始
			}
            dispatch(routerRedux.push({
                pathname: routerPath.LOADINGIMGS,
                query: queryData
            }))
		}
	}
  return (
    <div className='content-inner'>
	  <LoadingImgsToolBar {...toolBarProps}></LoadingImgsToolBar>
      <LoadingImgsList {...listProps}></LoadingImgsList>
    </div>
  );
}
LoadingImgs.contextTypes = {
    router: PropTypes.object.isRequired
}
function mapStateToProps ({ loadingImgs }) {
  return { loadingImgs }
}

export default connect(mapStateToProps)(LoadingImgs)
