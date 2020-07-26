import React from 'react'
import PropTypes from 'prop-types';
import {Form, Row, Col, Icon, DatePicker, Input, Radio, Button,Select} from 'antd';
import EInput from './e-input';
import ESelect from './e-select';
import ECheckboxes from './e-checkboxes';
import EDate from './e-date';
import ERadio from './e-radio';
import ETextarea from './e-textarea';
import EImage from './e-image';
import ECheckbox from './e-checkbox';
import EMedia from './e-media';
import EUrl from './e-url';
import EEmail from './e-email';
import ENumeric from './e-numeric';
import EPhone from './e-phone';
import {formItemMainLayout, FIELD_TYPE, FIELD_KEYS} from '../../constants';
import {
    Jt
} from '../../utils';
import { checkFieldExist } from '../../modules/fields/services/fields';
import styles from './fieldEdit.less';
const Option = Select.Option;

const FormItem = Form.Item;

formItemMainLayout.colon = !0;
const dateFormat = 'YYYY-MM-DD HH:mm:ss';

class FieldEdit extends React.Component {
	
	constructor(props) {
		super(props);
		this.state = {
	       fieldData : props.value || []
	    }
	    this.receivevali = props.toValidate;
	}
	validate = () => {
	    // 当触发当前表单的检查
	    const { validateFields, getFieldsValue } = this.props.form;
	    const { onValidated } = this.props;
	    validateFields((err, values) => {
		    if(err) {
		    	return;
		    }
			const data = {
				...getFieldsValue(),
			}
			const special = data.special;
			delete data.special;
		    const onChange = this.props.onChange;
		    if(onChange) {
		     	onChange(Object.assign({}, special, data));
		    }
		    onValidated();
		});
	}

	getInitFieldVal = (field) => {
		const res = {};
		FIELD_KEYS.forEach(key => {
			res[key] = field[key];
		});
		return res;
	}

	getFieldTpl = (field, getFieldDecorator) => {
		const props = {opts: FIELD_TYPE[field.type], layout: formItemMainLayout};
		const emap = {
			'input': <EInput {...props}/>,
			'select': <ESelect {...props}/>,
			'checkboxes': <ECheckboxes {...props}/>,
			'date': <EDate {...props}/>,
			'radio': <ERadio {...props}/>,
			'textarea': <ETextarea {...props}/>,
			'image': <EImage {...props}/>,
			'checkbox': <ECheckbox {...props}/>,
			'media': <EMedia {...props}/>,
			'url': <EUrl {...props}/>,
			'email': <EEmail {...props}/>,
			'numeric': <ENumeric {...props}/>,
			'phone': <EPhone {...props}/>
		};
		const fc = emap[field.type];
		if(!fc) {
			return null;
		}
		return (
			<FormItem className="field-special-item">
				{
					getFieldDecorator('special', {
						initialValue: this.getInitFieldVal(field)
					})(fc)
				}
			</FormItem>
		);
	}

	typeChg = (type) => {
		const fieldData = this.state.fieldData;
		fieldData.type = type;
		this.setState({
			fieldData
		});
	}

	onRemoveField = (e,id) =>{
		e.preventDefault();
		this.props.removeField(id);
	}
	getFieldTypes = (e) => {
		const fieldTypes = [];
		for(let key of Object.keys(FIELD_TYPE)) {
		    fieldTypes.push(key)				
		}
		return fieldTypes;
	}
	componentWillReceiveProps(nextProps) {
	    if (nextProps.value !== this.state.fieldData) {
	      	this.setState(
	        	{fieldData:nextProps.value}
	        );
	    }
	    if(nextProps.toValidate != this.receivevali) {
	    	this.receivevali = nextProps.toValidate;
	    	this.validate();
	    }
	}

	render() {
		const { getFieldDecorator } = this.props.form;
		const {
			id,
			id_key,
			name,
			slug,
			type,
			description,
			defaultValue,
			placeholder,
			simpleOrMore,
			isAllowSearch,
			validate,
			options,
		} = this.state.fieldData;
		return (
			<div className={['ant-form', styles['field-edit-box']].join(' ')}>
				<Form.Item {...formItemMainLayout}>
                {
                	getFieldDecorator('id', {
                    	initialValue: id
                	})(<Input type="hidden" />)
                }
                </Form.Item>
				<Form.Item label="字段名称" {...formItemMainLayout}>
				{
					getFieldDecorator('name', {
                    	initialValue: name,
                      	rules: [
	                        { required: true, message: '字段名称未填写'}
                      	]
                    })(<Input type="text" />)
	            }
				</Form.Item>
				<Form.Item label='字段别名' {...formItemMainLayout}>
				{
					getFieldDecorator('slug', {
	                    initialValue: slug,
	                    rules: [
	                        {
	                          	required: true,
	                          	validator(rule,value,callback){
	                          		if(Jt.string.isEmpty(value)){
	                          			callback(new Error('字段别名未填写'));
	                          		}else if(!slug){
	                          			checkFieldExist({slug:value}).then(function(data){
	                          				if(data.code===0 && data.data === true){
	                          					callback(new Error('别名已经存在'));
									    	}else{
									    		callback();
									    	}
								      	},function(error){
								        	callback();
								      	});
	                          		}else{
	                          			callback();
	                          		}
							    }
	                        }
	                    ]
	                })(<Input type="text" disabled={id ? true : false} />)
	            }
				</Form.Item>
				<Form.Item label='字段类型' {...formItemMainLayout}>
				{
					getFieldDecorator('type', {
	                    initialValue: type,
	                })(
	                    <Select style={{ width: 200 }} onChange={this.typeChg}>
	                    	{this.getFieldTypes().map((item_type,index)=>{
	                    		const type_swc = FIELD_TYPE[type||'input'].swicth_rel;
	                    		const this_type_swc = FIELD_TYPE[item_type].swicth_rel;
	                    		return <Option value={item_type} disabled={type_swc==this_type_swc?false:true} key={index}>{item_type}</Option>
	                    	})}
					    </Select>
	                )
	            }
				</Form.Item>
				<Form.Item label='字段说明' {...formItemMainLayout}>
				{
					getFieldDecorator('description', {
	                	initialValue: description,
	            	})(<Input type="text" />)
	        	}
				</Form.Item>
				<hr className="cus-hr" />
				{this.getFieldTpl(this.state.fieldData, getFieldDecorator)}
				<p className="mb10"><a onClick={(e)=>{this.onRemoveField(e,id||id_key)}}><Icon type="delete" />删除字段组</a></p>
			</div>
		);
	}
}

export default Form.create()(FieldEdit);