import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'dva'
import { Jt, Immutable } from '../../../utils'
import { routerPath } from '../../../constants'
import List from '../../../components/category/list'
import {message} from  'antd';

function CategoryList({ location, dispatch, category }, context) {
    const { loading, catgModels, catgs } = category
    const listProps = {
        loading,
        catgModels,
        dataSource: catgs,
        deleteCatg(id) {
            dispatch({
                type: 'category/effect:delete:catg',
                payload: {
                    catg: { id },
                    location
                }
            })
        },
        updateCatg(catg) {
            dispatch({
                type: 'category/effect:update:catg',
                payload: { catg, location }
            })
        },
        onOffCatg(record,flg) {
            if(flg){
                if(record.menuList&&record.menuList.length>0){
                    message.error("该栏目已经关联客户端菜单，请先下线客户端菜单中的该栏目")
                    return
                }
            }

            dispatch({
                type: 'category/effect:onOff:catg',
                payload: {
                    catg: { id:record.id },
                    location
                }
            })
        },
        updateCatgs(catgs) {
            dispatch({
                type: 'category/effect:update:catgs',
                payload: {
                    catgs: catgs,
                    location
                }
            })
        }
    }
    return (
        <div className="content-inner">
            <List {...listProps}/>
        </div>
    )
}

CategoryList.contextTypes = {
    router: PropTypes.object.isRequired
}

CategoryList.propTypes = {
    location: PropTypes.object,
    dispath: PropTypes.func,
    category: PropTypes.object
}

function mapStateToProps({category}) {
    return {
        category
    }
}

export default connect(mapStateToProps)(CategoryList)
