import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, InputNumber, Select, TreeSelect, Switch, Button } from 'antd'
import ImgUploader from '../form/imgUploader'
import styles from './edit.less'
import VisibleWrap from '../ui/visibleWrap'

const FormItem = Form.Item

const Option = Select.Option

const formItemLayout = {
    labelCol: {
        span: 3
    },
    wrapperCol: {
        span: 8
    }
}
const goBack = () => {
    history.back()
}

const edit = ({
    curCatg,
    catgs,
    offices,
    catgModels,
    catgViews,
    createCatg,
    updateCatg,
    form: {
        getFieldDecorator,
        validateFields,
        validateFieldsAndScroll,
        getFieldsValue
    }
}) => {
    let officeId = null
    if(curCatg.officeId) {
        officeId = curCatg.officeId + ''
    }
    else if(offices.length > 0) {
        officeId = offices[0].id + ''
    }

    function submitHandle() {
        validateFieldsAndScroll((errors) => {
            if(errors) {
                return
            }
            const data = { ...curCatg, ...getFieldsValue() }
        if(data.image && data.image.fileList&& data.image.fileList[0]) {
            data.image = data.image.fileList[0].url
        }
        else {
            delete data.image
        }
        if(!curCatg.id) {
            createCatg(data)
        }
        else {

            data.id = curCatg.id
            data.parentIds = curCatg.parentIds
            updateCatg(data)
        }
    });
    }
    return (
        <Form>
        <FormItem
    label="归属机构"
    {...formItemLayout}
>
    {
        getFieldDecorator('officeId', {
            initialValue: officeId || null
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
</FormItem>
    <FormItem
    label="上级栏目"
    {...formItemLayout}
>
    {
        getFieldDecorator('parentId', {
            initialValue: curCatg.parentId ? curCatg.parentId + '' : null,
            rules: [
            {required: true, message: '请输入栏目名称！'}
        ]
        })(
        <TreeSelect
        allowClear
        showSearch
        treeNodeFilterProp="label"
        dropdownStyle={{maxHeight: 300, overflow: 'auto'}}
        treeData={catgs}
            />
    )
    }
</FormItem>
    <FormItem
    label="栏目模型"
    {...formItemLayout}
>
    {
        getFieldDecorator('modelId', {
            initialValue: curCatg.modelId ? curCatg.modelId + '' : null
        })(
        <Select
        allowClear
        >
        {
            catgModels.map((item) => {
            return <Option key={item.id} value={item.id + ''}>{item.name}</Option>
    })
    }
    </Select>
    )
    }
</FormItem>
    <FormItem
    label="栏目名称"
    {...formItemLayout}
    hasFeedback
    >
    {
        getFieldDecorator('name', {
        initialValue: curCatg.name || '',
            rules: [
            {required: true, message: '请输入栏目名称！'}
        ]
    })(
    <Input type="text" />
)
}
</FormItem>
    <FormItem
    label="栏目别名"
    {...formItemLayout}
>
    {
        getFieldDecorator('slug', {
            initialValue: curCatg.slug || ''
        })(
        <Input type="text" />
    )
    }
</FormItem>
    <FormItem
    label="栏目图片"
    {...formItemLayout}
>
    {
        getFieldDecorator('image', {
            initialValue: {fileList: curCatg.image ? [{name:curCatg.image, url: curCatg.image, uid: -1}] : null}
        })(
        <ImgUploader single={true}/>
    )
    }
</FormItem>
    <FormItem
    label="展示类型"
    {...formItemLayout}
>
    {
        getFieldDecorator('viewType', {
            initialValue: curCatg.viewType || null
        })(
        <Select
        allowClear
        >
        {
            catgViews.map((item) => {
            return <Option key={item.id} value={item.value}>{item.label}</Option>
    })
    }
    </Select>
    )
    }
</FormItem>
    <FormItem
    label="描述"
    {...formItemLayout}
>
    {
        getFieldDecorator('description', {
            initialValue: curCatg.description || ''
        })(
        <Input type="textarea" rows={4} />
    )
    }
</FormItem>
    <FormItem
    label="排序"
    {...formItemLayout}
>
    {
        getFieldDecorator('sort', {
            initialValue: curCatg.sort || 1
        })(
        <InputNumber />
    )
    }
<span className="ant-form-text">栏目的排列次序</span>
        </FormItem>
        <FormItem
    label="先发后审"
    {...formItemLayout}
>
    {
        getFieldDecorator('isAutoOnline', {
            valuePropName: 'checked',
            initialValue: typeof curCatg.isAutoOnline === 'undefined' ? false : curCatg.isAutoOnline
        })(
        <Switch checkedChildren="是" unCheckedChildren="否"/>
    )
    }
</FormItem>
    <FormItem
    wrapperCol={{span: 8, offset: 3}}
>
<VisibleWrap permis="edit:modify">
        <Button type="primary" size="large" onClick={submitHandle}>保存</Button>
        </VisibleWrap>
        <Button style={{marginLeft: '30px'}} onClick={goBack}>取消</Button>
        </FormItem>
        </Form>
)
}

export default Form.create()(edit)
