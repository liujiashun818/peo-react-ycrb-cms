import React from 'react'
import PropTypes from 'prop-types';
import {routerRedux} from 'dva/router';
import {connect} from 'dva';
import {routerPath} from '../../../constants';
import BlockTabs from '../../../components/topic/blockTabs';
import BlockSearch from '../../../components/topic/blockSearch';
import BlockToolbar from '../../../components/topic/blockToolbar';
import BlockArts from '../../../components/topic/blockArts';
import BlockEditModal from '../../../components/topic/blockEditModal';
import CiteArtModal from '../../../components/cms/citeArtModal';
import EditArtModal from '../../../components/cms/editArtModal';

let uuid = 0;

function TopicDetail({
    location,
    dispatch,
    cms,
    topic
}, context) {

    const btProps = {
        blocks: topic.blocks,
        curBlock: topic.curBlock,
        addBlock: () => {
            dispatch({
                type: 'topic/effect:show:bem',
                payload: {
                    blockOpType: 'add'
                }
            });
        },
        deleteBlock: (id) => {
            dispatch({
                type: 'topic/effect:delete:block',
                payload: {
                    id,
                    location
                }
            });
        },
        switchBlock: (id) => {
            dispatch({type: 'topic/effect:switch:block', payload: {
                    id
                }});
        }
    };

    const bsProps = {
        query: topic.baQuery,
        updateBlock: () => {
            dispatch({
                type: 'topic/effect:show:bem',
                payload: {
                    blockOpType: 'update'
                }
            });
        },
        onSearch: (params) => {
            dispatch({
                type: 'topic/effect:query:blockArts',
                payload: {
                    ...params,
                    pageNumber: 1,
                    pageSize: topic.baPag.pageSize
                }
            });
        }
    };

    const btbProps = {
        wgtChgObj: topic.wgtChgObj,
        selArts: topic.baSelArts,
        curBlock: topic.curBlock,
        loading: topic.baLoading,
        createArt: (key) => {
            if (key === 'add') {
                dispatch({
                    type: 'article/reducer:initcreate',
                    payload: {

                    }
                })
                dispatch(routerRedux.push({
                    pathname: routerPath.ARTICLE_EDIT,
                    query: {
                        source: 'subject',
                        blockId: topic.curBlock.id
                    }
                }))
            } else if (key === 'cite') {
                dispatch({
                    type: 'cms/effect:toggle:cam',
                    payload: {
                        camv: true,
                        sysCode: 'subject'
                    }
                });
            }
        },
        onOffArts: (ids, tips) => {
            dispatch({
                type: 'topic/effect:onOff:arts',
                payload: {
                    articleIds: ids,
                    tips
                }
            });
        },
        saveWeight(arts) {
            dispatch({
                type: 'topic/effect:update:artsTopicWeight',
                payload: {
                    arts,
                    location
                }
            });
        }
    };

    const baProps = {
        artTypes: cms.artTypes,
        artTags: cms.artTags,
        curBlock: topic.curBlock,
        loading: topic.baLoading,
        dataSource: topic.blockArts,
        pagination: topic.baPag,
        selArts: topic.baSelArts,
        wgtChgObj: topic.wgtChgObj,
        onPageChange: (page) => {
            dispatch({
                type: 'topic/effect:query:blockArts',
                payload: {
                    ...topic.baQuery,
                    pageNumber: page.current,
                    pageSize: page.pageSize
                }
            });
        },
        selectArt: (selArts) => {
            dispatch({type: 'topic/effect:update:baSelArts', payload: selArts});
        },
        onOffArt: (id) => {
            dispatch({type: 'topic/effect:onOff:art', payload: {
                    id
                }});
        },
        updateWgtChgObj: (obj) => {
            dispatch({type: 'topic/effect:update:wgtChgObj', payload: obj});
        },
        onEdit: (record) => {
            dispatch({
                type: 'cms/effect:toggle:eam',
                payload: {
                    art: record,
                    eamv: true
                }
            });
        },
        onDelete: (id) => {
            dispatch({type: 'topic/effect:delete:art', payload: {
                    id
                }})
        }
    };

    const bemProps = {
        id: ++uuid,
        visible: topic.bemVisible,
        action: topic.blockOpType,
        topic: topic.base,
        curBlock: topic.curBlock,
        onSave: (block) => {
            const action = block.id
                ? 'update'
                : 'create';
            const type = `topic/effect:${action}:block`;
            dispatch({
                type,
                payload: {
                    block
                }
            });
        },
        onCancel: () => {
            dispatch({type: 'topic/effect:hide:bem'});
        }
    };

    const camProps = {
        catgs: cms.catgs,
        visible: cms.camv,
        loading: cms.cal,
        pagination: cms.cap,
        dataSource: cms.cas,
        artTypes: cms.artTypes,
        artTags: cms.artTags,
        catgId: topic.curBlock.id,
        onPageChange: (page) => {
            dispatch({
                type: 'cms/effect:query:cas',
                payload: {
                    ...cms.caq,
                    sysCode: 'subject',
                    pageNumber: page.current,
                    pageSize: page.pageSize
                }
            });
        },
        onCancel: () => {
            dispatch({
                type: 'cms/effect:toggle:cam',
                payload: {
                    camv: false
                }
            });
        },
        onCite: (params) => {
            dispatch({
                type: 'cms/effect:cite:arts',
                payload: {
                    ...params,
                    source: 'subject'
                }
            });
        },
        onSearch: (params) => {
            dispatch({
                type: 'cms/effect:query:cas',
                payload: {
                    ...params,
                    sysCode: 'subject',
                    pageNumber: 1,
                    pageSize: cms.cap.pageSize
                }
            });
        }
    };

    const eamProps = {
        visible: cms.eamv,
        art: cms.art,
        source: 'subject',
        onCancel: () => {
            dispatch({
                type: 'cms/effect:toggle:eam',
                payload: {
                    eamv: false
                }
            });
        },
        onSave: (art) => {
            dispatch({
                type: 'cms/effect:update:citeArt',
                payload: {
                    art,
                    source: 'subject'
                }
            });
        },
        onHide: () => {
            dispatch({
                type: 'cms/effect:toggle:eam',
                payload: {
                    eamv: false
                }
            });
        }
    };

    return (
        <div className='content-inner'>
            <BlockTabs {...btProps}/>
            <BlockSearch {...bsProps}/>
            <BlockToolbar {...btbProps}/>
            <BlockArts {...baProps}/>
            <BlockEditModal {...bemProps}/>
            <CiteArtModal {...camProps}/>
            <EditArtModal {...eamProps}/>
        </div>
    );
}

TopicDetail.contextTypes = {
    router: PropTypes.object.isRequired
}

function mapStateToProps({cms, topic}) {
    return {cms, topic}
}

export default connect(mapStateToProps)(TopicDetail)
