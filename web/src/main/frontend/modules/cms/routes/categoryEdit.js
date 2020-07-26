import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'dva'
import { Jt, Immutable } from '../../../utils'
import { routerPath } from '../../../constants'
import Edit from '../../../components/category/edit'

function CategoryEdit({ history, location, dispatch, category }, context) {
    let { catgs, catgModels, curCatg, offices, catgViews } = category
    const editProps = {
        catgs,
        catgModels,
        curCatg,
        offices,
        catgViews,
        updateCatg(catg) {
            dispatch({
                type: 'category/effect:update:catg',
                payload: {
                    catg,
                    success: () => {
                        context.router.push(routerPath.CATEGORY_LIST)
                    }
                }
            })
        },
        createCatg(catg) {
            dispatch({
                type: 'category/effect:create:catg',
                payload: {
                    catg,
                    success: () => {
                        context.router.push(routerPath.CATEGORY_LIST)
                    }
                }
            })
        }
    }
    return (
        <div className="content-inner">
            <Edit {...editProps}/>
        </div>
    )
}

CategoryEdit.contextTypes = {
    router: PropTypes.object.isRequired
}

CategoryEdit.propTypes = {
    location: PropTypes.object,
    dispath: PropTypes.func,
    category: PropTypes.object
}

function mapStateToProps({category}) {
    return {
        category
    }
}

export default connect(mapStateToProps)(CategoryEdit)
