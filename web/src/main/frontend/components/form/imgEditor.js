import React from 'react';
import PropTypes from 'prop-types';
import {Row, Col, Slider, Button, Input} from 'antd';
import AvatarEditor from 'react-avatar-editor';
import './imgEditor.less';


class ImgEditor extends React.Component {
	constructor(props) {
		super(props);
		this.state = this.getInitState();
	}

	getInitState() {
		return {
			position: {x: 0.5, y: 0.5},
			rotate: 0,
			scale: 1,
			w_r: 0,
			h_r: 0
		};
	}

	getFinalSize = (props, state) => {
		const {width, height, ratio} = this.props;
		const {w_r, h_r} = this.state;
		const size = {w_f: w_r, h_f: h_r};
		if(width) {
			size.w_f = width;
		}
		else if(!width && ratio && height) {
			size.w_f = parseInt(height * ratio);
		}
		else if(height) {
			size.h_f = height;
		}
		else if(!height && ratio && width) {
			size.h_f = parseInt(width / ratio);
		}
		return size;
	}

	imgLoadSuccess = ({resource}) => {
		this.setState({
			w_r: resource.width,
			h_r: resource.height
		});
	}

	posChg = (position) => {
		this.setState({
			position
		});
	}

	scaleChg = (scale) => {
		this.setState({
			scale
		});
	}

	calcHeight = (width, height, ratio) => {
		const {w_r, h_r} = this.state;
		if(!w_r) {
			return 300;
		}
		else {
			return 400*h_r/w_r;
		}
	}

	onSave = () => {
		const {position, scale, rotate, w_r, h_r} = this.state;
		let {imgId, imgUrl, imgSave} = this.props;
		imgUrl = imgUrl.split("?x-oss-process")[0];
		const vh = parseInt(400*h_r/w_r);
		const px = position.x;
		const py = position.y;
		const sl = scale;
		const sx = parseInt(200*(sl-1)+400*sl*(px-0.5));
		const sy = parseInt(vh/2*(sl-1) + vh*sl*(py-0.5));
		const {w_f, h_f} = this.getFinalSize();
		imgUrl += `?x-oss-process=image/resize,m_fixed,limit_0,w_400,h_${vh}/resize,m_mfit,limit_0,w_${parseInt(400*sl)}/crop,x_${sx},y_${sy},w_400,h_${vh}/resize,m_mfit,limit_0,w_${w_f},h_${h_f}`;
		imgSave(imgId, imgUrl);
		setTimeout(() => {
			this.state = this.getInitState();
		});
	}

	render() {
		const {position, rotate, scale} = this.state;
		const {width, height, ratio, imgUrl, imgSave, imgCancel} = this.props;
		return (
			<div className="img-editor">
				<AvatarEditor
					onPositionChange={this.posChg}
					position={position}
					rotate={rotate}
					border={0}
					image={imgUrl}
					width={400}
					height={this.calcHeight()}
					scale={scale}
					onLoadSuccess={this.imgLoadSuccess}
				/>
				<div className="op-box">
					<Row>
						<Col span={4} className="label-col">缩放：</Col>
						<Col span={20}>
							<Slider 
								min={1}
								max={10}
								step={0.1}
								value={scale}
								onChange={this.scaleChg}
							/>
						</Col>
					</Row>
				</div>
				<div className="btn-box">
					<Button type="primary" onClick={this.onSave}>确定</Button>
					<Button onClick={imgCancel}>取消</Button>
				</div>
			</div>
		);
	}
}

export default ImgEditor;