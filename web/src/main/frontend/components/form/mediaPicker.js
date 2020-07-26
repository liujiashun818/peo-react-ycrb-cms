import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'dva';
import { Modal, Table, Select, Button ,Tabs, Upload, Icon, message,DatePicker,Input, Progress, Form} from 'antd';
import { PAGE_SIZE, UPLOAD_FILES, urlPath } from '../../constants'
import styles from './mediaPicker.less'

const FormItem = Form.Item
const TabPane = Tabs.TabPane;
const Dragger = Upload.Dragger;
const Option = Select.Option;
const RangePicker = DatePicker.RangePicker;

const uploaderProps = {
  name: 'file',
  multiple: true,
  showUploadList: false,
  action: urlPath.UPLOAD_FILES,
};

const columns = [{
  title: '文件',
  dataIndex: 'file',
  render:function (value, row, index){
    return {
    children:
      <div>
        {value.cover? <img src={value.cover} className={styles.coverImg} />:'' }
        <h3><a>{value.name}</a></h3><br />
        <span>{value.autoName}</span>
      </div>,
    props: {},
    };
  },
}, {
  title: '大小',
  dataIndex: 'size',
  render:function (value, row, index){
    if(value) {
        let size = ''
        size = parseInt((value)/1024)
        if (size >= 1024) {
            let mbVal = size/1024
            size = mbVal.toFixed(1) + ' MB'
        }else {
            size = size + ' KB'
        }
        return size
    } else {
        return ''
    }
  }
}, 
{
  title: '上传时间',
  dataIndex: 'uploadTime',
  render:function (value, row, index){
    if(value) {
        return (new Date(value)).format('yyyy-MM-dd hh:mm:ss')
    }else {
        return ''
    }
  }
}];

let mediaType = "";


const dispatchQueryMediaList = (dispatch,payload={}) => {
     let params = Object.assign({
          type:mediaType,
          pageNumber:1,
          pageSize:PAGE_SIZE
      }, payload)
      dispatch({
          type: 'app/queryMediaList',
          payload: {...params}
      })
}
class MediaPickerTmp extends React.Component {
  constructor(props) {
	super(props)
	this.state = {
      selectedRowKeys: [],
	    loading: false,
	    visible: false,
      uploadFileList: [],
      type:this.props.mediaType
	  }
  }
  showModal = () => {
      
    this.setState({
      visible: true,
    });
    const {resetFields} = this.props.form;
    resetFields()
    //加载媒体列表
    mediaType = this.props.mediaType;
    dispatchQueryMediaList(this.props.dispatch,{type:this.props.mediaType})
  }
  handleOk = () => {
    this.setState({ loading: true });
    const newMediaData = this.props.mediaList.filter((item) => {
      return this.state.selectedRowKeys == item.id;
    });
    console.log("mmmm",newMediaData)
   	// Should provide an event to pass value to Form.
  	const onChange = this.props.onChange;
  	if (onChange) {
  	  onChange(Object.assign({}, {newMediaData}));
  	}

    this.setState({ loading: false, visible: false });

  }
  handleCancel = () => {
    this.setState({ visible: false });
  }
  handleSearch = () => {
    const {getFieldsValue} = this.props.form
    const data = getFieldsValue()
    if(data.date && data.date.length !== 0){
      data.startTime = data.date[0].format('YYYY-MM-DD')
      data.endTime = data.date[1].format('YYYY-MM-DD')
    }
    if(!data.keyword){
      delete data.keyword;
    }
    delete data.date;
    data.pageNumber = 1
    dispatchQueryMediaList(this.props.dispatch,data)
  }
  handleSelectTypeChange = (value) => {
    mediaType = value;
    dispatchQueryMediaList(this.props.dispatch)
  }
  onMideaSelectChange = (selectedRowKeys) => {
    this.setState({ selectedRowKeys });
  }
  onUploaderChange = (info) => {
    const status = info.file.status;
    if (status === 'uploading') {
      this.setState({
        uploadFileList:info.fileList
      })
    }
    console.log("sdadassadsasdssa",info)
    if (status === 'done') {
      if(info.file.response&&info.file.response.code==-1){
        message.error(`${info.file.response.msg}.`);
     
      } else {
        message.success(`${info.file.name} 上传成功.`);
        dispatchQueryMediaList(this.props.dispatch,{type:this.props.mediaType})
      }
     
    } else if (status === 'error') {
      message.error(`${info.file.name} 上传失败.`);
    }
  }
  render() {
    const { selectedRowKeys } = this.state;
    const {getFieldDecorator} = this.props.form
    const rowSelection = {
      type:'radio',
      selectedRowKeys,
      onChange: this.onMideaSelectChange,
    };
    console.log("2222222222222222222222",this.props.mediaList)
    //待处理调整数据结构
    const listData = this.props.mediaList.map((item,index) => {
      const newItem = {
        key:item.id,
        file:{
          cover:item.cover || '',
          name:item.name,
          autoName:item.autoName,
          hdUrl:item.hdUrl
        },
        size:item.hdSize,
        uploadTime:item.uploadTime,
        //mediaId:item.mediaId
        id: item.id
      }
      return newItem;
    });

    const tabelProps = {
      dataSource:listData,
      pagination:this.props.mediaListPagination,
      onChange: (page) => {
            this.props.dispatch({
              type: 'app/queryMediaList',
              payload: {
                type:mediaType,
                pageNumber:page.current,
                pageSize: page.pageSize
              }
          })
        }
    }
   const progress = this.state.uploadFileList.map((item, index) => {
                  return (
                      <div className="styles.progressWarp" key={item.uid} >
                       上传文件“{item.name}”<Progress percent={item.percent} />
                      </div>
                    )
                }
              )

    return (
      <span>
        <Button type="primary" icon="appstore-o" onClick={this.showModal}>
          选择媒体文件
        </Button>
        {
          this.state.visible?
          <Modal
          wrapClassName='media-modal-wrap'
          visible={this.state.visible}
          title="选择媒体文件"
          width='90%'
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          footer={[
           
            <Button key="submit" type="primary"  size="large" loading={this.state.loading} onClick={this.handleOk}>
             确定
            </Button>,
             <Button key="back" size="large" onClick={this.handleCancel}>取消</Button>,
          ]}
        >
          <Tabs type="card" defaultActiveKey="2">
            <TabPane tab="上传文件" key="1">
              <Dragger {...uploaderProps} accept="audio/mp3,video/mp4" onChange={this.onUploaderChange} className={styles.draggerWarper} >
                <div className={styles.draggerWarp}>
                  <p className="ant-upload-drag-icon">
                    <Icon type="inbox" />
                  </p>
                  <p className="ant-upload-text">拖拽到此处上传</p>
                  <p className="ant-upload-hint">支持拖拽一个或多个文件上传,仅支持mp3、mp4格式文件</p>
                </div>
              </Dragger>
              {progress}
            </TabPane>
            <TabPane tab="媒体库" key="2">
              <div>
                <Form layout="inline">
                  <FormItem>
                    <Select defaultValue={this.props.mediaType==='video'?'video':'audio'} style={{ width: 120 }} size="large" onChange={this.handleSelectTypeChange}>
                      <Option value="video">视频</Option>
                      {this.props.onlyVideo?'':<Option value="audio">音频</Option>}
                    </Select>
                  </FormItem>
                  <FormItem>
                    {
                        getFieldDecorator('date', {
                            initialValue: null
                        })(
                            <RangePicker format={'YYYY-MM-DD'}/>
                        )
                    }
                  </FormItem>
                  <FormItem>
                      {
                          getFieldDecorator('keyword', {
                              initialValue: ''
                          })(
                              <Input placeholder="关键词" style={{ width: 200 }}/>
                          )
                      }
                  </FormItem>
                  <FormItem>
                    <Button type="primary" icon="search" onClick={this.handleSearch}/>
                  </FormItem>
                </Form>
              </div>
              <div style={{ marginTop: 10 }}>
                <Table rowSelection={rowSelection}  columns={columns}  {...tabelProps} />
              </div>
            </TabPane>
          </Tabs>
        </Modal>:null
        }
        
      </span>
    );
  }
}

MediaPickerTmp.propTypes = {
  value: PropTypes.object,
  onChange:PropTypes.func
}

//model app.js 中的状态值为全局的
function mapStateToProps({app,article}) {
  const mediaList = app.mediaList || [];
  const mediaListPagination = app.mediaListPagination;
 // const mediaType = article.mediaType;
  return {mediaList,mediaListPagination};
}
const MediaPicker = connect(mapStateToProps)(MediaPickerTmp);

export default Form.create()(MediaPicker)
