import React from 'react';
import './f-base.less';

class FBase extends React.Component {
	constructor(props) {
		super(props);
	}

	triggerChg(value) {
		const {simpleOrMore, onChange} = this.props;
		if(onChange) {
			if(simpleOrMore == 2) {
				const res = [];
				value.forEach(item => {
					res.push(item.value);
				});
				value = JSON.stringify(res);
			}
			onChange(value);
		}
	}

	parseVal = ({simpleOrMore, value}) => {
		if(simpleOrMore == 2) {
			try {
				const items = JSON.parse(value);
				const res = [];
				items.forEach((item, index) => {
					res.push({key: -(index + 1), value: item});
				});
				return res;
			}catch(e) {
				return [{key: -1, value}];
			}
		}
		return value;
	}

	parseValidate = ({validate}) => {
		if(validate) {
			return JSON.parse(validate);
		}
		else {
			return {};
		}
	}

	parseLogic = ({logic}) => {
		if(logic) {
			return JSON.parse(logic);
		}
		else {
			return {};
		}
	}

	addInstance = () => {
		const value = this.state.value;
		value.map((item, index) => item.key = -(index + 1));
		value.push({
			key: -(value.length + 1),
			value: ''
		});
		this.state.value = value;
		this.triggerChg(value);
	}

	delInstance = (key) => {
		const value = this.state.value.filter(item => item.key !== key);
		this.state.value = value;
		this.triggerChg(value);
	}
}

export default FBase;
