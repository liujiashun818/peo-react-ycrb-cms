import React from 'react'
import { Table, Input, DatePicker, Select, Icon } from 'antd' 
import moment from 'moment'
import styles from './editableCell.less'

class EditableCell extends React.Component {
	state = {
		type: this.props.type || 'input',
		value: this.props.value,
		dateFormat: this.props.dateFormat || 'YYYY-MM-DD HH:mm:ss',
		options: this.props.options || [],
		editable: false
	}

	inputChange = (e) => {
		const value = e.target.value;
		this.setState({ value });
		this.props.onChange && this.props.onChange(value)
	}

	dateChange = (moment, dateString) => {
		this.setState({
			value: dateString,
			editable: false
		});
		this.props.onChange && this.props.onChange(dateString)
	}

	dateOpenChange = (status) => {
		if(status === false) {
			this.setState({
				editable: false
			})
			this.props.onChange && this.props.onChange(this.state.value)
		}
	}

	selectChange = (value) => {
		this.setState({
			value,
			editable: false
		})
		this.props.onChange && this.props.onChange(value)
	}

	check = () => {
		this.setState({ editable: false });
		this.props.onChange && this.props.onChange(this.state.value)
	}

	edit = () => {
		this.setState({ editable: true });
	}

	componentWillReceiveProps(nextProps) {
		this.setState({
			...nextProps
		})
	}

	getEditTpl() {
		const { type, value, dateFormat, options } = this.state;
		let tpl = null;
		if(type === 'date') {
			return <DatePicker 
						value={value ? moment(this.getDateString(value), dateFormat) : null} 
						format={dateFormat}
						onOpenChange={this.dateOpenChange}
						onChange={this.dateChange}
					/>
		}
		else if(type === 'select') {
			return <Select
						value={value ? value : null}
						onSelect={this.selectChange}
				   >
						{
							options.map((opt, index) => {
								return <Select.Option key={index} value={opt.value}>{opt.label}</Select.Option>
							})
						}
				   </Select>
		}
		else {
			return <Input value={value} onChange={this.inputChange} onPressEnter={this.check}/>
		}
	}

	getDateString(value) {
		if(typeof value === 'number') {
			if((value + '').length === 10) {
				value += '000'
			}
			value = new Date(value).format('yyyy-MM-dd hh:mm:ss')
		}
		return value;
	}

	getText() {
		const { type, value, options } = this.state
		if(type === 'select') {
			for(let i = 0, len = options.length; i < len; i++) {
				if(options[i].value == value) {
					return options[i].label
				}
			}
		}
		else if(type === 'date') {
			return this.getDateString(value)
		}
		else {
			return value || '';
		}
	}

	render() {
		const { value, editable } = this.state;
		return (
			<div className={styles.cell}>
				{
					editable ?
					<div className="input-wrapper">
						{this.getEditTpl()}	
						<Icon 
							type="check"
							className="icon-check"
							onClick={this.check}
						/>
					</div>
					:
					<div className="text-wrapper" onClick={this.edit}>
						<span className="text">{this.getText()}</span>
						<Icon
							type="edit"
							className="icon-edit"
						/>
					</div>
				}
			</div>
		);
	}
}

export default EditableCell