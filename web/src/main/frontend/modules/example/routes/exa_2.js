import React from 'react'
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import { Form, Button, Collapse } from 'antd';
const FormItem = Form.Item;
const Panel = Collapse.Panel;

import ImgUploader from '../../../components/form/imgUploader'
import DraftEditor from '../../../components/form/editor'
import AlbumUploader from '../../../components/form/albumUploader'
// 媒体文件选择控件
import MediaUploader from '../../../components/form/mediaUploader' 
import VisibleWrap from '../../../components/ui/visibleWrap'
import UEditor from '../../../components/form/ueditor';

  // 图片上传控件默认值-多图
  const imgListProps = {
      fileList: [{
        name: 'xxx.png',
        url: 'https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=1856655420,2938535364&fm=58'
      }, {
        name: 'yyy.png',
        url: 'https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png'
      }, {
        name: 'zzz.png',
        url: 'https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png'
      }
      ]
    };

    // 图集控件默认值
    const albumProps = {
      fileList: [{
        key: 0,
        url: 'https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=1856655420,2938535364&fm=58',
        dec: '描述内容一描述内容一描述内容一描述内容一描述内容一'    
      },{
        key: 1,
        url: 'https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=1187964786,2284821672&fm=58',
        dec: '描述内容二描述内容二描述内容二描述内容二描述内容二'   
      },{
        key: 2,
        url: 'https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=1187964786,2284821672&fm=58',
        dec: '描述内容3'
      }
      ]
    };

    // 富文本编辑器默认值
    const editorProps = {
      html:'<p><strong>fsdsf</strong>sdsd<em>9999</em></p>',
      innerChange:false
    }

    const customPanelStyle = {
      background: '#f7f7f7',
      borderRadius: 4,
      marginBottom: 24,
      border: 0
    };

    // 媒体上传默认值
    const mediaProps = {
          coverImg:'https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=1856655420,2938535364&fm=58',
          // url:'http://medias-product.oss-cn-beijing.aliyuncs.com/m3u8-sd/2daf2472a4c944b1a7072cf52937eee3/e7b15349-f2bd-479e-a0c6-2423f041e638.m3u8',
          mediaId:'123',
          url:''
        }



class Demo extends React.Component {

  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        console.log('Received values of form: ', values);
      }
    });

    //动态更改图集值
    this.props.form.setFieldsValue({album:{fileList: [{
        key: 0,
        url: 'https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=1187964786,2284821672&fm=58',
        dec: '描述内容动态'   
      }]}
    })

    //动态更改图片上传控件值
    this.props.form.setFieldsValue({imgUploader:{fileList: [{
        uid:-1,
        name: '2423523',
        url: 'https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=1187964786,2284821672&fm=58',   
      }]}
    })

    //动态更改富文本编辑器值
    this.props.form.setFieldsValue({htmlContent:{html:'<p>动态更改富文本编辑器值</p>'}})
  }

  render() {
    const { getFieldDecorator } = this.props.form;

    return (
      <Form onSubmit={this.handleSubmit}>
        <FormItem>
          <VisibleWrap permis="sys:dict:edit"><Button>字典修改按钮权限测试</Button></VisibleWrap>
        </FormItem>

       <FormItem>
          <Collapse bordered={true} defaultActiveKey={['1']}>
            <Panel header="媒体文件" key="1" style={customPanelStyle}>
                {getFieldDecorator('mediafile', {
                  initialValue: mediaProps,
                })(<MediaUploader type="video" />)}
            </Panel>
          </Collapse>
        </FormItem>
        <br /><br /><br /><br />
        <FormItem label="图片上传">
          {getFieldDecorator('imgUploader', {
            initialValue: imgListProps
          })(<ImgUploader single={false} />)}
        </FormItem>
        <FormItem label="富文本编辑">
          {getFieldDecorator('htmlContent', {
            initialValue:  editorProps
           })(<DraftEditor />)}
        </FormItem>
        <FormItem>
          <Collapse bordered={true} defaultActiveKey={['1']}>
            <Panel header="图集" key="1" style={customPanelStyle}>
                {getFieldDecorator('album', {
                  initialValue: albumProps,
                  rules: [{ validator: (rule, value, callback) => {
                      if (value.fileList.length <= 6) {
                        callback();
                        return;
                      }
                      callback('图片不能添加超过6张!');
                    } }],
                })(<AlbumUploader />)}
            </Panel>
          </Collapse>
        </FormItem>

        <FormItem>
          <UEditor id="ueditorContainer" name="content" 
                    width={'70%'} 
                    height={500} 
                    uconfigSrc='/static/ueditor/ueditor.config.js'
                    ueditorSrc='/static/ueditor/ueditor.all.min.js'
                />
        </FormItem>
        
        <FormItem>
          <Button type="primary" htmlType="submit">Submit</Button>
        </FormItem>
      </Form>
    );
  }
}

const Exa_2 = Form.create()(Demo);

function mapStateToProps ({ exa_2 }) {
  return { exa_2 }
}

export default connect(mapStateToProps)(Exa_2)
