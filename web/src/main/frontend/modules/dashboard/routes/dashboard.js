import React from 'react'
import PropTypes from 'prop-types'
import {connect} from 'dva'
import {Row, Col, Card} from 'antd'
import styles from './dashboard.less'
import {color} from '../../../utils'

const bodyStyle = {
  bodyStyle: {
    height: 432,
    background: '#fff'
  }
}

function Dashboard ({dashboard, dispatch}) {
  // const {weather, sales, quote, numbers, recentSales, comments, completed, browser, cpu, user} = dashboard
  // const numberCards = numbers.map((item, key) => <Col key={key} lg={6} md={12}>
    
  // </Col>)

  return (
    null
  )
}

export default connect(({dashboard}) => ({dashboard}))(Dashboard)
