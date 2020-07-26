import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import MenusForm from '../../../components/menus/form';
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

function Menus({ location, dispatch, menus }, context) {
	const { list, parentId, id, type, modalVisible, selectedIcon,submitIcon,listView=[]} = menus //type为传进来的编辑方法 是update还是add
  const treeData = formatTreeData(Immutable.fromJS(list).toJS())
  const treeViewData = formatTreeData(Immutable.fromJS(listView).toJS())
  let item = {}
  let parentMenu = '顶级菜单'
  function getArray(data,parentId,id){
    for (var i in data) {
      if(parentId==0){
        if(data[i].id==id){
          item = data[i]
          break;
        }
      }else{
        if (data[i].id == parentId) {
            parentMenu = data[i].name;
            for(var j in data[i].child){
              if(data[i].child[j].id == id){
                item = data[i].child[j];
                break;
              }
            }              
            break;
        }else{
            getArray(data[i].child, parentId, id);
        }
      }
    }
  }
  getArray(list,parentId,id)
  const FormProps = {
    treeData,
    treeViewData,
    modalVisible,
    selectedIcon,
    handleSubmit(data){ //拿数据submit请求借口
      if(type=='update'){
        data.id = id
        dispatch({
          type:'menus/update',
          payload:{
            data:data,
            callback:() => {
              context.router.push('/sys/menu')
            }
          }
        })        
      }else{
        dispatch({
          type:'menus/create',
          payload:{
            data:data,
            callback:() => {
              context.router.push('/sys/menu')
            }
          }
        })       
      }
    },
    onOk () {
      dispatch({
        type: 'menus/showModal'
      })
    },      
    onCancel () {
      dispatch({
        type: 'menus/hideModal'
      })
    },
    onSubmitIcon(submitIcon){
      dispatch({
        type:'menus/changeIcon',
        payload:{
          submitIcon:submitIcon
        }
      })
    },
    onChangeIcon (selectedIcon){
      dispatch({
        type:'menus/changeIcon',
        payload:{
          selectedIcon:selectedIcon
        }
      })
    }
  }
  if(type=='update'){
    FormProps.item = item
    FormProps.submitIcon = submitIcon 
  }else{ //点击添加下级菜单情况(新增也是添加下级菜单)
      FormProps.item = {}
      FormProps.submitIcon = submitIcon 
      FormProps.item.parentId = item.id || 1
  }
  return (
    <div className='content-inner'>
      <MenusForm {...FormProps}/>
    </div>
  );
}
Menus.contextTypes = {
    router: PropTypes.object.isRequired
}
function mapStateToProps ({ menus }) {
  return { menus }
}

export default connect(mapStateToProps)(Menus)