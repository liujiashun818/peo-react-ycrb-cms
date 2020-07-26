import React from 'react'
import PropTypes from 'prop-types'
import { Form, Button, Input, Row, Col, Modal} from 'antd'
const FormItem = Form.Item
class SensModal extends React.Component{
	constructor(props) {
		super(props)
	}
	handlePost(){
		let {handleOk,form} = this.props
		handleOk(form.getFieldsValue())
	}
	render(){
		let {showSensModal,visible,handleOk,handleCancel,sensWord} = this.props
		let {getFieldDecorator} = this.props.form
		return (
			<Form>
				<Button onClick={showSensModal} type="primary" size="large" style={{marginBottom:10}}>敏感词管理</Button>
				<Modal title="敏感词管理" visible={visible}
 					footer={[
 						<Button key='1' type="primary" style={{ marginRight: '10px' }} size="large" onClick={this.handlePost.bind(this)}>保存</Button>,
 						<Button key='0' size="large" onClick={handleCancel}>返回</Button>
 					]}
 		           onOk={()=> this.handlePost()} onCancel={handleCancel}
 		        >
                    <div style={{color:"red"}}>(请用半角逗号对多个敏感词进行分隔)</div>
	                <FormItem>
	                        {
	                            getFieldDecorator('sensitiveWord', {
	                                initialValue: sensWord
	                            })(
	                                <Input type="textarea" rows={10} placeholder="无" />
	                            )
	                        }
                    </FormItem>
        		</Modal>
			</Form>
		)
	}
}

export default Form.create()(SensModal)
