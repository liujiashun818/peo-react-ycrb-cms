import React from 'react';
import EBase from './e-base';

class ENumeric extends EBase {
	constructor(props) {
		super(props);
	}

	getFormatMsg = (msg) => {
		return msg || '请输入数值数据';
	}

	getFormatActiveLabel = () => {
		return '数字';
	}
}

export default ENumeric;