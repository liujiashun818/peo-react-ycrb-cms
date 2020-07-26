import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'dva'
import Login from './login'
import Header from '../components/layout/header'
import Bread from '../components/layout/bread'
import Footer from '../components/layout/footer'
import Sider from '../components/layout/sider'
import styles from '../components/layout/main.less'
import { Spin } from 'antd'
import { classnames } from '../utils'
import '../components/layout/common.less'

function App ({children, location, dispatch, app},context) {
  const {login, loading, loginButtonLoading, user, siderFold, darkTheme, isNavbar, menuPopoverVisible, menu } = app
  const loginProps = {
    loading,
    loginButtonLoading,
    onOk (data) {
      dispatch({type: 'app/login', payload: {
        data
      }})
    }
  }

  const headerProps = {
    menu,
    user,
    siderFold,
    location,
    isNavbar,
    menuPopoverVisible,
    switchMenuPopover () {
      dispatch({type: 'app/switchMenuPopver'})
    },
    logout () {
      dispatch({type: 'app/logout'})
    },
    switchSider () {
      dispatch({type: 'app/switchSider'})
    }
  }

  const siderProps = {
    menu,
    siderFold,
    darkTheme,
    location,
    changeTheme () {
      dispatch({type: 'app/changeTheme'})
    }
  }
  return (
    <div>{login
        ? <div className={classnames(styles.layout, {[styles.fold]: isNavbar ? false : siderFold}, {[styles.withnavbar]: isNavbar})}>
          {!isNavbar ? <aside className={classnames(styles.sider, {[styles.light]: !darkTheme})}>
            <Sider {...siderProps} />
          </aside> : ''}
          <div className={styles.main} id="container">
            <Header {...headerProps} />
            <Bread location={location} menu={menu} />
            <div className={styles.container}>
              <div className={styles.content}>
                {children}
              </div>
            </div>
            <Footer />
          </div>
        </div>
        : <div className={styles.spin}><Spin tip='加载用户信息...' spinning={loading} size='large'>
          {loading?' ':<Login {...loginProps} />}
          </Spin></div>}</div>
  )
}

App.propTypes = {
  children: PropTypes.element.isRequired,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
  loginButtonLoading: PropTypes.bool,
  login: PropTypes.bool,
  user: PropTypes.object,
  siderFold: PropTypes.bool,
  darkTheme: PropTypes.bool
}

App.contextTypes = {
    router: PropTypes.object.isRequired
}
window.onload =function () {
    if(document.getElementById("password")){
        document.getElementById("password").onpaste = function () {
            return false
        }
        document.getElementById("password").oncut = function () {
            return false
        }
    }

}
export default connect(({app}) => ({app}))(App)
