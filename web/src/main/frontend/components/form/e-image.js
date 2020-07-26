import React from 'react';
import {Row, Col, Input, Radio} from 'antd';
import EBase from './e-base';

class EImage extends EBase {
	constructor(props){
		super(props);
	}

	heightChg = (e) => {
		const {fieldData} = this.state;
		fieldData.validate.imgCut.height = e.target.value;
		this.triggerChg(fieldData);
	}
	widthChg = (e) => {
		const {fieldData} = this.state;
		fieldData.validate.imgCut.width = e.target.value;
		this.triggerChg(fieldData);
	}
	ratioChg = (e) => {
		const {fieldData} = this.state;
		fieldData.validate.imgCut.ratio = e.target.value;
		this.triggerChg(fieldData);
	}
	
	render(){
		const {layout: {labelCol, wrapperCol}} = this.props;
		const {fieldOpts, fieldData} = this.state;
		return (
			<div className="e-base e-image">
				<Row className="opt-row">
					<Col span={labelCol.span} className="label-col">
						<label>宽度：</label>
					</Col>
					<Col span={wrapperCol.span}>
						<Input value={fieldData.validate.imgCut.width || ''} onChange={this.widthChg}/>
					</Col>
				</Row>
				<Row className="opt-row">
					<Col span={labelCol.span} className="label-col">
						<label>高度：</label>
					</Col>
					<Col span={wrapperCol.span}>
						<Input value={fieldData.validate.imgCut.height || ''} onChange={this.heightChg}/>
					</Col>
				</Row>
				<Row className="opt-row">
					<Col span={labelCol.span} className="label-col">
						<label>宽高比：</label>
					</Col>
					<Col span={wrapperCol.span}>
						<Input placeholder="例如：4/3" value={fieldData.validate.imgCut.ratio || ''} onChange={this.ratioChg}/>
					</Col>
				</Row>
				<hr className="cus-hr"/>
			</div>
		)
	}

}

export default EImage;