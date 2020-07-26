import React from 'react';
import {Select, Button, Icon} from 'antd';

const Option = Select.Option;

class FSelect extends React.Component {
	constructor(props) {
		super(props);
		const options = JSON.parse(props.options);
		let value = props.value;
		if(!value) {
			for(let i = 0, len = options.length; i < len; i++) {
				if(options[i].isDefault == true) {
					value = options[i].value;
				}
			}
		}
		this.state = {
			value: value,
			options: options
		};
	}

	triggerChg(value) {
		const {onChange} = this.props;
		if(onChange) {
			onChange(value);
		}
	}

	selChg = (value) => {
		this.state.value = value;
		this.triggerChg(value);
	}

	render() {
		const {name} = this.props;
		const {value, options} = this.state;
		return (
			<div className="f-select">
				<label>{name}：</label>
				<Select value={value} onChange={this.selChg}>
				{
					options.map((option, index) => {
						return (
							<Option key={index} value={option.value}>{option.label}</Option>
						);
					})
				}
				</Select>
			</div>
		);
	}
}

export default FSelect;