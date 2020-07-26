import React from 'react';
import {Input, Button, Icon} from 'antd';
import FBase from './f-base';
import './f-input.less';

class FInput extends FBase {
	constructor(props) {
		super(props);
		this.state = {
			value: this.parseVal(props)
		};
	}

	inputChg = (e, simpleOrMore, key) => {
		const val = e.target.value;
		let value = this.state.value;
		if(simpleOrMore == 2) {
			value.forEach(item => {
				if(item.key === key) {
					item.value = val;
				}
			});
		}
		else {
			value = val;
		}
		this.state.value = value;
		this.triggerChg(value);
	}

	getInputTpl = (value, simpleOrMore, placeholder, type='text') => {
		if(simpleOrMore == 2) {
			return value.map(item => {
				return (
					<div key={item.key} className="instance-item">
						<div className="g-mn">
							<div className="g-mnc">
								<Input
									type={type}
									size="large"
									placeholder={placeholder}
									value={item.value}
									onChange={(e) => this.inputChg(e, simpleOrMore, item.key)}
									autosize={{minRows: 2, maxRows: 6}}
								/>
							</div>
						</div>
						{
							value.length > 1
							? <div className="g-sd">
								<Icon type="minus-circle-o" className="del-btn" onClick={() => this.delInstance(item.key)}/>
							  </div>
							: null
						}
						
					</div>
				);
			});
		}
		return <Input type={type} value={value} onChange={(e) => this.inputChg(e, simpleOrMore)} autosize={{minRows: 2, maxRows: 6}}/>;
	}

	getAddBtnTpl = (simpleOrMore) => {
		if(simpleOrMore == 2) {
			return <Button type="dashed" className="add-btn" onClick={this.addInstance}><Icon type="plus" />添加</Button>;
		}
		return null;
	}

	render() {
		const {name, placeholder, simpleOrMore} = this.props;
		const {value} = this.state;
		return (
			<div className="f-base f-input">
				<label>{name}：</label>
				{this.getInputTpl(value, simpleOrMore, placeholder)}
				{this.getAddBtnTpl(simpleOrMore)}
			</div>
		);
	}
}

export default FInput;