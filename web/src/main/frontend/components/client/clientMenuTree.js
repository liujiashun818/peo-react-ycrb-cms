import React from 'react'
import PropTypes from 'prop-types'
import { Table, Popconfirm} from 'antd'
import { Link } from 'dva/router'
import VisibleWrap from '../ui/visibleWrap'
import { fieldMap } from '../../constants';
// import styles from './clientMenuTree.less'

function clientMenuTree ({
  dataSource,
  onChangeDelflag,
  onDelete
}) {
  const columns = [
    {
      title: fieldMap.NAME,
      dataIndex: 'name',
      key: 'name',
      // render: (text,record) => {
      //   return <span><Icon width={4} type={record.icon} style={{marginRight:4}}/>{text}</span>
      // }
    },
    {
      title: fieldMap.SIMPLENAME,
      dataIndex: 'simpleName',
      key: 'simpleName',
      width:180,
      render: (text,record) => {
        return <span>{record.slug?(text+'('+record.slug+')'):text}</span>
      }
    },
    {
      title: fieldMap.POS,
      dataIndex: 'position',
      key: 'position',
      render:(text,record) => {
        let position
        switch(record.position){
          case 'NORMAL':
            position =(<span>默认</span>)
            break;
          case 'ONE_TOP':
            position = (<span>首次置顶</span>)
            break;
          case 'ALWAYS_TOP':
            position=(<span>总是置顶</span>)
            break;
          case 'FIX':
            position =(<span>固定置顶</span>)
            break;
          default:
            position =(<span>默认</span>)
        }
        return position
      }
    },
    {
      title: fieldMap.REMARK,
      dataIndex: 'remark',
      key: 'remark'
    },
    {
      title: fieldMap.SORT,
      dataIndex: 'sort',
      key: 'sort',
      width:80
    },
    {
      title: fieldMap.ACTION,
      key: 'operation',
      width: 150,
      render: (text, record) => (
        (record.id==1? (<p>
          <VisibleWrap permis="edit:modify">
            <Link to={{pathname:`/client/clientMenu/clientMenuEdit`, query: {id: record.id, type: 'add'}}} style={{
              marginRight: 4 }}>
              新增菜单
            </Link>
          </VisibleWrap>
        </p>):
          (<p>
            <VisibleWrap permis="view">
              <Link to={{pathname:`/client/clientMenu/clientMenuEdit`, query: {id: record.id, type: 'update'}}} style={{
                marginRight: 4 }}>
                修改
              </Link>
            </VisibleWrap>
            <VisibleWrap permis="edit:online">
              <Popconfirm title={'确定要'+(record.delFlag==0?'下线':'上线')+'吗？'} onConfirm={()=>onChangeDelflag(record.id,record.delFlag)} style={{
                marginRight: 4
              }}>
                <a style={{marginRight: 4}}>{record.delFlag==0?'下线':'上线'}</a>
              </Popconfirm>
            </VisibleWrap>
            <VisibleWrap permis="edit:delete">
              <Popconfirm title='确定要删除吗？' onConfirm={()=>onDelete(record.id)} style={{
                marginRight: 4
              }}>
                <a style={{marginRight: 4}}>删除</a>
              </Popconfirm>
            </VisibleWrap>
          </p>)
        )
      )
    }
  ]
  const defaultExpandAllRows = true;
  return (
    <div>
      <Table
        bordered
        columns={columns}
        dataSource={dataSource}
        simple
        pagination={false}
        defaultExpandedRowKeys={['1']}
      />
    </div>
  )
}

clientMenuTree.propTypes = {
  onPageChange: PropTypes.func,
  onDeleteItem: PropTypes.func,
  onEditItem: PropTypes.func,
  dataSource: PropTypes.array,
  loading: PropTypes.any,
  pagination: PropTypes.any
}

export default clientMenuTree
