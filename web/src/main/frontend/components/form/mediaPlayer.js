import React from 'react'
import PropTypes from 'prop-types'
import { Modal, Button } from 'antd'
import styles from './mediaPlayer.less'

class MediaPlayer extends React.Component {
	constructor(props) {
		super(props)
		this.state = {
	    	visible: props.visible || false,
			type:props.type || 'video',
			url:props.url || ''
		}
		//console.log('tag-url', this.state.url)//选择媒体文件中所涉及的视频路径
	}
	componentWillReceiveProps(nextProps) {
		this.setState({
			...nextProps
		})
	}
	handleClose = () => {
		const type = this.state.type;
		const media = this.refs[type];
		if(media){
		media.pause();
		}
		this.setState({ visible: false });
	}
	onOk = () => {
		this.handleClose()
	}
	onCancel = () => {
		this.handleClose()
	}
	showMediaPlayerModal = () => {
		if(!this.state.url){
			this.props.nourl && this.props.nourl()
			return
		}
		this.setState({ visible: true });
	}
	render() {
		const {type, url, visible} = this.state;

		const modelProps = {
			visible,
			url,
			title: '预览',
			onOk: this.onOk,
			onCancel: this.onCancel,
			footer:[
				<Button key="submit" type="primary"  size="large" onClick={this.onOk}>
					确定
				</Button>,
				<Button key="back" size="large" onClick={this.onCancel}>取消</Button>,
			]
		}

		let name = ''
 		if (this.props.file) {
 			name = this.props.file.title || ''
 		}
		if (this.state.mediaData) name = this.state.mediaData.name
		
		return (
			<span>
				<span style={{marginRight: '10px'}}>{name}</span>
				<a onClick={this.showMediaPlayerModal}>预览</a>
				<Modal {...modelProps} className={styles.mediaPalyer}>
					{   visible?(
						type === 'video'
						? <video src={url} controls>浏览器版本过低，不支持audio标签</video>
						: <audio src={url} controls>浏览器版本过低，不支持audio标签</audio>)
                        :''
					}
				</Modal>
			</span>
		)
	}
}
MediaPlayer.propTypes = {
  type: PropTypes.string,
  visible:PropTypes.bool,
  url:PropTypes.string
}
export default MediaPlayer
