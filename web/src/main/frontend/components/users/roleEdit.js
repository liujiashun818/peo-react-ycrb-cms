import React from 'react'
import PropTypes from 'prop-types';
import { Form, Input, Radio, Modal, Button, Select, TreeSelect} from 'antd';

const formItemLayout = {
    labelCol: {
        span: 3
    },
    wrapperCol: {
        span: 12
    }
};

class RoleEdit extends React.Component {
    constructor(props) {
        super(props);
    }

    saveHandle() {
        const {role, onSave, form: {validateFields}} = this.props;
        validateFields((errors, values) => {
            if(errors) {
                return;
            }
            values.id = role.id;
            onSave(values);
        })
    }

    render() {
        const {offices, dataScopes, menus, role, goBack, form: {getFieldDecorator}} = this.props;
        role.menuIds = role.menuIds || [];
        return (
            <Form horizontal>
                <Form.Item
                    hasFeedback
                    label="归属机构"
                    {...formItemLayout}
                >
                    {
                        getFieldDecorator('officeId', {
                            initialValue: role.officeId + '',
                            rules: [
                                {required: true, message: '请选中归属机构'}
                            ]
                        })(
                            <TreeSelect 
                                allowClear
                                showSearch
                                treeNodeFilterProp="label"
                                dropdownStyle={{maxHeight: 300, overflow: 'auto'}}
                                treeData={offices}
                            />
                        )
                    }
                </Form.Item>
                <Form.Item
                    hasFeedback
                    label="角色名称"
                    {...formItemLayout}
                >
                    {
                        getFieldDecorator('name', {
                            initialValue: role.name,
                            rules: [
                                {required: true, message: '请输入角色名称'}
                            ]
                        })(
                            <Input />
                        )
                    }
                </Form.Item>
                <Form.Item
                    hasFeedback
                    label="数据范围"
                    {...formItemLayout}
                >
                    {
                        getFieldDecorator('dataScope', {
                            initialValue: role.dataScope ? role.dataScope + '' : '',
                            rules: [
                                {required: true, message: '请选择数据范围'}
                            ]
                        })(
                            <Select>
                                {
                                    dataScopes.map(ds => {
                                        return <Select.Option key={ds.id} value={ds.value+''}>{ds.label}</Select.Option>
                                    })
                                }
                            </Select>
                        )
                    }
                </Form.Item>
                <Form.Item
                    hasFeedback
                    label="角色授权"
                    {...formItemLayout}
                >
                    {
                        getFieldDecorator('menuIds', {
                            initialValue: role.menuIds.length > 0 ? role.menuIds.join(',').split(',') : null,
                            rules: [
                                {required: true, message: '请选择角色授权'}
                            ]
                        })(
                            <TreeSelect 
                                allowClear
                                showSearch
                                showCheckedStrategy={TreeSelect.SHOW_ALL}
                                treeNodeFilterProp="label"
                                dropdownStyle={{maxHeight: 300, overflow: 'auto'}}
                                treeData={menus}
                                multiple={true}
                                treeCheckable={true}
                            />
                        )
                    }
                </Form.Item>
                <Form.Item wrapperCol={{span: formItemLayout.wrapperCol.span, offset: formItemLayout.labelCol.span}}>
                    <Button type="primary" size="large" style={{ marginRight: 20 }} onClick={this.saveHandle.bind(this)}>保存</Button>
                    <Button size="large" onClick={goBack}>返回</Button>
                </Form.Item>
            </Form>
        );
    }
}

export default Form.create()(RoleEdit);