import React from 'react'
import PropTypes from 'prop-types'
import { Breadcrumb, Icon } from 'antd'
import { pageTypes } from '../../constants'
import styles from './main.less'

function Bread ({ location,menu }) {
  if(!menu || menu.length === 0) {
    return (null);
  }
  let pathSet = []
  const getPathSet = function (menuArray, parentPath) {
    parentPath = parentPath || '/'
    menuArray.map(item => {
      pathSet[(parentPath + item.key).replace(/\//g, '-').hyphenToHump()] = {
        path: parentPath + item.key,
        name: item.name,
        icon: item.icon || '',
        clickable: item.clickable === undefined
      }
      if (item.child) {
        getPathSet(item.child, parentPath + item.key + '/')
      }
    })
  }
  getPathSet(menu)

  let pathNames = []
  location.pathname.substr(1).split('/').map((item, key) => {
    if (key > 0) {
      pathNames.push((pathNames[key - 1] + '-' + item).hyphenToHump())
    } else {
      pathNames.push(('-' + item).hyphenToHump())
    }
  })
  const breads = pathNames.map((item, key) => {
    // if (!(item in pathSet)) {
    //   item = 'Dashboard'
    // }
    return (
      <Breadcrumb.Item key={key} {...((pathNames.length - 1 === key) || (pathSet[item] && !pathSet[item].clickable)) ? '' : { href: '#' + (pathSet[item] && pathSet[item].path) }}>
        {
          pathSet[item] && pathSet[item].icon ? <Icon type={pathSet[item].icon} /> : ''
        }
        <span>
          {
            pathSet[item] ? pathSet[item].name : pageTypes[item]
          }
        </span>
      </Breadcrumb.Item>
    )
  })

  return (
    <div className={styles.bread}>
      <Breadcrumb>
        <Breadcrumb.Item href='/dashboard.html#/'><Icon type='home' />
          <span>主页</span>
        </Breadcrumb.Item>
        {breads}
      </Breadcrumb>
    </div>
  )
}

Bread.propTypes = {
  location: PropTypes.object
}

export default Bread
