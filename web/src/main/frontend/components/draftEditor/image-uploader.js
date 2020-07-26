import React, {Component} from 'react';
import classnames from 'classnames';
import styles from './draftEditor.less';

function getDefaultState(props) {
    return {
        imgSrc: '',
        dragEnter: false,
        uploadHighlighted: props.config.uploadEnabled && !!props.config.uploadCallback,
        showImageLoading: false,
        height: props.config.defaultSize.height,
        width: props.config.defaultSize.width,
        alt: '',
        uploadedImage:[]
    };
}

class ImageUploader extends Component {
    constructor(props){
        super(props);
        this.state = getDefaultState(props);
    }

    componentWillReceiveProps(props) {
        if(this.props.expanded && !props.expanded) {
            this.setState({
                ...getDefaultState(this.props)
            });
        } else if(props.config.uploadCallback !== this.props.config.uploadCallback || props.config.uploadEnabled !== this.props.config.uploadEnabled) {
            this.setState({
                uploadHighlighted: props.config.uploadEnabled && !!props.config.uploadCallback,
            });
        }
    }

    onDragEnter = (event) => {
        this.stopPropagation(event);
        this.setState({
            dragEnter: true
        });
    }

    onImageDrop = (event) => {
        event.preventDefault();
        event.stopPropagation();
        this.setState({
            dragEnter: false
        });
        let data;
        let dataIsItems;
        if(event.dataTransfer.items) {
            data = event.dataTransfer.items;
            dataIsItems = true;
        } else {
            data = event.dataTransfer.files;
            dataIsItems = false;
        }
        for(let i = 0; i < data.length; i++) {
            if((!dataIsItems || data[i].kind === 'file') && data[i].type.match('^image/')) {
                const file = dataIsItems ? data[i].getAsFile() : data[i];
                this.uploadImage(file);
            }
        }
    }

    showImageUploadOption = () => {
        this.setState({
            // uploadedImage : [],
            uploadHighlighted: true
        });
    }

    addImageFromState = () => {
        const {height, width, alt, uploadedImage} = this.state;
        const {onChange} = this.props;
        uploadedImage.map((item,index)=>{
            setTimeout(function(){
                onChange(item.imgSrc, item.height, item.width, alt);
            })

        })
    }

    showImageURLOption = () => {
        this.setState({
            // uploadedImage : [{}],
            uploadHighlighted: false
        });
    }

    toggleShowImageLoading = () => {
        const showImageLoading = !this.state.showImageLoading;
        this.setState({
          showImageLoading,
        });
    }

    updateValue = (index, isUrl, event) => {
        let { uploadedImage } = this.state;
        if(isUrl){
            // uploadedImage = [{}];
            if(!uploadedImage.length){
                uploadedImage = [{imgSrc:'',width:'',height:''}];
            }
            uploadedImage[index][`${event.target.name}`] = event.target.value
        }else{
            uploadedImage[index][`${event.target.name}`] = event.target.value
        }
        this.setState({
            uploadedImage
        });
    }

    updateUrlValue = (event) => {
        this.setState({
            [`${event.target.name}`]: event.target.value,
        });
    }

    selectImage = (event) => {
        if(event.target.files && event.target.files.length > 0) {
            this.uploadImage(event.target.files[0]);
        }
    }

    uploadImage = (file) => {
        this.toggleShowImageLoading();
        // const {width, height} = this.state;
        const {uploadCallback} = this.props.config;
        uploadCallback(file).then(({data}) => {
            const obj = {
                imgSrc : data.link,
                width : this.props.config.defaultSize.height,
                height : this.props.config.defaultSize.height
            }
            const uploadedImage = this.state.uploadedImage;
            uploadedImage.push(obj);
            this.setState({
                showImageLoading: false,
                dragEnter: false,
                uploadedImage
            });
            this.fileUpload = false;
        }).catch(() => {
            console.error('upload error')
            this.setState({
                showImageLoading: false,
                dragEnter: false
            });
        });
    }

    fileUploadClick = (event) => {
        this.fileUpload = true;
        event.stopPropagation();
    }

    stopPropagation = (event) => {
        if (!this.fileUpload) {
            event.preventDefault();
            event.stopPropagation();
        } else {
            this.fileUpload = false;
        }
    }

    renderUploadedImageSrc = () => {
        const {uploadedImage} = this.state;
        return uploadedImage.map((item,index)=>{
            return (
                <div className="uploaded_blk" key={index}>
                    <p className="uploaded_url">{item.imgSrc}</p>
                    <div className="rdw-image-modal-size">
                        &#8597;&nbsp;
                        <input
                            onChange={this.updateValue.bind(this,index,false)}
                            onBlur={this.updateValue.bind(this,index,false)}
                            value={item.height}
                            name="height"
                            className="rdw-image-modal-size-input"
                            placeholder="Height"
                        />
                        <span className="rdw-image-mandatory-sign">*</span>
                        &nbsp;&#8597;&nbsp;
                        <input
                            value={item.width}
                            name="width"
                            className="rdw-image-modal-size-input"
                            placeholder="Width"
                            onChange={this.updateValue.bind(this,index,false)}
                            onBlur={this.updateValue.bind(this,index,false)}
                        />
                        <span className="rdw-image-mandatory-sign">*</span>
                    </div>
                </div>
            )
        })
    }

    testHeightWidth = () =>{
        const { uploadedImage } = this.state;
        for(let i=0, len=uploadedImage.length; i<len; i++){
            if(!uploadedImage[i].height || !uploadedImage[i].width){
                return false
            }
        }
        return true;
    }

    renderAddImageModal = () =>{
        const {
            imgSrc,
            uploadHighlighted,
            showImageLoading,
            dragEnter,
            height,
            width,
            alt,
            uploadedImage
        } = this.state;
        const {
            config: {
                popupClassName,
                uploadCallback,
                uploadEnabled,
                urlEnabled,
                inputAccept,
                alt: altConf
            },
            doCollapse,
            translations
        } = this.props;

        return (
            <div
                className={classnames('rdw-image-modal', popupClassName)}
                onClick={this.stopPropagation}
            >
                <div className="rdw-image-modal-header">
                    {
                        uploadEnabled && uploadCallback &&
                        <span
                            onClick={this.showImageUploadOption}
                            className="rdw-image-modal-header-option"
                        >
                            {translations['components.controls.image.fileUpload']}
                            <span
                                className={classnames('rdw-image-modal-header-label', {'rdw-image-modal-header-label-highlighted': uploadHighlighted})}
                            />
                        </span>
                    }
                    {
                        urlEnabled &&
                        <span
                            onClick={this.showImageURLOption}
                            className="rdw-image-modal-header-option"
                        >
                            {translations['components.controls.image.byURL']}
                            <span
                                className={classnames('rdw-image-modal-header-label', {'rdw-image-modal-header-label-highlighted': !uploadHighlighted})}
                            />
                        </span>
                    }
                </div>
                {
                    uploadHighlighted ?
                    <div onClick={this.fileUploadClick}>
                        <div
                            onDragEnter={this.onDragEnter}
                            onDragOver={this.stopPropagation}
                            onDrop={this.onImageDrop}
                            className={classnames('rdw-image-modal-upload-option', {'rdw-image-modal-upload-option-highlighted': dragEnter})}
                        >
                            <label
                                htmlFor="file"
                                className="rdw-image-modal-upload-option-label"
                            >
                                {translations['components.controls.image.dropFileText']}
                            </label>
                        </div>
                        <input
                            type="file"
                            id="file"
                            accept={inputAccept}
                            onChange={this.selectImage}
                            className="rdw-image-modal-upload-option-input"
                        />
                        <div className={styles.uploaded_image_wrap}>
                            {this.renderUploadedImageSrc()}
                        </div>
                    </div> :
                    <div className="rdw-image-modal-url-section" style={{'flexDirection':'column'}}>
                        <div style={{'display':'flex','alignItems':'center','margin':'0'}}>
                            <input
                                className="rdw-image-modal-url-input"
                                placeholder={translations['components.controls.image.enterlink']}
                                name="imgSrc"
                                onChange={this.updateValue.bind(this,0,true)}
                                onBlur={this.updateValue.bind(this,0,true)}
                                value={!!uploadedImage[0]?uploadedImage[0].imgSrc:''}
                            />
                            <span className="rdw-image-mandatory-sign">*</span>
                        </div>
                        <div className="rdw-image-modal-size" style={{'width':'90%','margin':'0'}}>
                            &#8597;&nbsp;
                            <input
                                onChange={this.updateValue.bind(this,0,true)}
                                onBlur={this.updateValue.bind(this,0,true)}
                                value={!!uploadedImage[0]?uploadedImage[0].height:''}
                                name="height"
                                className="rdw-image-modal-size-input"
                                placeholder="Height"
                            />
                            <span className="rdw-image-mandatory-sign">*</span>
                            &nbsp;&#8596;&nbsp;
                            <input
                                onChange={this.updateValue.bind(this,0,true)}
                                onBlur={this.updateValue.bind(this,0,true)}
                                value={!!uploadedImage[0]?uploadedImage[0].width:''}
                                name="width"
                                className="rdw-image-modal-size-input"
                                placeholder="Width"
                            />
                            <span className="rdw-image-mandatory-sign">*</span>
                        </div>
                    </div>
                }
                {
                    altConf.present &&
                    <div className="rdw-image-modal-size">
                        <span className="rdw-image-modal-alt-lbl">Alt Text</span>
                        <input
                            onChange={this.updateValue}
                            onBlur={this.updateValue}
                            value={alt}
                            name="alt"
                            className="rdw-image-modal-alt-input"
                            placeholder="alt"
                        />
                        <span className="rdw-image-mandatory-sign">{altConf.mandatory && '*'}</span>
                    </div>
                }
                {/*<div className="rdw-image-modal-size">
                    &#8597;&nbsp;
                    <input
                        onChange={this.updateValue}
                        onBlur={this.updateValue}
                        value={height}
                        name="height"
                        className="rdw-image-modal-size-input"
                        placeholder="Height"
                    />
                    <span className="rdw-image-mandatory-sign">*</span>
                    &nbsp;&#8596;&nbsp;
                    <input
                        onChange={this.updateValue}
                        onBlur={this.updateValue}
                        value={width}
                        name="width"
                        className="rdw-image-modal-size-input"
                        placeholder="Width"
                    />
                    <span className="rdw-image-mandatory-sign">*</span>
            </div>*/}
                <span className="rdw-image-modal-btn-section">
                    <button
                        className="rdw-image-modal-btn"
                        onClick={this.addImageFromState}
                        disabled={!uploadedImage.length || !this.testHeightWidth() || (altConf.mandatory && !alt)}
                    >
                        {translations['generic.add']}
                    </button>
                    <button
                        className="rdw-image-modal-btn"
                        onClick={doCollapse}
                    >
                        {translations['generic.cancel']}
                    </button>
                </span>
                {
                    showImageLoading ?
                    <div className="rdw-image-modal-spinner">
                        <div className="rdw-spinner">
                            <div className="rdw-bounce1" />
                            <div className="rdw-bounce2" />
                            <div className="rdw-bounce3" />
                        </div>
                    </div> :
                    undefined
                }
            </div>
        );
    }

    render(){
        const {
            config: { icon, className, title },
            expanded,
            onExpandEvent,
            translations
        } = this.props;
        return(
            <div
                className="rdw-image-wrapper"
                aria-haspopup="true"
                aria-expanded={expanded}
                aria-label="rdw-image-control"
            >
                <div className="rdw-option-wrapper" onClick={onExpandEvent} title={title || translations['components.controls.image.image']}>
                    <img src={icon} alt=""/>
                </div>
                {expanded ? this.renderAddImageModal() : undefined}
            </div>
        )
    }
}

export default ImageUploader;
