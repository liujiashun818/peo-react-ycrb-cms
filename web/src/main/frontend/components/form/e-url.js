import React from 'react';
import EBase from './e-base';

class EUrl extends EBase {
	constructor(props) {
		super(props);
	}

	getFormatMsg = (msg) => {
		return msg || '请输入正确的URL地址';
	}

	getFormatActiveLabel = () => {
		return 'URL';
	}
}

export default EUrl;