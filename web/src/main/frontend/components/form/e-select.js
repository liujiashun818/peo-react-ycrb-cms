import React from 'react';
import {Row, Col, Icon, Input, Checkbox, Button} from 'antd';
import EBase from './e-base';

class ESelect extends EBase {
	constructor(props) {
		super(props);
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
			<div className="e-base e-select">
				{this.getOptionsTpl(layout, options)}
				<hr className="cus-hr"></hr>
				{this.getRequiredTpl(req_rel, layout, required)}
				{
					req_rel ? <hr className="cus-hr"/> : null
				}
			</div>
		);
	}
}

export default ESelect;
