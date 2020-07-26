import React from 'react'
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import ArtList from '../../../components/articles/list'

function Exa_1 ({ location, dispatch, exa_1 }) {
  const { list } = exa_1

  const articleListProps = {
    dataSource: list
  }

  return (
    <div className='content-inner'>
      <h2>示例页面</h2>
      <br />
      文章列表
    </div>
  )
}

Exa_1.propTypes = {
  exa_1: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func
}

function mapStateToProps ({ exa_1 }) {
  return { exa_1 }
}

export default connect(mapStateToProps)(Exa_1)
