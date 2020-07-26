import React from 'react';
import {Row, Col, Input, Radio, Checkbox, Button, Icon} from 'antd';
import './e-base.less';

const RadioGroup = Radio.Group;

class EBase extends React.Component {
	constructor(props) {
		super(props);
		let {opts, value} = props;
		value = this.fromValue(value);
		this.state = {
			fieldOpts: opts,
			fieldData: value
		};
	}

	fromValue = (value) => {
		let {options, validate, logic} = value;
		options = options ? JSON.parse(options) : [];
		options.map((option, index) => option.key = -(index + 1));
		const defValidate = {required: {}, format: {}, imgCut: {}};
		validate = validate ? {...defValidate, ...JSON.parse(validate)} : defValidate;
		logic = logic ? JSON.parse(logic) : {};
		return {...value, options, validate, logic};
	}

	toValue = (value) => {
		let {options, validate, logic} = value;
		options = options ? JSON.stringify(options) : undefined;
		validate = validate ? JSON.stringify(validate) : undefined;
		logic = logic ? JSON.stringify(logic) : undefined;
		return {...value, options, validate, logic};
	}

	triggerChg = (value) => {
		const {onChange} = this.props;
		if(onChange) {
			value = this.toValue(value);
			onChange(value);
		}
	}

	placeholderChg = (e) => {
		const {fieldData} = this.state;
		fieldData.placeholder = e.target.value;
		this.triggerChg(fieldData);
	}

	getPlaceholderTpl = (holder_rel, {labelCol, wrapperCol}, placeholder) => {
		if(!holder_rel) {
			return null;
		}
		return (
			<Row className="opt-row">
				<Col span={labelCol.span} className="label-col">
					<label>占位符：</label>
				</Col>
				<Col span={wrapperCol.span}>
					<Input value={placeholder} onChange={this.placeholderChg}/>
				</Col>
			</Row>
		);
	}

	defaultValueChg = (e) => {
		const {fieldData} = this.state;
		fieldData.defaultValue = e.target.value;
		this.triggerChg(fieldData);
	}

	getDefaultValueTpl = (def_rel, {labelCol, wrapperCol}, defaultValue) => {
		if(!def_rel) {
			return null;
		}
		return (
			<Row className="opt-row">
				<Col span={labelCol.span} className="label-col">
					<label>默认值：</label>
				</Col>
				<Col span={wrapperCol.span}>
					<Input value={defaultValue} onChange={this.defaultValueChg}/>
				</Col>
			</Row>
		);
	}

	simpleOrMoreChg = (e) => {
		const {fieldData} = this.state;
		fieldData.simpleOrMore = e.target.value;
		this.triggerChg(fieldData);
	}

	getSimpleOrMoreTpl = (multi_rel, {labelCol, wrapperCol}, simpleOrMore) => {
		if(!multi_rel) {
			return null;
		}
		return (
			<Row className="opt-row">
				<Col span={labelCol.span} className="label-col">
					<label>单一或重复：</label>
				</Col>
				<Col span={wrapperCol.span}>
					<RadioGroup value={simpleOrMore} onChange={this.simpleOrMoreChg}>
						<Radio value="1">只能有一个值</Radio>
						<Radio value="2">允许多个实例</Radio>
					</RadioGroup>
				</Col>
			</Row>
		);
	}

	addOption = () => {
		const {fieldData} = this.state;
		let options = fieldData.options;
		options.map((option, index) => {option.key = -(index + 1)});
		options.push({
			key: -(options.length + 1),
			label: '',
			value: '',
			isDefault: false
		});
		fieldData.options = options;
		this.triggerChg(fieldData);
	}

	delOption = (key) => {
		const {fieldData} = this.state;
		let options = fieldData.options;
		options = options.filter(option => option.key !== key);
		fieldData.options = options;
		this.triggerChg(fieldData);
	}

	optionLabelChg = (e, key) => {
		const {fieldData} = this.state;
		let options = fieldData.options;
		for(let i = 0, len = options.length; i < len; i++) {
			if(options[i].key === key) {
				options[i].label = e.target.value;
				break;
			}
		}
		fieldData.options = options;
		this.triggerChg(fieldData);
	}

	optionValueChg = (e, key) => {
		const {fieldData} = this.state;
		let options = fieldData.options;
		for(let i = 0, len = options.length; i < len; i++) {
			if(options[i].key === key) {
				options[i].value = e.target.value;
				break;
			}
		}
		fieldData.options = options;
		this.triggerChg(fieldData);
	}

	optionDefaultChg = (e, key) => {
		const {fieldData} = this.state;
		let options = fieldData.options;
		for(let i = 0, len = options.length; i < len; i++) {
			if(options[i].key === key) {
				options[i].isDefault = true;
			}
			else {
				options[i].isDefault = false;
			}
		}
		fieldData.options = options;
		this.triggerChg(fieldData);
	}

	getOptionsTpl = ({labelCol, wrapperCol}, options) => {
		return (
			<Row className="opt-row options-box">
				<Col span={labelCol.span} className="label-col">
					<label>选项：</label>
				</Col>
				<Col span={wrapperCol.span}>
					<Row className="label-row">
						<Col span={6}>标题</Col>
						<Col span={6} offset={1}>存储的值</Col>
						<Col span={2} offset={1}>默认选中</Col>
					</Row>
					{
						options.map((option, index) => {
							return (
								<Row key={option.key} className="option-row">
									<Col span={6}><Input value={option.label} onChange={(e) => this.optionLabelChg(e, option.key)}/></Col>
									<Col span={6} offset={1}><Input value={option.value} onChange={(e) => this.optionValueChg(e, option.key)}/></Col>
									<Col span={2} offset={1}><Checkbox size="large" checked={option.isDefault} onChange={(e) => this.optionDefaultChg(e, option.key)}/></Col>
									<Col span={3} offset={1}>
										<Icon type="minus-circle-o" className="del-btn" onClick={() => this.delOption(option.key)}/>
									</Col>
								</Row>
							);
						})
					}
					<Button type="dashed" className="add-btn" onClick={this.addOption}><Icon type="plus" />添加选项</Button>
				</Col>
			</Row>
		);
	}

	requiredChg = (type, e) => {
		const {fieldData} = this.state;
		if(type === '1') {
			fieldData.validate.required.active = e.target.checked;
		}
		else if(type === '2') {
			fieldData.validate.required.msg = e.target.value;
		}
		this.triggerChg(fieldData);
	}

	getRequiredTpl = (req_rel, {labelCol, wrapperCol}, {active, msg}) => {
		if(!req_rel) {
			return null;
		}
		msg = msg || '此字段是必须的';
		return (
			<div className="validate-box required-box">
				<Row className="opt-row">
					<Col span={labelCol.span} className="label-col">
						<label>验证：</label>
					</Col>
					<Col span={wrapperCol.span}>
						<Checkbox checked={active} onChange={this.requiredChg.bind(this, '1')}>必须</Checkbox>
					</Col>
				</Row>
				<Row className="opt-row">
					<Col span={labelCol.span} className="label-col">
						<label>验证错误消息：</label>
					</Col>
					<Col span={wrapperCol.span}>
						<Input value={msg} onChange={this.requiredChg.bind(this, '2')}/>
					</Col>
				</Row>
			</div>
		);
	}

	getFormatMsg = (msg) => {
		return msg || '';
	}

	getFormatActiveLabel = () => {
		return '';
	}

	formatChg = (type, e) => {
		const {fieldData} = this.state;
		if(type === '1') {
			fieldData.validate.format.active = e.target.checked;
		}
		else if(type === '2') {
			fieldData.validate.format.msg = e.target.value;
		}
		this.triggerChg(fieldData);
	}

	getFormatTpl = (format_rel, {labelCol, wrapperCol}, {active, msg}) => {
		if(!format_rel) {
			return null;
		}
		msg = this.getFormatMsg(msg);
		const activeLabel = this.getFormatActiveLabel();
		return (
			<div className="validate-box format-box">
				<Row className="opt-row">
					<Col span={labelCol.span} className="label-col">
						<label>验证：</label>
					</Col>
					<Col span={wrapperCol.span}>
						<Checkbox checked={active} onChange={this.formatChg.bind(this, '1')}>{activeLabel}</Checkbox>
					</Col>
				</Row>
				<Row className="opt-row">
					<Col span={labelCol.span} className="label-col">
						<label>验证错误消息：</label>
					</Col>
					<Col span={wrapperCol.span}>
						<Input value={msg} onChange={this.formatChg.bind(this, '2')}/>
					</Col>
				</Row>
			</div>
		);
	}

	render() {
		const {layout} = this.props;
		const {
			fieldOpts: {
				holder_rel,
				def_rel,
				multi_rel,
				req_rel,
				format_rel
			}, 
			fieldData: {
				placeholder, 
				defaultValue, 
				simpleOrMore='1',
				validate: {
					required={}, 
					format={}
				}
			}
		} = this.state;
		return (
			<div className="e-base e-url">
				{this.getPlaceholderTpl(holder_rel, layout, placeholder)}
				{this.getDefaultValueTpl(def_rel, layout, defaultValue)}
				{
					holder_rel || def_rel ? <hr className="cus-hr"/> : null
				}
				{this.getRequiredTpl(req_rel, layout, required)}
				{
					req_rel ? <hr className="cus-hr"/> : null
				}
				{this.getFormatTpl(format_rel, layout, format, 'Email')}
				{
					format_rel ? <hr className="cus-hr"/> : null
				}
				{this.getSimpleOrMoreTpl(multi_rel, layout, simpleOrMore)}
				{
					multi_rel ? <hr className="cus-hr"/> : null
				}
			</div>
		);
	}
}

export default EBase;