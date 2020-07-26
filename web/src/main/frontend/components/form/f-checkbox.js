import React from 'react';
import {Checkbox} from 'antd';
import FBase from './f-base';

class FCheckbox extends FBase {
	constructor(props) {
		super(props);
		let {value, logic} = props;
		logic = this.parseLogic(props);
		this.state = {
			...props,
			logic,
			checked: this.isChecked(value, logic),
			
		};
	}

	isChecked = (value, {checkedVal, unCheckedVal, defChecked}) => {
		if(!value) {
			return defChecked;
		}
		return value === checkedVal;
	}


	checkChg = (e) => {
		const {checkedVal, unCheckedVal} = this.state;
		const checked = e.target.checked;
		const value = checked ? checkedVal || true : unCheckedVal || false;
		this.state.checked = checked;
		this.triggerChg(value);
	}

	render() {
		const {name, checked, logic} = this.state;
		return (
			<div className="f-base f-checkbox">
				<label>{name}：</label>
				<Checkbox checked={checked} onChange={this.checkChg}/>
			</div>
		);
	}
}

export default FCheckbox;