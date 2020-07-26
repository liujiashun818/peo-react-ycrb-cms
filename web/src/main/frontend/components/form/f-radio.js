import React from 'react';
import {Radio} from 'antd';
import FBase from './f-base';

const RadioGroup = Radio.Group;

class FRadio extends FBase {
	constructor(props) {
		super(props);
		const options = props.options ? JSON.parse(props.options) : [];
		this.state = {
			...props,
			options
		};
	}

	chgHandle = (e) => {
		const value = e.target.value;
		this.state.value = value;
		this.triggerChg(value);
	}

	render() {
		const {name, value, options} = this.state;
		return (
			<div className="f-radio">
				<label>{name}：</label>
				<RadioGroup value={value} onChange={this.chgHandle}>
				{
					options.map((option, index) => {
						return <Radio key={index} value={option.value}>{option.label}</Radio>
					})
				}
				</RadioGroup>
			</div>
		);
	}
}

export default FRadio;
