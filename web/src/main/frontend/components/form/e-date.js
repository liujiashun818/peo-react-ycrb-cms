import React from 'react';
import {Row, Col, Input} from 'antd';
import EBase from './e-base';

class EDate extends EBase {
	constructor(props) {
		super(props);
	}

	dateFormatChg = (e) => {
		const {fieldData} = this.state;
		fieldData.validate = {
			...fieldData.validate,
			format: {
				regExp: e.target.value
			}
		};
		this.triggerChg(fieldData);
	}

	getDateFormatTpl = ({labelCol, wrapperCol}, dateFormat) => {
		return (
			<Row className="opt-row">
				<Col span={labelCol.span} className="label-col">
					<label>日期格式：</label>
				</Col>
				<Col span={wrapperCol.span}>
					<Input placeholder="例如：YYYY-MM-DD HH:mm:ss" value={dateFormat} onChange={this.dateFormatChg}/>
				</Col>
			</Row>
		);
	}

	render() {
		const {layout} = this.props;
		const {
			fieldOpts: {
				holder_rel,
				def_rel,
				multi_rel,
				req_rel
			},
			fieldData: {
				placeholder, 
				defaultValue, 
				simpleOrMore='1',
				validate: {
					required={}, 
					format={}
				}
			}
		} = this.state;
		const dateFormat = format.regExp || null;
		return (
			<div className="e-base e-date">
				{this.getPlaceholderTpl(holder_rel, layout, placeholder)}
				{this.getDateFormatTpl(layout, dateFormat)}
				{
					holder_rel || def_rel ? <hr className="cus-hr"/> : null
				}
				{this.getRequiredTpl(req_rel, layout, required)}
				{
					req_rel ? <hr className="cus-hr"/> : null
				}
				{this.getSimpleOrMoreTpl(multi_rel, layout, simpleOrMore)}
				{
					multi_rel ? <hr className="cus-hr"/> : null
				}
			</div>
		);
	}
}

export default EDate;