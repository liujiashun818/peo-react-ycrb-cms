import React from 'react';
import PropTypes from 'prop-types';
import {Form, Input, Select, Modal} from 'antd';
import EInput from '../form/e-input';
import ESelect from '../form/e-select';
import ECheckboxes from '../form/e-checkboxes';
import EDate from '../form/e-date';
import ERadio from '../form/e-radio';
import ETextarea from '../form/e-textarea';
import EImage from '../form/e-image';
import ECheckbox from '../form/e-checkbox';
import EMedia from '../form/e-media';
import EUrl from '../form/e-url';
import EEmail from '../form/e-email';
import ENumeric from '../form/e-numeric';
import EPhone from '../form/e-phone';
import {FIELD_TYPE, FIELD_KEYS} from '../../constants';
import styles from './fieldEditModal.less'

const FormItem = Form.Item;
const Option = Select.Option;

const layout = {
	labelCol: {
		span: 4
	},
	wrapperCol: {
		span: 18
	}
};

const fieldTypes = Object.keys(FIELD_TYPE);

class FieldEditModal extends React.Component {
	constructor(props) {
		super(props);
	}

	onOk = () => {
		const {curField, updateField, form: {validateFields, resetFields}} = this.props;
		validateFields((error, values) => {
			if(error) {
				return;
			}
			const special = values.special;
			delete values.special;
			const data = {id: curField.id,  ...special, ...values};
			updateField(data);
			resetFields();
		});
	}

	onCancel = () => {
		const {updateState, form: {resetFields}} = this.props;
		updateState({
			femv: false
		});
		resetFields();
	}

	typeChg = (type) => {
		this.props.curField.type = type;
	}

	getInitFieldVal = (field) => {
		const res = {};
		FIELD_KEYS.forEach(key => {
			res[key] = field[key];
		});
		return res;
	}

	getFieldTpl = (field, getFieldDecorator) => {
		const props = {opts: FIELD_TYPE[field.type], layout};
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

	render() {
		const {visible, curField, onCancel, form: {getFieldDecorator}} = this.props;
		const modalProps = {
			title: '字段编辑',
			visible,
			width: 600,
			onCancel: this.onCancel,
			onOk: this.onOk,
			wrapClassName: 'vertical-center-modal'
		};
		return (
			<Modal {...modalProps} className={styles['field-edit-modal']}>
				<Form>
					<FormItem label="字段名称" {...layout}>
						{
							getFieldDecorator('name', {
								initialValue: curField.name,
								rules: [
	                        		{ required: true, message: '字段名称未填写'}
                      			]
							})(<Input type="text"/>)
						}	
					</FormItem>
					<FormItem label="字段别名" {...layout}>
						{
							getFieldDecorator('slug', {
								initialValue: curField.slug
							})(<Input type="text" disabled/>)
						}
					</FormItem>
					<FormItem label="字段类型" {...layout}>
						{
							getFieldDecorator('type', {
								initialValue: curField.type
							})(
								<Select onChange={this.typeChg}>
									{
										fieldTypes.map((item, index) => {
											return <Option key={index} value={item} disabled={FIELD_TYPE[item].swicth_rel !== FIELD_TYPE[curField.type || 'input'].swicth_rel}>{item}</Option>
										})
									}
								</Select>
							)
						}
					</FormItem>
					<FormItem label="字段说明" {...layout}>
						{
							getFieldDecorator('description', {
								initialValue: curField.description
							})(<Input type="text"/>)
						}
					</FormItem>
					<hr className="cus-hr"/>
					{this.getFieldTpl(curField, getFieldDecorator)}
				</Form>
			</Modal>
		);
	}
}

export default Form.create()(FieldEditModal);
