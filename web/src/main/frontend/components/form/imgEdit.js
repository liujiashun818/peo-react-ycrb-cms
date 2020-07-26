import React from 'react'
import PropTypes from 'prop-types'
import { Button,Tag,Input } from 'antd'
import AvatarEditor from 'react-avatar-editor'

const myStyle = {
    "margin":"0 auto auto",
    "display":"block"
}

class ImgEdit extends React.Component {

    state = {
        position:{x:0.5,y:0.5},
        rotate:0,
        scale:1
    }


    handleScale = (e) => {
    const scale = parseFloat(e.target.value)
    this.setState({ scale })
}

handleImgSave =()=>{
    const {imgSave,imgId,width,height,balance,imgurl} = this.props;
    const imgTemp = new Image();
    imgTemp.onload = function (){
    }
    imgTemp.src=imgurl;
    const ph = parseInt(400*(imgTemp.height)/(imgTemp.width));
    const py = this.state.position.y;
    const px = this.state.position.x;
    const sl = parseInt(this.state.scale);
    const vh = parseInt(400*this.props.height/this.props.width||400/this.props.balance);
    const sx = parseInt(200*(sl-1)+400*sl*(px-0.5));
    const sy = parseInt(ph*sl/2-vh/2+ph*sl*(py-0.5));
    if(imgurl.indexOf("?x-oss-process")>0){
        var iEditUrl = imgurl.split("?")[0];
        if(!height) {var imgEditUrl = iEditUrl+"?x-oss-process=image/resize,m_fixed,limit_0,w_400,h_"+ph+"/resize,m_mfit,limit_0,w_"+400*sl+"/crop,x_"+sx+",y_"+sy+",w_400,h_"+vh+"/resize,m_mfit,limit_0,h_"+width/balance+",w_"+width;imgSave(imgEditUrl,imgId);}
        else if(!width) {var imgEditUrl = iEditUrl+"?x-oss-process=image/resize,m_fixed,limit_0,w_400,h_"+ph+"/resize,m_mfit,limit_0,w_"+400*sl+"/crop,x_"+sx+",y_"+sy+",w_400,h_"+vh+"/resize,m_mfit,limit_0,h_"+height+",w_"+height*balance;imgSave(imgEditUrl,imgId);}
        else {var imgEditUrl = iEditUrl+"?x-oss-process=image/resize,m_fixed,limit_0,w_400,h_"+ph+"/resize,m_mfit,limit_0,w_"+400*sl+"/crop,x_"+sx+",y_"+sy+",w_400,h_"+vh+"/resize,m_mfit,limit_0,h_"+height+",w_"+width;imgSave(imgEditUrl,imgId);}
    }else{
        if(!height) {var imgEditUrl = imgurl+"?x-oss-process=image/resize,m_fixed,limit_0,w_400,h_"+ph+"/resize,m_mfit,limit_0,w_"+400*sl+"/crop,x_"+sx+",y_"+sy+",w_400,h_"+vh+"/resize,m_mfit,limit_0,h_"+width/balance+",w_"+width;imgSave(imgEditUrl,imgId);}
        else if(!width) {var imgEditUrl = imgurl+"?x-oss-process=image/resize,m_fixed,limit_0,w_400,h_"+ph+"/resize,m_mfit,limit_0,w_"+400*sl+"/crop,x_"+sx+",y_"+sy+",w_400,h_"+vh+"/resize,m_mfit,limit_0,h_"+height+",w_"+height*balance;imgSave(imgEditUrl,imgId);}
        else {var imgEditUrl = imgurl+"?x-oss-process=image/resize,m_fixed,limit_0,w_400,h_"+ph+"/resize,m_mfit,limit_0,w_"+400*sl+"/crop,x_"+sx+",y_"+sy+",w_400,h_"+vh+"/resize,m_mfit,limit_0,h_"+height+",w_"+width;imgSave(imgEditUrl,imgId);}
    }

    this.setState({ previewVisible: false })
}

logCallback (e) {
}

setEditorRef = (editor) => {
    if (editor) this.editor = editor
}

handlePositionChange = position => {
    this.setState({ position })
}

render () {
    const {imgSave,imgCancle} = this.props;
    return (
        <div>
        <AvatarEditor
    ref={this.setEditorRef}
    onPositionChange={this.handlePositionChange}
    position={this.state.position}
    rotate={parseFloat(this.state.rotate)}
    onLoadFailure={this.logCallback.bind(this, 'onLoadFailed')}
    onLoadSuccess={this.logCallback.bind(this, 'onLoadSuccess')}
    onImageReady={this.logCallback.bind(this, 'onImageReady')}
    onImageLoad={this.logCallback.bind(this, 'onImageLoad')}
    onDropFile={this.logCallback.bind(this, 'onDropFile')}
    border={0}
    image={this.props.imgurl}
    width={400}
    height={400*this.props.height/this.props.width||400/this.props.balance}
    scale={this.state.scale}
    balance={this.props.balance}
    style={myStyle}
        />
        <br/>
        <Tag >缩放：</Tag>
    <br /><br />
    <input
    name='scale'
    type='range'
    onChange={this.handleScale}
    min='1'
    max='10'
    step='0.1'
    defaultValue='1'
        />
        <br /><br />
        <Button type="primary" onClick={this.handleImgSave} >裁剪图片</Button>
    <Button onClick={imgCancle} style={{marginLeft:320}}>取消</Button>
    </div>
)
}
}

export default ImgEdit
