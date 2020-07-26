import React from 'react'
import {
	message,
	Form,
	DatePicker,
	Select,
	TreeSelect,
	Checkbox,
	Input,
	Button,
	Radio,
	Row,
	Col,
	Collapse,
	Switch,
	Popconfirm
} from 'antd';
import moment from 'moment';
import {Jt} from '../../utils';
import {delFlags, liveStatus,delFlags2} from '../../constants';
import ImgUploader from '../form/imgUploader'
import UserEidtModal from './userEditModal'
import styles from './liveEdit.less'
import VisibleWrap from '../ui/visibleWrap'

const dateFormat = 'YYYY-MM-DD HH:mm:ss'

const layout_l = {
	labelCol: {
		span: 3
	},
	wrapperCol: {
		span: 21
	}
};

const layout_r = {
	labelCol: {
		span: 6
	},
	wrapperCol: {
		span: 18
	}
}

class LiveEdit extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			catgsModelId: [],
 			categoryId: null,
			umv: false,
			curUser: {}
		}
	}
	componentWillMount () {
		const {catgs} = this.props
		this.loopGetModelId(catgs)
	}
	
	componentDidMount () {
		setTimeout(() => {
			const { live, form: { setFieldsValue } } = this.props
			const { catgsModelId, categoryId } = this.state
			if (catgsModelId.indexOf(categoryId || live.categoryId) > -1) {
				setFieldsValue({block: '2'});
			}
		}, 100)
	}

	loopGetModelId(arr) {
		arr.map((item) => {
			if (item.modelId === 6) {
				let { catgsModelId } = this.state
				catgsModelId.push(item.id+'')
				this.setState({ catgsModelId })
			}
		   
			if (item.children && item.children.length) {
				this.loopGetModelId(item.children)
			}
		});
	}
	catgChg (categoryId) {
		// categoryId = Number(categoryId)
		this.setState({categoryId})
		const { live, form: { setFieldsValue } } = this.props
		const { catgsModelId } = this.state
		if (catgsModelId.indexOf(categoryId || live.categoryId) > -1) {
			setFieldsValue({block: '2'});
		}
	}
	editUser(e, role, user) {
		e.stopPropagation();
		if(!user) {
			user = {role};
		}
		this.setState({
			umv: true,
			curUser: user
		});
	}

	hideUserModal() {
		this.setState({
			umv: false,
			curUser: {}
		});
	}

	getUserSelect(role, users=[]) {
		const placeholder = role === 'host' ? '请选择主持人' : '请选择嘉宾'
		const options = [];
		users.forEach(user => {
			options.push(
				<Select.Option key={user.id} value={user.id + ''} label={user.name}>
					<div>
						{user.name}
						<VisibleWrap permis="view">
							<a className="edit-btn" onClick={(e) => this.editUser(e, role, user)}>编辑</a>
						</VisibleWrap>
					</div>
				</Select.Option>
			)
		});

		options.push(
			<Select.Option key="add" value="add" label="添加人员" className="add-option">
				<div className="add-btn-wrap" onClick={(e) => e.stopPropagation()} >
					<Button type="dashed" onClick={(e) => this.editUser(e, role)}>添加人员</Button>
				</div>
			</Select.Option>
		);
		return (
			<Select
				showSearch
				optionFilterProp="label"
				optionLabelProp="label"
				mode="multiple"
				labelInValue={true}
				placeholder={placeholder}
				getPopupContainer={() => document.getElementById('liveEditForm')}
			>
			{options}
			</Select>
		);
	}

	getUserInitVal(users=[]) {
		const res = [];
		users.forEach(user => {
			res.push({
				key: user.id + '',
				label: user.name
			});
		});
		return res;
	}

	getDelBtn() {
		// const {live, deleteLive} = this.props;
		// if(live.id && live.delFlag == 1) {
		// 	return (
		// 		<VisibleWrap permis="edit:delete">
		// 			<Popconfirm title="确定删除吗？" onConfirm={() => deleteLive(live.id)}>
		// 				<Button type="danger" className="del-btn">删除</Button>
		// 			</Popconfirm>
		// 		</VisibleWrap>
		// 	);
		// }
		return null;
	}

	getFocusImg({block, viewType, imageUrl}, getFieldDecorator) {
		const label = block == 1 ? '焦点图' : '封面图';
		if(block == 1 || (block == 2 && viewType === 'banner')) {
			return (
				<Form.Item label={label} {...layout_l}>
				{
					getFieldDecorator('imageUrl', {
						initialValue: {fileList: imageUrl ? [{name: imageUrl, url: imageUrl, uid: -1}] : []}
					})(
						<ImgUploader single={true} />
					)
				}
				</Form.Item>
			);
		}
		return null;
	}

	getTplByLiveType({liveType, video, playback, image}, getFieldDecorator) {
		if(liveType == 1) {
			return (
				<div>
					<Form.Item label="直播地址" {...layout_l}>
					{
						getFieldDecorator('video', {
							initialValue: video
						})(<Input placeholder="请输入直播地址"/>)
					}
					</Form.Item>
					<Form.Item label="回放地址" {...layout_l}>
					{
						getFieldDecorator('playback', {
							initialValue: playback
						})(<Input placeholder="请输入回放地址"/>)
					}
					</Form.Item>
					<Form.Item label="视频封面" {...layout_l}>
					{
						getFieldDecorator('image1', {
							initialValue: {fileList: image ? [{name: image, url: image, uid: -2}] : []}
						})(<ImgUploader single={true} />)
					}
					</Form.Item>
				</div>
			);
		}
		else if(liveType == 2) {
			return (
				<Form.Item label="头图图片" {...layout_l}>
				{
					getFieldDecorator('image2', {
						initialValue: {fileList: image ? [{name: image, url: image, uid: -3}] : []}
					})(<ImgUploader single={true}/>)
				}
				</Form.Item>
			);
		}
		else {
			return null;
		}
	}

	getMore() {
		const {live, form: {getFieldDecorator}} = this.props;
		const tpl1 = this.getFocusImg(live, getFieldDecorator);
		const tpl2 = this.getTplByLiveType(live, getFieldDecorator);
		if(!tpl1 && !tpl2) {
			return null;
		}
		return (
			<Collapse bordered={false} defaultActiveKey={['3']}>
				<Collapse.Panel key="3" header="更多">
				{tpl1}
				{tpl2}
				</Collapse.Panel>
			</Collapse>
		);
	}

	blockChg(e) {
		this.props.updateLiveAttrs({
			block: e.target.value
		});
	}

	viewTypeChg(e) {
		const {updateLiveAttrs, form: {setFieldsValue}} = this.props;
		const viewType = e.target.value;
		const obj = {viewType};
		if(viewType === 'banner') {
			obj.liveType = '1';
			setFieldsValue({liveType: obj.liveType});
		}
		updateLiveAttrs(obj);
	}

	liveTypeChg(value) {
		this.props.updateLiveAttrs({
			liveType: value
		});
	}

	liveTypeIsDisabled(viewType, liveType) {
		if(viewType === 'banner' && (liveType == 2 || liveType == 3)) {
			return true;
		}
		return false;
	}

	validateValues(values) {
		const {catgs} = this.props;
		const {categoryId, title, liveTime, imageUrl, block, viewType, liveType, video, image} = values;
		if(!categoryId) {
			message.error('请选择归属栏目');
			return false;
		}
		if(!Jt.tree.isLeaf(catgs, categoryId)) {
			message.error('请选择子级栏目');
			return false;
		}

		if(!title) {
			message.error('请输入正文标题');
			return false;
		}

		if(!liveTime) {
			message.error('请选择开始时间');
			return false;
		}

		if(!imageUrl) {
			if(block == 1) {
			    if(values.viewType!='live'){
                    message.error('请选择焦点图');
                    return false;
			    }

			}
			else if(block == 2 && viewType === 'banner') {
				message.error('请选择封面图');
				return false;
			}
		}

		if(liveType == 1 && !video) {
			message.error('请输入直播地址');
			return false;
		}
		else if(liveType == 2 && !image) {
			message.error('请选择头图图片');
			return false;
		}
		return true;
	}

	onSave() {
		const {live, saveLive, form:{getFieldsValue}} = this.props;
		const values = {...getFieldsValue()};
		values.liveTime = values.liveTime ? values.liveTime.format(dateFormat) : '';
		values.publishDate = values.publishDate ? values.publishDate.format(dateFormat) : '';

		const {hosts, guests} = values;
		if(!Jt.array.isEmpty(hosts)) {
			const arr1 = [], arr2 = [];
			hosts.forEach(host => {
				arr1.push(host.label);
				arr2.push(host.key);
			});
			values.host = arr1.join(',');
			values.hostIds = arr2.join(',');
		}
		else {
			values.host = '';
			values.hostIds = '';
		}
		delete values.hosts;

		if(!Jt.array.isEmpty(guests)) {
			const arr1 = [], arr2 = [];
			guests.forEach(guest => {
				arr1.push(guest.label);
				arr2.push(guest.key);
			});
			values.guest = arr1.join(',');
			values.guestIds = arr2.join(',');
		}
		else {
			values.guest = '';
			values.guestIds = '';
		}
		delete values.guests;

		if(values.image1 && values.image1.fileList && values.image1.fileList.length > 0) {
			values.image = values.image1.fileList[0].url;
		}
		else if(values.image2 && values.image2.fileList && values.image2.fileList.length > 0) {
			values.image = values.image2.fileList[0].url;
		}
		else {
			values.image = '';
		}
		delete values.image1;
		delete values.image2;

		if(values.imageUrl && values.imageUrl.fileList && values.imageUrl.fileList.length > 0) {
			values.imageUrl = values.imageUrl.fileList[0].url;
		}
		else {
			values.imageUrl = '';
		}

		values.id = live.id;

		if(this.validateValues(values)) {
			saveLive(values);
		}
	}

	render() {
		const { catgsModelId, categoryId } = this.state;
		const {catgs, live, hosts, guests, saveUser, deleteUser, goBack, form:{getFieldDecorator}} = this.props;
		const {umv, curUser} = this.state;
		const modalProps = {
			visible: umv,
			user: curUser,
			onCancel: this.hideUserModal.bind(this),
			saveUser,
			deleteUser
		};

		return (
			<div>
				<Form className={styles.form} id="liveEditForm">
					<Row>
						<Col span={16}>
							<Form.Item>
							{
								getFieldDecorator('title', {
									initialValue: live.title
								})(
									<Input placeholder="请输入标题（为了APP端更好的显示效果，请结合具体文章将标题控制在8-22个字符内）"/>
								)
							}
							</Form.Item>
							<Form.Item>
							{
								getFieldDecorator('description', {
									initialValue: live.description
								})(
									<Input type="textarea" rows={5} placeholder="请输入导语"/>
								)
							}
							</Form.Item>
							<Form.Item>
							{
								getFieldDecorator('hosts', {
									initialValue: this.getUserInitVal(live.hosts)
								})(this.getUserSelect('host', hosts))
							}
							</Form.Item>
							<Form.Item>
							{
								getFieldDecorator('guests', {
									initialValue: this.getUserInitVal(live.guests)
								})(this.getUserSelect('guest', guests))
							}
							</Form.Item>
							{this.getMore()}
						</Col>
						<Col span={7} offset={1} className="col-r">
							<Collapse bordered={false} defaultActiveKey={['1', '2']}>
								<Collapse.Panel key="1" header="保存&发布">
									<Form.Item label="直播状态" {...layout_r}>
									{
										getFieldDecorator('status', {
											initialValue: live.status + ''
										})(
											<Select>
											{
												liveStatus.map((item, index) => {
													return <Select.Option key={index} value={item.value}>{item.label}</Select.Option>
												})
											}
											</Select>
										)
									}
									</Form.Item>
									<Form.Item label="发布状态" {...layout_r}>
									{
										getFieldDecorator('delFlag', {
											initialValue: live.delFlag + ''
										})(
											<Select>
											{
												delFlags2.map((item, index) => {
													return <Select.Option key={index} value={item.value}>{item.label}</Select.Option>
												})
											}
											</Select>
										)
									}
									</Form.Item>
									<Form.Item label="开始时间" {...layout_r}>
									{
										getFieldDecorator('liveTime', {
											initialValue: live.liveTime ? moment(live.liveTime, dateFormat) : null
										})(<DatePicker showTime format={dateFormat} />)
									}
									</Form.Item>
									<Form.Item label="发布时间" {...layout_r}>
									{
										getFieldDecorator('publishDate', {
											initialValue: moment(live.publishDate, dateFormat)
										})(
											<DatePicker showTime format={dateFormat}/>
										)
									}
									</Form.Item>
									<Form.Item label="标签" {...layout_r}>
									{
										getFieldDecorator('tag', {
											initialValue: live.tag || '直播'
										})(
											<Radio.Group>
												<Radio value="直播">直播</Radio>
												<Radio value="访谈">访谈</Radio>
											</Radio.Group>
										)
									}
									</Form.Item>
									<Row className="btn-row">
										<Col offset={layout_r.labelCol.span} span={layout_r.wrapperCol.span}>
											{this.getDelBtn()}
											<VisibleWrap permis="edit:modify">
												<Button className="save-btn" type="primary" onClick={this.onSave.bind(this)}>保存</Button>
											</VisibleWrap>
											<Button className="back-btn" onClick={goBack}>返回</Button>
										</Col>
									</Row>
								</Collapse.Panel>
								<Collapse.Panel key="2" header="栏目&样式">
									<Form.Item label="所属栏目" {...layout_r}>
									{
										getFieldDecorator('categoryId', {
											initialValue: live.categoryId + ''
										})(
											<TreeSelect
												allowClear
												showSearch
												treeNodeFilterProp="label"
												treeData={catgs}
												onChange={this.catgChg.bind(this)}
											/>
										)
									}
									</Form.Item>
									{/*<Form.Item label="显示标题" {...layout_r}>*/}
									{/*{*/}
										{/*getFieldDecorator('showTitle', {*/}
											{/*valuePropName: 'checked',*/}
											{/*initialValue: live.showTitle*/}
										{/*})(*/}
											{/*<Switch checkedChildren="是" unCheckedChildren="否"/>*/}
										{/*)*/}
									{/*}*/}
									{/*</Form.Item>*/}
									<Form.Item label="展示位置" {...layout_r}>
									{
										getFieldDecorator('block', {
											initialValue: live.block + ''
										})(
											<Radio.Group onChange={this.blockChg.bind(this)}>
												{/* <Radio value="1">焦点图</Radio>
												<Radio value="2">列表</Radio>
												<Radio value="3">待选区</Radio> */}
												{
 													 catgsModelId.indexOf(categoryId || live.categoryId) > -1?
 													 (<div>
 														<Radio value="2">列表</Radio>
 														<Radio value="3">待选区</Radio>
 													 </div>)
 													 :
 													 (<div>
 														<Radio value="1">焦点图</Radio>
 													    <Radio value="2">列表</Radio>
 													    <Radio value="3">待选区</Radio>
 													</div>)
 												}
											</Radio.Group>
										)
									}
									</Form.Item>
									<Form.Item label="展示类型" {...layout_r}>
									{
										getFieldDecorator('viewType', {
											initialValue: live.viewType
										})(
											<Radio.Group onChange={this.viewTypeChg.bind(this)}>
												<Radio value="live">常规直播</Radio>
												<Radio value="banner">视频直播</Radio>
											</Radio.Group>
										)
									}
									</Form.Item>
									<Form.Item label="直播类型" {...layout_r}>
									{
										getFieldDecorator('liveType', {
											initialValue: live.liveType + ''
										})(
											<Select onChange={this.liveTypeChg.bind(this)}>
												<Select.Option value="1" disabled={this.liveTypeIsDisabled(live.viewType, 1)}>视频</Select.Option>
												<Select.Option value="2" disabled={this.liveTypeIsDisabled(live.viewType, 2)}>图片</Select.Option>
												<Select.Option value="3" disabled={this.liveTypeIsDisabled(live.viewType, 3)}>无</Select.Option>
											</Select>
										)
									}
									</Form.Item>
								</Collapse.Panel>
							</Collapse>
						</Col>
					</Row>
				</Form>
				<UserEidtModal {...modalProps}/>
			</div>
		);
	}
}

export default Form.create()(LiveEdit);
