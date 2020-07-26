import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, InputNumber, Radio, Modal, Icon, Row, Col } from 'antd'
import styles from './iconModal.less'

const modal = ({
  modalVisible,
  item,
  onOk,
  onCancel,
  onChangeIcon,
  onSubmitIcon,
  currentIcon = '',
  selectedIcon,
  submitIcon
}) => {
  let iconArray = ['laptop','up','left','bars','file','file-text','edit','delete']
  if(!!selectedIcon && selectedIcon!==currentIcon){
    currentIcon = selectedIcon
  }
  function selectIcon(type){
    onChangeIcon(type)
  }
  function handleOk(){ //点击modal保存
    onSubmitIcon(selectedIcon)
    onCancel()
  }
  function handleOnCancel(){
    onCancel()
    onChangeIcon(submitIcon)
  }
  const modalOpts = {
    title: '图标修改',
    visible:modalVisible,
    onOk:handleOk,
    onCancel:handleOnCancel,
    wrapClassName: 'vertical-center-modal'
  }
  return (
    <Modal {...modalOpts}>
      <Row>
        {iconArray.map(function(type,index){
          return <Col span={6} style={{textAlign:'center'}} key={'col'+index}>
                  <a style={{'color':'#666'}}>
                    <Icon type={type} onClick={() => selectIcon(type)} className={currentIcon === type ? styles.active : ''}/>
                  </a>  
                 </Col> 
        })}
      </Row>
    </Modal>
  )
}

modal.propTypes = {
  // visible: PropTypes.any,
  // form: PropTypes.object,
  // item: PropTypes.object,
  // onOk: PropTypes.func,
  // onCancel: PropTypes.func
}

export default modal