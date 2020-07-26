import React from 'react';
import {DatePicker, Button, Icon} from 'antd';
import moment from 'moment';
import FBase from './f-base';
import './f-date.less';

const dateFormat = 'YYYY-MM-DD HH:mm:ss';

class FDate extends FBase {
	constructor(props) {
		super(props);
		let {value, validate} = props;
		value = this.parseVal(props);
		validate = this.parseValidate(props);
		this.state = {
			...props,
			value,
			validate
		};
	}

	dateChg = (dateStr, simpleOrMore, key) => {
		let value = this.state.value;
		if(simpleOrMore == 2) {
			value.forEach(item => {
				if(item.key === key) {
					item.value = dateStr;
				}
			});
		}
		else {
			value = dateStr;
		}
		this.state.value = value;
		this.triggerChg(value);
	}

	render() {
		const {name, placeholder, simpleOrMore, value, validate} = this.state;
		const format = validate.format
								? validate.format.regExp ? validate.format.regExp : dateFormat
								: dateFormat;
		let inputTpl = null;
		let addBtnTpl = null;
		if(simpleOrMore == 2) {
			inputTpl = value.map((item) => {
				return (
					<div key={item.key} className="instance-item">
						<DatePicker
							showTime
							placeholder={placeholder}
							format={format}
							value={item.value ? moment(item.value, format) : null}
							onChange={(date, dateStr) => this.dateChg(dateStr, simpleOrMore, item.key)}
						/>
						{
							value.length > 1
							? <Icon type="minus-circle-o" className="del-btn" onClick={() => this.delInstance(item.key)}/>
							: null
						}
					</div>
				);
			});
			addBtnTpl = <Button type="dashed" className="add-btn" onClick={this.addInstance}><Icon type="plus" />添加</Button>;
		}
		else {
			inputTpl = <div><DatePicker showTime placeholder={placeholder} format={dateFormat} value={value ? moment(value, dateFormat) : null} onChange={(date, dateStr) => this.dateChg(dateStr, simpleOrMore)}/></div>
		}

		return (
			<div className="f-base f-date">
				<label>{name}：</label>
				{inputTpl}
				{addBtnTpl}
			</div>
		);
	}
}

export default FDate;