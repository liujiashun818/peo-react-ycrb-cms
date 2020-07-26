import React from 'react'
import PropTypes from 'prop-types';
import {
    message,
    Modal,
    Table,
    Popconfirm,
    Form,
    Input,
    InputNumber,
    Radio,
    Button,
    Tag
} from 'antd';

import {artTypes as types} from '../../constants';
import CiteArtSearch from './citeArtSearch';
import {Immutable} from '../../utils';

class CiteArtModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            selArts: [],
            flag :false
        };
    }

    componentWillReceiveProps(nextProps) {
        this.setState({flag:false})
        if (!nextProps.visible) {
            this.state.selArts = [];
        }
    }

    onOk() {
        const {catgId, onCite, naq} = this.props;
        if (!this.state.selArts.length) {
            return message.error('请选择您引入的文章！');
        }
        if(this.state.flag == false){
            this.state.flag = true;
            this.setState({flag:true},()=>{
                const obj = {
                    categoryId: catgId,
                    list: this.state.selArts,
                }
                if (naq && naq.block) {
                    obj.block = naq.block;
                }

                onCite(obj);
            })

        }

    }

    getColumns() {
        const {artTypes} = this.props;
        return [
            {
                title: '栏目',
                dataIndex: 'categoryName',
                key: 'categoryName',
                width: 100
            }, {
                title: '标题',
                dataIndex: 'title',
                key: 'title'

            }, {
                title: '权重',
                dataIndex: 'weight',
                key: 'weight',
                width: 100
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
                                    if (types[tag]) {
                                        return <Tag key={index} color={types[tag].color}>{types[tag].label}</Tag>
                                    } else {
                                        return <Tag key={index}>{tag}</Tag>
                                    }
                                })
                            }
                        </div>
                    );
                }
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
            }
        ];
    }

    selectArt(selArts) {
        const cloneData = Immutable.fromJS(selArts).toJS()
        cloneData.map((item,index)=>{
            item.weight = 0
        })
        this.setState({selArts:cloneData});
    }

    render() {
        const {
            catgs,
            visible,
            loading,
            pagination,
            dataSource,
            onCancel,
            onSearch,
            onPageChange
        } = this.props;
        const {selArts} = this.state;

        const modalProps = {
            visible,
            title: '文章引入',
            width: '80%',
            onCancel,
            onOk: this.onOk.bind(this)
        };

        const searchProps = {
            catgs,
            visible,
            onSearch
        };

        const rowSelection = {
            selectedRowKeys: selArts.map(art => art.id),
            onChange: (ids, arts) => {
                this.selectArt(arts)
            }
        };
        return (
            <Modal {...modalProps}>
                <CiteArtSearch {...searchProps}/>
                <Table
                    simple
                    bordered
                    scroll={{x: 1200, y: 400}}
                    columns={this.getColumns()}
                    dataSource={dataSource}
                    pagination={pagination}
                    rowKey={record => record.id}
                    rowSelection={rowSelection}
                    loading={loading}
                    onChange={onPageChange}
                />
            </Modal>
        );
    }
}

export default CiteArtModal;
