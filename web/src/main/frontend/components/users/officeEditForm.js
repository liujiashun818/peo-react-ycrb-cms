import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, Radio, Modal, Button, Select, TreeSelect} from 'antd'
import { Link } from 'dva/router'
import VisibleWrap from '../ui/visibleWrap'

const FormItem = Form.Item
const formItemLayout = {
  labelCol: {
    span: 3
  },
  wrapperCol: {
    span: 8
  }
}
const OfficeForm = ({
  tree,
  item,
  createOffice,
  updateOffice,
  backToList,
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
        ...getFieldsValue(),
      }

      if(!item.id) {
        createOffice(data)
      }
      else {
        data.id = item.id;
        updateOffice(data)
      }
      
    })
  }
   return (
    <Form horizontal>
      <FormItem label='上级机构' {...formItemLayout}>
        {getFieldDecorator('parentId', {
          initialValue: item.parentId ? item.parentId+"" : '1'
        })(<TreeSelect 
              allowClear
              showSearch
              treeNodeFilterProp="label"
              dropdownStyle={{maxHeight:300,overflow:'auto'}}
              treeData={tree}
          />
        )}
      </FormItem> 
      <FormItem label='机构名称' hasFeedback {...formItemLayout}>
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
      <FormItem label='英文名' {...formItemLayout}>
        {getFieldDecorator('slug', {
          initialValue: item.slug,
        })(<Input />)}
      </FormItem>
      <FormItem label='备注' {...formItemLayout}>
        {getFieldDecorator('remark', {
          initialValue: item.remark
        })(<Input />)}
      </FormItem>
      <FormItem wrapperCol={{span: 8, offset: 5}}>
        <VisibleWrap permis="edit:modify">
          <Button type="primary" size="large" onClick={submitForm}>保存</Button>
        </VisibleWrap>
        <Button style={{ marginLeft: 8 }} size="large">
          <a onClick={backToList}>返回</a>
        </Button>
      </FormItem>
    </Form>
  )
}

export default Form.create()(OfficeForm)