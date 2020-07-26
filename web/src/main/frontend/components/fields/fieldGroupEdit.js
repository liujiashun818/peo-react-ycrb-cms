import React from 'react'
import PropTypes from 'prop-types'
import { Form, Row, Col, Collapse, Input, Radio, Modal, Button, Select, TreeSelect,Popconfirm} from 'antd'
import { Link } from 'dva/router'
import {formItemMainLayout} from '../../constants';
import { Jt } from '../../utils';
import VisibleWrap from '../ui/visibleWrap'
import Submiter from '../form/submit'
import DetailEdit from '../layout/detailEdit'
import FieldsModule from '../form/fieldsModule'

const FormItem = Form.Item;
const SHOW_ALL = TreeSelect.SHOW_ALL;
const Option = Select.Option;
formItemMainLayout.colon = !0;

class FieldGroupEdit extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      item:props.item,
      saveLoading:props.saveLoading,
      toValidate:'1', //触发子组件表单验证
      collapse_1:['1'],// 第一组折叠面板的展开项
      collapse_2:['1'],// 第二组折叠面板的展开项
    }
    this.toValiConut = 1; //待验证子组件数量
    this.valiedConut = 0; //已验证子组件数量
  }
  onSave = (e) => {
    e.preventDefault();
    const { getFieldValue } = this.props.form;
    const hasChild = getFieldValue('fields') && getFieldValue('fields').length > 0 ? true : false;
    if(hasChild){
      this.validateChildren(); //有子组件带嵌套验证表单项的时候需要调此方法出发验证和回调提交
    }else{
      this.onValidated();
    }
  }
  validateChildren = () => {
    // 当保存按钮的时候，重置valiedConut， 改变传入子组件的props的属性值， 保证只传一次，然后传入onValidated，等待回调，重构
    this.valiedConut = 0;
    this.setState({
      toValidate: this.state.toValidate === '1'?'2':'1'
    })
  }
  onValidated = () => {
    this.valiedConut++;
    if(this.valiedConut < this.toValiConut){ //已验证的子组件尚未达到总数，不提交
      return;
    }
    const { validateFields, getFieldsValue } = this.props.form;
    const { item,save,submitFail,submitBefore } = this.props;
    submitBefore()
    validateFields((err, values) => {
      if (err) {
        submitFail()
        return
      }
      const data = {
        ...getFieldsValue(),
      }
      data.categoryModels = Jt.array.listToMap(data.categoryModels)
      data.categories = data.categories.map(function(v,i){
            return {id:v.value};
        });
      if(!data.id){
        delete data.id;
      }
      save(data)
    });
  }
  componentWillReceiveProps(nextProps) {
    // Should be a controlled component.
    if (nextProps.saveLoading !== this.props.saveLoading) {
      this.setState(
          {saveLoading:nextProps.saveLoading || false}
        );
    }
  }
  render(){
    const { getFieldDecorator } = this.props.form;
    const { item,catgsTree,catgModels,onBack,del } = this.props;
    const fieldsModuleProps = {
      toValidate:this.state.toValidate,
      onValidated:this.onValidated,
      fieldList:this.props.fieldList
    }
    return(
      <Form>
          <DetailEdit>
            <div type="main">
              <Collapse bordered={true} activeKey={this.state.collapse_1} onChange={(key)=>{ this.setState({collapse_1:key}) }} className="cus-panel">
                <Collapse.Panel key="1" header="字段组信息" >
                  <FormItem {...formItemMainLayout}>
                    {getFieldDecorator('id', {
                      initialValue: item.id
                    })(<Input type="hidden" />)}
                  </FormItem>
                  <FormItem label='字段组名称' hasFeedback {...formItemMainLayout}>
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
                  <FormItem label='说明' {...formItemMainLayout}>
                    {getFieldDecorator('description', {
                      initialValue: item.description
                    })(<Input type="textarea" />)}
                  </FormItem>
               </Collapse.Panel>
              </Collapse>
              <Collapse bordered={true} activeKey={this.state.collapse_2} onChange={(key)=>{ this.setState({collapse_2:key}) }} className="cus-panel">
                <Collapse.Panel key="1" header="字段组关联位置">
                  <FormItem label='栏目模型' {...formItemMainLayout}>
                    {getFieldDecorator('categoryModels', {
                      initialValue: item.categoryModels && Jt.tree.getIds(item.categoryModels) || []
                    })(<Select 
                      allowClear
                      mode="multiple"
                      >
                        {
                          catgModels.map((option, index) => {
                            return <Option key={index} value={option.id+''}>{option.name}</Option>  
                          })
                        }
                      </Select>)}
                  </FormItem>
                  <FormItem label='栏目本身' {...formItemMainLayout}>
                    {getFieldDecorator('categories', {
                      initialValue: item.categories && Jt.tree.getIds(item.categories,true) || []
                    })(<TreeSelect
                      treeData={catgsTree}
                      allowClear
                      multiple={true}
                      treeCheckable={true}
                      showCheckedStrategy= {SHOW_ALL}
                      treeCheckStrictly = {true}
                    />)}
                  </FormItem>
               </Collapse.Panel>
              </Collapse>
              <FormItem>
                {
                  getFieldDecorator('fields', {
                  initialValue: item.fields || []
                })(<FieldsModule {...fieldsModuleProps} />)}
              </FormItem>
              
            </div>
            <div type="sidebar">
              <Collapse bordered={true} defaultActiveKey={['1']}>
                <Collapse.Panel key="1" header="保存">
                   <Submiter 
                    onBack = {onBack}
                    onSave = {(e)=>{this.onSave(e)}}
                    onDelete = {del}
                    saveLoading = {this.state.saveLoading}
                   />
                </Collapse.Panel>
              </Collapse>
            </div>
          </DetailEdit>
      </Form>
    )
  }
}

FieldGroupEdit.propTypes = {
    item: PropTypes.object
}

export default Form.create()(FieldGroupEdit)