import React from 'react'
import PropTypes from 'prop-types';
import moment from 'moment'
import {Row, Col, Icon, DatePicker, Input, Radio, Button, Badge} from 'antd';
import { classnames } from '../../utils'
import styles from './surveyModule.less';

let uuid = 0;

const dateFormat = 'YYYY-MM-DD HH:mm:ss';

class SurveyModule extends React.Component {
	
	constructor(props) {
		super(props);
		const {value = {}} = this.props;
		const {surveys = []} = value;
		if(surveys.length === 0) {
			surveys.push({
				title: '',
				date: null,
				type: '1',
				isShowResult: true,
				options: [{
					title: ''
				}]
			});
		}
		surveys.map((survey, i) => {
			survey.key = i;
			survey.options.map((option, j) => {
				option.key = j
			})
		})
		this.state = {
			surveys
		}
	}

	componentWillReceiveProps(nextProps) {
		const surveys = nextProps.value.surveys;
		if(surveys && surveys.length > 0) {
			surveys.map((survey, i) => {
				survey.key = i;
				survey.options.map((option, j) => {
					option.key = j;
				})
			})
			this.setState({surveys});
		}
	}

	triggerChg(value) {
		this.props.onChange && this.props.onChange(value);
	}

	surveyTtChg(surveyKey, title) {
		const {surveys} = this.state;
		surveys.map(survey => {
			if(survey.key == surveyKey) {
				survey.title = title;
			}
		});
		this.setState({surveys});
		this.triggerChg({surveys});
	}

	multiSelChg(surveyKey, value) {
		const {surveys} = this.state;
		surveys.map(survey => {
			if(survey.key == surveyKey) {
				survey.type = value;
			}
		});
		this.setState({surveys});
		this.triggerChg({surveys});
	}

	showResChg(surveyKey, value) {
		const {surveys} = this.state;
		surveys.map(survey => {
			if(survey.key == surveyKey) {
				survey.isShowResult = value;
			}
		});
		this.setState({surveys});
		this.triggerChg({surveys});
	}

	dateChg(surveyKey, dateString) {
		const {surveys} = this.state;
		surveys.map(survey => {
			if(survey.key == surveyKey) {
				survey.date = dateString;
			}
		});
		this.setState({surveys});
		this.triggerChg({surveys});
	}

	optionTtChg(surveyKey, optionKey, title) {
		const {surveys} = this.state;
		surveys.map(survey => {
			if(survey.key == surveyKey) {
				survey.options.map(option => {
					if(option.key == optionKey) {
						option.title = title;
					}
				})
			}
		});
		this.setState({surveys});
		this.triggerChg({surveys});
	}

	addOption(surveyKey) {
		const {surveys} = this.state;
		surveys.map(survey => {
			if(survey.key == surveyKey) {
				survey.options.map((option, index) => {
					option.key = index;
				})
				survey.options.push({
					key: survey.options.length,
					title: ''
				});
			}
		});
		this.setState({surveys});
		this.triggerChg({surveys});
	}

	delOption(surveyKey, optionKey) {
		const {surveys} = this.state;
		surveys.map(survey => {
			if(survey.key == surveyKey) {
				survey.options = survey.options.filter(option => option.key !== optionKey)
			}
		});
		this.setState({surveys});
		this.triggerChg({surveys});
	}

	getOptions(surveyKey, options = []) {
		const {layout} = this.props;
		const layoutWithOutLabel = {
			wrapperCol: {
				span: layout.wrapperCol.span,
				offset: layout.labelCol.span
			}
		};
		return (
			options.map((option, index) => {
				return (
					<Row key={option.key}>
						<Col span={layout.labelCol.span} className="label-col">
							<label>
								{index === 0 ? '调查选项：' : ''}
							</label>
						</Col>
						<Col span={layout.wrapperCol.span - 4}>
							<Input
								placeholder="调查选项"
								size="large"
								value={option.title}
								onChange={(e) => this.optionTtChg(surveyKey, option.key, e.target.value)}
							/>
						</Col>
						<Col span={4}>
							<Badge showZero={true} count={options.hits || 0} overflowCount={9999999999} />
							{
								options.length !== 1 ?
								<Icon
									className="del-opt-btn"
									type="minus-circle-o"
									onClick={() => this.delOption(surveyKey, option.key)}
								/> : null
							}
							{
								index === (options.length - 1) ?
								<Icon
									className="add-opt-btn"
									type="plus-circle-o"
									onClick={() => this.addOption(surveyKey)}
								/> : null
							}
						</Col>
					</Row>
				)
			})
		);
	}

	addSurvey() {
		const {surveys} = this.state;
		surveys.map((survey, index) => {
			survey.key = index;
		});
		surveys.push({
			key: surveys.length,
			title: '',
			date: null,
			type: '1',
			isShowResult: true,
			options: [{
				key: 0,
				title: ''
			}]
		});
		this.setState({surveys});
		this.triggerChg({surveys});
	}

	delSurvey(key) {
		let {surveys} = this.state;
		surveys = surveys.filter(survey => survey.key != key);
		this.setState({surveys});
		this.triggerChg({surveys});
	}

	render() {
		const {layout} = this.props;
		const {surveys} = this.state;
		const classNames = classnames(styles.surveyModule, this.props.wrapClassName);
		return (
			<div className={classNames}>
				{
					surveys.map((survey, index) => {
						return (
							<div className="survey-item" key={survey.key}>
								<Row>
									<Col span={layout.labelCol.span} className="label-col">
										<label>调查标题：</label>
									</Col>
									<Col span={layout.wrapperCol.span}>
										<Input placeholder="调查标题" size="large" value={survey.title} onChange={(e) => this.surveyTtChg(survey.key, e.target.value)}/>
									</Col>
								</Row>
								{this.getOptions(survey.key, survey.options)}
								<Row className="multi-choice-row">
									<Col span={layout.labelCol.span} className="label-col"><label>是否可以多选：</label></Col>
									<Col span={layout.wrapperCol.span} className="radio-col">
										<Radio.Group value={survey.type+''} onChange={(e) => this.multiSelChg(survey.key, e.target.value)}>
											<Radio value="2">多选</Radio>
											<Radio value="1">单选</Radio>
										</Radio.Group>
									</Col>
								</Row>
								<Row className="show-result-row">
									<Col span={layout.labelCol.span} className="label-col"><label>是否显示结果：</label></Col>
									<Col span={layout.wrapperCol.span} className="radio-col">
										<Radio.Group value={survey.isShowResult} onChange={(e) => this.showResChg(survey.key, e.target.value)}>
											<Radio value={true}>是的</Radio>
											<Radio value={false}>不是</Radio>
										</Radio.Group>
									</Col>
								</Row>
								<Row>
									<Col span={layout.labelCol.span} className="label-col"><label>结束时间：</label></Col>
									<Col span={layout.wrapperCol.span}>
										<DatePicker 
											showTime 
											format={dateFormat} 
											value={survey.date ? moment(survey.date, dateFormat) : null}
											onChange={(dateMoment, dateString) => this.dateChg(survey.key, dateString)}
										/>
									</Col>
								</Row>
								{
									surveys.length > 1 ?
									<div className="del-survey-btn-wrap">
										<a className="del-survey-btn" onClick={() => this.delSurvey(survey.key)}>删除</a>
									</div> : null
								}
							</div>
						)
					})
				}
				<Row>
					<Col span={layout.wrapperCol.span} offset={layout.labelCol.span}>
						<Button type="dashed" onClick={this.addSurvey.bind(this)}>
							<Icon type="plus" />添加调查
						</Button>
					</Col>
				</Row>
			</div>
		);
	}
}

export default SurveyModule;

