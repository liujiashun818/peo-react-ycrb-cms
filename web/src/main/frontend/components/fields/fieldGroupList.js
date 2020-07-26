import React from 'react'
import PropTypes from 'prop-types'
import { Table, Popconfirm, Row, Col, Button} from 'antd'
import { Link } from 'dva/router'
import { fieldMap } from '../../constants'

function FieldGroupList ({
  pagination,
  dataSource,
  onPageChange,
  onDelete,
  onBatchDelete,
  onSelectedRowKeys,
  selectedRowKeys
}) {
  const columns = [
    {
      title: fieldMap.TITLE,
      dataIndex: 'name',
      key: 'name',
      render: (text, record) => (
          <Link to={{pathname:`/fields/fieldGroup/edit`, query: {id: record.id, type: 'update'}}} >
            {record.name}
          </Link>
        )
    },  
    {
      title: fieldMap.MODEL,
      dataIndex: 'categoryModels',
      key: 'categoryModels',
    },
    {
      title: fieldMap.CATG_NAME,
      dataIndex: 'categories',
      key: 'categories',
    },
    {
      title: fieldMap.INTRO,
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: fieldMap.ACTION,
      key: 'operation',
      width: 200,
      render: (text, record) => (
          <p>
            <Link to={{pathname:`/fields/fieldGroup/edit`, query: {id: record.id, type: 'update'}}} style={{
              marginRight: 10 }}>
              修改
            </Link>
            <Popconfirm title='确定要删除吗？' onConfirm={()=>onDelete(record.id)} style={{
              marginRight: 10
            }}>
              <a style={{marginRight: 4}}>删除</a>
            </Popconfirm>
          </p>
      )
    }
  ]

  // rowSelection object indicates the need for row selection
  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys, selectedRows) => {
      console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
      onSelectedRowKeys(selectedRowKeys)
    }
  };

  return (
    <div>
      <Row  style={{ marginBottom: '10px' }}>
        <Col span={24}>
          <Link to={{pathname:`/fields/fieldGroup/edit`, query: {type: 'create'}}} >
            <Button type="primary">添加一个字段组</Button>
          </Link>
          <Button style={{ marginLeft: '10px' }} onClick={onBatchDelete}>批量删除</Button>
        </Col>
      </Row>
      <Table
        rowSelection={rowSelection} 
        rowKey={record => record.id}
        bordered
        columns={columns}
        dataSource={dataSource}
        simple
        pagination={pagination}
        onChange={onPageChange}
        defaultExpandAllRows={true}
      />
    </div>
  )
}

FieldGroupList.propTypes = {
  onDelete: PropTypes.func,
  dataSource: PropTypes.array,
}

export default FieldGroupList