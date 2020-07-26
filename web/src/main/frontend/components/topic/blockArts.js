import React from 'react'
import PropTypes from 'prop-types';
import {
    Table,
    Popconfirm,
    InputNumber,
    Tag
} from 'antd';
import {
    Link
} from 'dva/router';
import {
    routerPath,
    artTypes as types
} from '../../constants';
import VisibleWrap from '../ui/visibleWrap';

class BlockArts extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            wgtObj: {}
        }
    }

    wgtOnChg(id, weight) {
        const obj = {};
        obj[id] = weight;
        this.props.updateWgtChgObj(obj);
    }

    editHandle(record) {
        this.props.onEdit(record);
    }

    getEnterLink(record) {
        const type = record.type;
        if (type === 'live') {
            return {
                pathname: routerPath.LIVE_ROOM,
                query: {
                    id: record.articleId,
                    action: 'edit'
                }
            };
        } else if (type === 'subject') {
            return {
                pathname: routerPath.TOPIC_DETAIL,
                query: {
                    id: record.id,
                    articleId: record.articleId,
                    isReference: record.isReference
                }
            };
        }
    }

    getEditLink(record, block) {
        const type = record.type;
        if (type === 'live') {
            return {
                pathname: routerPath.LIVE_EDIT,
                query: {
                    id: record.articleId,
                    action: 'edit',
                    source: 'subject'
                }
            };
        } else if (type === 'subject') {
            return {
                pathname: routerPath.TOPIC_EDIT,
                query: {
                    id: record.id,
                    action: 'update',
                    source: 'subject'
                }
            };
        } else {
            return {
                pathname: routerPath.ARTICLE_EDIT,
                query: {
                    id: record.id,
                    action: 'edit',
                    source: 'subject',
                    blockId: block.id
                }
            };
        }
    }

    getTtTpl(record, block) {
        const {
            type,
            isReference,
            listTitle
        } = record;
        const getEditLink = this.getEditLink.bind(this);
        if (type === 'live' || type === 'subject') {
            return <p><Link to={this.getEnterLink(record)}>{listTitle}</Link></p>
        } else if (!isReference) {
            return <p><VisibleWrap permis="view" replace={listTitle}><Link to={getEditLink(record, block)}>{listTitle}</Link></VisibleWrap></p>
        } else {
            return <p><VisibleWrap permis="view" replace={listTitle}><a onClick={() => this.editHandle(record)}>{listTitle}</a></VisibleWrap></p>
        }
    }

    getColumns() {
        const {
            loading,
            selArts,
            artTypes,
            dataSource,
            pagination,
            onPageChange,
            updateWgtChgObj,
            onOffArt,
            onDelete,
            curBlock,
            wgtChgObj
        } = this.props;

        return [{
            title: '列表标题',
            dataIndex: 'listTitle',
            key: 'listTitle',
            width: 150,
            render: (text, record) => {
                const images = record.imageUrl ? record.imageUrl.split(',http') : [];
                for (var i = 1; i < images.length; i++) {
                    images[i] = "http" + images[i];
                }
                return (  
                        <div className="tt-td" style={{ minHeight: 45}}>
                         <div className="tt-td-title" style={{ float: 'left' }} >
                            {this.getTtTpl(record, curBlock)}
                            {
                                images.map((image, index) => {
                                    return <img width={120} height={80} key={index} src={image} />;
                                })
                            }
                        </div>
                        </div>
                );
            }
        }, {
            title: '权重',
            dataIndex: 'weight',
            key: 'weight',
            width: 100,
            render: (text, record) => {
                let value = text;
                if (typeof wgtChgObj[record.id] !== 'undefined') {
                    value = wgtChgObj[record.id];
                }
                return (
                    <InputNumber
                        value={value}
                        min={0}
                        onChange={(weight) => this.wgtOnChg(record.id, weight)}
                    />
                );
            }
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            width: 100,
            render: (text, record) => {
                if (!text) {
                    return '';
                }
                let tags = [text];
                if (record.isReference) {
                    tags.push('cite');
                }
                return (
                    <div className="tag-td">
                    {
                        tags.map((tag, index) => {
                            if(types[tag]) {
                                return <Tag key={index} color={types[tag].color}>{types[tag].label}</Tag>
                            }
                            else {
                                return <Tag key={index}>{tag}</Tag>
                            }
                        })
                    }
                    </div>
                );
            }
        }, {
            title: '访问量',
            dataIndex: 'hits',
            key: 'hits',
            width: 80,
            sorter: true
        }, {
            title: '评论数',
            dataIndex: 'comments',
            key: 'comments',
            width: 80,
            sorter: true
        }, {
            title: '发布时间',
            dataIndex: 'publishDate',
            key: 'publishDate',
            width: 150,
            render: (text, record) => {
                if (text) {
                    return (new Date(text)).format('yyyy-MM-dd hh:mm:ss')
                } else {
                    return ''
                }
            }
        }, {
            title: '发稿人',
            dataIndex: 'contributor',
            key: 'contributor',
            width: 150
        }, {
            title: '操作',
            key: 'action',
            width: 150,
            render: (text, record) => (
                <p>
                    {
                        record.type === 'live' || record.type === 'subject'
                        ? <span>
                            <Link to={this.getEnterLink(record)}>进入</Link>
                            <span className="ant-divider"></span>
                          </span>
                        : null
                    }
                    {
                        !record.isReference
                        ? <VisibleWrap permis="view"><span><Link to={this.getEditLink(record, curBlock)}>编辑</Link><span className="ant-divider"/></span></VisibleWrap>
                        : <VisibleWrap permis="view"><span><a onClick={() => this.editHandle(record)}>编辑</a><span className="ant-divider"/></span></VisibleWrap>
                    }
                    <VisibleWrap permis="edit:online">
                        <span>
                            <a onClick={() => onOffArt(record.id)}>
                                { record.delFlag === 0 ? '下线' : '上线' }
                            </a>
                            <span className="ant-divider"></span>
                        </span>
                    </VisibleWrap>
                    <VisibleWrap permis="edit:delete">
                        <Popconfirm title="确定删除吗？" onConfirm={() => onDelete(record.id)}>
                            <a>删除</a>
                        </Popconfirm>
                    </VisibleWrap>
                </p>
            )
        }];
    }

    render() {
        const {
            loading,
            selArts,
            dataSource,
            pagination,
            onPageChange,
            selectArt
        } = this.props;

        const selectedRowKeys = selArts.map((art) => art.id)

        const rowSelection = {
            selectedRowKeys,
            onChange: function(ids, arts) {
                selectArt(arts)
            }
        };
        return (
            <Table
                simple
                bordered
                // scroll={{ x: 1200 }}
                columns={this.getColumns()}
                dataSource={dataSource}
                loading={loading}
                onChange={onPageChange}
                pagination={pagination}
                rowKey={record => record.id}
                rowSelection={rowSelection}
            />
        );
    }
}

export default BlockArts;
