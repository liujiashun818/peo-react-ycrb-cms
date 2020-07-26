import React from 'react'
import PropTypes from 'prop-types'
import { Table, Popconfirm, Row, Col, Button} from 'antd'
import { Link } from 'dva/router'
import VisibleWrap from '../ui/visibleWrap'
import { fieldMap } from '../../constants'

function OfficeTree ({
  dataSource,
  onDelete
}) {
  const columns = [
    {
      title: fieldMap.NAME,
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: fieldMap.REMARK,
      dataIndex: 'remark',
      key: 'remark',
      render: (text, record) =>(
             text&&text!='null'?text:''

      )
    },
    {
      title: fieldMap.ACTION,
      key: 'operation',
      width: 200,
      render: (text, record) => (
          <p>
            <VisibleWrap permis="view">
              <Link to={{pathname:`/users/office/officeEdit`, query: {id: record.id, type: 'update'}}} style={{
                marginRight: 10 }}>
                修改
              </Link>
            </VisibleWrap>
            {/*<VisibleWrap permis="edit:delete">*/}
              {/*<Popconfirm title='确定要删除吗？' onConfirm={()=>onDelete(record.id)} style={{*/}
                {/*marginRight: 10*/}
              {/*}}>*/}
                {/*<a style={{marginRight: 4}}>删除</a>*/}
              {/*</Popconfirm>*/}
            {/*</VisibleWrap>*/}
            <VisibleWrap permis="edit:modify">
              <Link to={{pathname:`/users/office/officeEdit`, query: {parentId: record.id, type: 'createSub'}}} style={{
                marginRight: 10 }}>
                添加下级机构
              </Link>
            </VisibleWrap>
          </p>
      )
    }
  ]

  return (
    <div>
      <Row  style={{ marginBottom: '10px' }}>
        <Col span={24} style={{ textAlign: 'right' }}>
          <VisibleWrap permis="edit:modify">
            <Link to={{pathname:`/users/office/officeEdit`, query: {type: 'create'}}} >
              <Button type="primary">添加一个机构</Button>
            </Link>
          </VisibleWrap>
        </Col>
      </Row>
      <Table
        rowKey={record => record.id}
        bordered
        columns={columns}
        dataSource={dataSource}
        simple
        pagination={false}
        defaultExpandAllRows={true}
      />
    </div>
  )
}

OfficeTree.propTypes = {
  onDelete: PropTypes.func,
  dataSource: PropTypes.array,
}

export default OfficeTree
