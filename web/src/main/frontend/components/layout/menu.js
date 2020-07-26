import React from 'react'
import { Menu, Icon } from 'antd'
import { Link } from 'dva/router'

function Menus ({ menu, siderFold, darkTheme, location, isNavbar, handleClickNavMenu, className }) {
  const winPathname = window.location.pathname;
  const curNode = winPathname.split('/')[winPathname.split('/').length - 1].replace('.html','') || winPathname.split('/')[winPathname.split('/').length - 2];
  const topMenus = menu.map(item => item.key)
  const getMenus = function (menuArray, siderFold, parentPath, namePath, isCurNode=false) {
    parentPath = parentPath || '/'
    return menuArray.map(item => {
      let isCurNodeTmp = isCurNode;
      if(item.key == curNode || isCurNode){
        isCurNodeTmp = true
      }else{
        isCurNodeTmp = false
      }
      let namePathTmp = namePath || '';
      if(!namePathTmp){
        if(item.key != curNode){
          namePathTmp = item.href || item.key+'.html'
        }
      }
      if (item.child) {
        return (
          <Menu.SubMenu key={item.key} title={<span>{item.icon ? <Icon type={item.icon} /> : ''}{siderFold && topMenus.indexOf(item.key) >= 0 ? '' : item.name}</span>}>
            {getMenus(item.child, siderFold, parentPath + item.key + '/', namePathTmp, isCurNodeTmp)}
          </Menu.SubMenu>
        )
      } else {
        return (
          <Menu.Item key={item.key}>
            {item.type != 'outlink' && isCurNodeTmp ? 
            <Link to={parentPath + item.key}>
              {item.icon ? <Icon type={item.icon} /> : ''}
              {siderFold && topMenus.indexOf(item.key) >= 0 ? '' : item.name}
            </Link>:
            <a href={namePathTmp+ "#"+ parentPath + item.key}>
              {item.icon ? <Icon type={item.icon} /> : ''}
              {siderFold && topMenus.indexOf(item.key) >= 0 ? '' : item.name}
            </a>
            }
          </Menu.Item>
        )
      }
    })
  }
  const menuItems = getMenus(menu, siderFold)
  const pathArray = location.pathname.split('/');
  const pathIndex = pathArray.length > 3 ? 2 : pathArray.length-1 ;
  const selectedKeys = pathArray[pathIndex];
  return (
    <Menu
      className={className}
      mode={siderFold ? 'vertical' : 'inline'}
      theme={darkTheme ? 'dark' : 'light'}
      onClick={handleClickNavMenu}
      defaultOpenKeys={isNavbar ? menuItems.map(item => item.key) : [curNode]}
      selectedKeys={[selectedKeys || 'dashboard']}>
      {menuItems}
    </Menu>
  )
}

export default Menus
