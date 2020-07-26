import React from 'react';
import EBase from './e-base';

class EEmail extends EBase {
	constructor(props) {
		super(props);
	}

	getFormatMsg = (msg) => {
		return msg || '请输入正确的Email地址';
	}

	getFormatActiveLabel = () => {
		return 'Email';
	}
}

export default EEmail;