import React from 'react';
import {Row, Col, Input, Radio} from 'antd';
import EBase from './e-base';

class ERadio extends EBase {
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
			<div className="e-base e-radio">
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

export default ERadio;