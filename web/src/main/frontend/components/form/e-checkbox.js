import React from 'react';
import {Row, Col, Input, Checkbox} from 'antd';
import EBase from './e-base';

class ECheckbox extends EBase {
	constructor(props) {
		super(props);
	}

	logicChg (name, e) {
		const {fieldData} = this.state;
		fieldData.logic[name] = name === 'defChecked' ? e.target.checked : e.target.value;
		this.triggerChg(fieldData);
	}

	render() {
		const {layout} = this.props;
		const {labelCol, wrapperCol} = layout;
		const {
			fieldOpts: {
				req_rel
			},
			fieldData: {
				logic,
				validate: {
					required={}
				}
			}
		} = this.state;
		return (
			<div className="e-base e-checkbox">
				<Row className="opt-row">
					<Col span={labelCol.span} className="label-col">
						<label>选中值：</label>
					</Col>
					<Col span={wrapperCol.span}>
						<Input value={logic.checkedVal} onChange={this.logicChg.bind(this, 'checkedVal')}/>
					</Col>
				</Row>
				<Row className="opt-row">
					<Col span={labelCol.span} className="label-col">
						<label>未选中值：</label>
					</Col>
					<Col span={wrapperCol.span}>
						<Input value={logic.uncheckedVal} onChange={this.logicChg.bind(this, 'uncheckedVal')}/>
					</Col>
				</Row>
				<Row className="opt-row">
					<Col span={labelCol.span} className="label-col">
						<label>默认选中：</label>
					</Col>
					<Col span={wrapperCol.span}>
						<Checkbox checked={logic.defChecked} onChange={this.logicChg.bind(this, 'defChecked')}/>
					</Col>
				</Row>
				<hr className="cus-hr"></hr>
			</div>
		);
	}
}

export default ECheckbox;