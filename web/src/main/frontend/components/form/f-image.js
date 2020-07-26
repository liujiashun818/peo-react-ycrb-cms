import React from 'react';
import {Input, Button, Icon} from 'antd';
import ImgUploader from './imgUploader';

class FImage extends React.Component{
	constructor(props){
		super(props);
	}

	getUploadTpl() {
		let {validate, onChange, value } = this.props;
		validate = JSON.parse(validate);
		if(typeof value === 'string') {
			value = JSON.parse(value);
		}
		return (
			<ImgUploader tip="上传" {...validate.imgCut} value={value} onChange={onChange}/>
		);
	}

	render(){
		const {name} = this.props;
		return (
			<div>
				<label>{name}：</label>
				{this.getUploadTpl()}
			</div>	
		)
	}
}

export default FImage;