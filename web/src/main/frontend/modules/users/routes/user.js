import React from 'react'
import PropTypes from 'prop-types'
import {routerRedux} from 'dva/router'
import {connect} from 'dva'
import {routerPath} from '../../../constants'
import UserList from '../../../components/users/user/userList'

function User({
    location,
    dispatch,
    user
}, context) {
    const listProps = {
        dataSource: user.list,
        pagination: user.pagination,
        onPageChange(page) {
            dispatch(routerRedux.push({
                pathname: routerPath.USERS_USER,
                query: {
                    pageNumber: page.current,
                    pageSize: page.pageSize
                }
            }))
        },
        addUser: () => {
            context.router.push('/users/user/userEdit?type=add');
        },
        onDelete(id) {
            const query = location.query
            const pager = {
                pageNumber: 1,
                pageSize: 10,
                ...query
            }
            dispatch({
                type: 'user/userDelete',
                payload: {
                    id: id,
                    pager
                }
            })
        }
    }
    return (
        <div className='content-inner'>
            <UserList {...listProps}></UserList>
        </div>
    )
}

function mapStateToProps({user}) {
    return {user}
}
User.contextTypes = {
    router: PropTypes.object.isRequired
}
export default connect(mapStateToProps)(User)
