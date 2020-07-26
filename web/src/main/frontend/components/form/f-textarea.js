import React from 'react';
import {Input, Button, Icon} from 'antd';
import FInput from './f-input';

class FTextarea extends FInput {
	constructor(props) {
		super(props);
	}

	render() {
		const {name, placeholder, simpleOrMore} = this.props;
		const {value} = this.state;
		return (
			<div className="f-input">
				<label>{name}：</label>
				{this.getInputTpl(value, simpleOrMore, placeholder, 'textarea')}
				{this.getAddBtnTpl(simpleOrMore)}
			</div>
		);
	}
}

export default FTextarea;