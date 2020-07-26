import React from 'react';
import {Modal, Upload, Form, Input, Icon, Button, Row, Col, Tooltip, message} from 'antd';
import {urlPath, IMG_UPLOAD_TYEPS} from '../../constants';
import styles from './albumUploader.less';

const FormItem = Form.Item;


const Dragger = Upload.Dragger;

class AlbumUploader extends React.Component {
	constructor(props) {
		super(props);
		let {accept, value={},batchFlag,number} = this.props;
		if(!accept) {
			accept = [];
			IMG_UPLOAD_TYEPS.forEach(type => {
				accept.push(`image/${type}`);
			});
			accept=accept.join(',');
		}
        this.state = {
			accept,
			bamv: false,
			fileList: value.fileList || [],
            batchList:[],
            batchFlag:batchFlag=='无'?false: true,
            number:number?number:99999
		};
		this.add = this.add.bind(this);
	}

	componentWillReceiveProps(nextProps) {
		// console.log("nextProps",nextProps.value)
		const {numberChange} =this.props;
		if('value' in nextProps) {
			console.log(value,'nextprops')
			const value = nextProps.value;
			console.log(nextProps.value,'这是nextpropsvalue')
			// nextProps.value.fileList.length==1?numberChange():'';
			const fileList = value.fileList || [];
			console.log(value.fileList,'zheshi value')
			this.setState({fileList});
		}
        let {accept, value={},batchFlag,number} = this.props;
		this.setState({'number':number?number:99999})
	}

	remove(k) {
		const fileList = this.state.fileList.filter(item => item.key !== k);
		this.setState({fileList});
		this.triggerChange({fileList});
	}

	add() {
		const {numberChange} = this.props;
		console.log(numberChange);
		console.log(this.props,'这是numberchange');
        if(numberChange){
            numberChange();
        }
	    let num = this.state.num++;
		const fileList = this.state.fileList;
		fileList.map((item, i) => {item.key = i});
		fileList.push({
			key: fileList.length,
			url: '',
			dec: ''
		});
		this.setState({fileList,num});
		this.triggerChange({fileList});
	}

	batchAdd() {
		const fileList = this.state.fileList;
		const batchList = this.batchList || [];
		const length = fileList.length;
		batchList.forEach((url, index) => {
			fileList.push({
				key: length + index,
				url,
				dec: ''
			});
		});
		this.setState({fileList});
		this.triggerChange({fileList});
		this.hideBam();
	}

	showBam() {
		this.setState({
			bamv: true,
            batchList:[]
		});
	}

	hideBam() {
		this.setState({
			bamv: false
		});
	}

	move(key, direc) {
		const fileList = this.state.fileList;
		if(direc == 'up' && key == 0 || direc == 'down' && key == fileList.length - 1) {
			return;
		}
		if(direc == 'up') {
			key = key - 1;
		}
		for(let i = 0, len = fileList.length; i < len; i++) {
			if(fileList[i].key == key) {
				const tmp1 = {...fileList[i]};
				const tmp2 = {...fileList[i + 1]}
				fileList[i] = {...tmp2, key: tmp1.key};
				fileList[i + 1] = {...tmp1, key: tmp2.key};
				break;
			}
		}
		this.setState({fileList});
		this.triggerChange({fileList});
	}

	handleUploadChange(info, key) {
		const fileList = this.state.fileList;
		const file = info.file;
		if(file.status === 'done') {
			if(file.response && file.response.code == -1) {
				message.error(file.response.msg);
			}
			else if(file.response && file.response.code == 0 && file.response.data) {
				const fileUrl = info.file.response.data.fileUrl;
				fileList.map(item => {
					if(item.key == key) {
						item.url = fileUrl;
					}
				})
			}
		}
		this.setState({fileList});
		this.triggerChange({fileList});
	}

	handleBatchUploadChange({fileList}) {
		const batchList = [];


		fileList.forEach(file => {
			if(file.status === 'done') {
				if(file.response && file.response.code == -1) {
					message.error(file.response.msg);
				}
				else {
					batchList.push(file.response.data.fileUrl);
				}

			}
		});

		this.batchList = batchList;

        this.setState({batchList:fileList});
	}

	handleInputChange(e, key) {
		const dec = e.target.value;
		const fileList = this.state.fileList;
		fileList.map(item => {
			if(item.key == key) {
				item.dec = dec;
			}
		});
		this.setState({fileList});
		this.triggerChange({fileList});
	}

	triggerChange(changedValue) {
		const onChange = this.props.onChange;
		if(onChange) {
			onChange({...this.state, ...changedValue});
		}
	}
	render() {
		const {fileList, bamv, accept} = this.state;
		const formItems = fileList.map((file, index) => {
			const imageUrl = file.url;
			return (
				<div key={file.key} className="album-item">
					<Row type="flex" justify="space-between" align="top">
						<Col span={3}>
							<Upload
								className="album-uploader"
								name="file"
								accept={accept}
								showUploadList={false}
								action={urlPath.UPLOAD_FILE}
								onChange={info => this.handleUploadChange(info, file.key)}
							>
							{
								imageUrl
								? <div className="album-image-wrap"><img src={imageUrl} className="album-img"/></div>
								: <Icon type="plus" className="album-uploader-trigger"/>
							}
							</Upload>
						</Col>
						<Col span={18}>
							<Input
								type="textarea"
								rows={5}
								value={file.dec}
								onChange={(e) => this.handleInputChange(e, file.key)}
							/>
						</Col>
						<Col span={3}>
							<Tooltip title="删除" placement="top">
								<Icon
									className="del-btn"
									type="minus-circle-o"
									disabled={fileList.length === 1}
									onClick={() => this.remove(file.key)}
								/>
							</Tooltip>
							<Tooltip title="向上移动" placement="top">
								<Icon type="up-circle-o" disabled={index == 0} onClick={() => this.move(file.key, 'up')} />
							</Tooltip>
							<Tooltip title="向下移动" placement="top">
								<Icon type="down-circle-o" disabled={index == fileList.length - 1} onClick={() => this.move(file.key, 'down')} />
							</Tooltip>
						</Col>
					</Row>
				</div>
			);
		});

		const modalProps = {
			visible: bamv,
			title: '批量添加',
			onCancel: this.hideBam.bind(this),
			onOk: this.batchAdd.bind(this)
		};
        const num = this.props.num?this.props.num:0;
        console.log("1111",num,this.state.number)
		return (
			<div className="m-album">
				{formItems}
				<div>
					{/* {console.log(this.state.number,'邓伦')} */}
					{/* {console.log(this.state.value,'this is state.value')} */}
                    {num<this.state.number?
                    <Button type="dashed" onClick={this.add.bind(this)}>
                        <Icon type="plus"/> 添加一项
                    </Button>:null}
                    {this.state.batchFlag?<Button type="dashed" className="batch-add-btn" onClick={this.showBam.bind(this)}>
                        <Icon type="plus"/> 批量添加
                    </Button>:null}
          		</div>
          		<Modal {...modalProps} className="batch-upload-modal">
          			<div className="drag-wrap">
	          			<Dragger
							accept={accept}
	          				multiple={true}
	          				showUploadList={true}
                            fileList={this.state.batchList}
							action={urlPath.UPLOAD_FILE}
							onChange={this.handleBatchUploadChange.bind(this)}
	          			>
	          				<p className="ant-upload-drag-icon">
	          					<Icon type="inbox"/>
	          				</p>
	          			</Dragger>
          			</div>
          		</Modal>
			</div>
		);
	}
}

export default AlbumUploader;
