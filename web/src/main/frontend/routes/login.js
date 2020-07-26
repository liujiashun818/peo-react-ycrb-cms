import React from 'react'
import PropTypes from 'prop-types'
import { Button, Row, Form, Input ,Checkbox,Col,Message} from 'antd'
import { config } from '../utils'
import styles from './login.less'
import { Base64 } from 'js-base64';
const FormItem = Form.Item
let rememberMe = 'no';

const login = ({
  loginButtonLoading,
  onOk,
  form: {
    getFieldDecorator,
    validateFieldsAndScroll
  }
}) => {
  function changeCode(e) {
     document.getElementById("code").src = "/captcha/captcha.jpg?time="+(new Date().getTime())
  }
  function handleOk () {
    validateFieldsAndScroll((errors, values) => {
      if (errors) {
        return
      }
      if(values.rememberMe){
        rememberMe = 'yes';
      }
      values.username = Base64.encode(values.username);
       values.password = Base64.encode(values.password);
      onOk(Object.assign({},values,{rememberMe:rememberMe},{'callback':()=>{changeCode();Message.error('验证码错误')}}))
    })
  }

  // document.onkeyup = e => e.keyCode === 13 && handleOk()

  return (
    <div className={styles.form}>
      <div className={styles.logo}>
        <img src={config.logoSrc} />
        <span>新闻客户端CMS</span>
      </div>
      <form>
        <FormItem hasFeedback>
          {getFieldDecorator('username', {
            rules: [

                {
                    validator: (rule, value, callback) => {
                        if (value == undefined || value == null || value.replace(/\s/gi, '').length == 0) {
                            callback('请填写用户名');
                            return false;
                        }
                        callback();
                    }
                }
            ]
          })(<Input size='large' onPressEnter={handleOk} placeholder='用户名' />)}
        </FormItem>
        <FormItem hasFeedback>
          {getFieldDecorator('password', {
            rules: [
                {
                    validator: (rule, value, callback) => {
                        var patt1 = /[a-z0-9A-Z]+$/
                        if (value == undefined || value == null || value.replace(/\s/gi, '').length == 0) {
                            callback('请填写密码');
                            return false;
                        }
                        callback();
                    }
                }
            ]
          })(<Input  size='large' type='password' onPaste={()=>{return false}} onCopy={()=>{return false}}  onPressEnter={handleOk} placeholder='密码' />)}
        </FormItem>


              <Row gutter={8}>
                  <Col span={10}>
                      <FormItem hasFeedback>
                      {getFieldDecorator('validateCode', {
                          rules: [
                              {
                                  validator: (rule, value, callback) => {
                                      var patt1=/[a-z0-9A-Z]+$/
                                      if (value==undefined||value==null||value.replace(/\s/gi,'').length== 0) {
                                          callback('请输入验证码');
                                          return false;
                                      } else if(!patt1.test(value)) {
                                          callback('验证码格式错误');
                                          return;
                                      }
                                      callback();
                                  }
                              }
                          ]
                      })(<Input style={{'display':'inline-block'}}  size='large' type='text' maxLength="4" onPressEnter={handleOk} placeholder='验证码' />)}
                      </FormItem>
                  </Col>
                  <Col span={14}>
                      <a href="javascript:;" className={styles.vcode} onClick={(e)=>{changeCode(e)}}><img style={{'width':'70%'}} id={'code'}  onClick={(e)=>{changeCode(e)}} src={"/captcha/captcha.jpg"} alt=""/>
                     看不清?</a>
                  </Col>
              </Row>

        <FormItem>
          {getFieldDecorator('rememberMe')(<Checkbox>记住登录</Checkbox>)}
        </FormItem>
        <Row>
          <Button type='primary' size='large' onClick={handleOk} loading={loginButtonLoading}>
            登录
          </Button>
        </Row>
      </form>
    </div>
  )
}

login.propTypes = {
  form: PropTypes.object,
  loginButtonLoading: PropTypes.bool,
  onOk: PropTypes.func
}

export default Form.create()(login)
