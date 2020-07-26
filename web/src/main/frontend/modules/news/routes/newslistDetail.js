import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { routerRedux } from 'dva/router'
import { routerPath } from '../../../constants'
import { Jt, Immutable } from '../../../utils'
import ListDetail from '../../../components/news/listDetail'

function NewsListDetail({ location, dispatch, newslist },context) {
	const {newslistDetail={}} = newslist
	const ListDetailProps = {
        queryName : location.query.name || '',
        newslistDetail: newslist.newslistDetail,
        goBack(){
            dispatch({
                type:'loadingImgs/changeNum',
                payload:{
                    num:0
                },
            })
			context.router.goBack()
		},
		handleSearch(data){
			const query = location.query
			const queryData = {
				pageSize:10,
				...query,
				...data, //用搜索词覆盖原来query的搜索词
				pageNumber:1, //搜索后一定是从第一页开始
			}
			dispatch(routerRedux.push({
					pathname: routerPath.CLIENT_NEWS_DETAIL,
					query: queryData
			}))
		}
	}
  return (
    <div className='content-inner'>
	  	<ListDetail {...ListDetailProps}></ListDetail>
    </div>
  );
}
NewsListDetail.contextTypes = {
    router: PropTypes.object.isRequired
}
function mapStateToProps ({ newslist }) {
  return { newslist }
}

export default connect(mapStateToProps)(NewsListDetail)
