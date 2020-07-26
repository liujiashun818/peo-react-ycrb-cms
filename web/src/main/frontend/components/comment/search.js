import React from 'react'
import PropTypes from 'prop-types'
import { Form, Button, Input, Row, Col,Radio,Dropdown,Menu} from 'antd'

const FormItem = Form.Item
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

const formItemLayout = {
    labelCol: {
        span: 6
    },
    wrapperCol: {
        span: 16
    }
}

class Search extends React.Component{
	constructor(props) {
		super(props)
	}	
	handleSearch = () =>{
		const {onSearch} = this.props
		const {validateFields,getFieldsValue} = this.props.form
		const data = {
			...getFieldsValue()
		}
		onSearch(data)
	}
	render(){
		const {getFieldDecorator} = this.props.form
		const {onDelFlagChange,batchMenuClick,showSensModal,content='',delFlag=0} = this.props
		const batchMenu = (
			<Menu onClick={batchMenuClick}>
				<Menu.Item key="on">批量上线</Menu.Item>
				<Menu.Item key="off">批量下线</Menu.Item>
			</Menu>
		)
		return (
        	<Form>
				<Row>
					<Col span={4}>
						<Dropdown.Button overlay={batchMenu} size="large">
							批量操作
						</Dropdown.Button>
					</Col>
	               <Col span={8}>
                    <RadioGroup onChange={onDelFlagChange} size="large" value={delFlag+''}>
                        <RadioButton value="0">上线</RadioButton>
                        <RadioButton value="4">审核未通过</RadioButton>
                        <RadioButton value="2">待审核</RadioButton>
                        <RadioButton value="1">下线</RadioButton>
                    </RadioGroup>
                	</Col>
						<Col span={9}>
		                    <FormItem
		                        label="评论内容"
		                        {...formItemLayout}
		                    >
		                        {
		                            getFieldDecorator('content', {
		                                initialValue: content || ''
		                            })(
		                                <Input/>
		                            )
		                        }
		                    </FormItem>
	                    </Col>
	                    <Col span={3}>
		                    <FormItem>
		                    	<Button type="primary" onClick={this.handleSearch}>查询</Button>
		                    </FormItem>
						</Col>
				</Row>
			</Form>
		)
	}
}
export default Form.create()(Search)