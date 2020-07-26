import React from 'react';
import PropTypes from 'prop-types';
import {routerRedux} from 'dva/router';
import {connect} from 'dva';
import {routerPath} from '../../../constants';
import List from '../../../components/analysis/send-list';
import Search from '../../../components/analysis/send-search';
import {Jt} from '../../../utils';

function Sending({
                 location,
                 dispatch,
                 anls_sending
             }, context) {
    let {
        loading,
        list,
        query,
        pagination
    } = anls_sending;
    const searchProps = {
        query,
        searchHandle: (params) => {
            dispatch(routerRedux.push({
                pathname: routerPath.ANLS_SENDING,
                query: params
            }));
        }
    };
    //
    const listProps = {
        loading,
        dataSource: list,
        pagination:pagination

    };

    return (
        <div className='content-inner'>
            <Search {...searchProps}/>
            <List {...listProps}/>
        </div>
    );
}

Sending.contextTypes = {
    router: PropTypes.object.isRequired
};

function mapStateToProps({anls_sending}) {

    return {
        anls_sending
    };
}
export default connect(mapStateToProps)(Sending)
