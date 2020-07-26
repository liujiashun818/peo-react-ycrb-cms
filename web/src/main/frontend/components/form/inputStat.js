import React from 'react';
import { Input } from 'antd';
import { classnames } from '../../utils'
import styles from './inputStat.less'

function calcLength(str) {
	return Math.floor(str.replace(/[^\x00-\xff]/g, 'aa').length / 2);
}

class InputStat extends React.Component {
	constructor(props) {
		super(props);
		const { type='text', value='', rows=4 } = this.props; //参数解构
		const count = calcLength(value);
		this.state = {
			type,
			value,
			rows,
			count
		};
	}


	handleChange(e) {
		const value = e.target.value;
		const count = calcLength(value);
		this.setState({
			value,
			count
		});
		this.props.onChange && this.props.onChange(value);
	}

	componentWillReceiveProps(nextProps) {
		const value = nextProps.value || '';
		this.setState({
			value: value,
			count: calcLength(value)
		});
	}

	render() {
		const { type, value, rows, count } = this.state;
		const classNames = classnames(styles.inputStatWrap, this.props.wrapClassName);
		return (
			<div className={classNames}>
				<Input 
					size={this.props.size || 'large'}
					type={type}
					rows={rows}
					value={value}
					onChange={this.handleChange.bind(this)}
				/>
				<div className={styles.tip}>已输入<em>{count}</em>个字</div>
			</div>
		)
	}
}

export default InputStat;

