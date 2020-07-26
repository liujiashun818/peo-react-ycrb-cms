import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import {
    Alert,
    message,
    notification,
    Form,
    Input,
    InputNumber,
    Radio,
    Select,
    TreeSelect,
    DatePicker,
    Collapse,
    Button,
    Checkbox,
    Popconfirm,
    Row,
    Col,
    Switch,
    Icon,
    Upload,
    Modal,
    Carousel
} from 'antd';
import moment from 'moment';
import {Jt} from '../../utils';
import {blocks, blocks2, delFlags,delFlags2} from '../../constants';
import FInput from '../form/f-input';
import FSelect from '../form/f-select';
import FCheckboxes from '../form/f-checkboxes';
import FDate from '../form/f-date';
import FRadio from '../form/f-radio';
import FTextarea from '../form/f-textarea';
import FImage from '../form/f-image';
import FCheckbox from '../form/f-checkbox';
import InputStat from '../form/inputStat';
import ImgUploader from '../form/imgUploader';
import DraftEditor from '../form/editor';
import UEditor from '../form/ueditor';
import AlbumUploader from '../form/albumUploader';
import SurveyModule from '../form/surveyModule';
import MediaUploader from '../form/mediaUploader';
import styles from './edit.less';
import VisibleWrap from '../ui/visibleWrap';
import {urlPath, IMG_UPLOAD_TYEPS} from '../../constants';  //从这里引进来接口
import readBtn from '../../image/btn_Read.png';
import { getClientUploadState } from '../../modules/cms/services/article';
import RecommondModal from './recommondModal'
import recommondModal from './recommondModal';
const CollapsePanel = Collapse.Panel;
const FormItem = Form.Item;

const Jt_V = Jt.validate;
message.config({
    duration: 4.5
});

const formItemLayout = {
    labelCol: {
        span: 6
    },
    wrapperCol: {
        span: 18
    }
};

const formTailLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 8, offset: 6 },
};

const formItemLayoutWithOutLabel = {
    wrapperCol: {
        span: formItemLayout.wrapperCol.span,
        offset: formItemLayout.labelCol.span,
    }
};
const dateFormat = 'YYYY-MM-DD HH:mm:ss';

class Edit extends React.Component {
    constructor(props) {
        super(props);
        const {artNewStatus} = this.props;
        this.tip = null;
        this.state={
            catgsModelId: [],
            flag :false,
            previewVisible: false,
            previewImage: '',
            fileList:artNewStatus.fileList?artNewStatus.fileList:[],
            fileList_two:artNewStatus.fileList_two,
            previewVisible_two: false,
            previewImage_two: '',
            fileList_three:artNewStatus.fileList_three,
            previewVisible_three: false,
            previewImage_three: '',
            fileList_four:artNewStatus.fileList_four,
            fileList_five:artNewStatus.fileList_five,
            previewVisible_five: false,
            previewImage_five: '',
            fileList_length:artNewStatus.viewType=='three'?3:1,
            mediaUrl:'',
            sensitiveWordsVisible:this.props.sensitiveWordsVisible,
            visibleRecommond:this.props.visibleRecommond,
        }
    }
    // componentDidMount() {
    //     const { updateState, curArt, form:{getFieldDecorator} } = this.props;
    //     console.log(this.props.curArt.type,'hhhhhhhhhhhhhhhhhhhhhhhhh');
    //     {curArt.type=='video'||curArt.type=='common'?this.getVideos():null}
    //   }
    componentWillReceiveProps(nextProps){
      //  this.setState({'fileList_two':nextProps.artNewStatus.fileList_two?nextProps.artNewStatus.fileList_two:[],'fileList_four':nextProps.artNewStatus.fileList_four?nextProps.artNewStatus.fileList_four:[],'fileList_five':nextProps.artNewStatus.fileList_five?nextProps.artNewStatus.fileList_five:[],'fileList':nextProps.artNewStatus.fileList.length?nextProps.artNewStatus.fileList:[],fileList_length:nextProps.artNewStatus.viewType=='three'?3:1})
      this.setState({'fileList_three':nextProps.artNewStatus.fileList_three?nextProps.artNewStatus.fileList_three:[],'fileList_two':nextProps.artNewStatus.fileList_two?nextProps.artNewStatus.fileList_two:[],'fileList_four':nextProps.artNewStatus.fileList_four?nextProps.artNewStatus.fileList_four:[],'fileList_five':nextProps.artNewStatus.fileList_five?nextProps.artNewStatus.fileList_five:[],'fileList':nextProps.artNewStatus.fileList.length?nextProps.artNewStatus.fileList:[],fileList_length:nextProps.artNewStatus.viewType=='three'?3:1})
    };
    componentWillUnmount () {
        // 退出时清除数据
        const { updateState } = this.props
        updateState({
            curArt:{}
        })
    }
    componentWillMount () {
        const {catgs} = this.props
        // console.log('catgs', catgs)
        this.loopGetModelId(catgs)
    }

    loopGetModelId(arr) {
        arr.map((item) => {
            if (item.modelId === 6) {
                let { catgsModelId } = this.state
                catgsModelId.push(item.key)
                this.setState({ catgsModelId })
            }
           
            if (item.children && item.children.length) {
                this.loopGetModelId(item.children)
            }
        });
    }

    attrChange(e, attr) {
        const { updateState, curArt } = this.props;
		if(attr === 'title') {
			updateState({
				curArt:{
					...curArt,
					title: e.target.value,
				}
			})
		}else if(attr === "listTitle") {
			updateState({
				curArt:{
					...curArt,
                    listTitle: e.target.value,
				}
			})
		} else if(attr === "authors") {
			updateState({
				curArt:{
					...curArt,
					authors: e.target.value,
				}
			})
        }
    }
    EditChange(html) {
        const { updateState, curArt, form:{getFieldDecorator} } = this.props;
        let self = this;
        // console.log(curArt, 'EditChange-curArt');
        this.props.form.setFieldsValue({
            content: {html: self.getEditor(html)  }
          });
        let articleData = curArt.articleData || {}
        articleData = Object.assign(articleData, {
            content: self.getEditor(html)
        })
        updateState({
            curArt:{
                ...curArt,
                articleData: articleData,
                content:{html:self.getEditor(html)}
            }
        })
    }
    /**
     * 新增加百度 ueditor 获取富文本内容
     * @return {[type]} [description]
     */
    getEditerContent = () => {

        const htmls = this.editerNode.getContent()
        const { updateState, curArt, form:{getFieldDecorator} } = this.props;
        let articleData = curArt.articleData
        articleData = Object.assign(articleData, {
            content: htmls
        })
        return htmls
        // updateState({
        //     curArt:{
        //         ...curArt,
        //         articleData: articleData
        //     }
        // })
    }
    getListTt() {
        const {isShowListTitle,curArt, curArt:{listTitle}, form:{getFieldDecorator}} = this.props;
        return (
            curArt.isShowListTitle?
            <Form.Item>
            {
                getFieldDecorator('listTitle', {
                    initialValue: listTitle
                })(
                    <Input placeholder="请输入列表标题" onBlur={value => this.attrChange(value, 'listTitle') } />
                )
            }
            </Form.Item>
            :null
        );
    }

    getLink() {
        const {curArt:{type, link}, form:{getFieldDecorator}} = this.props;
        if(type === 'link') {
            return (
                <Form.Item>
                {
                    getFieldDecorator('link', {
                        initialValue: link
                    })(
                        <Input placeholder="请输入跳转链接"/>
                    )
                }
                </Form.Item>
            );
        }
        return null;
    }

    fromThumbnail({fileList=[]}) {
        if(fileList.length === 0) {
            return '';
        }
        const urls = [];
        fileList.forEach(file => {
            urls.push(file.url)
        });
        return urls.join(',');
    }

    toThumbnail(imageUrl='') {
        if(!imageUrl) {
            return {};
        }
        const urls = imageUrl.split(',');
        const fileList = [];
        urls.forEach((url, i) => {
            fileList.push({
                uid: -(i+1),
                name: url,
                url: url
            });
        });
        return {fileList};
    }

    getThumbnail() {
        const {curArt:{imageUrl}, form:{getFieldDecorator}} = this.props;
        return (
            <Form.Item>
            {
                getFieldDecorator('thumbnail', {
                    initialValue: this.toThumbnail(imageUrl)
                })(<ImgUploader tip="上传图片"/>)
            }
            </Form.Item>
        );
    }

    getSurvey() {
        let {surveyNum, curArt:{votes}, form: {getFieldDecorator}} = this.props;
        if(surveyNum === 0) {
            return null;
        }
        if(Jt.array.isEmpty(votes)) {
            votes = [{
                title: '',
                date: null,
                type: '1',
                isShowResult: true,
                options: [{
                    title: ''
                }]
            }];
        }
        return (
            <Collapse bordered={false} defaultActiveKey={['survey']}>
                <Collapse.Panel key="survey" header="调查调查调查调查">
                    <Form.Item>
                    {
                        getFieldDecorator('votes', {
                            initialValue: {surveys: votes}
                        })(
                            <SurveyModule layout={{labelCol: {span: 3}, wrapperCol: {span: 20}}} wrapClassName="m-survey" />
                        )
                    }
                    </Form.Item>
                </Collapse.Panel>
            </Collapse>
        );
    }

    fromAlbum({fileList=[]}) {
        const images = [];
        fileList.forEach(item => {
            if(item.url) {
                images.push({
                    index: +item.key,
                    image: item.url,
                    description: item.dec
                });
            }
        });
        return images;
    }

    toAlbum() {
        const {curArt:{articleData=[]}} = this.props;
        const imageJson = articleData.imageJson || [];
        if(Jt.array.isEmpty(imageJson)) {
            return {fileList: []};
        }
        const fileList = [];
        imageJson.forEach((item, index) => {
            fileList.push({
                key: index,
                url: item.image,
                dec: item.description
            });
        });
       // return {fileList:[]}
       return {fileList};
    }
    albumChange = (data)=>{

        const {curArt,artNewStatus,updateState,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        data.fileList.forEach((item,index)=>{
            item.image = item.url;
            item.description = item.dec;
        });
        if(!values.articleData){
            values.articleData = {}
        }
        values.articleData.imageJson = data.fileList;
        //updateState({curArt: {...values}})
    }
    getAlbum = ()=> {
        const {albumNum, curArt, form:{getFieldDecorator},artNewStatus} = this.props;
        const {fileList_three,previewVisible_three,previewImage_three} = this.state;
        if(albumNum === 0) {
            return null;
        }
        return (
            <Collapse bordered={false} defaultActiveKey={['album']}>
                <Collapse.Panel key="album" header="图集">
                    <Form.Item>
                    {
                        getFieldDecorator('album', {
                            initialValue: this.toAlbum()
                        })(<AlbumUploader onChange={this.albumChange} />)
                    }
                    </Form.Item>
                    {!artNewStatus.isTailor && artNewStatus.viewType==='banner'?
                    <Form.Item >
                        <Upload
                            action={urlPath.UPLOAD_FILE}
                            listType="picture-card"
                            fileList={fileList_three}
                            onPreview={this.handlePreview_three}
                            onChange={this.handleChange_three}
                            onRemove={this.onRemove_three}
                            accept="image/png,image/jpeg,image/jpeg,image/gif"
                        >
                            {/* {fileList_three.length >= 4 ? null : this.setBtn('人工剪裁缩略图')} */}
                            {fileList_three.length < 0 ? null : this.setBtn('人工剪裁缩略图')}
                        </Upload>
                        <span style={{width: '300px',color: '#f66',textAlign: 'left',lineHeight: "100px"}}>请至少上传4张1:2的缩略图供信息流中展示</span>
                        <Modal visible={previewVisible_three} footer={null} onCancel={this.handleCancel_three}>
                            <img alt="example" style={{ width: '100%' }} src={previewImage_three} />
                        </Modal>
                    </Form.Item>:null
                    }
                </Collapse.Panel>
            </Collapse>
        );
    }

    toMedia(type) {
        const {curArt:{articleData={}}} = this.props;
        const mediaJson = type === 'video' ? articleData.videoJson : articleData.audioJson;
        if(Jt.array.isEmpty(mediaJson)) {
            return {id: '', url: '', coverImg: ''};
        }
        const media = mediaJson[0];
        return {
            id: media.id,
            url: media.resources[0].url,
            coverImg: media.image
        };
    }

    getAudio() {
        const {audioNum, curArt, form:{getFieldDecorator}} = this.props;
        if(audioNum === 0) {
            return null;
        }
        let file = {}
         if (curArt.articleData && curArt.articleData.audioJson) {
             file = curArt.articleData.audioJson[0]
         }
         const audioProps = {
             file
         }
        return (
            <Collapse bordered={false} defaultActiveKey={['audio']}>
                <Collapse.Panel key="audio" header="音频">
                    <Form.Item>
                    {
                        getFieldDecorator('audio', {
                            initialValue: this.toMedia('audio')
                        })(<MediaUploader {...audioProps}  onChange={this.mediaChange} type="audio" />)
                    }
                    </Form.Item>
                </Collapse.Panel>
            </Collapse>
        );
    }

    getVideo() {
        const {videoNum, curArt, form:{getFieldDecorator}} = this.props;
        if(videoNum === 0) {
            return null;
        }
        let file = {}
        if (curArt.articleData && curArt.articleData.videoJson) {
            file = curArt.articleData.videoJson[0]
        }
        const videoProps = {
            file
        }
        return (
            <Collapse bordered={false} defaultActiveKey={['video']}>
                <Collapse.Panel key="video" header="视频">
                    <Form.Item>
                    {
                        getFieldDecorator('video', {
                            initialValue: this.toMedia('video')
                        })(<MediaUploader {...videoProps} onChange={this.mediaChange} type="video" />)
                    }
                    </Form.Item>
                </Collapse.Panel>
            </Collapse>
        );
    }

    mediaChange = (data)=>{
        const {curArt, updateState} = this.props
        this.setState({
            mediaUrl:data.url
        })
        
        let articleData
        if(curArt.type=='video') {
            articleData = {
                videoJson: [{
                    id: data.id,
                    image: data.coverImg,
                    resources:[{
                        url: data.url
                    }],
                    title:data.title
                }
                ]
            }
        }else if(curArt.type=='audio') {
            articleData = {
                audioJson: [{
                    id: data.id,
                    image: data.coverImg,
                    resources:[{
                        url: data.url
                    }]
                }
                ]
            }
        }
        let curt_2 =  Object.assign({},curArt.articleData,articleData)
        const curArt_1 = Object.assign(curArt, {
            articleData: curt_2
        })
       
        updateState({
            curArt: {...curArt_1}
        });
    }
    remove_four(file){

        const {curArt,artNewStatus,updateState,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        if((file.response && file.response.code==0) || file.status=='removed'){
            let thumbnailArr_four = artNewStatus.thumbnailArr_four;
            let fileList_four = artNewStatus.fileList_four;
            const imgUrl = file.response?file.response.data.fileUrl: file.url
            const index = thumbnailArr_four.indexOf(imgUrl);
            if(index>-1){
                thumbnailArr_four.splice(index,1)
                fileList_four.splice(index,1)
                const artNewStatus_new = Object.assign(artNewStatus,{thumbnailArr_four:thumbnailArr_four,fileList_four:fileList_four})
                updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
                let list = this.state.fileList_four.filter((item,index)=>{
                    return item.uid != file.uid;
                });
            this.setState({'fileList_four':list});
            }
        }else if(file.response && file.response.code==-2){
            this.tipError("上传失败,请刷新重试")
        }
        return true

    }
    beforeUpload_four(file){
        if(file.type.indexOf('mp4')<0){
            this.tipError("格式不正确")
            return false;
        }
        let total = 0;
        this.state.fileList_four.forEach((item,index)=>{
            total+=item.size;
        });
        if((total/1024/1024)>600){
            this.tipError("视频总量不能大于600MB")
            return false
        }
        if((file.size/1024/1024)>300){
            this.tipError("单个视频不能大于300MB")
            return false
        }

    }
    getVideos() {

        const {fileList_four,fileList_five,previewVisible_five,previewImage_five} = this.state;
        const props = {
            //accept:"video/mp4",
            action: urlPath.UPLOAD_FILES,
            onChange: this.handleChange_four,
            multiple: true,
        };
       
        return (
            <Collapse bordered={false} defaultActiveKey={['1']}>
                <Collapse.Panel header="上传多个视频" key="1">
                    <Form.Item>
                        <Upload {...props} accept="video/mp4" fileList={fileList_four} beforeUpload={this.beforeUpload_four.bind(this)} onRemove={this.remove_four.bind(this)}>
                            <Button>
                                <Icon type="upload" /> Upload
                            </Button>
                        </Upload>
                    </Form.Item>
                    <Form.Item>
                        <Upload
                            action={urlPath.UPLOAD_FILE}
                            listType="picture-card"
                            fileList={fileList_five}
                            onPreview={this.handlePreview_five}
                            onChange={this.handleChange_five}
                            onRemove={this.onRemove_five}
                            accept="image/png,image/jpeg,image/jpeg,image/gif"
                            //
                        >
                            {this.setBtn('视频封面')}
                        </Upload>
                        <span style={{width: '300px',color: '#f66',textAlign: 'left',lineHeight: "100px"}}>请上传一张尺寸为16:9的图片</span>
                        <Modal visible={previewVisible_five} footer={null} onCancel={this.handleCancel_five}>
                            <img alt="example" style={{ width: '100%' }} src={previewImage_five} />
                        </Modal>
                    </Form.Item>
                </Collapse.Panel>
            </Collapse>
        );
    }
    checkedHandle = (data)=>{
        const {curArt, updateState} = this.props
       
        
       
       
        updateState({
            curArt:{
                ...curArt,
                relationArticles:data
            }
           
        });
        this.setState({
            visibleRecommond:false
        })
        // this.setState({
        //     articleId:data.id,
        //     articleTitle:data.title,
        //     visible:false
        // })
    }
    checkedHandleCancel(){
        this.setState({
            visibleRecommond:false
        })
    }
    removeRecommond(data){
        const {curArt, updateState} = this.props
       
        let list  = curArt.relationArticles? curArt.relationArticles.filter((item)=>{
                return item.id !=data.id
        }):[]
       
       
        updateState({
            curArt:{
                ...curArt,
                relationArticles:list
            }
           
        });
    }
    getRecommondList() {

        const {visibleRecommond} = this.state;
        const {form:{getFieldDecorator}} = this.props;
       let list  = this.props.curArt.relationArticles
       let listids = (list?list:[]).map((item)=>{
            return item.id
        })
        return (
            <Collapse bordered={false} defaultActiveKey={['1']}>
                <Collapse.Panel header="相关文章" key="1">
                    <Form.Item>
                        {
                            (list?list:[]).map((item)=>{
                                return <p key={item.id} style={{'padding':'1%'}}> <Alert
                                message={item.title}
                                type="info"
                                closable={true}
                                onClose={()=>{this.removeRecommond(item)}}
                              /></p>
                            })
                        }
                        
                        <Button onClick={()=>{this.setState({'visibleRecommond':true})}}>添加文章</Button> 
                    </Form.Item>
                    <Form.Item>
                       
                          { getFieldDecorator('relationIds', {
                                initialValue:listids
                            })(
                                <Input type='hidden' />
                            )
                            } 
                        
                    </Form.Item>
                    {
                        visibleRecommond?<RecommondModal
                        visible={visibleRecommond}
                        selectedRowKeys = {listids}
                        selectRows = {this.props.curArt.relationArticles?this.props.curArt.relationArticles:[]}
                        checkedHandle={this.checkedHandle.bind(this)}
                        handleCancel={this.checkedHandleCancel.bind(this)}
                    />:null
                    }
                    
                   
                </Collapse.Panel>
            </Collapse>
        );
    }

    getDelBtn() {
        const {curArt, deleteArt} = this.props;
        if(curArt.id && curArt.delFlag == 1) {
            return (
                <VisibleWrap permis="edit:delete">
                    <Popconfirm title="确定删除吗？" onConfirm={() => deleteArt(curArt.id)}>
                        <Button type="danger" className="del-btn">删除</Button>
                    </Popconfirm>
                </VisibleWrap>
            )
        }
        return null;
    }

    fromMetas(values) {
        const metas = [];
        const {curArt:{fields}} = this.props;
        if(fields && fields.length > 0) {
            fields.forEach(field => {
                metas.push({
                fieldCode: field.slug,
                fieldValue: typeof(values[field.slug])=='string'?values[field.slug]:JSON.stringify(values[field.slug])
            });
            delete values[field.slug];
        });
        }
        delete values.fields;
        return metas;
    }

    getMetaField(field) {
        const type = field.type;
        const props = {...field};
        delete props.defaultValue;
        const fmap= {
            'input': <FInput {...props}/>,
            'select': <FSelect {...props}/>,
            'checkbox': <FCheckbox {...props}/>,
            'checkboxes': <FCheckboxes {...props}/>,
            'date': <FDate {...props}/>,
            'radio': <FRadio {...props}/>,
            'textarea': <FTextarea {...props}/>,
            'image': <FImage {...props}/>,
            'email': <FInput {...props}/>,
            'url': <FInput {...props}/>,
            'phone': <FInput {...props}/>,
            'numeric': <FInput {...props}/>
        };

        return fmap[type];
    }

    getMetaInitVal(field, metas) {
        let {type, slug, defaultValue, simpleOrMore, logic, options} = field;
        logic = logic ? JSON.parse(logic) : {};
        options = options ? JSON.parse(options) : [];
        if(metas[slug]) {
            return metas[slug];
        }
        else if(type === 'radio') {
            for(let i = 0, len = options.length; i < len; i++) {
                if(options[i].isDefault) {
                    return options[i].value;
                }
            }
        }
        else if(type === 'checkbox') {
            if(logic.defChecked) {
                return logic.checkedVal;
            }
        }
        else if(type === 'checkboxes') {
            const value  = [];
            options.forEach(option => {
                if(option.isDefault) {
                value.push(option.value);
            }
        });
            return JSON.stringify(value);
        }

        if(simpleOrMore == 2) {
            return JSON.stringify([defaultValue]);
        }
        return defaultValue;
    }

    getMetas() {
        let {curArt, curArt: {fields, metas}, form: {getFieldDecorator}} = this.props;
        if(Jt.array.isEmpty(fields)) {
            return null;
        }
        fields = fields.map(field => {
            const fc = this.getMetaField(field);
            if(!fc) {
                return null;
            }
            return (
                <FormItem key={field.id}>
                    {
                        getFieldDecorator(field.slug, {
                            initialValue: this.getMetaInitVal(field, metas)
                        })(fc)
                    }
                </FormItem>
            );
        });
        return (
            <Collapse bordered={false} defaultActiveKey={['custom-field']}>
                <CollapsePanel key="custom-field" header="自定义信息">
                    {fields}
                </CollapsePanel>
            </Collapse>
        );
    }

    validateMetas(metas) {
        const {curArt:{fields}} = this.props;
        if(!fields || fields.length === 0) {
            return true;
        }
        const fieldMap = {};
        fields.forEach(field => {
            fieldMap[field.slug] = field;
        });

        for(let i = 0, len = metas.length; i < len; i++) {
            const {type, name, simpleOrMore, validate} = fieldMap[metas[i].fieldCode];
            let value = metas[i].fieldValue;
            if(!validate) {
                return true;
            }
            if(simpleOrMore == 2) {
                value = JSON.parse(value);
            }
            const {required={}, format={}} = JSON.parse(validate);
            if(required.active) {
                const msg = required.msg || `${name}不能为空`;
                if(!value) {
                    this.tipError(msg || '');
                    return false;
                }
                else if(simpleOrMore == 2) {
                    for(let j = 0, len = value.length; j < len; j++) {
                        if(!value[j]) {
                            this.tipError(msg || '');
                            return false;
                        }
                    }
                }
            }

            if(format.active) {
                const msg = format.msg || `${name}输入格式不正确`;
                const vFns = {
                    'email': Jt_V.isEmail,
                    'url': Jt_V.isUrl,
                    'phone': Jt_V.isTel,
                    'numeric': Jt_V.isNumber
                };
                const vFn = vFns[type];
                if(vFn) {
                    if(simpleOrMore == 2) {
                        for(let j = 0, len = value.length; j < len; j++) {
                            if(!vFn(value[j])) {
                                this.tipError(msg);
                                return false;
                            }
                        }
                    }
                    else if(!vFn(value)) {
                        this.tipError(msg);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    changeFileList (file, num) {
        setTimeout(() => {
            if (num === 1) {
                this.onRemove(file)
            } else {
                this.onRemove_two(file)
            }
        }, 0)
    }
    catgChg(categoryId, label, extra) {
        const {updateArtCatg,curArt, form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        values.categoryId=categoryId;
        updateArtCatg({categoryId,curArt:values});
        const { form: { setFieldsValue } } = this.props
        const { catgsModelId } = this.state
        if (catgsModelId.indexOf(categoryId || curArt.categoryId) > -1) {
            setFieldsValue({block: '2'});
        }
    }

    typeChg(value) {
        const {curArt, updateState,updateMediaType,artNewStatus, form:{getFieldsValue, setFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        let threeFlag = false;
        let viewType = artNewStatus.viewType
        if(value==='video' || value === 'audio'){
            updateMediaType(value);
            threeFlag = false;
           
            if (viewType === 'three') {
                viewType = 'normal'
                setFieldsValue({viewType: 'normal'})
            }
        }else{
            threeFlag = true;
        }
        curArt.imageUrl = '';
        curArt.realImageUrl = '';
        const artNewStatus_new = Object.assign(artNewStatus,{threeFlag, viewType})
        updateState({
            surveyNum: 0,
            albumNum: value === 'image' ? 1 : 0,
            videoNum: value === 'video' ? 1 : 0,
            audioNum: value === 'audio' ? 1 : 0,
            curArt: {...values, type: value},
            artNewStatus:artNewStatus_new
        });
    }

    toggleListTt(e) {
        const {curArt, updateState, form:{getFieldsValue, setFieldsValue}} = this.props;
         let curArt_1 = { ...curArt, ...getFieldsValue()};
         let isShowListTitle = curArt.isShowListTitle
         let listTitleValue = ''
         if (!isShowListTitle) {
             const title = document.getElementById('title')
             listTitleValue = title.value
         }

         const curArt_2 = Object.assign(curArt_1, {
            isShowListTitle: !isShowListTitle,
            listTitle: listTitleValue
        })
       
        updateState({
            curArt: {...curArt_2}
        });
    }

    toggleSurvey(checked) {
        const {curArt, updateState, form:{getFieldsValue}} =  this.props;
        const values = { ...curArt, ...getFieldsValue()};
        if(checked) {
            updateState({surveyNum: 1,curArt: {...values, votes: []}});
        }
        else {
            updateState({
                surveyNum: 0,
                curArt: {...values, votes: []}
        });
        }
    }
    toggleOriginal(checked){
        const {curArt,updateState, form:{getFieldsValue}} =this.props;
        const values = { ...curArt, ...getFieldsValue()};
        if(checked){
            values.doc_original_flag=1;
            updateState({curArt:{...values}})
        }else{
            values.doc_original_flag=0;
            updateState({curArt:{...values}})
        }
    }
    validateValues(values) {
        const {catgs} = this.props;
        const {type, inSubject, articleData: {imageJson}} = values;
        if(!inSubject) {
            if(!values.categoryId) {
                this.tipError('请选择归属栏目');
                return false;
            }
            if(!Jt.tree.isLeaf(catgs, values.categoryId)) {
                this.tipError('请选择子级栏目');
                return false;
            }
        }

        if(!values.title) {
            this.tipError('请输入正文标题');
            return false;
        }

        if((type === 'common'||type === 'help') && values.votes && values.votes.length > 0) {
            const votes = values.votes;
            for(let i = 0, len1 = votes.length; i < len1; i++) {
                if(!votes[i].title) {
                    this.tipError('请填写调查标题');
                    return false;
                }
                const options = votes[i].options;
                for(let j = 0, len2 = options.length; j < len2; j++) {
                    if(!options[j].title) {
                        this.tipError('请填写调查选项');
                        return false;
                    }
                }
            }
        }

        else if(type === 'image') {
            if(!values.imageUrl) {
                this.tipError('请选择缩略图');
                return false;
            }
            if(imageJson.length < 1) {
                this.tipError('图集请至少选择一张图片');
                return false;
            }
        }
        else if(type === 'audio' && values.mediaIds.length === 0 && !values.audioUrl) {
            this.tipError('请选择音频');
            return false;
        }
        else if(type === 'video' && values.mediaIds.length === 0 && !values.videoUrl) {
            this.tipError('请选择视频');
            return false;
        }
        else if(type === 'link' && !values.link) {
            this.tipError('请输入跳转链接');
            return false;
        }else if(type === 'link' && values.link) {
            let Expression=/http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?/;
            let objExp=new RegExp(Expression);
            if(objExp.test(values.link) == false) {
                this.tipError('请输入以http或https为开头的跳转链接');
                return false;
            }
        }

        const metaIsValidate =  this.validateMetas(values.metas);
        if(!metaIsValidate) {
            return false;
        }
        return true;
    }

    typeChange = (value)=>{
        const {curArt,updateState,artNewStatus,form:{getFieldsValue}} = this.props;
        const { fileList, fileList_two } = this.state
        const values = { ...curArt, ...getFieldsValue()};
        let fileList_length = 1;
        if(value=='three'){
            fileList_length = 3
        }else{
            fileList_length = 1
            if (fileList.length > 1) {
                fileList.map((item, index) => {
                    item.status = 'removed'
                    if (index !== 0) {
                        this.changeFileList(item, 1)
                    }
                })
            }
            if (fileList_two.length > 1) {
                fileList_two.map((item, index) => {
                    item.status = 'removed'
                    if (index !== 0) {
                        this.changeFileList(item, 2)
                    }
                })
            }
        }
        this.setState({fileList_length});
        if(!!values.audio) {
            values.articleData.audioJson=[{id: values.audio.id,resources:[{url: values.audio.url}] , image: values.audio.coverImg}]
        }
        if(!!values.video) {
            values.articleData.videoJson=[{id: values.video.id,resources:[{url: values.video.url}] , image: values.video.coverImg}]
        }
        const artNewStatus_new = Object.assign(artNewStatus,{viewType:value})
        updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
    }
    autoCutChange = (checked)=>{
        const {curArt,updateState,artNewStatus,form:{getFieldsValue}} = this.props;

        // const values = { ...curArt, ...getFieldsValue(), doc_original_flag: curArt.doc_original_flag};
        const values = { ...curArt, ...getFieldsValue()};
        if(checked){
        }else{
            this.setState({fileList_two : [],thumbnailArr : []})
        }
        const artNewStatus_new = Object.assign(artNewStatus,{isTailor:checked})
        if(!!values.audio) {
            values.articleData.audioJson=[{id: values.audio.id,resources:[{url: values.audio.url}] , image: values.audio.coverImg}]
        }
        if(!!values.video) {
            values.articleData.videoJson=[{id: values.video.id,resources:[{url: values.video.url}] , image: values.video.coverImg}]
        }
        updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
    }
    voiceChange = (checked)=>{
        const {curArt,updateState,artNewStatus,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        const artNewStatus_new = Object.assign(artNewStatus,{isVoice:checked})
        if(!!values.audio) {
            values.articleData.audioJson=[{id: values.audio.id,resources:[{url: values.audio.url}] , image: values.audio.coverImg}]
        }
        if(!!values.video) {
            values.articleData.videoJson=[{id: values.video.id,resources:[{url: values.video.url}] , image: values.video.coverImg}]
        }
        updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
    }

    topPicChange = (checked)=>{
        const {curArt,updateState,artNewStatus,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        const artNewStatus_new = Object.assign(artNewStatus,{isShowTopImg:checked})
        if(!!values.audio) {
            values.articleData.audioJson=[{id: values.audio.id,resources:[{url: values.audio.url}] , image: values.audio.coverImg}]
        }
        if(!!values.video) {
            values.articleData.videoJson=[{id: values.video.id,resources:[{url: values.video.url}] , image: values.video.coverImg}]
        }
        updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
    }

    handlePreview = (file) => {
        this.setState({
            previewImage: file.url || file.thumbUrl,
            previewVisible: true,
        });
    }
    handleCancel = () => this.setState({ previewVisible: false })
    handleChange = ({ file,fileList }) => {
        const {curArt,artNewStatus,updateState,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        if(file.response && file.response.code==0 && file.status !='removed'){
            const imgArr = artNewStatus.imgArr;
            imgArr.push(file.response.data.fileUrl);
            // console.log('xxxaaaa',imgArr,file.response.data.fileUrl)
            const artNewStatus_new = Object.assign(artNewStatus,{imgArr:imgArr,fileList:fileList})
            if(!!values.audio) {
                values.articleData.audioJson=[{id: values.audio.id,resources:[{url: values.audio.url}] , image: values.audio.coverImg}]
            }
            if(!!values.video) {
                values.articleData.videoJson=[{id: values.video.id,resources:[{url: values.video.url}] , image: values.video.coverImg}]
            }

            curArt.imageUrl = this.praseStrEmpty(curArt.imageUrl)
            let curImgUrl = curArt.imageUrl.split(',')
            curImgUrl.push(file.response.data.fileUrl)
            curImgUrl = curImgUrl.filter(function (s) {
                return s && s.trim();
            });
            curImgUrl = curImgUrl.join(',')
            let curArt_1 = Object.assign(values, {imageUrl: curImgUrl})

            // updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
            updateState({artNewStatus:artNewStatus_new,curArt: {...curArt_1}})
            // {fileList.length >= fileList_length ? null : this.setBtn('上传缩略图')}
        }else if(file.response && file.response.code==-2){
            this.tipError("上传图片失败,请刷新重试");
            this.state.fileList.pop();
            this.setState({'fileList':this.state.fileList})
        } else if(file.response && file.response.code==-1){

            this.tipError(file.response.msg);
            this.state.fileList.pop();
            this.setState({'fileList':this.state.fileList})
        } else if(file.status =='removed'){
            this.setState({'fileList':this.state.fileList})
        }
        else{
            this.setState({fileList})
        }
    }

    onRemove = (file)=>{
        const {curArt,artNewStatus,updateState,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        if((file.response && file.response.code==0) || file.status=='removed'){
            let imgArr = artNewStatus.imgArr;
            let fileList = artNewStatus.fileList;
            const imgUrl = file.response?file.response.data.fileUrl: file.url
            const index = imgArr.indexOf(imgUrl);

            let curArt_1 = {}
            if(curArt&&curArt.imageUrl) {
                let imgCurArr = []
                imgCurArr = curArt.imageUrl.split(',')
                if(imgCurArr.indexOf(imgUrl) > -1) {
                    imgCurArr.splice((imgCurArr.indexOf(imgUrl)),1)
                    imgCurArr = imgCurArr.join(',')
                    curArt_1 = Object.assign(values,{imageUrl: imgCurArr.length > 0 ? imgCurArr : '' })
                }
            }

            if(index>-1){
                imgArr.splice(index,1)
                fileList.splice(index,1)
                const artNewStatus_new = Object.assign(artNewStatus,{imgArr:imgArr,fileList:fileList})
                // updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
                updateState({artNewStatus:artNewStatus_new,curArt: {...curArt_1}})
            }
        }else if(file.response && file.response.code==-2){
            this.tipError("上传图片失败,请刷新重试")
        }
        return true
    }

    handlePreview_two = (file) => {
        this.setState({
            previewImage_two: file.url || file.thumbUrl,
            previewVisible_two: true,
        });
    }

    praseStrEmpty(str){
        if(!str || str=="undefined" || str=="null"){
        return "";
    }
        return str;
    }
    handleChange_two = ({ file,fileList }) => {
        const {curArt,artNewStatus,updateState,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        if(file.response && file.response.code==0 && file.status !='removed'){
            const thumbnailArr = artNewStatus.thumbnailArr;
            thumbnailArr.push(file.response.data.fileUrl);
            const artNewStatus_new = Object.assign(artNewStatus,{thumbnailArr:thumbnailArr,fileList_two:fileList})
            if(!!values.audio) {
                values.articleData.audioJson=[{id: values.audio.id,resources:[{url: values.audio.url}] , image: values.audio.coverImg}]
            }
            if(values.video) {
                values.articleData.videoJson=[{id: values.video.id,resources:[{url: values.video.url}] , image: values.video.coverImg}]
            }
            curArt.realImageUrl = this.praseStrEmpty(curArt.realImageUrl)
            let curImgUrl = curArt.realImageUrl.split(',')
            curImgUrl.push(file.response.data.fileUrl)
            curImgUrl = curImgUrl.filter(function (s) {
                return s && s.trim();
            });
            curImgUrl = curImgUrl.join(',')
            let curArt_1 = Object.assign(values, {realImageUrl: curImgUrl})

            // updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
            updateState({artNewStatus:artNewStatus_new,curArt: {...curArt_1}})


        }
        else if(file.response && file.response.code==-1){

            this.tipError(file.response.msg);
            this.state.fileList_two.pop();
            this.setState({'fileList_two':this.state.fileList_two})
        }
        else if(file.response && file.response.code==-2){
            this.tipError("上传图片失败！,请刷新重试")
        }else{
            this.setState({ fileList_two:fileList})
        }
    }
    handleCancel_two = () => this.setState({ previewVisible_two: false })
    onRemove_two = (file)=>{
        const {curArt,artNewStatus,updateState,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        if((file.response && file.response.code==0) || file.status=='removed'){
            let thumbnailArr = artNewStatus.thumbnailArr;
            let fileList_two = artNewStatus.fileList_two;
            const imgUrl = file.response?file.response.data.fileUrl: file.url
            const index = thumbnailArr.indexOf(imgUrl);

            let curArt_1 = {}
            if(curArt&&curArt.realImageUrl) {
                let imgCurArr = []
                imgCurArr = curArt.realImageUrl.split(',')
                if(imgCurArr.indexOf(imgUrl) > -1) {
                    imgCurArr.splice((imgCurArr.indexOf(imgUrl)),1)
                    imgCurArr = imgCurArr.join(',')
                    curArt_1 = Object.assign(values,{realImageUrl: imgCurArr.length > 0 ? imgCurArr : '' })
                }
            }

            if(index>-1){
                thumbnailArr.splice(index,1)
                fileList_two.splice(index,1)

                const artNewStatus_new = Object.assign(artNewStatus,{thumbnailArr:thumbnailArr,fileList_two:fileList_two})
                // updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
                updateState({artNewStatus:artNewStatus_new,curArt: {...curArt_1}})
            }
        }else if(file.response && file.response.code==-2){
            this.tipError("上传图片失败,请刷新重试")
        }
        return true
    }

    handlePreview_three = (file) => {
        this.setState({
            previewImage_three: file.url || file.thumbUrl,
            previewVisible_three: true,
        });
    }
    handleChange_three = ({ file,fileList }) => {
        const {curArt,artNewStatus,updateState,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        if(file.response && file.response.code==0 && file.status !='removed'){
            const thumbnailArr_three = artNewStatus.thumbnailArr_three;
            thumbnailArr_three.push(file.response.data.fileUrl);
            if(!!values.audio) {
                values.articleData.audioJson=[{id: values.audio.id,resources:[{url: values.audio.url}] , image: values.audio.coverImg}]
            }
            if(!!values.video) {
                values.articleData.videoJson=[{id: values.video.id,resources:[{url: values.video.url}] , image: values.video.coverImg}]
            }
            const artNewStatus_new = Object.assign(artNewStatus,{thumbnailArr_three:thumbnailArr_three,fileList_three:fileList})
            updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
        }else if(file.response && file.response.code==-2){
            this.tipError("上传图片失败,请刷新重试")
        }else{
            this.setState({ fileList_three:fileList})
        }
    }
    handleCancel_three = () => this.setState({ previewVisible_three: false })
    onRemove_three = (file)=>{
        const {curArt,artNewStatus,updateState,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        if((file.response && file.response.code==0) || file.status=='removed'){
            let thumbnailArr_three = artNewStatus.thumbnailArr_three;
            let fileList_three = artNewStatus.fileList_three;
            const imgUrl = file.response?file.response.data.fileUrl: file.url
            const index = thumbnailArr_three.indexOf(imgUrl);
            if(index>-1){
                thumbnailArr_three.splice(index,1)
                fileList_three.splice(index,1)
                const artNewStatus_new = Object.assign(artNewStatus,{thumbnailArr_three:thumbnailArr_three,fileList_three:fileList_three})
                updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
            }
        }else if(file.response && file.response.code==-2){
            this.tipError("上传图片失败,刷新重试")
        }
        return true
    }

    handleChange_four = (info) => {
        // console.log("info",info)
        const {curArt,artNewStatus,updateState,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        let fileList = info.fileList;
        fileList = fileList.slice(0);
       // console.log(fileList,'删除视频');//上传多个视频的时候所设计的名称
        fileList = fileList.map((file) => {
            if (file.response&&file.response.code!=-1) {
                file.url = file.response.url;
            }
            return file;
        });
        //过滤上传成功的文件
        /*fileList = fileList.filter((file) => {
            if (file.response) {
                return file.response.status === 'success';
            }
            return true;
        });*/
        if(info.file.response && info.file.response.code==0){
            const artNewStatus_new = Object.assign(artNewStatus,{fileList_four:info.fileList})
            if(!!values.video) {
                values.articleData.videoJson=[{id: values.video.id,resources:[{url: values.video.url}] , image: values.video.coverImg}]
            }
            updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
        }else if(info.file.response && info.file.response.code==-2){
            this.tipError("上传图片失败,请刷新重试")
        }
        else if(info.file.response && info.file.response.code==-1){
            this.tipError(info.file.response.msg)
            let arr = this.state.fileList_four.filter((item)=>{
                return item.name != info.file.name
            })
            this.setState({'fileList_four':arr});
        }
        else if(!info.file.response&&!info.file.status){

        }
        else{
            this.setState({ fileList_four:fileList });
        }
    }

    handlePreview_five = (file) => {
        this.setState({
            previewImage_five: file.url || file.thumbUrl,
            previewVisible_five: true,
        });
    }
    handleChange_five = ({ file,fileList }) => {
        const {curArt,artNewStatus,updateState,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        if(file.response && file.response.code==0 && file.status !='removed'){
            const thumbnailArr_five = artNewStatus.thumbnailArr_five?artNewStatus.thumbnailArr_five:[];
            thumbnailArr_five.push(file.response.data.fileUrl);
            const artNewStatus_new = Object.assign(artNewStatus,{thumbnailArr_five:thumbnailArr_five,fileList_five:fileList})
            if(!!values.video) {
                values.articleData.videoJson=[{id: values.video.id,resources:[{url: values.video.url}] , image: values.video.coverImg}]
            }
            updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
        }
        else if(file.response && file.response.code==-1){
            this.tipError(file.response.msg)
            let arr = this.state.fileList_five.filter((item)=>{
                return item.name != file.name
            })
            this.setState({'fileList_five':arr});
        }
        else if(file.response && file.response.code==-2){
            this.tipError("上传图片失败,请刷新重试")
            this.state.fileList_five.pop();
            this.setState({'fileList_five':this.state.fileList_five})
        }else{
            this.setState({ fileList_five:fileList},()=>{
            })
        }
    }
    handleCancel_five = () => this.setState({ previewVisible_five: false })
    onRemove_five = (file)=>{
        const {curArt,artNewStatus,updateState,form:{getFieldsValue}} = this.props;
        const values = { ...curArt, ...getFieldsValue()};
        if((file.response && file.response.code==0) || file.status=='removed'){
            let thumbnailArr_five = artNewStatus.thumbnailArr_five;
            let fileList_five = artNewStatus.fileList_five;
            const imgUrl = file.response?file.response.data.fileUrl: file.url
            const index = thumbnailArr_five.indexOf(imgUrl);
            if(index>-1){
                thumbnailArr_five.splice(index,1)
                fileList_five.splice(index,1)
                const artNewStatus_new = Object.assign(artNewStatus,{thumbnailArr_five:thumbnailArr_five,fileList_five:fileList_five})
                updateState({artNewStatus:artNewStatus_new,curArt: {...values}})
            }
        }else if(file.response && file.response.code==-2){
            this.tipError("上传图片失败,请刷新重试")
        }
        return true
    }

    setBtn = (text)=>{
        return (
            <div>
                <Icon type="plus" />
                <div className="ant-upload-text">{text}</div>
            </div>
        )
    }
    getImg = (imgArr)=>{
        if(Array.prototype.isPrototypeOf(imgArr)){
            let imgUrl = ''
            imgArr.forEach(function (item) {
                imgUrl += ','+item;
            })
            imgUrl = imgUrl.substring(1);
            return imgUrl
        }else{
            console.log('参数类型错误')
        }
    }
    editorHtml = (string,type)=>{
        string = !!string?string:"";
        let self = this;
        return new Promise((resolve, reject) => {
            var reg = new RegExp(/(⭐⭐⭐video⭐⭐⭐<br\/>)+/g);
            let html = '';
            const {artNewStatus,curArt} = this.props;
            const str = '⭐⭐⭐video⭐⭐⭐<br/>'
            const times =string&&string.indexOf(str)>-1? string.split(str).length-1:0;
            const thumbnailArr_five = artNewStatus.thumbnailArr_five;
        
            // return
            if((curArt.type=='video' ||curArt.type=='common'||curArt.type=='help') ){
            // if((curArt.type=='video' ||curArt.type=='common') && artNewStatus.fileList_four.length!=0){
                if(artNewStatus.fileList_four.length != artNewStatus.thumbnailArr_five.length){
                    self.tipError('视频个数和视频封面个数不一致')
                    resolve({html:"",flag:false});
                }
                else if(times==0&&artNewStatus.fileList_four.length==0){
                    html = times>0?string.replace(reg,''):string;
                    resolve({html,flag:true});
                }
                else if(times!=0&&artNewStatus.fileList_four.length!=0&&artNewStatus.fileList_four.length ==times){
                    this.getUploadState().then(function (data) {
                        if(data.status){
                            var reg1 = new RegExp(/(⭐⭐⭐video⭐⭐⭐<br\/>)/g);
                            html = string.replace(reg1,'⭐⭐⭐video⭐⭐⭐<br\/>');
                            data.hdUrlList.forEach(function (item,i) {
                                let video=''
                                if(typeof item =='string'){
                                    if(html.substr(html.indexOf('⭐⭐⭐video⭐⭐⭐<br/>')-3,3)=='<p>'){
                                        video = '<video style="width:100%; object-fit:fill" '+(type=='save'?"":"controls=controls")+' poster='+thumbnailArr_five[i]+' src='+item+' webkit-playsinline="true" playsinline="true" x5-playsinline="true" x-webkit-airplay="allow"></video></p><p>'
                                    }else{
                                        video = '</p><p><video style="width:100%;object-fit:fill;" '+(type=='save'?"":"controls=controls")+' poster='+thumbnailArr_five[i]+' src='+item+' webkit-playsinline="true" playsinline="true" x5-playsinline="true" x-webkit-airplay="allow"></video></p><p>'
                                    }
                                }else{
                                    if(html.substr(html.indexOf('⭐⭐⭐video⭐⭐⭐<br/>')-3,3)=='<p>'){
                                        video = '<video '+(type=='save'?"":"controls=controls")+' style="width:100%;object-fit:fill" poster='+thumbnailArr_five[i]+' src='+item+' webkit-playsinline="true" playsinline="true" x5-playsinline="true" x-webkit-airplay="allow"></video></p><p>'
                                    }else{
                                        video = '</p><p><video '+(type=='save'?"":"controls=controls")+' style="width:100%;object-fit:fill" poster='+thumbnailArr_five[i]+' src='+item+' webkit-playsinline="true" playsinline="true" x5-playsinline="true" x-webkit-airplay="allow"></video></p><p>'
                                    }
                                }
                                html = html.replace('⭐⭐⭐video⭐⭐⭐<br/>',video)
                            })
                            resolve({html,flag:true,fileList_four:data.hdUrlList});
                        }else{
                            self.tipError('视频转码未完成，请稍后重试')
                            resolve({html:"",flag:false});
                        }
                    })
                }else{
                    self.tipError('视频标记和上传视频个数不一致')
                    resolve({html:"",flag:false});
                }
            }else{
                html = times>0?string.replace(reg,''):string;
                resolve({html,flag:true});
            }
        })
    }
    getEditor(string){
        if(!string)return
        const reg = new RegExp(/<p><video.*?<\/video><\/p>/g);
        var html = string.replace(reg,'⭐⭐⭐video⭐⭐⭐<br/>')
        return html
    }
    getMoreVideos =(values,fileList_four)=>{
        const {artNewStatus} = this.props;
        let moreVideos=[]
        if(!!fileList_four){
            fileList_four.forEach(function (item,i) {
                artNewStatus.fileList_four[i].response.data[0].fileUrl =item;
                moreVideos[i] = {
                    video:artNewStatus.fileList_four[i],
                    image:artNewStatus.thumbnailArr_five[i]
                }
            })
        }
        values.moreVideos = JSON.stringify(moreVideos);
        return values;
    }
    preview(){
        const {curArt,form:{getFieldsValue},artNewStatus} = this.props;
        const values = {articleData: {}, ...curArt, ...getFieldsValue()};
        values.imageUrl = this.getImg(artNewStatus.imgArr); // mediaUrl
        const getMainVideoUrl = this.toMedia('video')
        const getMainAudioUrl = this.toMedia('audio')

        if(!values.content && !values.content.html) {
            return
        }
        this.editorHtml(this.getEditor(values.articleData.content),"preview").then(({html,flag})=>{
            if(!flag)return
            var top = '';
            //curArt.title 标题 curArt.authors 作者
            if(curArt.type=='video'){
                top += `<div class=${styles.top}>
                        <video controls="controls" src=${getMainVideoUrl.url}></video>`
            }else if(curArt.type=='audio'){
                top += `<div class=${styles.top}>
                        <audio controls="controls" src=${getMainAudioUrl.url}></audio>`
            }else if(curArt.type=='common'||curArt.type=='help'){
                // const imageSrl = artNewStatus.viewType=='three'?values.imageUrl.split(',')[0]:values.imageUrl
                // top += `<div class=${styles.top}>${imageSrl?`<img src=${imageSrl} />`:""}`
               
                if (values.isShowTopImg) {
                    const imageSrl = artNewStatus.viewType=='three'?values.imageUrl.split(',')[0]:values.imageUrl
                    top += `<div class=${styles.top}>${imageSrl?`<img src=${imageSrl} />`:""}`
                }
            }
            if(curArt.type=='common'||curArt.type=='help'){
                top+=`<div class=${styles.title}>${values.title?values.title:""}</div>
                        <div class=${styles.timeTitle}>
                        ${values.isVoice ?`<img style="width:35px !important;float:left;" src=${readBtn} />`:""}
                        <span class=${styles.timeTitleSpan} class="">${curArt.publishDate["_i"]?curArt.publishDate["_i"]:curArt.publishDate}  ${values.authors} ${values.source}</span>
                        </div>
                    </div>`
                html = top+html;
            }else if(curArt.type!='image'){
                top+=`<div class=${styles.title}>${values.title?values.title:""}</div>
                        <div class=${styles.timeTitle}>
                        <span class=${styles.timeTitleSpan}>${moment(values.publishDate).format(dateFormat)}  ${values.authors}  ${values.source}   </span>
                        </div>
                    </div>`
                html = top+html;
            }
            if(curArt.type=='image'){
                values.articleData.imageJson = this.fromAlbum(values.album);
                values.articleData.imageJson.forEach(function (item,i) {
                    top += `,<img style="margin-top:15%" src=${item.image} /><span class="carousel-text">${item.description?item.description:""}</span>`
                })
            }
            this.props.dispatch({
                type:'article/effect:markSensitiveWord',
                payload:
                    {
                        content:curArt.type=='image'?top.substring(1):html,
                        title:values.title,
                        authors:values.authors,
                        source:values.source,
                        editorContent:values.content.html,
                        id:curArt.id
                    }
            })
        })
    }
    getUploadState = (callback)=>{
        return new Promise((resolve) =>{
            const {artNewStatus,uploadState} = this.props;
            let fileList = "";
            artNewStatus.fileList_four.forEach((item,i)=>{
                fileList += ","+item.response.data[0].fileUrl
            })
            this.props.dispatch({
                type:'article/effect:getUploadState',
                payload:{
                    fileUrls:fileList.substring(1),
                    resolve
                }   
            })
        })
    }
    previewHandleOk = (e) => {
        this.preview();
    }
    previewHandleCancel = (e) => {
        this.props.dispatch({
            type:'article/reducer:update',
            payload:{visible:false,sensitiveWordsVisible:false}
        })
    }
    sensitiveWordsHandleOk=(e)=>{
        this.setState({
            sensitiveWordsVisible:false
        })
    }
    focus = (e)=>{
        // if(e.target.value=='成都日报“锦观”新闻客户端'){
        //     e.target.value = ""
        // }
    }
    getAudioBtn=(type,isTailor,list,length)=>{
        //{fileList_two.length >= fileList_length ? null : this.setBtn('上传缩略图')}
        if(type=='audio' && !isTailor){
            if(list.length==0){
                return this.setBtn('人工剪裁缩略图')
            }else if(list.length==1){
                return this.setBtn('播放控件缩略图')
            }else{
                return null
            }
        }else{
            return list.length >= length ? null : this.setBtn('人工剪裁缩略图')
        }
    }
    labelHandleChange=(value)=>{
        value = value?value:"";
        const {curArt} = this.props
        this.props.dispatch({
            type:'article/reducer:update',
            payload:{
                curArt:{...curArt,tags:value}
            }
        })
    }
    helpStatusHandleChange=(value)=>{
        value = value?value:"";
        const {curArt} = this.props
        this.props.dispatch({
            type:'article/reducer:update',
            payload:{
                curArt:{...curArt,helpStatus:value}
            }
        })
    }
    toppingChange = (value) => {
      this.props.form.setFieldsValue({
        weight: value.target.checked ? 999 : 0,
      });
    }
    tipError(msg){
        if(!this.tip){
            this.tip = message.error(msg,3,()=>{
                this.tip =null
            });
        }
    }
    async onSave() {
        const {surveyNum, curArt,isTailor, saveArt,article, form:{getFieldsValue},artNewStatus,uploadState} = this.props;
        const values = {articleData: {}, ...curArt, ...getFieldsValue()};
       
        let editorHtmlFlag = null
        let a =await this.editorHtml(this.getEditor(values.articleData.content),"save").then(function ({html,flag,fileList_four}) {
            editorHtmlFlag = flag
        });

        if (editorHtmlFlag) {
            await this.setState({flag: false})     
        }
        


        if(this.state.flag == false){
            this.state.flag = true;
            this.setState({flag:true},()=>{


        const {surveyNum, curArt,isTailor, saveArt,article, form:{getFieldsValue},artNewStatus,uploadState} = this.props;
        const values = {articleData: {}, ...curArt, ...getFieldsValue()};
        
        values.isVoice = artNewStatus.isVoice;
        values.realImageUrl= '';
        values.imageUrl = this.getImg(artNewStatus.imgArr);
        if(!artNewStatus.isTailor){
            values.realImageUrl = this.getImg(artNewStatus.thumbnailArr);
        }
        const type = curArt.type;
        const {imgArr,thumbnailArr} = this.state;
        values.sysCode = values.sysCode || 'article';
        values.inSubject = values.inSubject || false;
        /**
         * htmls 改为从百度 uediter 获取
         * @type {[type]}
         */
        // const htmls = this.getEditerContent()
        const htmls = curArt.articleData&&curArt.articleData.content?curArt.articleData.content:''

        if(!Jt.object.isEmpty(htmls)) {
            // console.log(values.content, htmls, 'values.content')
            values.articleData.content = htmls || '';
            // values.articleData.content = values.content.html || '';
        }
        delete values.content;
        if(values.publishDate) {
            values.publishDate = values.publishDate.format(dateFormat);
        }
        if(values.weightDate) {
            values.weightDate = values.weightDate.format(dateFormat);
        }
        if(values.startDate && values.endDate){
            if(!moment(values.startDate).isBefore(values.endDate)){
                this.tipError('开始时间需早于结束时间');
            }
        }
        if(values.startDate) {
            values.startDate = values.startDate.format(dateFormat);
        }
       
        if(values.endDate) {
            values.endDate = values.endDate.format(dateFormat);
        }
        const mediaIds = [];
        if(type === 'common'||type === 'help') {
            if(surveyNum === 1) {
                values.votes = values.votes.surveys || [];
            }
            else {
                delete values.votes;
            }
        }
        else if(type === 'image') {
            values.articleData.imageJson = this.fromAlbum(values.album);
            values.isVoice = false
            delete values.album;
        }
        else if(type === 'audio') {
            if(values.audio.id) {
                mediaIds.push(values.audio.id);
            }
            else {
                values.audioUrl = values.audio.url;
            }
            values.isVoice = false
            values.audioCover = values.audio.coverImg;
            delete values.audio;
        }
        else if(type === 'video') {
            if(values.video.id) {
                mediaIds.push(values.video.id);
            }
            else {
                values.videoUrl = values.video.url;
            }
            values.isVoice = false
            values.videoCover = values.video.coverImg;
            delete values.video;
        }
        values.doc_original_flag==true?values.doc_original_flag=1:values.doc_original_flag=0;
        values.mediaIds = mediaIds;
        values.metas = this.fromMetas(values);


        this.props.form.validateFieldsAndScroll((err, value) => {
            // console.log("---------------1",err,artNewStatus.fileList)
            if (!err) {
                if(values.title==undefined||values.title.length==0||values.title.replace(/\s/gi,'').length==0){
                    const s =this.tipError('请输入标题');
                    this.setState({flag:false})
                    return;
                }
                if(type != 'image'&&values.articleData.content.replace(/\s/gi,'').length==0){
                    // const s =this.tipError('请输入正文');;
                    // return;
                }
                if(artNewStatus.fileList.length !=artNewStatus.imgArr.length){
                    console.log('上传图片----显示图片和保存图片数量不一致');
                }
                if(!artNewStatus.isTailor&&(artNewStatus.fileList_two.length !=artNewStatus.thumbnailArr.length) ){
                    console.log('缩略图----显示图片和保存图片数量不一致');
                }
                if(artNewStatus.viewType=='three'){
                    if(artNewStatus.imgArr.length !=3){
                        this.tipError('三图显示至少传3张图片');
                        this.setState({flag:false})
                        return;
                    }
                }
                if(!artNewStatus.isTailor && values.type!='audio'){
                    if(artNewStatus.imgArr.length !=artNewStatus.thumbnailArr.length){
                        this.tipError('缩略图和剪裁图数量不一致');
                        this.setState({flag:false})
                        return;
                    }
                }
                // if(values.viewType  =="normal") {
                //     if(values.type =='video' || values.type =='audio' || values.type =='common') {
                //         if(artNewStatus.imgArr.length == 0) {
                //             this.tipError('非通栏类型，请上传缩略图');
                //             return
                //         }
                //     }
                //
                // }


                if(values.block  =="1"){
                    if(artNewStatus.imgArr.length == 0) {
                        this.tipError('请上传缩略图');
                        this.setState({flag:false})
                        return
                    }
                }

                if(values.type  =="video"){
                    if(artNewStatus.imgArr.length == 0) {
                        this.tipError('请上传缩略图');
                        this.setState({flag:false})
                        return
                    }
                }
                if(values.viewType  =="normal") {
                    // if(values.type =='video' || values.type =='audio' || values.type =='common'|| values.type =='link') {
                    if( values.type !='common'&& values.type !='link' &&values.type !='help') {
                        if(artNewStatus.imgArr.length == 0) {
                            this.tipError('请上传缩略图');
                            this.setState({flag:false})
                            return
                        }
                    }

                } else if(values.viewType  =="banner"){
                    // values.type =='vedio' ||values.type =='audio' ||
                    if( values.type =='common' ||values.type =='link'||values.type =='help') {
                        if(artNewStatus.imgArr.length == 0) {
                            this.tipError('请上传缩略图');
                            this.setState({flag:false})
                            return
                        }
                    }
                } else {
                    if( values.type =='common' ||values.type =='link'||values.type =='help') {
                        if(artNewStatus.imgArr.length == 0) {
                            this.tipError('请上传缩略图');
                            this.setState({flag:false})
                            return
                        }
                    }
                }

               // return
                if(values.viewType  =="normal" && values.type =='audio' && !artNewStatus.isTailor) {
                    if(artNewStatus.thumbnailArr.length<=0) {
                        this.tipError('请上传人工剪裁缩略图');
                        this.setState({flag:false})
                        return
                    }
                }
                if(values.type=='image'&& artNewStatus.viewType=='banner'&& values.articleData.imageJson.length<4){
                    this.tipError('图集通栏新闻图片至少传4张');
                    this.setState({flag:false})
                    return;
                }
                if(values.type=='image' && artNewStatus.viewType=='banner' && !artNewStatus.isTailor && (values.articleData.imageJson.length !=artNewStatus.thumbnailArr_three.length)){
                // if(values.type=='image'&& !artNewStatus.isTailor && (values.articleData.imageJson.length !=artNewStatus.thumbnailArr_three.length)){
                    this.tipError('图集中通栏新闻图片和剪裁图片不一致');
                    this.setState({flag:false})
                    return;
                }
                const validate = this.validateValues(values);
                if(validate) {
                    if(!values.isShowListTitle){
                        //delete values['listTitle']
                        values.listTitle = values.title
                    }
                    if(values.articleData &&values.articleData.imageJson){
                        values.articleData.imageJson.forEach(function (item,i) {
                            item.realImage = artNewStatus.thumbnailArr_three[i];
                            return item
                        })
                    }
                    const _this = this;
                    this.editorHtml(this.getEditor(values.articleData.content),"save").then(function ({html,flag,fileList_four}) {
                        if(!flag){
                            return
                        }
                        values.articleData.content = html;
                        _this.getMoreVideos(values,fileList_four);
                        if(values.relationIds){

                        }
                        values.isReference= _this.props.isReference;
                        values.type !='video' && values.articleData &&delete values.articleData.videoJson;
                        values.type !='video' && values.articleData &&delete values.articleData.videos;
                        saveArt(JSON.stringify(values));
                    });


                }

            } else {
                console.log("aaaa",err)
            }
        });
            })

        }
    }
    textContentchange = (editer) => {
        this.uediterNodes = editer
        this.uediterNodes.addListener('contentChange', () => {
            // console.log('textContentchange', this.uediterNodes.getContent());
            this.EditChange(this.uediterNodes.getContent())
        })
        this.uediterNodes.addListener('inserthtml1', (a,audios) => {
           console.log("----------", document.querySelector("iframe").contentWindow)
          // audios = document.getElementById("ueditor_0").contentWindow.document.getElementsByTagName('audio')
           //audios = document.querySelector("iframe[id]").contentWindow.document.getElementsByTagName('audio')
            function pauseAll() {
             
                var self = this;
                [].forEach.call(audios, function (i) {
                    // 将audios中其他的audio全部暂停
                    i !== self && i.pause();
                })
            }
            // 给play事件绑定暂停函数
            [].forEach.call(audios, function (i) {
                
                i.addEventListener("play", pauseAll.bind(i));
                // i.parentNode.children[i.parentNode.children.length-1].addEventListener("click", (e)=>{
                //     e.target.parentNode.innerHTML=''
                // });


                // i.parentNode.addEventListener("mouseover", (e)=>{
                //     console.log("in",e.target)
                //     let el = e.target;
                //     el = el.children[el.children.length-1];
                //     if(el && el.style)
                //     el.style.display='inline-block';
                    
                // });
                // i.parentNode.children[i.parentNode.children.length-1].addEventListener("mouseleave", (e)=>{
                    
                   
                //     e.target.style.display='none';
                    
                // });
            })
            
        })
    }
    disabledDate = (current) =>{
        // var today = moment().format('YYYY-MM-DD HH:mm:ss');
        // var last = moment().subtract('days', 1).format('YYYY-MM-DD HH:mm:ss')
        return current && current <= moment().startOf('day');
       // return currt ? currt< moment(last): moment(last);
    }
    disabledDateTime =(da) =>{
        function range(start, end) {
            const result = [];
            for (let i = start; i < end; i++) {
                result.push(i);
            }
            return result;
        }

        return {
          disabledHours: () => moment(da).date()>new Date().getDate()?range(-1,-1):range(0 ,new Date().getHours()),
          disabledMinutes: () => moment(da).date()>new Date().getDate()||moment(da).hours()>new Date().getHours() ?range(-1,-1) :range(0,new Date().getMinutes()),
          disabledSeconds: () => moment(da).date()>new Date().getDate()||moment(da).hours()>new Date().getHours() ||moment(da).seconds()>new Date().getSeconds()?range(-1,-1) :range(0,new Date().getSeconds()),
        };
      }

      disabledDateTime2 =(da) =>{
        function range(start, end) {
            const result = [];
            for (let i = start; i < end; i++) {
                result.push(i);
            }
            return result;
        }

        return {
          disabledHours: () => moment(da).date()>new Date().getDate()?range(-1,-1):range(0 ,new Date().getHours()),
          disabledMinutes: () => moment(da).date()>new Date().getDate()||moment(da).hours()>new Date().getHours() ?range(-1,-1) :range(0,new Date().getMinutes()),
          disabledSeconds: () => moment(da).date()>new Date().getDate()||moment(da).hours()>new Date().getHours() ||moment(da).seconds()>new Date().getSeconds()?range(-1,-1) :range(0,new Date().getSeconds()),
        };
      }
    tranfer(data,id){
        if(!data) return [];
        let obj ;
            return data.map((item,index)=>{
                obj = {...item}
                if(item.children){
                    if(item.id == id){
                        obj.disabled = true;
                    }
                    obj.children = this.tranfer(item.children,id);
                    return obj;
                } else {
                    if(item.id == id){
                        obj.disabled = true;
                    }
                    return obj;
                }
        })
    }

    tranfer2(data,id){
        if(!data) return [];
        let arr = []
        let obj ;
         data.forEach((item,index)=>{
                obj = {...item}
                if(obj.modelId=='8'){
                    arr.push(obj)
                } else {
                    if(item.children){
                       
                         let temp = this.tranfer2(item.children,id);
                         
                         arr = arr.concat(...temp);
                    } else {
                        return false
                    }
                }
               
        });
       
        return arr;
    }
    getTitle(str){
        if(str){
            return <p>预览 {str} <Button onClick={()=>{this.props.oncopy(str)}} >复制</Button></p>
        }
        return <p>预览</p>
    }
 
    render() {
        const {
            modelId,
            artTypes,
            artTags,
            helpDeclare,
            helpEndDate,
            helpStartDate,
            helpStatus,
            // isShowListTitle,
            curArt,
            surveyNum,
            goBack,
            artNewStatus,
            sensitiveWordsContent,
            visible,
            labelList=[],
            oncopy,
            isReference,
            categoryId,
            form:{getFieldDecorator, getFieldValue},
            saveLoading,
            previewModalUrl
        } = this.props;
        console.log("props",this.props)
        let {catgs} = this.props;
        // const { previewVisible,sensitiveWordsVisible, previewImage, fileList,previewVisible_two, previewImage_two, fileList_two,fileList_length } = this.state;
        const { catgsModelId, previewVisible,sensitiveWordsVisible, previewImage, fileList,previewVisible_two, previewImage_two, fileList_two,fileList_length } = this.state;
        let cid = (curArt.categoryId  ? curArt.categoryId + '' : categoryId || null);
        if(catgs){
           catgs = !isReference&&modelId==8?this.tranfer2(catgs,8) : this.tranfer(catgs,cid)
        }
        if(!curArt) return null;
        const uploadButton = (
            <div>
              <Icon type="plus" />
              <div className="ant-upload-text">上传缩略图</div>
            </div>
        );

        
           const selectType =  (!isReference&&modelId=='8'?
            (<Select onChange={this.typeChange}>
                <Select.Option value="banner">通栏显示</Select.Option>
            </Select>)
            :
            (<Select onChange={this.typeChange}>
            <Select.Option value="banner">通栏显示</Select.Option>
            <Select.Option value="normal">非通栏显示</Select.Option>
            <Select.Option value="three">三图显示</Select.Option>
            </Select>))
           
            const selectType2 =  (!isReference&&modelId=='8'?
            (<Select onChange={this.typeChange}>
                <Select.Option value="banner">通栏显示</Select.Option>
            </Select>)
            :
            <Select onChange={this.typeChange}>
            <Select.Option value="banner">通栏显示</Select.Option>
            <Select.Option value="normal">非通栏显示</Select.Option>
            </Select>)

           
        return (

            <Form className={styles.form}>
                <Row>
                    <Col span={16}>
                        <Form.Item>
                        {
                            getFieldDecorator('title', {
                                initialValue: curArt.title
                            })(
                                <Input onBlur={value => this.attrChange(value, 'title') } style={{width:"65%"}} placeholder="请输入标题（为了APP端更好的显示效果，请结合具体文章将标题控制在8-22个字符内）"/>

                            )
                        }
                        {/* <div style={{float:"right",width:"30%",marginRight:"3%",lineHeight:"normal",color:"red"}}>标题不限字数，为了app端更好的显示效果，请结合具体文章将标题控制在8-16个字符内</div> */}
                        </Form.Item>
                        {this.getListTt()}
                        {this.getLink()}
                        {curArt.type !== 'help'&&(curArt.type==='link'||((curArt.type === 'common')&&artNewStatus.viewType=='banner'))?
                            <Form.Item>
                                {
                                    getFieldDecorator('description', {
                                        initialValue: curArt.description || '' ,
                                    })(<Input maxLength="1000" type="textarea" rows="4" placeholder="请输入摘要（为了APP端更好的显示效果，请结合具体文章将标题控制在45个字符内）"/>)
                                }
                            </Form.Item>:null
                        }
                        {/*<Form.Item>
                            {
                                getFieldDecorator('content', {
                                    initialValue: {
                                        html: curArt.articleData ? this.getEditor(curArt.articleData.content) : '' ,
                                        innerChange: false
                                    }
                                })(<DraftEditor onEditChange={this.EditChange.bind(this)}/>)
                                //  onEditChange={this.EditChange.bind(this)}
                            }
                        </Form.Item> */}
                        <Form.Item>
                            {
                                curArt.articleData && getFieldDecorator('content', {
                                    initialValue: {
                                        html: curArt.articleData ? this.getEditor(curArt.articleData.content) : '' ,
                                        innerChange: false
                                    }
                                })(<UEditor
                                    _id="ueditorContainer"
                                    id="ueditorContainer"
                                    name="content"
                                    afterInit={this.textContentchange}
                                    afterInitw={(editer) => { this.editerNode = editer; }}
                                    initialContent={curArt.articleData? this.getEditor(curArt.articleData.content) : ''}
                                    width={'100%'}
                                    height={600}
                                  />)
                                //  onEditChange={this.EditChange.bind(this)}
                            }
                        </Form.Item>
                            <Form.Item>
                            <Upload
                                action={urlPath.UPLOAD_FILE}
                                listType="picture-card"
                                fileList={fileList}
                                onPreview={this.handlePreview}
                                onChange={this.handleChange}
                                onRemove={this.onRemove}
                                accept="image/png,image/jpeg,image/jpeg,image/gif"

                            >
                                {fileList.length >= fileList_length ? null : this.setBtn('上传缩略图')}
                                {/* {console.log(fileList_length,'fileList1的长度')} */}
                            </Upload>
                                <span style={{width: '300px',color: '#f66',textAlign: 'left',lineHeight: "100px"}}>缩略图尺寸为16:9</span>
                            <Modal visible={previewVisible} footer={null} onCancel={this.handleCancel}>
                                <img alt="example" style={{ width: '100%' }} src={previewImage} />
                            </Modal>
                        </Form.Item>
                        {/*{!(curArt.type=='image'&&artNewStatus.viewType=='banner')&&!artNewStatus.isTailor?<Form.Item>*/}
                        {!artNewStatus.isTailor?<Form.Item>
                            <Upload
                                action={urlPath.UPLOAD_FILE}
                                listType="picture-card"
                                fileList={fileList_two}
                                onPreview={this.handlePreview_two}
                                onChange={this.handleChange_two}
                                onRemove={this.onRemove_two}
                                accept="image/png,image/jpeg,image/jpeg,image/gif"
                            >
                                {this.getAudioBtn(curArt.type,artNewStatus.isTailor,fileList_two,fileList_length)}
                             {/*{fileList_two.length >= 1 ? null : uploadButton}*/}
                                {/* {console.log(this.getAudioBtn(curArt.type,artNewStatus.isTailor,fileList_two,fileList_length),'这是什么')}  */}
                                {/* {fileList_two.length >= fileList_length ? null : this.setBtn('上传缩略图')}
                                {console.log(fileList_length,'fileList的长度')} */}
                            </Upload>
                                {/* <span style={{width: '300px',color: '#f66',textAlign: 'left',lineHeight: "100px"}}>请上传一张尺寸为1:1的缩略图，供专题列表使用</span> */}
                                <span style={{width: '300px',color: '#f66',textAlign: 'left',lineHeight: "100px"}}>请上传一张尺寸为1:1的缩略图</span>
                            <Modal visible={previewVisible_two} footer={null} onCancel={this.handleCancel_two}>

                                <img alt="example" style={{ width: '100%' }} src={previewImage_two} />
                            </Modal>
                        </Form.Item>:null}

                        <Form.Item>
                            {
                                getFieldDecorator('authors', {
                                initialValue: curArt.authors || ''
                            })(<Input placeholder="请输入作者" onBlur={value => this.attrChange(value, 'authors') } />)
                        }
                        </Form.Item>
                        <Form.Item>
                        {
                            getFieldDecorator('source', {
                                initialValue: curArt.source || ''
                            })(<Input onFocus={this.focus} placeholder="请输入来源"/>)
                        }
                        </Form.Item>
                        {/* {this.getSurvey()} */}
                        {this.getAlbum()}
                        {this.getAudio()}
                        {this.getVideo()}
                        {curArt.type=='video'||curArt.type=='common'||curArt.type=='help'?this.getVideos():null}
                        {this.getMetas()}
                        {curArt.type!='live'&&curArt.type!='link'&&curArt.type!='image'?this.getRecommondList():null}
                    </Col>
                    <Col span={7} offset={1} className="col-r">
                        <Collapse bordered={false} defaultActiveKey={['1', '2']}>
                            <Collapse.Panel key="1" header="保存&发布">
                                <Form.Item label="权重"
                                    style={{
                                      marginBottom: 0
                                    }}
                                    extra="数值越大排序越靠前"
                                    {...formItemLayout}>
                                {
                                    getFieldDecorator('weight', {
                                        initialValue: curArt.weight
                                    })(<InputNumber min={0} />)
                                }
                                </Form.Item>
                                <Form.Item
                                  style={{
                                    marginBottom: 0
                                  }}
                                    {...formTailLayout}>
                                  {getFieldDecorator('topping', {
                                    valuePropName: 'checked',
                                    initialValue: curArt.weight === 999,
                                  })(
                                    <Checkbox onChange={this.toppingChange}>置顶</Checkbox>
                                  )}
                                </Form.Item>
                                <Form.Item label="到期时间" {...formItemLayout}>
                                {
                                    getFieldDecorator('weightDate', {
                                        initialValue: this.props.form.getFieldValue('topping')&&curArt.weightDate ? moment(curArt.weightDate): null
                                    })(<DatePicker disabled={!this.props.form.getFieldValue('topping')} showTime format={dateFormat} disabledDate={this.disabledDate} disabledTime={this.disabledDateTime}/>)
                                }
                                </Form.Item>
                                <Form.Item label="状态" {...formItemLayout}>
                                {
                                    getFieldDecorator('delFlag', {
                                        initialValue: curArt.delFlag||curArt.delFlag==0 ? curArt.delFlag + '' : null
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
                                <Form.Item label="发布时间" {...formItemLayout}>
                                {
                                    getFieldDecorator('publishDate', {
                                        initialValue: curArt.publishDate ? moment(curArt.publishDate, dateFormat) : null
                                    })(<DatePicker showTime format={dateFormat}/>)
                                }
                                </Form.Item>
                                <Row className="btn-row">
                                    <Col offset={0}>
                                        {this.getDelBtn()}
                                        {curArt.type!='link'?<Button className="preview-btn" type="primary" onClick={this.preview.bind(this)}>预览</Button>:null}
                                        <VisibleWrap permis="edit:modify">
                                            <Button className="save-btn" loading={saveLoading} type="primary" onClick={this.onSave.bind(this)}>保存</Button>
                                        </VisibleWrap>
                                        {
                                            curArt.id? <Button onClick={this.props.viewComment.bind(this, curArt)}>查看评论</Button>:''
                                        }

                                        <Button style={{ marginTop: 10 }} onClick={goBack}>返回</Button>

                                    </Col>
                                </Row>
                            </Collapse.Panel>
                            <Collapse.Panel key="2" header="栏目&样式">
                                {
                                    !curArt.inSubject
                                    ?	<div>
                                            <Form.Item label="所属栏目" {...formItemLayout}>
                                            {
                                                getFieldDecorator('categoryId', {

                                                    //initialValue: categoryId || null
                                                    initialValue:!isReference&&modelId==8? (catgs[0]?(catgs[0].value):null):(curArt.categoryId  ? curArt.categoryId + '' : categoryId || null)

                                                    // initialValue: curArt.categoryId+'' || null
                                                    // initialValue: curArt.categoryId !== undefined ? curArt.categoryId + '' : null

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
                                            <Form.Item label="展示位置" {...formItemLayout}>
                                            {
                                                getFieldDecorator('block', {
                                                    initialValue: curArt.block + ''
                                                })(
                                                    <Radio.Group>
                                                    {
                                                        !isReference&&modelId==8 ||catgsModelId.indexOf(curArt.categoryId) > -1?
                                                        blocks2.map((item, index) => {
                                                            return <Radio  help="The information is being validated..." key={index} value={item.value}>{item.label}</Radio>
                                                        })
                                                        :
                                                        blocks.map((item, index) => {
                                                            return <Radio  help="The information is being validated..." key={index} value={item.value}>{item.label}</Radio>
                                                        })
                                                    }
                                                    </Radio.Group>
                                                )
                                            }
                                            </Form.Item>

                                            {
                                                getFieldValue('block') === '3' &&
                                                <Form.Item label="注意" {...formItemLayout}>
                                                    <div>待选区内文章不展示在客户端</div>
                                                </Form.Item>
                                            }
                                        </div>
                                    :   null
                                }
                                <Form.Item label="版式类型" {...formItemLayout}>
                                    {
                                        getFieldDecorator('type', {
                                            initialValue: !isReference&&modelId=='8'?'help': curArt.type
                                        })(
                                            <Select onChange={this.typeChg.bind(this)}>
                                            {   !isReference&&modelId==8? <Select.Option key={0} value={'help'}>{'公益'}</Select.Option>
                                                :artTypes.map((item, index) => {
                                                    if(item.value=='help'){
                                                        return null
                                                    }
                                                    return <Select.Option key={index} value={item.value}>{item.label}</Select.Option>
                                                })
                                            }
                                            </Select>
                                        )
                                    }
                                </Form.Item>
                                {/* {
                                    curArt.type === 'common'
                                    ?	<Form.Item label="添加调查" {...formItemLayout}>
                                            <Switch checked={surveyNum === 1} checkedChildren="是" unCheckedChildren="否" onChange={this.toggleSurvey.bind(this)}/>
                                        </Form.Item>
                                    :   null
                                } */}
                                {artNewStatus.threeFlag?
                                    <Form.Item label="显示类型" {...formItemLayout}>
                                    {
                                        getFieldDecorator('viewType', {
                                            initialValue: (!isReference&&modelId==8?'banner':(artNewStatus.viewType?artNewStatus.viewType :'normal'))
                                        })(
                                            selectType
                                       
                                    )}
                                </Form.Item>:
                                <Form.Item label="显示类型" {...formItemLayout}>
                                    {
                                        getFieldDecorator('viewType', {
                                            initialValue: (!isReference&&modelId==8?'banner':(artNewStatus.viewType&&artNewStatus.viewType!="three"?artNewStatus.viewType:"normal"))
                                        })(
                                            selectType2
                                        )}
                                </Form.Item>}
                                {
                                     curArt.type === 'common'?
                                     <Form.Item label="显示头图" {...formItemLayout}>
                                        {
                                            getFieldDecorator('isShowTopImg', {
                                                initialValue: artNewStatus.isShowTopImg || false
                                            })(
                                        // <Switch checked={artNewStatus.isShowTopImg} onChange={this.topPicChange} checkedChildren="开" unCheckedChildren="关"/>
                                        <Switch checked={artNewStatus.isShowTopImg} onChange={this.topPicChange} checkedChildren="是" unCheckedChildren="否"/>
                                        )}
                                    </Form.Item>:null
                                }
                                <Form.Item label="自动剪裁" {...formItemLayout}>
                                    {
                                        getFieldDecorator('isTailor', {
                                            initialValue: artNewStatus.isTailor || false
                                        })(
                                    <Switch checked={artNewStatus.isTailor} onChange={this.autoCutChange} checkedChildren="开" unCheckedChildren="关"/>
                                    )}
                                </Form.Item>

                                {curArt.type === 'common'||curArt.type === 'help'?<Form.Item label="语音播报" {...formItemLayout}>
                                    {
                                        getFieldDecorator('isVoice', {
                                            initialValue: artNewStatus.isVoice || true
                                        })(
                                            <Switch checked={artNewStatus.isVoice} onChange={this.voiceChange} checkedChildren="开" unCheckedChildren="关"/>
                                        )}
                                </Form.Item>:null}

                                <Form.Item label="列表标题" {...formItemLayout}>
                                {
                                    getFieldDecorator('isShowListTitle',{
                                        initialValue:curArt.isShowListTitle || false
                                    })(
                                        <Switch checked={curArt.isShowListTitle} checkedChildren="开" unCheckedChildren="关" onChange={this.toggleListTt.bind(this)}/>
                                    )}
                                </Form.Item>

                                <Form.Item label="原创" {...formItemLayout}>
                                    {
                                    getFieldDecorator('doc_original_flag',{
                                        initialValue:curArt.doc_original_flag || false
                                    })(
                                        <Switch checked={curArt.doc_original_flag===1?true:false} checkedChildren="是" unCheckedChildren="否" onChange={this.toggleOriginal.bind(this)}/>
                                    )
                                }
                                </Form.Item>
                                {
                                    (curArt.type!='help'&&modelId!='8'&&!isReference)?
                                
                                <Form.Item label="标签" {...formItemLayout}>
                                {
                                    getFieldDecorator('tags', {
                                        initialValue: curArt.tags ? curArt.tags + '' : ""
                                    })(
                                        <Select
                                            showSearch
                                            allowClear
                                            placeholder="请选择标签"
                                            onChange={this.labelHandleChange}
                                            filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                        >
                                        {
                                            labelList.map((item, index) => {
                                                return <Select.Option key={item.id} value={item.name}>{item.name}</Select.Option>
                                            })
                                        }
                                        </Select>
                                    )
                                }
                                </Form.Item>
                                :null}
                                <Form.Item label="关键词" {...formItemLayout}>
                                {
                                    getFieldDecorator('keywords', {
                                        initialValue: curArt.keywords
                                    })(
                                        <Input placeholder="逗号或者空格分割"/>
                                    )
                                }
                                </Form.Item>
                                {
                                    (curArt.type=='help'||(modelId=='8'&&!isReference))?<div>
                                    <Form.Item label="开始时间" {...formItemLayout}>
                                    {
                                    getFieldDecorator('startDate', {
                                        initialValue:  curArt.startDate ? moment(curArt.startDate, dateFormat) : null
                                    })(<DatePicker  showTime format={dateFormat} disabledDate={this.disabledDate} disabledTime={this.disabledDateTime}/>)
                                    }
                                    </Form.Item>
                                    <Form.Item label="结束时间" {...formItemLayout}>
                                   
                                    {
                                    getFieldDecorator('endDate', {
                                        initialValue:  curArt.endDate ? moment(curArt.endDate, dateFormat) : null
                                    })(<DatePicker  showTime format={dateFormat} disabledDate={this.disabledDate} disabledTime={this.disabledDateTime}/>)
                                    }
                                    </Form.Item>
                                    <Form.Item label="帮的声明" {...formItemLayout}>
                                    {
                                        getFieldDecorator('helpState', {
                                            initialValue:  curArt.helpState?curArt.helpState:''
                                        })(<Input.TextArea autosize={{ minRows: 6, maxRows: 6 }} />)
                                    }
                                    </Form.Item>
                                    <Form.Item label="帮的状态" {...formItemLayout}>
                                    {
                                        getFieldDecorator('helpStatus', {
                                            initialValue: curArt.helpStatus?(curArt.helpStatus+''):'1'
                                        })(
                                            <Select
                                               
                                                onChange={this.helpStatusHandleChange}
                                                
                                            >
                                                <Select.Option value={'1'}>募集中</Select.Option>
                                                <Select.Option value={'2'}>募集结束</Select.Option>
                                                <Select.Option value={'3'}>捐助反馈</Select.Option>
                                            </Select>
                                        )
                                    }
                                    </Form.Item></div>
                                    :''
                                }
                               
                            </Collapse.Panel>
                        </Collapse>
                    </Col>
                </Row>
                <Modal
                    title={this.getTitle(previewModalUrl)}
                    visible={visible}
                    closable={false}
                    footer={[
                        <Button type="primary" key="close" onClick={this.previewHandleCancel}>确定</Button>,
                    ]}
                >
                    {visible?(curArt.type=='image'?
                        <div className={styles.detailModal}>
                            <div
                                className={styles.imageContent}
                            >
                            <Carousel>
                                {
                                    sensitiveWordsContent?sensitiveWordsContent.split(',').map(function(item,i){
                                        return <div key={i} className={styles.Carousel} dangerouslySetInnerHTML={{__html: item}}></div>
                                    }):null
                                }
                            </Carousel>
                            </div>
                        </div>
                        :<div className={styles.detailModal}>
                            <div
                                className={styles.detailContent}
                                dangerouslySetInnerHTML={{__html: sensitiveWordsContent}}
                            >
                            </div>
                        </div>):''}
                </Modal>
                {/*{sensitiveWordsVisible}*/}
                <Modal
                    title="敏感词提示"
                    visible={sensitiveWordsVisible}
                    closable={false}
                    footer={[
                        <Button key="back" onClick={this.sensitiveWordsHandleOk}>确定</Button>,
                    ]}
                >
                    <div>提示：发现敏感词并已标红，请及时检查！</div>
                </Modal>
            </Form>
        );
    }
}
function mapStateToProps({ article ,cms},context) {
    return {
        modelId:article.modelId,
        artTags: cms.artTags,
        artTypes: cms.artTypes,
        catgs: cms.catgs,
        curArt: article.curArt,
        surveyNum: article.surveyNum,
        albumNum: article.albumNum,
        videoNum: article.videoNum,
        audioNum: article.audioNum,
        artNewStatus:article.artNewStatus,
        visible:article.visible,
        sensitiveWordsContent:article.sensitiveWordsContent,
        sensitiveWordsVisible:article.sensitiveWordsVisible,
        labelList:article.labelList,
        saveLoading:article.saveLoading,
        previewModalUrl:article.previewModalUrl
    }
}
export default connect(mapStateToProps)(Form.create()(Edit))
