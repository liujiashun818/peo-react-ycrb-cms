import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { message } from 'antd';
// import { Jt, Immutable } from '../../../utils';
import { routerPath } from '../../../constants';
import PaperEdit from '../../../components/articles/edit-paper';
import ArtCommentModal from '../../../components/articles/artCommentModal';

function Paper({ location, dispatch, article }, context) {
  const editProps = {
    data: article.paperData,
    dispatch,
    updatePaper({ params, success }) {
      dispatch({
        type: 'article/effect:paper:update',
        payload: { params, success }
      })
    },
    updateState(obj) {
      dispatch({
        type: 'article/effect:update:paperstate',
        payload: obj,
      });
    },
  }
  return (
    <div className="content-inner">
      <PaperEdit {...editProps}></PaperEdit>
    </div>
  );
}

function mapStateToProps({ article, paper }) {
  return { article, paper };
}

export default connect(mapStateToProps)(Paper);
