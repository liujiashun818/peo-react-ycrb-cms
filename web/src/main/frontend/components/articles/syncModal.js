import React from 'react'
import PropTypes from 'prop-types'
import { Form, Modal, Tree, Icon, message } from 'antd'
import InputStat from '../form/inputStat'
import styles from './syncModal.less'
const FormItem = Form.Item
const { TreeNode } = Tree;

const formItemLayout = {
    labelCol: {
        span: 4
    },
    wrapperCol: {
        span: 18
    }
}

const SyncModal = ({
    visible,
    curArt={},
    publicCmsCategoryList,
    onTreeSelect,
    selectCategory,
    onOk,
    onCancel,
    form: {
        getFieldDecorator,
        validateFields,
        getFieldsValue
    }
}) => {
    function handleOk () {
        validateFields((errors) => {
            if (errors) {
                return
            }
            if (!selectCategory) {
                return message.error('请选择栏目！');
            }
            Modal.confirm({
                content: `确认同步文章到${selectCategory.name}栏目？`,
                onOk: () => {
                    const data = {
                        // ...getFieldsValue(),
                        articleId: curArt.id,
                        selectCategory
                    }
                    onOk(data)
                }
            })
        })
    }

    const modalOpts = {
        key: curArt.id,
        publicCmsCategoryList,
        title: '栏目列表',
        visible,
        width: 300,
        height:200,
        bodyStyle: {
          maxHeight: '400px',
          minHeight: '400px',
        },
        onOk: handleOk,
        onCancel,
        wrapClassName: 'vertical-center-modal'
    }
    const onCheck = (checkedKeys) => {
      // console.log('onCheck', checkedKeys);
    }

    const onSelect = (selectedKeys, info) => {
      onTreeSelect(info.selectedNodes[0])
    }
    const renderTreeNodes = data => data.map((item) => {
      if (item.children) {
        return (
          <TreeNode className={styles['cus-tree']} icon={<Icon type="file" />} {...item} title={item.name} key={item.id} dataRef={item}>
            {renderTreeNodes(item.children)}
          </TreeNode>
        );
      }
      return <TreeNode className={styles['cus-tree']} {...item} icon={<Icon type="file" />} key={item.id} title={item.name}/>;
    })
    return (
        <Modal {...modalOpts} style={{ top: 20 }}>
          {publicCmsCategoryList && publicCmsCategoryList[0] && <Tree
            defaultExpandAll
            blockNode
            onCheck={onCheck}
            onSelect={onSelect}
          >
            {renderTreeNodes(publicCmsCategoryList[0].children)}
          </Tree>}
        </Modal>
    )
}

SyncModal.propTypes = {
  visible: PropTypes.any,
  form: PropTypes.object,
  curArt: PropTypes.object,
  onOk: PropTypes.func,
  onTreeSelect: PropTypes.func,
  onCancel: PropTypes.func
}

export default Form.create()(SyncModal)
