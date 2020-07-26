import React from 'react'
import PropTypes from 'prop-types';
import ReactDOM from 'react-dom';
import {Form, DatePicker, Select, Collapse, Row, Col, Input, Dropdown, Button, Menu, Icon, Card} from 'antd';
import moment from 'moment';
import ImgUploader from '../form/imgUploader';
import { urlPath } from '../../constants';
import styles from './liveRoom.less';
import MediaUploader from '../form/mediaUploader';
import {Jt} from '../../utils';
const viewTypes = {
	live: '常规直播',
	banner: '视频直播',
};

const tags = {
	live: '直播',
	interview: '访谈'
};

const liveTypes = {
	1: '视频',
	2: '图片',
	3: '无'
};

const liveStatus = {
	1: '预告',
	2: '直播中',
	3: '已结束'
};

const userTypes = {
	host: '主持人',
	guest: '嘉宾',
	net: '网友'
};

const WebSocket = window.WebSocket || window.MozWebSocket;

class LiveRoom extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			ready: false,
			replyItem: null,
			topTalk: null,
			talks: [],
			comments: [],
			videoJson:{},
		};
	}
	componentDidMount(){
	}

	componentWillReceiveProps(nextProps) {
		let {ready} = this.state;
		let {live,query} = nextProps;
		if(ready === false && live.id) {
			this.initWs(query.id);
		}
	}

	wsOnMessage(e) {
		let data = JSON.parse(e.data);
		
		let method = data.method;
		let {form:{setFieldsValue}} = this.props;
		if(method === 'poll') {
			return;
		}
		 if(method === 'init') {
			let {talks, comments} = data;
			let topTalk = null;
			for(let i = 0, len = talks.length; i < len; i++) {
				if(talks[i].top) {
					topTalk = talks[i];
					talks.splice(i, 1);
					break;
				}
			}
			this.setState({
				topTalk,
				talks,
				comments
			});
		}
		else if(method === 'speak') {
			let talk = data.talk;
			let talks = this.state.talks;
			this.setState({
				talks: [talk, ...talks]
			});
			setFieldsValue({
				content: '',
				image: [],
				media:{
					coverImg:"",
					mediaData:{}
				}
			});
		}
		else if(method === 'reply') {
			let talk = data.talk;
			let talks = this.state.talks;
			this.setState({
				replyItem: null,
				talks: [talk, ...talks]
			});
			setFieldsValue({
				content: '',
				image: [],
				media:{
					coverImg:"",
					mediaData:{}
				}
			});
		}
		else if(method === 'statusTalk') {
			let talk = data.talk;
			let talks = this.state.talks;
			for(let i = 0, len = talks.length; i < len; i++) {
				if(talks[i].id == talk.id) {
					talks.splice(i, 1, talk);
					break;
				}
			}
			this.setState({
				talks
			});
		}
		else if(method === 'top') {
			let talk = data.talk;
			let {topTalk, talks} = this.state;
			for(let i = 0, len = talks.length; i < len; i++) {
				if(talks[i].id == talk.id) {
					talks.splice(i, 1);
					break;
				}
			}
			
			if(topTalk) {
				topTalk.top = false;
				if(talks.length === 0) {
					talks.push(topTalk);
				}
				else {
					for(let i = 0, len = talks.length; i < len; i++) {
						if(topTalk.createAt > talks[i].createAt) {
							talks.splice(i, 0, topTalk);
							break;
						}
						else if(i == len - 1) {
							talks.push(topTalk);
						}
					}
				}
			}
			this.setState({
				topTalk: talk,
				talks
			});
		}
		else if(method === 'down') {
			let talk = data.talk;
			let talks = this.state.talks;
			if(talks.length === 0) {
				talks.push(talk);
			}
			else {
				for(let i = 0, len = talks.length; i < len; i++) {
					if(talk.createAt > talks[i].createAt) {
						talks.splice(i, 0, talk);
						break;
					}
					else if(i == len - 1) {
						talks.push(talk);
					}
				}
			}
			this.setState({
				topTalk: null,
				talks
			});
		}
		else if(method === 'statusComment') {
			let comment = data.talk;
			let comments = this.state.comments;
			for(let i = 0, len = comments.length; i < len; i++) {
				if(comments[i].id == comment.id) {
					comments[i].delFlag = comment.delFlag;
					break;
				}
			}
			this.setState({
				comments
			});
		}
		else if(method === 'comment') {
			let comment = data.comment;
			this.setState({
				comments: [comment, ...this.state.comments]
			});
		}
	}

	wsOnOpen() {
		this.keepAliveWs();
		console.log("liveroom:websocket连接打开！");
	}

	wsOnClose() {
		this.cancelKeepAlive();
		console.log("liveroom:websocket连接关闭！");
	}

	wsOnError() {
		this.cancelKeepAlive();
		console.log("liveroom:websocket连接出错！");
	}

	keepAliveWs() {
		if(this.ws.readyState === WebSocket.OPEN) {
			this.ws.send('');
		}
		this.wsTimer = setTimeout(this.keepAliveWs.bind(this), 5000);
	}

	cancelKeepAlive() {
		if(this.wsTimer) {
			clearTimeout(this.wsTimer);
			this.wsTimer = null;
		}
	}

	initWs(roomId) {
		const url = urlPath.LIVE_TALK + roomId;
		//const url = "ws://tyccms.peopletech.cn/api/live/talk/" + roomId;
		 //ws://10.50.162.159:8090
		 console.log('ws-url',url)
		const ws = this.ws = new WebSocket(url);
		ws.onopen = () => this.wsOnOpen();
		ws.onmessage = (e) => this.wsOnMessage(e);
		ws.onclose = () => this.wsOnClose();
		ws.onerror = () => this.wsOnError();
		// this.state.ready = true;
		this.setState({"ready":true})
		
	}

	componentWillUnmount() {
		console.log("componentWillUnmount",this.ws)
		this.ws && this.ws.close();
	}

	onSpeak(e) {
		let {live, form:{validateFields, setFieldsValue}} = this.props;
		let {replyItem} = this.state;
		let props = e.item.props;
		validateFields((errors, values) => {
			if(errors) {
				return;
			}
			if(values.image && values.image.fileList && values.image.fileList.length > 0) {
				values.image = values.image.fileList[0].url;
			}
			else {
				values.image = '';
			}
			values.roomId = live.id;
			values.openId = e.key;
			values.userType = props['data-role'];
			values.userName = props['data-name'];
			values.userIcon = props['data-image'];
			values.area = props['data-area'];
			values.top = false;
			if(replyItem) {
				values.commentId = replyItem.id;
				values.method = 'reply';
			}
			else {
				values.method = 'speak';
			}
			
		
			if(!values.media.id){
				delete values.media
			}
			this.ws.send(JSON.stringify(values));
		})
	}

	onReply(item) {
		ReactDOM.findDOMNode(this.refs.speakPanel).scrollIntoView();
		this.setState({
			replyItem: item
		});
	}

	cancelReply() {
		this.setState({
			replyItem: null
		});
	}

	/**
	 * 上线|下线
	 */
	onOffLine(type, item) {
		let method = type === 'talk' ? 'statusTalk' : 'statusComment'
		this.ws.send(JSON.stringify({
				method,
				id: item.id
			}));
	}

	/**
	 * 置顶|取消置顶
	 */
	onOffTop(item) {
		if(item.top === true) {
			this.ws.send(JSON.stringify({
				method: 'down',
				id: item.id
			}));
		}
		else {
			this.ws.send(JSON.stringify({
				method: 'top',
				id: item.id
			}))
		}
	}

	getSpokesmanMune(users = []) {
		return (
			<Menu onClick={this.onSpeak.bind(this)}>
				{
					users.map(user => {
						return <Menu.Item 
									key={user.id} 
									data-name={user.name} 
									data-role={user.role} 
									data-image={user.image}
									data-area={user.area}
								>
									{user.name}
								</Menu.Item>
					})
				}
			</Menu>
		)
	}

	getReplyTpl(item) {
		if(!item) {
			return null;
		}
		return (
			<div className="reply-item">
				<div className="row row-t">
					<div className="col-l">
						<span className="item-icon" style={{backgroundImage: 'url(' + item.userIcon + ')'}}></span>
					</div>
					<div className="col-m">
						<span>【{userTypes[item.userType]?userTypes[item.userType]:'网友'}】：{item.userName}</span>
					</div>
				</div>
				<div className="row row-m">
					<p>{item.content}</p>
					{
						item.image
						? <p><img src={item.image}/></p>
						: null
					}
					{
									item.media
									? <p>{item.media.type=='video'?<video width='100%' src={item.media.url}></video>:<audio width='100%' controls src={item.media.url}></audio>}</p>
									: null
								}
				</div>
				<div className="row row-b">
					<span>时间：{new Date(item.createAt).format('yyyy-MM-dd HH:mm:ss')}</span>
				</div>
			</div>
		);
	}

	getTalkTpl() {
		let {topTalk, talks} = this.state;
		return (
			<div>
				{
					topTalk
					? <div className="comment-item on">
						<div className="main-item">
							<div className="row row-t">
								<div className="col-r">
									<Button onClick={() => this.onOffTop(topTalk)}>取消置顶</Button>
								</div>
								<div className="col-l">
									<span className="item-icon" style={{backgroundImage: 'url(' + topTalk.userIcon + ')'}}></span>
								</div>
								<div className="col-m">
									<span>【{userTypes[topTalk.userType]?userTypes[topTalk.userType]:'网友'}】：{topTalk.userName}</span>
								</div>
							</div>
							<div className="row row-m">
								<p>{topTalk.content}</p>
								{
									topTalk.image
									? <p><img src={topTalk.image}/></p>
									: null
								}{
									topTalk.media
									? <p>{topTalk.media.type=='video'?<video width='100%' src={topTalk.media.url}></video>:<audio width='100%' controls src={topTalk.media.url}></audio>}</p>
									: null 
								}
							</div>
							<div className="row row-b">
								<span>时间：{new Date(topTalk.createAt).format('yyyy-MM-dd HH:mm:ss')}</span>
							</div>
						</div>
						{this.getReplyTpl(topTalk.liveReply)}
					  </div>
					: null
				}
				
				<ul>
					{
						talks.map(talk => {
							return (
								<li key={talk.id} className={['comment-item', talk.delFlag == 0 ? 'on' : 'off'].join(' ')}>
									<div className="main-item">
										<div className="row row-t">
											<div className="col-r">
												<Button
													onClick={() => this.onOffLine('talk', talk)}
												>
													{
														talk.delFlag == 0
														? '下线'
														: '上线'
													}
												</Button>
												{
													talk.delFlag == 0
													? <Button onClick={() => this.onOffTop(talk)}>置顶</Button>
													: null
												}
												
											</div>
											<div className="col-l">
												<span className="item-icon" style={{backgroundImage: 'url(' + talk.userIcon + ')'}}></span>
											</div>
											<div className="col-m">
												<span>【{userTypes[talk.userType]?userTypes[talk.userType]:'网友'}】：{talk.userName}</span>
											</div>
										</div>
										<div className="row row-m">
											<p>{talk.content}</p>
											{
												talk.image
												? <p><img src={talk.image}/></p>
												: null
											}
											{
												talk.media
												? <p>{talk.media.type=='video'?<video width="100%" controls="controls" src={talk.media.url}></video>:<audio width="100%" controls src={talk.media.url}></audio>}</p>
												: null
											}
										</div>
										<div className="row row-b">
											<span>时间：{new Date(talk.createAt).format('yyyy-MM-dd HH:mm:ss')}</span>
										</div>
									</div>
									{this.getReplyTpl(talk.liveReply)}
								</li>
							);
						})
					}
				</ul>
			</div>
		);
	}

	getCommentTpl() {
		let comments = this.state.comments;
		return (
			<ul>
				{
					comments.map(item => {
					
						return (
							<li key={item.id} className={['comment-item', item.delFlag == 0 ? 'on' : 'off'].join(' ')}>
								<div className="main-item">
									<div className="row row-t">
										<div className="col-r">
											<Button
												onClick={() => this.onReply(item)}
											>
												回复
											</Button>
											<Button
												onClick={() => this.onOffLine('comment', item)}
											>
												{
													item.delFlag == 0
													? '下线'
													: '上线'
												}
											</Button>
										</div>
										<div className="col-l">
											<span className="item-icon" style={{backgroundImage: 'url(' + item.userIcon + ')'}}></span>
										</div>
										<div className="col-m">
											<span>【{userTypes[item.userType]?userTypes[item.userType]:'网友'}】：{item.userName}</span>
										</div>
									</div>
									<div className="row row-m">
										<p>{item.content}</p>
										{
											item.image
											? <p><img src={item.image}/></p>
											: null
										}
									</div>
									<div className="row row-b">
										<span>时间：{new Date(item.createAt).format('yyyy-MM-dd HH:mm:ss')}</span>
									</div>
								</div>
							</li>
						);
					})
				}
			</ul>
		);
	}
	toMedia(type) {
		//  const {curArt:{articleData={}}} = this.props;
		  const mediaJson = type === 'video' ? this.props.videoJson : this.props.audioJson;
		  if(Jt.array.isEmpty(mediaJson)) {
			  return {id: '', url: '', coverImg: '',title:'',times:0};
		  }
		  const media = mediaJson[0];
		  return {
			  id: media.id,
			  url: media.resources[0].url,
			  coverImg: media.image
		  };
	  }
	  mediaChange = (data)=>{
			const mediaJson =this.props.videoJson ? this.props.videoJson : this.props.audioJson;
		 
		  const {curArt, updateState} = this.props
		  let videoJson =  {
			  id: data.id,
			  image: data.coverImg,
			  resources:[{
				  url: data.url
			  }]
		  }
		  ;
		  videoJson = Object.assign({},this.state.videoJson,videoJson);
		  this.setState({"videoJson":videoJson})
		//   updateState({
		// 	videoJson
		//   });
	  }
	  getVideo() {
		  const {videoNum, form:{getFieldDecorator}} = this.props;
		  return (
			  <Collapse bordered={false} defaultActiveKey={['video']}>
				  <Collapse.Panel key="video" header="音/视频">
					  <Form.Item>
					  {
						  getFieldDecorator('media', {
							  initialValue: this.toMedia('video')
						  })(<MediaUploader hideTitle={true}  onChange={this.mediaChange} type="video" />)
					  }
					  </Form.Item>
				  </Collapse.Panel>
			  </Collapse>
		  );
	  }
	render() {
		let {live,query, form:{getFieldDecorator}} = this.props;
		let {ready, replyItem} = this.state;
		if(ready === false && live.id) {
			this.initWs(query.id);
		}
		return (
			<div className={styles.liveRoom}>
				<Collapse defaultActiveKey={['1', '2']}>
					<Collapse.Panel header="直播信息" key="1">
						<Row>
							<Col span={16}>
								<Row className="info-row">
									<Col span={2} className="label-col">
										<label>标题：</label>
									</Col>
									<Col span={20}>
										<p>{live.title}</p>
									</Col>
								</Row>
								<Row className="info-row">
									<Col span={2} className="label-col">
										<label>导语：</label>
									</Col>
									<Col span={20}>
										<p>{live.description}</p>
									</Col>
								</Row>
								<Row className="info-row">
									<Col span={2} className="label-col">
										<label>主持人：</label>
									</Col>
									<Col span={20}>
										<p>{live.host}</p>
									</Col>
								</Row>
								<Row className="info-row">
									<Col span={2} className="label-col">
										<label>嘉宾：</label>
									</Col>
									<Col span={20}>
										<p>{live.guest}</p>
									</Col>
								</Row>
							</Col>
							<Col span={8}>
								<Row className="info-row">
									<Col span={8} className="label-col">开始时间：</Col>
									<Col span={16}>{live.liveTime}</Col>
								</Row>
								<Row className="info-row">
									<Col span={8} className="label-col">展示类型：</Col>
									<Col span={16}>{viewTypes[live.viewType]}</Col>
								</Row>
								<Row className="info-row">
									<Col span={8} className="label-col">标签：</Col>
									<Col span={16}>{live.tag}</Col>
								</Row>
								<Row className="info-row">
									<Col span={8} className="label-col">直播类型：</Col>
									<Col span={16}>{liveTypes[live.liveType]}</Col>
								</Row>
								<Row className="info-row">
									<Col span={8} className="label-col">直播状态：</Col>
									<Col span={16}>{liveStatus[live.status]}</Col>
								</Row>
							</Col>
						</Row>
					</Collapse.Panel>
					<Collapse.Panel header="直播发言" key="2" ref="speakPanel">
						{
							replyItem
							? <div>
								<span>回复【{replyItem.userName}】</span>
								<a onClick={this.cancelReply.bind(this)}>取消回复</a>
							  </div>
							: null
						}
						<Form className="speak-form">
							<Row>
								<Col span={10}>
									<Form.Item label="文字">
										{
											getFieldDecorator('content', {
												initialValue: '',
												rules: [
													{required: true, message: '请输入发言内容'}
												]
											})(
												<Input type="textarea" rows="4" placeholder="请输入发言"/>
											)
										}
									</Form.Item>
								</Col>
								<Col span={6} offset={2}>
									<Form.Item label="图片">
										{
											getFieldDecorator('image', {
												initialValue: []
											})(
												<ImgUploader single={true}/>
											)
										}
									</Form.Item>
								</Col>
								
							</Row>
							<Row>
							<Col span={10} >
								{this.getVideo('video')}
								</Col>
							</Row>
							<Row>
								<Col span={4} offset={2}>
									<Dropdown overlay={this.getSpokesmanMune(live.hosts)}>
										<Button>主持人发言<Icon type="down" /></Button>
									</Dropdown>
								</Col>
								<Col span={4} offset={2}>
									<Dropdown overlay={this.getSpokesmanMune(live.guests)}>
										<Button>嘉宾发言<Icon type="down" /></Button>
									</Dropdown>
								</Col>
							</Row>
						</Form>
					</Collapse.Panel>
				</Collapse>
				<Row>
					<Col span={11}>
						<Card title="直播间" className="talk-card">
							{this.getTalkTpl()}
						</Card>
					</Col>
					<Col span={11} offset={2} className="comment-card">
						<Card title="网友互动">
							{this.getCommentTpl()}
						</Card>
					</Col>
				</Row>
				<Row>
					<Col span={11}>
						<div>&nbsp;</div>
					</Col>
					
				</Row>
				<Row>
					<Col span={11}>
						<Button onClick={this.props.goBack}>返回</Button>	
					</Col>
					
				</Row>
			</div>
		);
	}
}

export default Form.create()(LiveRoom);