import React from 'react'
import PropTypes from 'prop-types'
import { Table, Popconfirm,Icon } from 'antd'
import { Link } from 'dva/router'
import styles from './list.less'
import VisibleWrap from '../ui/visibleWrap'
import { fieldMap } from '../../constants';

function list ({
  loading,
  dataSource,
  pagination,
  onPageChange,
  onDeleteItem,
  onEditItem
}) {
  const columns = [
    {
      title: fieldMap.NAME,
      dataIndex: 'name',
      key: 'name',
      render: (text,record) => {
        return <span><Icon width={4} type={record.icon} style={{marginRight:4}}/>{text}</span>
      }
    },
    {
      title: fieldMap.HREF,
      dataIndex: 'href',
      key: 'href'
    },
    {
      title: fieldMap.SORT,
      dataIndex: 'sort',
      key: 'sort',
      width:80,
        render: (text,record) => {
            return text&&text!='0'?text:''
        }
    },
    {
      title: fieldMap.SHOW,
      dataIndex: 'show',
      key: 'show',
      width:80
    },
    {
      title: fieldMap.ACTION,
      key: 'operation',
      width: 150,
      render: (text, record) => (
        (record.id==1?(<p>
             <Link to={{pathname:`/sys/menu/menusEdit`,query:{parentId:record.parentId,id:record.id,type:'addMenu'}}} >
              添加下级菜单
             </Link>
           </p>
           ):(
              <p>
                <VisibleWrap permis="view">
                  <Link to={{pathname:`/sys/menu/menusEdit`,query:{parentId:record.parentId,id:record.id,type:'update'}}} style={{
                    marginRight: 4 }}>
                    修改
                  </Link>
                </VisibleWrap>
                <VisibleWrap permis="edit:delete">
                  <Popconfirm title='确定要删除吗？' onConfirm={() => onDeleteItem(record.id)} style={{
                    marginRight: 4
                  }}>
                    <a style={{marginRight: 4}}>删除</a>
                  </Popconfirm>
                </VisibleWrap>
               <Link to={{pathname:`/sys/menu/menusEdit`,query:{parentId:record.parentId,id:record.id,type:'addMenu'}}} >
                添加下级菜单
               </Link>
              </p>
           )
        )
      )
    }
  ]
  const defaultExpandAllRows = true;
  return (
    <div>
      <Table
        className={styles.table}
        bordered
        columns={columns}
        dataSource={dataSource}
        defaultExpandedRowKeys={['1']}
        pagination={false}
        simple
      />
    </div>
  )
}

list.propTypes = {
  onPageChange: PropTypes.func,
  onDeleteItem: PropTypes.func,
  onEditItem: PropTypes.func,
  dataSource: PropTypes.array,
  loading: PropTypes.any,
  pagination: PropTypes.any
}

export default list
