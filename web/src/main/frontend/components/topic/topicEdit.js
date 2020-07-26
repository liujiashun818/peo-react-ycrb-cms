import React from 'react'
import PropTypes from 'prop-types';
import MediaUploader from '../form/mediaUploader';
import {
    message,
    Form,
    Select,
    TreeSelect,
    DatePicker,
    Input,
    InputNumber,
    Radio,
    Button,
    Modal,
    Row,
    Col,
    Collapse,
    Switch,
    Upload
} from 'antd';
import moment from 'moment';
import {Jt} from '../../utils';
// import {blocks, delFlags} from '../../constants';
import {blocks, blocks2, delFlags} from '../../constants';
import ImgUploader from '../form/imgUploader';
import VisibleWrap from '../ui/visibleWrap';
import styles from './topicEdit.less';
import AlbumUploader from '../form/albumUploader';

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

const dateFormat = 'YYYY-MM-DD HH:mm:ss';


class TopicEdit extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            catgsModelId: [],
            categoryId: null,
            number: 0,
            saveLoading: false,
            display:'none'
        }
        this.viewTypeChange = this.viewTypeChange.bind(this);
        this.numberChange = this.numberChange.bind(this);
        this.albumChange = this.albumChange.bind(this);
        this.topDisplayChange = this.topDisplayChange.bind(this);
        this.getUploadState = this.getUploadState.bind(this);
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
                catgsModelId.push(item.id+'')
                this.setState({ catgsModelId })
            }
           
            if (item.children && item.children.length) {
                this.loopGetModelId(item.children)
            }
        });
    }

    catgChg (categoryId) {
        // console.log(categoryId)
        // categoryId = Number(categoryId)
        this.setState({categoryId})
        const { topic, form: { setFieldsValue } } = this.props
 		const { catgsModelId } = this.state
 		if (catgsModelId.indexOf(categoryId || topic.article.categoryId) > -1) {
 			setFieldsValue({block: '2'});
 		}
    }
    toThumbnail(imageUrl='') {
        let fileList = [];
        if(imageUrl) {
            let images = imageUrl.split(',');
            images.forEach((image, index) => {
                fileList.push({
                name: image,
                url: image,
                uid: index
            });
        });
        }
        return {fileList};
    }


    toggleListTt (val) {
        const { updateState, topic } = this.props;
        let listTitleValue = ''
        if (val) {
            const title = document.getElementById('title')
            listTitleValue = title.value
        }
        const topic_new = Object.assign(topic, { showTitle: val, article:{...topic.article,listTitle: listTitleValue }})
        updateState({ topic: topic_new })
    }
    fromThumbnail(fileList=[]) {
        let urls = []
        for(let i = 0, len = fileList.length; i < len; i++) {
            urls.push(fileList[i].url)
        }
        return urls.join(',')
    }

    validateValues(values,image) {
        const { catgs, topic } = this.props;
        const { categoryId, title, imageUrl, viewType,showTop } = values;
        if (!categoryId) {
            message.error('请选择归属栏目');
            return false;
        }
        if(!Jt.tree.isLeaf(catgs, categoryId)) {
            message.error('请选择子级栏目');
            return false;
        }

        if(!title) {
            message.error('请输入标题');
            return false;
        }
        // if (!imageUrl && viewType != 'banner') {
        if (values.viewType != 'banner'&&!imageUrl) {
            message.error('请选择缩略图');
            return false;
        }
        const flag = !!topic.bannerUrl?topic.bannerUrl.length < 2:false;
        if (flag && values.viewType == 'banner' &&values.block!=1) {
            message.error('请上传2张缩略图')
            return false
        }

       if(showTop=="1"&&!image){
            message.error('请上传头图')
            return false
       }

        if(showTop=="2"&&(!values.video||!values.video.url)){
            message.error('请选择视频')
            return false
        }
        if(showTop=="2"&&(!values.video||!values.video.coverImg)){
            message.error('请选择视频封面')
            return false
        }
        return true;
    }

    viewTypeChange(value) {
        const { updateState, topic } = this.props;
        let topic_new = '';
            topic_new = Object.assign(topic.article, { viewType: value.target.value })
        updateState({ base: {...topic,image:topic.image, showTitle: topic.showTitle, article: topic_new } })
        this.setState({"number":0})
    }
    topDisplayChange(value){
        const { updateState, topic } = this.props;
        console.log("11111",topic)
        let topic_new = '';
            topic_new = Object.assign(topic.article, { showTop: value.target.value })
        updateState({ base: {...topic,image:topic.image, showTitle: topic.showTitle, article: topic_new ,pageType:topic_new.pageType,showTop:topic_new.showTop} })
       
    }
    radioChange=(value) => {
        const { catgs, topic } = this.props;

        if(value.target.value==1){
             this.props.form.setFieldsValue({'viewType':'normal'});
            // this.viewTypeChange({'target':{
            //     value:'normal'
            //     }})
        }
        let radionum=value.target.value;
        if(!!topic.bannerUrl){

            this.setState({
                number:radionum==1?9:0
            })
            if(topic.bannerUrl.length==1){
                // this.state.number=radionum==1?9:0;
                this.setState({
                    number:radionum==1?9:0,
                    // display:'none'
                });
            }else{
                this.setState({
                    number:radionum==1?1:0
                })
            }
            // radionum==1?<Upload>上传文件</Upload>:null;

            }

        topic.bannerUrl=[]
    }
    albumChange(data) {
        const { updateState, topic } = this.props;
        const topic_new = Object.assign(topic, { bannerUrl: data.fileList })
        updateState({ topic: topic_new })
    }

    numberChange() {
        let number = (this.state.number+1);
        this.setState({
            number: number
        })
    }

    onSave() {
        const { topic, saveTopic, form: { getFieldsValue } } = this.props;

      
        const values = { ...getFieldsValue() };
        values.viewType = values.viewType;
        if (values.thumbnail && values.thumbnail.fileList && values.thumbnail.fileList.length > 0) {
            values.imageUrl = this.fromThumbnail(values.thumbnail.fileList);
        }
        delete values.thumbnail;
        let image = '';
        if (values.image && values.image.fileList && values.image.fileList.length > 0) {
            image = values.image.fileList[0].url;
        }
        delete values.image;
       
        const showTitle = values.showTitle || false;
        delete values.showTitle;
        values.publishDate = values.publishDate ? values.publishDate.format(dateFormat) : '';
        if (this.validateValues(values,image)) {
            
            this.setState({
                saveLoading: true
            })
            setTimeout(() => {
                this.setState({
                    saveLoading: false
                })
            }, 1000)
            let bannerUrl = '';
            let bannerUrlList =''
            if (values.viewType == 'banner') {
               
                bannerUrl = JSON.stringify(topic.bannerUrl);
                (topic.bannerUrl?topic.bannerUrl:[]).forEach((item)=>{
                    bannerUrlList +=item.url+","
                })
            } else{
                console.log("---------------",values)
                    bannerUrlList =values.imageUrl

            }
           
          
            const data = {
                id: topic.id,
                title: values.title,
                showTitle,
                image,
                article: { ...topic.article, ...values,videoCover:values.video?values.video.coverImg:"",
                mediaIds:values.video?[values.video.id]:null,imageUrl:bannerUrlList },
                bannerUrl,
                //video:
                // videoUrl:values.video?values.video.url:"",
                // videoCover:values.video?values.video.coverImg:"",
                // mediaIds:values.video?[values.video.id]:null,
                pageType:values.pageType,
                showTop:values.showTop
            };
            saveTopic(data);
            if(showTop==2){
                // this.getUploadState().then(function (data) {
                //     if(data.status){
                //         saveTopic(data);
                       
                //     }else{
                //         message.error('视频转码未完成，请稍后重试');
                        
                       
                        
                //     }
                // })
            }
           
          
        }
    }
    getUploadState = (callback)=>{
        let self = this;
        return new Promise((resolve) =>{
            const {artNewStatus,form,dispatch} = self.props;
            let fileList = "";
            const url =  form.getFieldValue('video')?form.getFieldValue('video').url:''
            dispatch({
                type:'article/effect:getUploadState',
                payload:{
                    fileUrls:url,
                    resolve
                }
            })
        })
    }
    toMedia(type) {
      //  const {curArt:{articleData={}}} = this.props;
        const mediaJson = type === 'video' ? (this.props.topic.article.articleData&&this.props.topic.article.articleData.videoJson&&this.props.topic.article.articleData.videoJson[0]?this.props.topic.article.articleData.videoJson[0]:null) : this.props.topic.audioJson;
        if(Jt.array.isEmpty(mediaJson)) {
            return {id: '', url: '', coverImg: ''};
        }
       const media = mediaJson[0];
       console.log("aaaxx",this.props.topic.article.articleData)
        return {
             id: mediaJson.id,
             url: mediaJson.resources[0].url,
            coverImg: mediaJson.image
        };
    }
    mediaChange = (data)=>{
        const {curArt, updateState} = this.props
        
        let videoJson =  [{
            id: data.id,
            image: data.coverImg,
            resources:[{
                url: data.url
            }]
        }
        ]
        updateState({
            topic: {videoJson}
        });
    }
    getVideo() {
        const {videoNum, form:{getFieldDecorator}} = this.props;
        return (
            <Collapse bordered={false} defaultActiveKey={['video']}>
                <Collapse.Panel key="video" header="视频">
                    <Form.Item>
                    {
                        getFieldDecorator('video', {
                            initialValue: this.toMedia('video')
                        })(<MediaUploader onlyVideo={true} onChange={this.mediaChange} type="video" />)
                    }
                    </Form.Item>
                </Collapse.Panel>
            </Collapse>
        );
    }
    render() {
        const { catgsModelId, categoryId } = this.state
        const { catgs, topic, goBack, form: { getFieldDecorator, getFieldValue} } = this.props;
        topic.article = topic.article || {};
        let renderNum = (this.props.form.getFieldValue('block')==1&&topic.article.viewType=='banner'?1:9);
        let imgdesc = ''
        if( topic.article.viewType == "normal"){
            imgdesc = '左图右文显示缩略图尺寸4:3'
        } else if(topic.article.viewType == "banner"){

        } else if(topic.article.viewType == "image"){
            imgdesc = '小图显示缩略图尺寸6:1'
        } else if(topic.article.viewType == "bigimage"){
            imgdesc = '大图显示缩略图尺寸2:1'
        }
      
        let showTitle = true;
        if(this.props.form.getFieldValue('showTitle')==undefined){
            showTitle = topic.showTitle;
        } else {
            showTitle = this.props.form.getFieldValue('showTitle');
        }
        return (
            <Form className={styles.form}>
                <Row>
                    <Col span={16}>
                        <Form.Item>
                            {
                                getFieldDecorator('title', {
                                    initialValue: topic.article.title
                                })(
                                    <Input 
                                    placeholder="请输入标题（为了APP端更好的显示效果，请结合具体文章将标题控制在8-22个字符内）" 
                                 
                                />
                                    // <Input placeholder="请输入标题（为了APP端更好的显示效果，请结合具体文章将标题控制在8-22个字符内）" />
                                )
                            }
                        </Form.Item>
                        
                        {
                             showTitle?
                             <Form.Item>
                                 {
                                     getFieldDecorator('listTitle', {
                                         initialValue: topic.article.listTitle
                                     })(
                                         <Input placeholder="请输入列表标题" />
                                     )
                                 }
                             </Form.Item>
                             :null
                         }
                        
                        <Form.Item>
                            {
                                getFieldDecorator('description', {
                                    initialValue: topic.article.description
                                })(
                                    <Input type="textarea" rows="4" placeholder="请输入导语" />
                                )
                            }
                        </Form.Item>
                        <Collapse bordered={false} defaultActiveKey={'3'}>
                            <Collapse.Panel key="3" header="更多">
                                {topic.article.viewType === 'banner' ? <Form.Item label="缩略图" {...layout_l}>
                                    <AlbumUploader value={{ fileList: topic.bannerUrl }} onChange={this.albumChange} numberChange={this.numberChange} batchFlag='无' num={this.state.number} number={renderNum}   />
                                </Form.Item> : <Form.Item label="缩略图" {...layout_l}>
                                        {
                                            getFieldDecorator('thumbnail', {
                                                initialValue: this.toThumbnail(topic.article.imageUrl)
                                            })(<ImgUploader single={true} />)
                                        }
                                    <span style={{ width: '300px', color: '#f66', textAlign: 'left', lineHeight: "100px", position: 'absolute', top: '60%', left: 0 }}>{imgdesc}</span>
                                    </Form.Item>}
                                {topic.showTop+''=='1'?
                                 <Form.Item label="头图" {...layout_l}>
                                 {
                                     getFieldDecorator('image', {
                                         initialValue: { fileList: topic.image ? [{ name: topic.image, url: topic.image, uid: -1 }] : [] }
                                     })(<ImgUploader single={true} />)
                                 }
                                 <span style={{ width: '300px', color: '#f66', textAlign: 'left', lineHeight: "100px", position: 'absolute', top: '60%', left: 0 }}>头图尺寸为2:1</span>
                             </Form.Item>:""
                            }                    
                               
                            </Collapse.Panel>
                        </Collapse>
                        {topic.showTop=='2'? this.getVideo():""}
                    </Col>
                    <Col span={7} offset={1} className="col-r">
                        <Collapse bordered={false} defaultActiveKey={['1', '2']}>
                            <Collapse.Panel key="1" header="保存&发布">
                                <Form.Item label="权重" {...layout_r}>
                                    {
                                        getFieldDecorator('weight', {
                                            initialValue: topic.article.weight
                                        })(<InputNumber min={0} />)
                                    }
                                </Form.Item>
                                <Form.Item label="状态" {...layout_r}>
                                    {
                                        getFieldDecorator('delFlag', {
                                            initialValue: topic.article.delFlag + '' || ''
                                        })(
                                            <Select>
                                                {
                                                    delFlags.map((item, index) => {
                                                        return <Select.Option key={index} value={item.value}>{item.label}</Select.Option>
                                                    })
                                                }
                                            </Select>
                                        )
                                    }
                                </Form.Item>
                                <Form.Item label="发布时间" {...layout_r}>
                                    {
                                        getFieldDecorator('publishDate', {
                                            initialValue: moment(topic.article.publishDate, dateFormat)
                                        })(
                                            <DatePicker showTime format={dateFormat} />
                                        )
                                    }
                                </Form.Item>
                                <Row className="btn-row">
                                    <Col offset={layout_r.labelCol.span} span={layout_r.wrapperCol.span}>
                                        <VisibleWrap permis="edit:modify">
                                            <Button className="save-btn" loading={this.state.saveLoading} type="primary" size="large" onClick={this.onSave.bind(this)}>保存</Button>
                                        </VisibleWrap>
                                        <Button className="back-btn" size="large" onClick={goBack}>返回</Button>
                                    </Col>
                                </Row>
                            </Collapse.Panel>
                            <Collapse.Panel key="2" header="栏目&样式">
                                <Form.Item label="所属栏目" {...layout_r}>
                                    {
                                        getFieldDecorator('categoryId', {
                                            initialValue: topic.article.categoryId + ''
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
                                <Form.Item label="列表标题" {...layout_r}>
                                    {
                                        getFieldDecorator('showTitle', {
                                            valuePropName: 'checked',
                                            initialValue: topic.showTitle || false
                                        })(
                                            <Switch onChange={this.toggleListTt.bind(this)} checkedChildren="是" unCheckedChildren="否" />
                                            // <Switch onChange={()=>this.setState({})} checkedChildren="是" unCheckedChildren="否" />
                                        )
                                    }
                                </Form.Item>
                                <Form.Item label="展示位置" {...layout_r}>
                                    {
                                        getFieldDecorator('block', {
                                            initialValue: topic.article.block + ''
                                        })(
                                            <Radio.Group onChange={this.radioChange}>
                                                {
                                                     catgsModelId.indexOf(categoryId || topic.article.categoryId) > -1?
                                                     blocks2.map((item, index) => {
                                                         return <Radio key={index} value={item.value}>{item.label}</Radio>
                                                     })
                                                     :
                                                    blocks.map((item, index) => {
                                                        return <Radio key={index} value={item.value}>{item.label}</Radio>

                                                    })
                                                }
                                            </Radio.Group>
                                        )
                                    }
                                </Form.Item>
                                {
                                    getFieldValue('block') === '3' &&
                                    <Form.Item label="注意" {...layout_r}>
                                        <div>待选区内文章不展示在客户端</div>
                                    </Form.Item>
                                }
                                {
                                    this.props.form.getFieldValue('block')!='1' &&
                                    <Form.Item label="显示样式" {...layout_r}>
                                        {
                                            getFieldDecorator('viewType', {
                                                initialValue: topic.article.viewType
                                            })(
                                                <Radio.Group onChange={this.viewTypeChange}>
                                                    {
                                                        [
                                                            {label: '左图右文', value: 'normal'},
                                                            {label: '通栏', value: 'banner'},
                                                            {label: '小图', value: 'image'},
                                                            {label: '大图', value: 'bigimage'},
                                                        ].map((item, index) => {
                                                            return <Radio key={index} value={item.value}>{item.label}</Radio>
                                                        })
                                                    }
                                                </Radio.Group>
                                            )
                                        }
                                    </Form.Item>
                                }
                                {
                                   
                                    <Form.Item label="顶部显示" {...layout_r}>
                                        {
                                            getFieldDecorator('showTop', {
                                                initialValue: topic.showTop+''
                                            })(
                                                <Radio.Group onChange={this.topDisplayChange}>
                                                    {
                                                        [
                                                            {label: '图片', value: '1'},
                                                            {label: '视频', value: '2'},
                                                        
                                                        ].map((item, index) => {
                                                            return <Radio key={index} value={item.value}>{item.label}</Radio>
                                                        })
                                                    }
                                                </Radio.Group>
                                            )
                                        }
                                    </Form.Item>
                                }
                                {
                                   
                                    <Form.Item label="专题页样式" {...layout_r}>
                                        {
                                            getFieldDecorator('pageType', {
                                                initialValue: topic.pageType?(topic.pageType+''):'1'
                                            })(
                                                <Select>
                                                {
                                                    [
                                                        {label: '普通', value: '1'},
                                                        {label: '时间轴', value: '2'},
                                                    
                                                    ].
                                                    map((item, index) => {
                                                        return <Select.Option key={index} value={item.value}>{item.label}</Select.Option>
                                                    })
                                                }
                                            </Select>
                                            )
                                        }
                                    </Form.Item>
                                }
                                
                            </Collapse.Panel>
                        </Collapse>
                      
                    </Col>
                </Row>
            </Form>
        );
    }
}

export default Form.create()(TopicEdit);
