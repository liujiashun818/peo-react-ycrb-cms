import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, InputNumber, Radio, Modal, Button, TreeSelect, Icon } from 'antd'
import { Link } from 'dva/router'
import MenusModal from './iconModal'
import VisibleWrap from '../ui/visibleWrap'

const FormItem = Form.Item
const RadioGroup = Radio.Group
const formItemLayout = {
  labelCol: {
    span: 6
  },
  wrapperCol: {
    span: 6
  }
}
function getArray(tree,name){
  for (var i in tree) {
    if(tree[i].name == name){
      data.parentId = tree[i].parentId
    }else{
      getArray(tree[i].child, name);
    }
  }
}
const form = ({
  modalVisible,
  parentMenu,
  item,
  handleSubmit,
  treeData,
  onOk,
  onCancel,
  onChangeIcon,
  onSubmitIcon,
  submitIcon,
  selectedIcon,
  form: {
    getFieldDecorator,
    validateFields,
    getFieldsValue
  }
}) => {
  function submitForm(){
    validateFields((errors) => {
      if (errors) {
        return
      }
      const data = {
        ...item,
        ...getFieldsValue(),
        icon:submitIcon
      }
      handleSubmit(data)
    })
  }
  function handleOk () { //点击选择图标
    onOk() //showModal
  }
  const modalOpts = {
    currentIcon:item.icon,
    modalVisible,
    onChangeIcon,
    selectedIcon,
    submitIcon,
    onSubmitIcon,
    item,
    onCancel,
    wrapClassName: 'vertical-center-modal'
  }
  console.log(2222222,item)
  // treeData = [{id:1,key:'1',label:'顶级菜单',value:'1',name:'顶级菜单',children:[...treeData]}] //树结构中加入顶级菜单
   return (
    <Form horizontal>
      <FormItem label='上级菜单' {...formItemLayout}>
        {getFieldDecorator('parentId', {
          initialValue: item.parentId+"",
        })(<TreeSelect
              allowClear
              showSearch
              treeNodeFilterProp="label"
              dropdownStyle={{maxHeight:300,overflow:'auto'}}
              treeData={treeData}
          />
        )}
      </FormItem>
      <FormItem label='名称' hasFeedback {...formItemLayout}>
        {getFieldDecorator('name', {
          initialValue: item.name,
          rules: [
            {
              required: true,
              message: '名称未填写'
            }
          ]
        })(<Input />)}
      </FormItem>
      <FormItem label='key' hasFeedback {...formItemLayout}>
        {getFieldDecorator('code', {
          initialValue: item.key,
          rules: [
            {
              required: true,
              message: 'key值未填写'
            }
          ]
        })(<Input />)}
      </FormItem>
      <FormItem label='链接' {...formItemLayout}>
        {getFieldDecorator('href', {
          initialValue: item.href
        })(<Input />)}
      </FormItem>
      <FormItem label='权限标识' {...formItemLayout}>
        {getFieldDecorator('permission', {
          initialValue: item.permission
        })(<Input />)}
      </FormItem>
      <FormItem label='图标' {...formItemLayout}>
        <Icon type={submitIcon} style={{marginRight: 4}}/>
          {submitIcon||'无'}
        <a onClick={() => handleOk()} style={{
            marginLeft: 10
          }}>
          选择
        </a>
        <MenusModal {...modalOpts}></MenusModal>
      </FormItem>
      <FormItem label='排序' {...formItemLayout}>
        {getFieldDecorator('sort', {
          initialValue: (item.sort&&item.sort!='0')?item.sort : ''
        })(<Input />)}
      </FormItem>
      <FormItem
        {...formItemLayout}
        label="可见"
      >
      {getFieldDecorator('show',{initialValue:item.show})(
          <RadioGroup >
            <Radio value={true}>可见</Radio>
            <Radio value={false}>隐藏</Radio>
          </RadioGroup>
        )}
      </FormItem>
      <FormItem {...{labelCol: {span: 6}, wrapperCol: {span: 10}}} style={{textAlign:'right'}}>
        <VisibleWrap permis="edit:modify">
          <Button type="primary" size="large" onClick={submitForm}>保存</Button>
        </VisibleWrap>
        <Button style={{ marginLeft: 8 }} size="large" >
          <Link to={{pathname:`/sys/menu`}}>返 回</Link>
        </Button>
      </FormItem>
    </Form>
  )
}

// modal.propTypes = {
//   visible: PropTypes.any,
//   form: PropTypes.object,
//   item: PropTypes.object,
//   onOk: PropTypes.func,
//   onCancel: PropTypes.func
// }

export default Form.create()(form)
