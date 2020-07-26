import React from 'react'
import PropTypes from 'prop-types'
import { Table ,Popconfirm, Row, Col, Button} from 'antd'
import { Link } from 'dva/router'
import VisibleWrap from '../ui/visibleWrap'
import { fieldMap } from '../../constants'

function ActivityCodeList ({
  dataSource,
  onDelete,
  pagination,
  onPageChange
}) {
  const columns = [
      {
        title: fieldMap.ACT_CODE,
        key : 'code',
        dataIndex: 'code'
      },
      {
        title: fieldMap.TITLE,
        dataIndex: 'title',
        key: 'title',
      },    
      {
        title: fieldMap.DIGEST,
        key: 'digest',
        dataIndex: 'digest'
      },
      {
        title: fieldMap.STATE,
        key: 'valid',
        dataIndex: 'valid',
        render:(text,record) =>{
          if(record.valid === true){
            return <span>生效中</span>
          }else{
            return <span>已无效</span>
          }
        }
      },
      {
        title: fieldMap.START_TIME,
        key: 'createTime',
        dataIndex: 'createTime'
      },
      {
        title: fieldMap.TIMESCOPE,
        key: 'beginTime',
        dataIndex: 'beginTime',
        render:(text,record) =>{
          const beginTime = record.beginTime;
          const endTime = record.endTime;
            return (beginTime+"至"+endTime);   
        }
      },
      {
        title: fieldMap.CEILINGTIMES,
        key: 'maxNumber',
        dataIndex: 'maxNumber'
      },
      {
        title: fieldMap.USEDTIMES,
        key: 'activeTimes',
        dataIndex: 'activeTimes'
      }
      
  ]
  return (
    <div>
      <Row  style={{ marginBottom: '10px' }}>
        <Col span={24} style={{ textAlign: 'right' }}>
          <VisibleWrap permis="edit:modify">
            <Link to={{pathname:`/client/activityCode/activityCodeEdit`, query: {type: 'createActivity'}}} >
              <Button type="primary">创建邀请码</Button>
            </Link>
          </VisibleWrap>
        </Col>
      </Row>
      <Table
        rowKey={record => record.code}
        bordered
        columns={columns}
        dataSource={dataSource}
        simple
        pagination={pagination}
        onChange = {onPageChange}
        defaultExpandAllRows={true}
      />
    </div>
  )
}

ActivityCodeList.propTypes = {
  onDelete: PropTypes.func,
  dataSource: PropTypes.array,
}

export default ActivityCodeList
