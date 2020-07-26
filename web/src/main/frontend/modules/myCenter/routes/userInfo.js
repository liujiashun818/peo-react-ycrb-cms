import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router'
import { routerPath } from '../../../constants'
import UserForm from '../../../components/myCenter/userInfo'
import { message } from 'antd'
function UserInfo ({location, dispatch, userInfo },context){
	const formProps = {
		currentItem:userInfo.currentItem,
		handleSubmit(data){
			dispatch({
				type:'userInfo/update',
				payload:{
					data,
					callback:function(res){
						if(res.code==0){
							message.success('保存成功');
						}
						else if(res.code==-1){
							message.success('原密码有误');
						}else if(res.code==-2){
							message.success('系统登陆超时，请重新登录1');
						}else if(res.code==-3){
							message.success('没有登录');
						}else if(data.code === -6){
                            message.success('系统登陆超时，请重新登录2');
                        }else{
							message.success('没有权限');
						}
					}
				}
			})
		}
	}
	return (
		<div className="content-inner">
			<UserForm {...formProps}></UserForm>
		</div>
		)
}
function mapStateToProps ({ userInfo }) {
  return { userInfo }
}

export default connect(mapStateToProps)(UserInfo)
