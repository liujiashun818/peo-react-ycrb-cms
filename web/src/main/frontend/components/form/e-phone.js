import React from 'react';
import EBase from './e-base';

class EPhone extends EBase {
	constructor(props) {
		super(props);
	}

	getFormatMsg = (msg) => {
		return msg || '请输入正确的电话号码';
	}

	getFormatActiveLabel = () => {
		return 'Phone';
	}
}

export default EPhone;