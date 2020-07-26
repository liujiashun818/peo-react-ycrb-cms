import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router'
import { routerPath } from '../../../constants'
import DictList from '../../../components/sys/dictList'
import DictToolBar from '../../../components/sys/dictToolBar'

function Dict ({location, dispatch, dict },context){
	const listProps = {
		dataSource : dict.list,
		pagination: dict.pagination,
		onDelete(id){
			const query = location.query
			const pager = {
				pageNumber:1,
				pageSize:10,
				...query
			}
            dispatch({
            	type:'dict/dictDeleteId',
            	payload:{
            		id:id,
            		pager
            	}
            })

		},
		onPageChange(page){
            const query = location.query
            dispatch(routerRedux.push({
                pathname: routerPath.DICT,
                query: {
                    ...query,
                    pageNumber: page.current,
                    pageSize: page.pageSize,
                }
            }))
		}
	}
	const toolBarProps = {
		typeData : dict.typeData,
		handleSearch(data){
			const query = location.query
			const queryData = {
				pageSize:10,
				...query,
				...data, //用搜索词覆盖原来query的搜索词
				pageNumber:1, //搜索后一定是从第一页开始
			}
            dispatch(routerRedux.push({
                pathname: routerPath.DICT,
                query: queryData
            }))
		}
	}
	return (
		<div className="content-inner">
			<DictToolBar {...toolBarProps}></DictToolBar>
			<DictList {...listProps}></DictList>
		</div>
		)
}
function mapStateToProps ({ dict }) {
  return { dict }
}

export default connect(mapStateToProps)(Dict)