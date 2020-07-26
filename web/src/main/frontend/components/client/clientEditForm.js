import React from 'react'
import PropTypes from 'prop-types'
import {Form, Input, Radio, Modal, Button, Select, TreeSelect, Switch} from 'antd'
import { Link } from 'dva/router'
import VisibleWrap from '../ui/visibleWrap'

const FormItem = Form.Item
const formItemLayout = {
  labelCol: {
    span: 6
  },
  wrapperCol: {
    span: 6
  }
}
class form extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isRedirect: this.props.item.isRedirect=='是'?true:false
        }
    }
    switchChange = (checked)=>{
        let isRedirect = ''
        if(checked){
            isRedirect = true
        }else{
            isRedirect = false
        }
        this.setState(
            {isRedirect}
        )
    }
    submitForm = ()=>{
        let {handleSubmit,item,form:{validateFields,getFieldsValue}} = this.props;
        console.log(this.props,'this.props')
        const items = Object.assign({isRedirect:this.state.isRedirect?this.state.isRedirect:''},item);
        console.log(items.categoryName,'items:');
        validateFields((errors) => {
            if (errors) {
                return
            }

            const data = {
                ...items,
                ...getFieldsValue(),
            }
            console.log(data,'data:')
            data.isRedirect = this.state.isRedirect?'是':'否';
            data.redirectUrl = this.state.isRedirect?data.redirectUrl:'';
            handleSubmit(data)
        })
    }
    tranfer(data,id){
        if(!data) return [];
        let obj ;
        return data.map((item,index)=>{
            obj = {...item}
            if(item.children){
                if(item.id == id){
                    obj.disabled = true;
                }
                obj.children = this.tranfer(item.children,id);
                return obj;
            } else {
                if(item.id == id){
                    obj.disabled = true;
                }
                return obj;
            }
        })
    }
    changeParentId(name,id){
        let item = this.props.item;
        let {treeData} = this.props
        treeData = this.tranfer(treeData,id);

    }
    changeCatgs(id){
        let item = this.props.item;
        let {categoryTree} = this.props
        categoryTree = this.tranfer(categoryTree,id);

    }
    render(){
        const {
            viewTreeData=[],
            sysTreeData=[],
            positionTreeData=[],
            treeViewData,
            item,
            jumpToLink1,
            form: {
                getFieldDecorator
            }
        } = this.props;

        let {categoryTree,treeData} = this.props
        treeData = this.tranfer(treeData,this.props.form.getFieldValue('parentId')?this.props.form.getFieldValue('parentId'):(item.parentId ? item.parentId+"" : '1'))
        categoryTree = this.tranfer(categoryTree,this.props.form.getFieldValue('categoryId')?this.props.form.getFieldValue('categoryId'):(item.categoryId ? item.categoryId+'' : ''))
        return (
            <Form horizontal>
            <FormItem required={true} label='上级菜单' {...formItemLayout}>
                {getFieldDecorator('parentId', {
                    initialValue: item.parentId ? item.parentId+"" : '1',
                    rules: [
                      {
                          validator: (rule, value, callback) => {
                              if (value == undefined || value == null || value.replace(/\s/gi, '').length == 0) {
                                  callback('请填写上级菜单');
                                  return;
                              } else{
                                  callback();
                              }

                          }
                      }
                  ]
                })(<TreeSelect
                        allowClear
                        showSearch
                        treeNodeFilterProp="label"
                        dropdownStyle={{maxHeight:300,overflow:'auto'}}
                        onSelect={this.changeParentId.bind(this)}
                        treeData={treeData}
                    />
                )}
            </FormItem>
            <FormItem required={true} label='全称' hasFeedback {...formItemLayout}>
                {getFieldDecorator('name', {
                    initialValue: item.name || '',
                    rules: [
                        {
                            validator: (rule, value, callback) => {
                                if (value == undefined || value == null || value.replace(/\s/gi, '').length == 0) {
                                    callback('名称未填写');
                                    return;
                                } else{
                                    callback();
                                }

                            }
                        }
                    ]
                })(<Input />)}
            </FormItem>
            <FormItem required={true} label='简称' {...formItemLayout}>
                {getFieldDecorator('simpleName', {
                    initialValue: item.simpleName || '',
                    rules: [
                        {
                            validator: (rule, value, callback) => {
                                if (value == undefined || value == null || value.replace(/\s/gi, '').length == 0) {
                                    callback('简称未填写');
                                    return;
                                } else{
                                    callback();
                                }

                            }
                        }
                    ]
                })(<Input />)}
            </FormItem>
            <FormItem label='英文名' {...formItemLayout}>
                {getFieldDecorator('slug', {
                    initialValue: item.slug || '',
                })(<Input />)}
            </FormItem>
            <FormItem label='系统类型' {...formItemLayout}>
                {getFieldDecorator('systemType', {
                    initialValue: item.systemType || '',
                })(<Select
                    allowClear
                >
                    {
                        sysTreeData.map((item) => {
                            return <Select.Option key={item.id} value={item.value}>{item.label}</Select.Option>
                        })
                    }
                </Select>)}
            </FormItem>
            <FormItem label='展示位置' {...formItemLayout}>
                {getFieldDecorator('position', {
                    initialValue: item.position?(item.position+''):'',
                    rules: [
                        {
                            required: true,
                            message: '展示位置未填写'
                        }
                    ]
                })(<Select
                    allowClear
                >
                    {
                        positionTreeData.map((item) => {
                            return <Select.Option key={item.id} value={item.value}>{item.label}</Select.Option>
                        })
                    }
                </Select>)}
            </FormItem>
            <FormItem label='对应栏目' {...formItemLayout}>
                {getFieldDecorator('categoryId', {
                    initialValue: item.categoryId ? item.categoryId+'' : '',
                })((<TreeSelect
                        allowClear
                        showSearch
                        treeNodeFilterProp="label"
                        onChange={this.changeParentId.bind(this)}
                        onSelect={this.changeParentId.bind(this)}
                        dropdownStyle={{maxHeight:300,overflow:'auto'}}
                        treeData={categoryTree}
                    />
                ))}
            </FormItem>
            <FormItem label='展示类型' {...formItemLayout}>
                {getFieldDecorator('viewType', {
                    initialValue: item.viewType || '',
                })(<Select
                    allowClear
                >
                    {
                        viewTreeData.map((item) => {
                            return <Select.Option key={item.id} value={item.value}>{item.label}</Select.Option>
                        })
                    }
                </Select>)}
            </FormItem>
            <FormItem label='排序' {...formItemLayout}>
                {getFieldDecorator('sort', {
                    initialValue: item.sort || ''
                })(<Input />)}
            </FormItem>
            <FormItem label='备注' {...formItemLayout}>
                {getFieldDecorator('remark', {
                    initialValue: item.remark || ''
                })(<Input />)}
            </FormItem>
            <FormItem label='菜单链接' labelCol={{span:6}} wrapperCol={{span:12}}>
                {getFieldDecorator('links', {
                    initialValue: item.links || '',
                })(<Input type="textarea" rows={5}/>)}
            </FormItem>
            <FormItem label='是否设置跳转' labelCol={{span:6}} wrapperCol={{span:12}}>
                <Switch
                    checkedChildren="开"
                    unCheckedChildren="关"
                    onChange={this.switchChange}
                    defaultChecked = {this.state.isRedirect}
                />

            </FormItem>
            {
                this.state.isRedirect?<FormItem label='跳转URL' {...formItemLayout}>
                    {getFieldDecorator('redirectUrl', {
                        initialValue: item.redirectUrl || '',
                        rules: [
                            {
                                required: true,
                                message: '跳转URL未填写'
                            }
                        ]
                    })(<Input />)}
                </FormItem>:null
            }
            <FormItem {...{labelCol: {span: 6}, wrapperCol: {span: 10}}} style={{textAlign:'right'}}>
                <VisibleWrap permis="edit:modify">
                    <Button type="primary" size="large" onClick={this.submitForm}>保存</Button>
                </VisibleWrap>
                <Button onClick={jumpToLink1} style={{ marginLeft: 8 }} size="large" >
                    返回
                </Button>
            </FormItem>
        </Form>
        )
    }
}
/*const form = ({
  viewTreeData=[],
  sysTreeData=[],
  positionTreeData=[],
  categoryTree,
  treeData,
  treeViewData,
  item,
  handleSubmit,
  jumpToLink,
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
      }
      handleSubmit(data)
    })
  }
   return (
    <Form horizontal>
      <FormItem label='上级菜单' {...formItemLayout}>
        {getFieldDecorator('parentId', {
          initialValue: item.parentId ? item.parentId+"" : '1'
        })(<TreeSelect
              allowClear
              showSearch
              treeNodeFilterProp="label"
              dropdownStyle={{maxHeight:300,overflow:'auto'}}
              treeData={treeData}
          />
        )}
      </FormItem>
      <FormItem label='全称' hasFeedback {...formItemLayout}>
        {getFieldDecorator('name', {
          initialValue: item.name || '',
          rules: [
            {
              required: true,
              message: '名称未填写'
            }
          ]
        })(<Input />)}
      </FormItem>
      <FormItem label='简称' {...formItemLayout}>
        {getFieldDecorator('simpleName', {
          initialValue: item.simpleName || '',
          rules: [
            {
              required: true,
              message: '简称未填写'
            }
          ]
        })(<Input />)}
      </FormItem>
      <FormItem label='英文名' {...formItemLayout}>
        {getFieldDecorator('slug', {
          initialValue: item.slug || '',
        })(<Input />)}
      </FormItem>
      <FormItem label='系统类型' {...formItemLayout}>
        {getFieldDecorator('systemType', {
          initialValue: item.systemType || '',
        })(<Select
              allowClear
            >
              {
                sysTreeData.map((item) => {
                  return <Select.Option key={item.id} value={item.value}>{item.label}</Select.Option>
                })
              }
          </Select>)}
      </FormItem>
      <FormItem label='展示位置' {...formItemLayout}>
        {getFieldDecorator('position', {
          initialValue: item.position?(item.position+''):'',
          rules: [
            {
              required: true,
              message: '展示位置未填写'
            }
          ]
        })(<Select
              allowClear
            >
              {
                positionTreeData.map((item) => {
                  return <Select.Option key={item.id} value={item.value}>{item.label}</Select.Option>
                })
              }
          </Select>)}
      </FormItem>
      <FormItem label='对应栏目' {...formItemLayout}>
        {getFieldDecorator('categoryId', {
          initialValue: item.categoryId ? item.categoryId+'' : '',
        })((<TreeSelect
              allowClear
              showSearch
              treeNodeFilterProp="label"
              dropdownStyle={{maxHeight:300,overflow:'auto'}}
              treeData={categoryTree}
          />
        ))}
      </FormItem>
      <FormItem label='展示类型' {...formItemLayout}>
        {getFieldDecorator('viewType', {
          initialValue: item.viewType || '',
        })(<Select
              allowClear
            >
              {
                viewTreeData.map((item) => {
                  return <Select.Option key={item.id} value={item.value}>{item.label}</Select.Option>
                })
              }
          </Select>)}
      </FormItem>
      <FormItem label='排序' {...formItemLayout}>
        {getFieldDecorator('sort', {
          initialValue: item.sort || 0
        })(<Input />)}
      </FormItem>
      <FormItem label='备注' {...formItemLayout}>
        {getFieldDecorator('remark', {
          initialValue: item.remark || ''
        })(<Input />)}
      </FormItem>
      <FormItem label='菜单链接' labelCol={{span:6}} wrapperCol={{span:12}}>
        {getFieldDecorator('links', {
          initialValue: item.links || '',
        })(<Input type="textarea" rows={5}/>)}
      </FormItem>
        <FormItem label='是否设置跳转' labelCol={{span:6}} wrapperCol={{span:12}}>
            <Switch
                checkedChildren="开"
                unCheckedChildren="关"
            />
        </FormItem>
        <FormItem label='跳转URL' {...formItemLayout}>
            {getFieldDecorator('remark', {
                initialValue: item.remark || ''
            })(<Input />)}
        </FormItem>
      <FormItem {...{labelCol: {span: 6}, wrapperCol: {span: 10}}} style={{textAlign:'right'}}>
        <VisibleWrap permis="edit:modify">
          <Button type="primary" size="large" onClick={submitForm}>保存</Button>
        </VisibleWrap>
        <Button style={{ marginLeft: 8 }} size="large" >
          <a onClick={jumpToLink}>返回</a>
        </Button>
      </FormItem>
    </Form>
  )
}*/

// modal.propTypes = {
//   visible: PropTypes.any,
//   form: PropTypes.object,
//   item: PropTypes.object,
//   onOk: PropTypes.func,
//   onCancel: PropTypes.func
// }

export default Form.create()(form)
