import React from 'react';
import {Row, Col, Radio} from 'antd';
import EBase from './e-base';

const RadioGroup = Radio.Group;

class EMedia extends EBase {
	constructor(props) {
		super(props);
	}

	typeChg = (e) => {
		const {fieldData} = this.state;
		fieldData.logic.type = e.target.value;
		this.triggerChg(fieldData);
	}

	getTypeTpl = ({labelCol, wrapperCol}, type='audio') => {
		return (
			<Row className="opt-row">
				<Col className="label-col" span={labelCol.span}>
					<label>媒体类型：</label>
				</Col>
				<Col span={wrapperCol.span}>
					<RadioGroup value={type} onChange={this.typeChg}>
						<Radio value="audio">音频</Radio>
						<Radio value="video">视频</Radio>
					</RadioGroup>
				</Col>
			</Row>
		);
	}

	render() {
		const {layout} = this.props;
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
			<div className="e-base e-media">
				{this.getTypeTpl(layout, logic.type)}
				<hr className="cus-hr"></hr>
				{this.getRequiredTpl(req_rel, layout, required)}
				{
					req_rel ? <hr className="cus-hr"/> : null
				}
			</div>
		);
	}
}

export default EMedia;