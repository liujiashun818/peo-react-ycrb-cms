import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import ClientEditForm from '../../../components/client/clientEditForm';
import { Spin } from 'antd'
import { Jt, Immutable } from '../../../utils'

function formatTreeData(data = []) {
  for(let i = 0; i < data.length; i++) {
    data[i].key = data[i].value = data[i].id + ''
    data[i].label = data[i].name //为了selectTreeFilter准备
    if(!Jt.array.isEmpty(data[i].child)) {
      data[i].children = data[i].child
      delete data[i].child
      formatTreeData(data[i].children);
    }
  }
  return data;
}
let flag = false
function ClientMenuEdit({ location, dispatch, client }, context) {
	const {list,type,id,sysTreeData,viewTreeData,categoryTreeData=[],currentItem} = client
	const editType = location.search.split('&')[1].split('=')[1];
	if(editType=='update'){
        flag = !flag;
    }
	let treeData = formatTreeData(Immutable.fromJS(list).toJS())
	let categoryTree = formatTreeData(Immutable.fromJS(categoryTreeData).toJS())
    let positionTreeData = [
    {id:1,key:'1',label:'默认',value:'NORMAL'},
    {id:2,key:'2',label:'首次置顶',value:'ONE_TOP'},
    {id:3,key:'3',label:'总是置顶',value:'ALWAYS_TOP'},
    {id:4,key:'4',label:'固定置顶',value:'FIX'},
  ]
	// treeData = [{id:1,key:'1',label:'顶级菜单',value:'1',name:'顶级菜单',children:[...treeData]}] //树结构中加入顶级菜单
	let handleSubmit = function(data){
   if(type=='update'){
      data.id = id
      dispatch({
        type:'client/update',
        payload:{
          data:data,
          callback:() => {
            context.router.push('/client/clientMenu')
          }
        }
      })
    }else{
      dispatch({
        type:'client/create',
        payload:{
          data:data,
          callback:() => {
            context.router.push('/client/clientMenu')
          }
        }
      })
    }
	}
    let jumpToLink1 = function(){
    context.router.push('/client/clientMenu')
    dispatch({
      type:'client/clearCurrentItem'
    })
  }
	let clientEditFormProps = {
		viewTreeData,
		sysTreeData,
		categoryTree,
    positionTreeData,
		treeData:treeData,
    item:currentItem,
    jumpToLink1,
		handleSubmit,
	}
  return (
    <div className='content-inner'>
        {/*{editType=='add'?<ClientEditForm {...clientEditFormProps}/>:
            <div>*/}
                {flag?<Spin tip='加载中...' style={{position:'fixed',top:"50%",left:"50%"}} size="large" />:null}
                {flag?null:<ClientEditForm {...clientEditFormProps}/>}
            {/*</div>
        }*/}
    </div>
  );
}


ClientMenuEdit.contextTypes = {
    router: PropTypes.object.isRequired
}
function mapStateToProps ({ client },context) {
  return { client ,context}
}

export default connect(mapStateToProps)(ClientMenuEdit)
