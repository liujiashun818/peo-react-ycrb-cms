import React from 'react'
import PropTypes from 'prop-types'
import {Tabs, Upload, Icon, message, Button, Input, Modal, Form} from 'antd'
import { UPLOAD_FILE, urlPath } from '../../constants';
import styles from './mediaUploader.less'
import MediaPicker from './mediaPicker'
import MediaPlayer from './mediaPlayer'

const TabPane = Tabs.TabPane;
const InputGroup = Input.Group;

class MediaUploader extends React.Component {

	constructor(props) {
		super(props)
		const state =  props.value || {
			coverImg:'',
			url:'',
			id: '',
			mediaData:this.props.file
			//mediaId:'',
		}
		// console.log("asdasda",this.props.type)
		this.state = {
			...state,
			type:this.props.type || 'video',
            previewVisible:false
		}
	}
	componentWillReceiveProps(nextProps) {
		const state = nextProps.value;
		
	    // if(!state.coverImg&&this.state.coverImg){
		// 	console.log("nextProps",state.coverImg)
        //     state.coverImg = this.state.coverImg
        // }
		this.setState({
			...state,
			type:nextProps.type || 'video',
			//mediaData:nextProps.file
		})
	}
	onCoverChange = (info) => {
				// console.log("onCoverChange",info.file.response.data.fileUrl)
			
		let coverImg = this.state.coverImg;
	  //  if(info.file.response && info.file.response.code==0 && info.file.status !='removed'){
		// if(info.file.status == 'done' ){
	      if (info.file.response && info.file.response.code==0 && info.file.response.data) {
             
	          coverImg = info.file.response.data.fileUrl;
              this.setState({ coverImg });
			  this.triggerChange({ coverImg});
	       }  else if(info.file.response && info.file.response.code==-1){
              message.error(info.file.response.msg)

              this.setState({coverImg:''});
          }
	    // }

	}
	onUrlChange = (e) => {
		const url = e.target.value;
		//const mediaId = '';
		const id = '';
		// this.setState({ url, mediaId });
		this.setState({ url, id });
		// this.triggerChange({ url, mediaId });
		this.triggerChange({ url, id });
	}
	onMeidaChange = (data) => {
		console.log("data",data)
		//根据传回来的mediaId判断媒体选择是否成功
		if(data && data.newMediaData && data.newMediaData.length > 0){
			const newMediaData = data.newMediaData[0];
			const mediaData = {
				// mediaId:newMediaData.mediaId,
				name: newMediaData.name,
				id:newMediaData.id,
				coverImg:newMediaData.cover || '',
				url:newMediaData.hdUrl || newMediaData.mp3BigUrl,
				size:newMediaData.size,
				// type:newMediaData.hdUrl?'video':'audio',
				type:newMediaData.type,
				videoCover:newMediaData.videoCover,
				title: newMediaData.name,
				times:newMediaData.duration
			}
			this.setState({mediaData});
			this.triggerChange({...mediaData});
		}
	}
	triggerChange = (changedValue) => {
		// console.log('changedValue===', changedValue)
		// Should provide an event to pass value to Form.
		console.log("file",this.props.file)
		const onChange = this.props.onChange;
        if (onChange) {

		  // const {coverImg,url,mediaId }= this.state;
		  // onChange(Object.assign({},{coverImg,url,mediaId }, changedValue));
		  const {coverImg,url,id,times,title,size,type }= this.state;
			// console.log("data",changedValue,Object.assign({},{coverImg,url,id }, changedValue))
			let o = Object.assign({},{coverImg,url,id,times,title,size,type }, changedValue)
		  
		  if(this.props.file && this.props.file.title){
			onChange(Object.assign({},{coverImg,url,id,times,title,size,type }, changedValue,{title:this.props.file.title}))
		  } else {
			onChange(o)
		  }
		  ;
		}
	}
    handlePreview = (file) => {
        this.setState({
            previewImage: file.url || file.thumbUrl,
            previewVisible: true,
        });
    }
    handleCancel = () => this.setState({ previewVisible: false })
	render() {
		const { mediaData } = this.state;
		const { file } = this.props
		const coverImg = this.state.coverImg;
		const url = this.state.url;
		const mediaPlayerProps = {
			type:this.state.type,
			url,
			mediaData,
 			file
		}
		console.log("mediaData",mediaData)
		// accept={this.props.type === 'video' ? 'video/*' : 'audio/*'}
		return (
			<div>
				
				<Tabs
					defaultActiveKey={this.state.url && !this.state.id ? '2':'1'}
					className={styles.tabCon}
					>
					<TabPane tab={<span><Icon type="appstore-o" />从媒体库选择</span>} key="1">
					 	<MediaPicker onlyVideo={this.props.onlyVideo} mediaType={this.state.type} onChange={this.onMeidaChange} />
					 	<span className={styles.mediaPlayer}>
					 		<MediaPlayer {...mediaPlayerProps} />
					 	</span>
					</TabPane>
					{/*<TabPane tab={<span><Icon type="link" />输入文件地址</span>} key="2">
					 	<InputGroup compact>
					 		<Input style={{ width: '80%' }} value={url} placeholder="请输入文件地址" onChange={this.onUrlChange} />
					 		<span className={styles.mediaPlayer}>
					 			<MediaPlayer {...mediaPlayerProps} />
					 		</span>
					 	</InputGroup>
					</TabPane>*/}
				</Tabs>
				{this.state.type=='video'&&(!mediaData||mediaData.type!='audio')?
					<Upload
					action={urlPath.UPLOAD_FILE}
					className="cover-uploader"
					name="file"
					showUploadList={false}
					onPreview={this.handlePreview}
					onChange={ this.onCoverChange}
					accept="image/png,image/jpeg,image/jpeg,image/gif"
					>
					{
						coverImg?
						<img src={coverImg} alt="" className="cover-img" /> :
						<Icon type="plus" className="cover-uploader-trigger" />
					}

					</Upload>:null
				}
				{this.state.type=='video'&&(!mediaData||mediaData.type!='audio')?
					<div className={styles.decWarper}>
	            	<span>封面图片</span>
	            	</div>:null
				}

	            
			</div>
		)
	}
}

MediaUploader.propTypes = {
  value: PropTypes.object
}
export default MediaUploader
