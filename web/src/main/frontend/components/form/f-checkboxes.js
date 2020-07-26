import React from 'react';
import {Checkbox} from 'antd';

const CheckboxGroup = Checkbox.Group;

class FCheckboxes extends React.Component {
	constructor(props) {
		super(props);
		let {name, value, options} = this.props;
		options = options ? JSON.parse(options) : [];
		value = JSON.parse(value);
		this.state = {
			name,
			value,
			options
		};
	}

	triggerChg = (value) => {
		const {onChange} = this.props;
		if(onChange) {
			onChange(JSON.stringify(value));
		}
	}

	checkChg = (checkedValue) => {
		this.state.value = checkedValue;
		this.triggerChg(checkedValue);
	}

	render() {
		const {name, value, options} = this.state;
		return (
			<div className="f-checkboxes">
				<label>{name}：</label>
				<CheckboxGroup value={value} onChange={this.checkChg}>
				{
					options.map((option, index) => {
						return <Checkbox key={index} value={option.value}>{option.label}</Checkbox>;
					})
				}
				</CheckboxGroup>
			</div>
		);
	}
}

export default FCheckboxes;
