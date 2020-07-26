import React from 'react'
import PropTypes from 'prop-types';
import {Table} from 'antd';
import {FIELED_SEND_KEYS} from '../../constants';

class List extends React.Component {
    constructor(props) {
        super(props);
    }

    getColumns() {
        return [{
            title: FIELED_SEND_KEYS.USERNAME,
            dataIndex: 'userName',
            key: 'userName',
            width: 200
        },{
            title: FIELED_SEND_KEYS.ORG,
            dataIndex: 'officeName',
            key: 'officeName',
            width: 200
        },{
            title: FIELED_SEND_KEYS.SENDCOUNT,
            dataIndex: 'articleCount',
            key: 'articleCount',
            width: 100
        },{
            title: FIELED_SEND_KEYS.COMMENTCOUNT,
            dataIndex: 'commentsCount',
            key: 'commentsCount',
            width: 100
        },{
            title: FIELED_SEND_KEYS.CLICKCOUNT,
            dataIndex: 'hitsCount',
            key: 'hitsCount',
            width: 100
        }];
    }

    render() {
        const {loading, dataSource} = this.props;
        return (

            <Table
                scroll={{x: 1100}}
                columns={this.getColumns()}
                loading={loading}
                dataSource={dataSource}
                pagination={false}
            />

        );
    }
}

export default List;
