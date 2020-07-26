import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { routerRedux } from 'dva/router'
import { routerPath } from '../../../constants'
import { Jt, Immutable } from '../../../utils'
import NewsLists from '../../../components/news/newsLists'
import NewsToolBar from '../../../components/news/newsToolBar'

function NewsList({ location, dispatch, newslist },context) {
	const listProps = {
		dataSource : newslist.list,
		pagination: newslist.pagination,
	    onPageChange(page){
					const query = location.query
	        dispatch(routerRedux.push({
	            pathname: routerPath.CLIENT_NEWS,
	            query: {
	                ...query,
	                pageNumber: page.current,
	                pageSize: page.pageSize,
	            }
	        }))
    	},
	    onDelete:function(id,delFlag){
				const query = location.query
	      dispatch({
	        type: 'newslist/deleteItem',
	        payload: {
	          data:{
	            id:id,
	          },	          
	          callback:function(){
  	          	dispatch(routerRedux.push({
	                pathname: routerPath.CLIENT_NEWS,
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
					pathname: routerPath.CLIENT_NEWS,
					query: queryData
			}))
		}
	}
  return (
    <div className='content-inner'>
	  	<NewsToolBar {...toolBarProps}></NewsToolBar>
      <NewsLists {...listProps}></NewsLists>
    </div>
  );
}
NewsList.contextTypes = {
    router: PropTypes.object.isRequired
}
function mapStateToProps ({ newslist }) {
  return { newslist }
}

export default connect(mapStateToProps)(NewsList)
