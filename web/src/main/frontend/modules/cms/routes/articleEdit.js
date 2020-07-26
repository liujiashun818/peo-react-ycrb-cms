import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { message } from 'antd';
// import { Jt, Immutable } from '../../../utils';
import { routerPath } from '../../../constants';
import Edit from '../../../components/articles/edit';
import ArtCommentModal from '../../../components/articles/artCommentModal';
import copy from 'copy-to-clipboard';
let num = 0;
function ArticleEdit({ location, dispatch, cms, article }, context) {
  const acmProps = {
    visible: article.viewComment,
    onCancel: () => {
      dispatch({
        type: 'article/reducer:update',
        payload: {
          viewComment: false,
        },
      });
    },
    onHide: () => {
      dispatch({
        type: 'article/reducer:update',
        payload: {
          viewComment: false,
        },
      });
    },
  };

  const commentListProps = {
    dataSource: article.commentList,
    pagination: article.commentPagination,
    selectedRowKeys: article.commentDelflagKeys,
    loading: article.commentLoading,
    delFlag: article.commentDelFlag || 0,
    onPageChange(page) {
      dispatch({
        type: 'article/reducer:update:pagination',
        payload: {
          current: page.current,
          pageSize: page.pageSize,
        },
      });
      dispatch({ type: 'article/queryComment' });
    },
    onSelectedDelflag(delflagKeys) {
      dispatch({
        type: 'article/putInState',
        payload: {
          commentDelflagKeys: delflagKeys,
        },
      });
    },
    handleClickTitle(record) {
      dispatch(routerRedux.push({
        pathname: '/cms/articles/edit',
        query: {
          action: 'edit',
          id: record.articleId,
        },
      }));
    },
    onChangeDelflag(delFlag, id) {
      dispatch({
        type: 'article/batchOnOff',
        payload: {
          commentIds: id,
        },
      });
    },
      onChangeDelflag2(delFlag,id,type){
          const query = location.query
          dispatch({
              type:'article/batchOnOff2',
              payload:{
                  ...query,
                  commentId:id,
                  "actionStatus":0
              }
          })
      },
    saveLikes(record) {
      dispatch({
        type: 'article/updateProps',
        payload: {
          params: {
            id: record.id,
            likes: record.likes || 0,
          },
          query: {
            articleId: record.articleId,
          },
        },
      });
    },
        // replyHandle(record){
        //     dispatch({
        //         type:'comment/replyShow',
        //         payload: record
        //     });
        // },
    handleSubmitReply(record, type) {
      const isAdmin = type === 'admin';
      dispatch({
        type: 'article/addReply',
        payload: {
          params: {
            content: isAdmin ? record.replyAdminValue : record.replyValue,
            parentId: record.id,
            isAdmin,
            userName: isAdmin ? '管理员' : record.replyName,
          },
          query: {
            articleId: record.articleId,
          },
        },
      });
    },
  };
  const commentSearchProps = {
    delFlag: article.commentDelFlag || 0,
    content: article.commentContent,
    onSearch(data) {
      dispatch({
        type: 'article/reducer:update',
        payload: {
          commentContent: data.content,
        },
      });
      dispatch({
        type: 'article/queryComment',
      });
    },
    onDelFlagChange(param) {
      dispatch({
        type: 'article/reducer:update',
        payload: {
          commentDelFlag: param.target.value,
        },
      });
      dispatch({
        type: 'article/queryComment',
      });
    },
    batchMenuClick(menu) {
      const delFlag = article.commentDelFlag || 0;
      let delflagKeysStr = article.commentDelflagKeys;
      if (!delflagKeysStr || delflagKeysStr.length === 0) {
        message.error('请选择要操作的条目');
      } else if (menu.key === 'on' && delFlag === 0) {
        message.error('选项已上线');
      } else if (menu.key === 'off' && delFlag === 1) {
        message.error('选项已下线');
      } else {
        delflagKeysStr = delflagKeysStr.join(',');
        dispatch({
          type: 'article/batchOnOff',
          payload: {
            commentIds: delflagKeysStr,
          },
        });
        dispatch({
          type: 'article/putInState',
          payload: {
            commentDelflagKeys: [],
          },
        });
        delflagKeysStr = '';
      }
    },
  };
  const handleSubmitReply = (values) => {
    dispatch({
      type: 'article/addComment',
      payload: {
        ...values,
      },
    });
  };
  console.log("xxxxxxxxxxxxxx",cms.naq.categoryId)
  const editProps = {
    isReference:article.isReference,
    categoryId: cms.naq.categoryId,
     oncopy:(str)=>{
      if(copy(str)){
          console.log("复制成功");
          }else{
              console.log("复制失败")
          }
    },
    deleteArt: (id) => {
      dispatch({
        type: 'article/effect:delete:art',
        payload: {
          id,
          success: () => {
            context.router.goBack();
          },
        },
      });
    },
    saveArt: (art) => {
      
      art = JSON.parse(art);
      let isReference = art.isReference
      console.log("xxxxxxxxxxxxxx2",art.isReference,art.categoryId)
      let	success = () => {
        context.router.push({
          pathname: routerPath.ARTICLE_LIST,
          query: {
            ...cms.naq,
            categoryId: isReference?cms.naq.categoryId:art.categoryId,
            block: article.modelId=='8'?'2':art.block,
            delFlag: art.delFlag,
          },
        });
      };
      if (location.query.source && location.query.source === 'subject') {
        success = () => {
          context.router.goBack();
        };
      }
      const action = art.id ? 'update' : 'create';
      const type = `article/effect:${action}:art`;
      delete art.isReference
      dispatch({
        type,
        payload: {
          art,
           loading:true,
          success,
        },
      });
    },
    goBack() {
      context.router.goBack();
    },
    updateState(obj) {
      dispatch({
        type: 'article/effect:update:state',
        payload: obj,
      });
    },
    updateArtAttrs(art) {
      dispatch({
        type: 'article/effect:update:artAttrs',
        payload: art,
      });
    },
    updateArtCatg(data) {
      dispatch({
        type: 'article/effect:update:artCatg',
        payload: {
          ...data,
        },
      });
    },
    updateMediaType(mediaType) {
      dispatch({
        type: 'article/reducer:update:mediaType',
        payload: mediaType,
      });
    },
    viewComment: (record) => {
      dispatch({ type: 'article/reducer:update', payload: { viewComment: true, curArt: record } });
      dispatch({ type: 'article/queryComment' });
    },

  };
  // num++;
  return (
    <div className="content-inner">
      <Edit key={num} {...editProps} />
      <ArtCommentModal
        {...acmProps}
        listProps={commentListProps}
        searchProps={commentSearchProps}
        handleSubmitReply={handleSubmitReply}
      />
    </div>
  );
}

ArticleEdit.contextTypes = {
  router: PropTypes.object.isRequired,
};

function mapStateToProps({ cms, article }) {
  return { cms, article };
}

export default connect(mapStateToProps)(ArticleEdit);
