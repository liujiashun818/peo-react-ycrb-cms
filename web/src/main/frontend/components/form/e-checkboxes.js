import React from 'react';
import {Row, Col, Icon, Input, Checkbox, Button} from 'antd';
import EBase from './e-base';

class ECheckboxes extends EBase {
	constructor(props) {
		super(props);
	}

	optionDefaultChg = (e, key) => {
		const {fieldData} = this.state;
		let options = fieldData.options;
		for(let i = 0, len = options.length; i < len; i++) {
			if(options[i].key === key) {
				options[i].isDefault = e.target.checked;
			}
		}
		fieldData.options = options;
		this.triggerChg(fieldData);
	}

	render() {
		const {layout} = this.props;
		const {
			fieldOpts: {
				req_rel
			},
			fieldData: {
				options,
				validate: {
					required={}
				}
			}
		} = this.state;
		return (
			<div className="e-base e-checkboxes">
				{this.getOptionsTpl(layout, options)}
				<hr className="cus-hr"/>
				{this.getRequiredTpl(req_rel, layout, required)}
				{
					req_rel ? <hr className="cus-hr"/> : null
				}
			</div>
		);
	}
}

export default ECheckboxes;