import React from 'react';
import {Table, Popconfirm, InputNumber, Tag,Select,Popover,message} from 'antd';
import {Link} from 'dva/router';
import {routerPath, artTypes as types} from '../../constants';
import VisibleWrap from '../ui/visibleWrap';
import CheatList from './cheatList';
import er_wei_ma from '../../image/er_wei_ma.png';
import styles from './list.less';
import CopyToClipboard from 'react-copy-to-clipboard';
import QRCode from 'qrcode.react';
class List extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            wgtObj: {}
        };
    }
    showPublishModal(record){
        this.props.showPublishModal(record);
    }
    wgtOnChg(id, weight) {
        const obj = {};
        obj[id] = weight;
        this.props.updateWgtChgObj(obj);
    }
    editHandle(record) {
        this.props.onEdit(record);
    }
    getSync(record) {
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
            }
        } else if (type === 'subject') {
            return {
                pathname: routerPath.TOPIC_DETAIL,
                query: {
                    id: record.id,
                    articleId: record.articleId,
                    isReference: record.isReference
                }
            }
        }
    }
    getEditLink(record, block) {
        const type = record.type;
        if (type === 'live') {
            return {
                pathname: routerPath.LIVE_EDIT,
                query: {
                    id: record.articleId,
                    action: 'edit'
                }
            };
        } else if (type === 'subject') {
            return {
                pathname: routerPath.TOPIC_EDIT,
                query: {
                    id: record.id,
                    action: 'update'
                }
            };
        } else {
            return {
                pathname: routerPath.ARTICLE_EDIT,
                query: {
                    id: record.id,
                    action: 'edit'
                }
            };
        }
    }
    preview(record){
        this.props.preview(record);
    }
    getTtTpl(record) {
        const {type, isReference, listTitle} = record;
        const getEditLink = this.getEditLink.bind(this);
        if(this.props.listType=='3'){
            return <p>
            <span>{listTitle}</span>
            </p>
        }
        else if (type === 'live' || type === 'subject') {
            return <p>
                <Link to={this.getEnterLink(record)}>{listTitle}</Link>
            </p>
        } else if (!isReference) {
            return <p>
                <VisibleWrap permis="view" replace={listTitle}>
                    <Link to={getEditLink(record)}>{listTitle}</Link>
                </VisibleWrap>
            </p>
        } else {
            return <p>
                <VisibleWrap permis="view" replace={listTitle}>
                    <a onClick={() => this.editHandle(record)}>{listTitle}</a>
                </VisibleWrap>
            </p>
        }
    }

    positionChange(id,value){
        const data = {id:id,position:value}
        this.props.updatePosition(data);
    }
    getColumns() {
        const {cfg, wgtChgObj, onOffArt, showPushModal, showSyncModal, deleteArt, viewComment, dataSource,listType,onLineArt,offLineArt,restore,preview,modelId,showPublishModal} = this.props;
        const getEnterLink = this.getEnterLink.bind(this);
        const getEditLink = this.getEditLink.bind(this);
        const editHandle = this.editHandle.bind(this);
        const getSync = this.getSync.bind(this);
        let columns;
        console.log("modelId modelIdmodelIdmodelId",modelId)
        columns = [
            {
                site :1,
                title: '栏目',
                dataIndex: 'categoryName',
                width:'140',
                key: 'categoryName',
          
            },
            {
                title: '列表标题',
                dataIndex: 'listTitle',
                width: '140',
                key: 'title',
                render: (text, record) => {
                    const imgurl = record.realImageUrl ? record.realImageUrl : record.imageUrl
                    const images = imgurl
                        ? imgurl.split(',http')
                        : [];
                    for (var i = 1; i < images.length; i++) {
                        images[i] = "http" + images[i];
                    }
                    return (
                        <div className="tt-td" style={{ minHeight: 45}}>
                        <div className="tt-td-title" style={{ float: 'left' }} >
                            {this.getTtTpl(record)}
                            {images.map((image, index) => { return <img className="tt-td-title-img" key={index} src={image} /> })}
                        </div>
                        {record.shareUrl&& record.delFlag === 0?(
                        <div className="tt-td-right">
                            <div style={{ marginLeft:20}}>
                                <Popover placement="right" content={(<QRCode
                                    value={record.shareUrl}
                                    fgColor="#000000"
                                    className="tt-td-er-wei-ma"
                                />)}>
                                    <QRCode
                                        value={record.shareUrl}  //value参数为生成二维码的链接
                                        size={40}
                                        fgColor="#000000"
                                        className="tt-td-er-wei-ma"  //二维码的颜色
                                    />
                                </Popover></div>
                            <div style={{ flex: 1 }}>
                                <CopyToClipboard text={record.shareUrl}
                                    onCopy={() => { message.success('已复制文章链接到粘贴板') }}>

                                    <span className="tt-td-copy-btn">复制链接</span>
                                </CopyToClipboard>
                            </div>
                        </div>
                        ):null}
                    </div>
                    );
                }
            },
            {
                title: '权重',
                dataIndex: 'weight',
                key: 'weight',
                width: 70,
                sorter: true,
                render: (text, record) => {
                    let value = text;
                    if (typeof wgtChgObj[record.id] !== 'undefined') {
                        value = wgtChgObj[record.id];
                    }
                    return (<InputNumber value={value} min={0} onChange={(weight) => this.wgtOnChg(record.id, weight)}/>);
                }
            },
            {
                title: '固定位置',
                dataIndex: 'position',
                key: 'position',
                width: 80,
                render: (text, record) => {
                    const defaultValue = text?String(text):"0";
                    return (
                        <Select disabled={record.delFlag === 2 || record.delFlag === 1?true:false} key={defaultValue} defaultValue={defaultValue} style={{ width: 80 }} onChange={(data) => this.positionChange(record.id, data)}>
                            <Select.Option key="0" value="0">无</Select.Option>
                            <Select.Option key="1" value="1">第一位</Select.Option>
                            <Select.Option key="2" value="2">第二位</Select.Option>
                            <Select.Option key="3" value="3">第三位</Select.Option>
                            <Select.Option key="4" value="4">第四位</Select.Option>
                            <Select.Option key="5" value="5">第五位</Select.Option>
                        </Select>
                    );
                }
            },
            {
                title: '类型',
                dataIndex: 'type',
                key: 'type',
                width: 60,
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
                            {tags.map((tag, index) => {
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
            },
            {
                title: '访问量',
                dataIndex: 'hits',
                key: 'hits',
                width: 85,
                sorter: true
            },
            {
                title: '评论数',
                dataIndex: 'comments',
                key: 'comments',
                width: 85,
                sorter: true
            },
            {
                title: '发布时间',
                dataIndex: 'publishDate',
                key: 'publishDate',
                width: 160,
                sorter: true,
                render: (text, record) => {
                    if (text) {
                        return (new Date(text)).format('yyyy-MM-dd hh:mm:ss')
                    } else {
                        return ''
                    }
                }
            },
            {
                title: '发稿人',
                dataIndex: 'contributor',
                key: 'contributor',
                width: 80
            },
            {
                title: '操作',
                key: 'action',
                width: 240,
                render: (text, record) => (
                    <p>
                        {
                            record.type != 'subject'?
                            <span>
                            <Link onClick={viewComment.bind(this, record)}>评论</Link>
                            <span className="ant-divider"/>
                        </span>:null
                        }
                       
                        {/* <VisibleWrap permis="view">
                            <span>

                                <a onClick={() => preview(record)}>预览</a>
                                <span className="ant-divider"/>
                            </span>
                        </VisibleWrap> */}
                        {
                            record.type === 'live' || record.type === 'subject'
                                ?   <span>
                                    <Link to={getEnterLink(record)}>进入</Link>
                                    <span className="ant-divider"/>
                                </span>
                                :   null
                        }
                        {
                            !record.isReference
                                ?   <VisibleWrap permis="view">
                                    <span>
                                        <Link to={getEditLink(record)}>编辑</Link><span className="ant-divider"/>
                                    </span>
                                </VisibleWrap>
                                :   <VisibleWrap permis="view">
                                    <span>
                                        <a onClick={() => editHandle(record)}>编辑</a><span className="ant-divider"/>
                                    </span>
                                </VisibleWrap>
                        }
                        {
                            record.delFlag == 0
                                ?   <VisibleWrap permis="edit:push">
                                    <span>
                                        <a onClick={() => showPushModal(record)}>推送</a><span className="ant-divider"/>
                                    </span>
                                </VisibleWrap>
                                : null
                        }
                        <VisibleWrap permis="edit:online">
                            <span>

                                {record.delFlag === 0 ?
                                    <Popconfirm title="确定下线吗？" onConfirm={() => onOffArt(record.id)}><a>下线</a></Popconfirm>
                                    : <a onClick={() => onOffArt(record.id)}>上线</a>
                                }

                                <span className="ant-divider"/>
                            </span>
                        </VisibleWrap>
                        <VisibleWrap permis="edit:online">
                            <span>

                                {record.delFlag ===2 ?
                                     <a onClick={() => showPublishModal(record)}>定时发布</a>:''
                                }
                                {record.delFlag ===2 ?
                                <span className="ant-divider"/>:''}
                            </span>
                        </VisibleWrap>
                        <VisibleWrap permis="edit:delete">
                            <span>
                                <Popconfirm title="确定删除吗？" onConfirm={() => deleteArt(record.id)}>
                                    <a>删除</a>
                                </Popconfirm>
                               
                            </span>
                        </VisibleWrap>

                        {/* <VisibleWrap permis="view">
                            <span>

                                {
                                    record.type!='subject'&&record.type!='live'&&(!record.moreVideos||record.moreVideos.indexOf(",")<0)?<a onClick={() => showSyncModal(record)}>同步</a>:''
                                }
                              
                            </span>
                        </VisibleWrap> */}
                       

                    </p>
                )
            }
        ];
        // if(modelId==8){

        // }
       
        if(listType==3){
            columns.splice(2,2);
            columns.splice(6,0,{
                            title: '删除时间',
                            dataIndex: 'deleteDate',
                            key: 'deleteDate',
                            width: 150,
                            sorter: true,
                            render: (text, record) => {
                                if (text) {
                                    return (new Date(text)).format('yyyy-MM-dd hh:mm:ss')
                                } else {
                                    return ''
                                }
                            }
                        },);

                        columns.splice(8,0,{
                                        title: '删除人',
                                        dataIndex: 'deletOr',
                                        key: 'deletOr',
                                        width: 80
                                    });            
                                    columns.splice(9,1, {
                                                    title: '操作',
                                                    key: 'action',
                                                    width: 240,
                                                    render: (text, record) => (
                                                        <p>
                                                            <span>
                                                            <Popconfirm title="确定还原吗？" onConfirm={() => restore(record.id)}>
                                                                        <a>还原</a>
                                                             </Popconfirm>
                                                              
                                                            </span>
                                                            
                                                         
                                                        </p>
                                                    )
                                                });     
                                            }         
                                            else if(listType==5){
                                                columns.splice(2,2);
                                                columns.splice(3,3, {
                                                    title: '定时发布时间',
                                                    dataIndex: 'fixedPublishDate',
                                                    key: 'fixedPublishDate',
                                                    width: 150,
                                                    sorter: true,
                                                    render: (text, record) => {
                                                        if (text) {
                                                            return (new Date(text)).format('yyyy-MM-dd hh:mm:ss')
                                                        } else {
                                                            return ''
                                                        }
                                                    }
                                                });
                                                columns.splice(5,1,{
                                                    title: '操作',
                                                    key: 'action',
                                                    width: 240,
                                                    render: (text, record) => (
                                                        <p>
                                                            <span>
                                                            <Popconfirm title="确定发布吗？" onConfirm={() => onLineArt(record.id)}>
                                                                        <a>提前发布</a>
                                                                    </Popconfirm>
                                                                <span className="ant-divider"/>
                                                            </span>
                                                            
                                                            <span>
                                                            <Popconfirm title="确定取消吗？" onConfirm={() => offLineArt(record.id)}>
                                                                        <a>取消发布</a>
                                                             </Popconfirm>
                                                               
                                
                                                            </span>
                                                        </p>
                                                    )
                                                } );
                                                
                                }      
                                                              
         else if(modelId&&modelId==8){

            columns.splice(3,1);
            columns.splice(4,0, {
                title: '帮的状态',
                dataIndex: 'helpStatus',
                key: 'helpStatus',
                width: 100,
                render: (text, record) => (
                    <p>
                        {record.isReference==true?'募集中':(text==1?'募集中':(text==2?'募集结束':'捐助反馈'))}
                        
                     
                    </p>
                )
            });

            columns.splice(7,0, {
                title: '爱心值',
                dataIndex: 'likes',
                key: 'likes',
                width: 100
               
            });
        }
        // if(listType==4){
        //     //定时发布
        //      columns = [
        //         {
        //             title: '栏目',
        //             dataIndex: 'categoryName',
        //             key: 'categoryName',
        //             width: 80
        //         },
        //         {
        //             title: '列表标题',
        //             dataIndex: 'listTitle',
        //             key: 'title',
        //             render: (text, record) => {
        //                 const images = record.imageUrl
        //                     ? record.imageUrl.split(',http')
        //                     : [];
        //                 for (var i = 1; i < images.length; i++) {
        //                     images[i] = "http" + images[i];
        //                 }
        //                 return (
        //                     <div className="tt-td">
    
        //                         {this.getTtTpl(record)}
        //                         {images.map((image, index) => {
        //                             return <img key={index} src={image}/>;
        //                         })
        //                         }
        //                     </div>
        //                 );
        //             }
        //         },
               
                
        //         {
        //             title: '类型',
        //             dataIndex: 'type',
        //             key: 'type',
        //             width: 60,
        //             render: (text, record) => {
        //                 if (!text) {
        //                     return '';
        //                 }
        //                 let tags = [text];
        //                 if (record.isReference) {
        //                     tags.push('cite');
        //                 }
        //                 return (
        //                     <div className="tag-td">
        //                         {tags.map((tag, index) => {
        //                             if (types[tag]) {
        //                                 return <Tag key={index} color={types[tag].color}>{types[tag].label}</Tag>
        //                             } else {
        //                                 return <Tag key={index}>{tag}</Tag>
        //                             }
        //                         })
        //                         }
        //                     </div>
        //                 );
        //             }
        //         },
                
        //         {
        //             title: '定时发布时间',
        //             dataIndex: 'publishDate',
        //             key: 'publishDate',
        //             width: 150,
        //             sorter: true,
        //             render: (text, record) => {
        //                 if (text) {
        //                     return (new Date(text)).format('yyyy-MM-dd hh:mm:ss')
        //                 } else {
        //                     return ''
        //                 }
        //             }
        //         },
        //         {
        //             title: '发稿人',
        //             dataIndex: 'contributor',
        //             key: 'contributor',
        //             width: 80
        //         },
        //         {
        //             title: '操作',
        //             key: 'action',
        //             width: 200,
        //             render: (text, record) => (
        //                 <p>
        //                     <span>
        //                     <Popconfirm title="确定发布吗？" onConfirm={() => onLineArt(record.id)}>
        //                                 <a>提前发布</a>
        //                             </Popconfirm>
        //                         <span className="ant-divider"/>
        //                     </span>
                            
        //                     <span>
        //                     <Popconfirm title="确定取消吗？" onConfirm={() => offLineArt(record.id)}>
        //                                 <a>取消发布</a>
        //                      </Popconfirm>
                               

        //                     </span>
        //                 </p>
        //             )
        //         }
        //     ];
        // } 
        // else if(listType==3){
        //     columns = [
        //         {
        //             title: '栏目',
        //             dataIndex: 'categoryName',
        //             key: 'categoryName',
        //             width: 80
        //         },
        //         {
        //             title: '列表标题',
        //             dataIndex: 'listTitle',
        //             key: 'title',
        //             render: (text, record) => {
        //                 const images = record.imageUrl
        //                     ? record.imageUrl.split(',http')
        //                     : [];
        //                 for (var i = 1; i < images.length; i++) {
        //                     images[i] = "http" + images[i];
        //                 }
        //                 return (
        //                     <div className="tt-td">
    
        //                         {this.getTtTpl(record)}
        //                         {images.map((image, index) => {
        //                             return <img key={index} src={image}/>;
        //                         })
        //                         }
        //                     </div>
        //                 );
        //             }
        //         },
               
                
        //         {
        //             title: '类型',
        //             dataIndex: 'type',
        //             key: 'type',
        //             width: 60,
        //             render: (text, record) => {
        //                 if (!text) {
        //                     return '';
        //                 }
        //                 let tags = [text];
        //                 if (record.isReference) {
        //                     tags.push('cite');
        //                 }
        //                 return (
        //                     <div className="tag-td">
        //                         {tags.map((tag, index) => {
        //                             if (types[tag]) {
        //                                 return <Tag key={index} color={types[tag].color}>{types[tag].label}</Tag>
        //                             } else {
        //                                 return <Tag key={index}>{tag}</Tag>
        //                             }
        //                         })
        //                         }
        //                     </div>
        //                 );
        //             }
        //         },
        //         {
        //             title: '访问量',
        //             dataIndex: 'hits',
        //             key: 'hits',
        //             width: 75,
        //             sorter: true
        //         },
        //         {
        //             title: '评论数',
        //             dataIndex: 'comments',
        //             key: 'comments',
        //             width: 75,
        //             sorter: true
        //         },
        //         {
        //             title: '发布时间',
        //             dataIndex: 'publishDate',
        //             key: 'publishDate',
        //             width: 150,
        //             sorter: true,
        //             render: (text, record) => {
        //                 if (text) {
        //                     return (new Date(text)).format('yyyy-MM-dd hh:mm:ss')
        //                 } else {
        //                     return ''
        //                 }
        //             }
        //         },
        //         {
        //             title: '删除时间',
        //             dataIndex: 'deleteDate',
        //             key: 'deleteDate',
        //             width: 150,
        //             sorter: true,
        //             render: (text, record) => {
        //                 if (text) {
        //                     return (new Date(text)).format('yyyy-MM-dd hh:mm:ss')
        //                 } else {
        //                     return ''
        //                 }
        //             }
        //         },
        //         {
        //             title: '发稿人',
        //             dataIndex: 'contributor',
        //             key: 'contributor',
        //             width: 80
        //         },
        //         {
        //             title: '删除人',
        //             dataIndex: 'deletOr',
        //             key: 'deletOr',
        //             width: 80
        //         },
        //         {
        //             title: '操作',
        //             key: 'action',
        //             width: 200,
        //             render: (text, record) => (
        //                 <p>
        //                     <span>
        //                     <Popconfirm title="确定还原吗？" onConfirm={() => restore(record.id)}>
        //                                 <a>还原</a>
        //                      </Popconfirm>
                              
        //                     </span>
                            
                         
        //                 </p>
        //             )
        //         }
        //     ];
        // }
        // else {

        // }
       
        return columns
    }

    render() {
        const {
            loading,
            dataSource,
            pagination,
            selArts,
            onPageChange,
            selectArt
        } = this.props;

        const selectedRowKeys = selArts.map((art) => art.id);

        const rowSelection = {
            selectedRowKeys,
            onChange: (ids, arts) => {
                selectArt(arts);
            }
        };
        return (
            <Table
                simple
                bordered
                // scroll={{x: 1200}}
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
export default List;
