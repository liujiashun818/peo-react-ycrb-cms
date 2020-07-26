import React from 'react';
import {
	Button,
	Form,
	Input,
	Row,
	Col
} from 'antd'
import VisibleWrap from '../ui/visibleWrap'

const FormItem = Form.Item;
const {TextArea} = Input;
class Reply extends React.Component{
    constructor(props){
        super(props)
    }
    handleSubmitReply() {
        const {handleSubmitReply} = this.props;
        this.props.form.validateFields((err, values) => {
        	if (err) {

        	} else {
                handleSubmitReply(values);
                setTimeout(()=>{
                    this.props.form.setFieldsValue({
                        'userName':'',
                        'content':''
                    })
                },500)
        	}
        })
    }
    render() {
        const {getFieldDecorator,resetFields} = this.props.form;
        return (
            <Form>
            	<Row type="flex" gutter={12} align="middle">
            		<Col span="8">
	            		<FormItem>
		                    {
		                    	getFieldDecorator('content', {
			                        initialValue: '',
			                        rules: [
			                            {
			                                required: true,
			                                message: '请填入评论内容'
			                            },
                                        {
                                            whitespace: true,
                                            message: '请填入评论内容'
                                        }
			                        ]
			                    })(<TextArea placeholder="回复内容" rows="4" ></TextArea>)
		                    }
		                </FormItem>
            		</Col>
            		<Col span="6">
		                <FormItem>
		                {
		                    getFieldDecorator('userName', {
                                rules: [
                                    {
                                        required: true,
                                        message: '回复人姓名'
                                    },
                                    {
                                        whitespace: true,
                                        message: '回复人姓名'
                                    }
                                ]
		                    })(<Input placeholder="回复人姓名"/>)
		                }
		                </FormItem>
            		</Col>
            		<Col span="4">
            			<Button type="primary" style={{marginBottom: '24px'}} onClick={this.handleSubmitReply.bind(this)}>提交评论</Button>
            		</Col>
            	</Row>
            </Form>
        )
    }
}

export default Form.create()(Reply)
