import React from 'react';
import { connect } from 'dva';
import ClientMenuTree from '../../../components/client/clientMenuTree';
import { Jt, Immutable } from '../../../utils'

function formatTreeData(data = []) {
  for(let i = 0; i < data.length; i++) {
    data[i].key = data[i].value = data[i].id + ''
    data[i].label = data[i].name //为了selectTreeFilter准备
    // if(data[i].delFlag == 3){
    //   data.splice(i,1)
    // }
    if(!Jt.array.isEmpty(data[i].child)) {
      data[i].children = data[i].child
      delete data[i].child
      formatTreeData(data[i].children);
    }
  }
  return data;
}
function getFormItem(data,id){
  var item = {};
  function closure(data,id){
    for (var i in data) {
      if (data[i].id == id) {
        item = data[i];
        break;
      }else {
        closure(data[i].child,id);
      }
    }
  }
  closure(data,id)
  return item
}
function ClientList({ location, dispatch, client }) {
  const {list} = client
  const treeData = formatTreeData(Immutable.fromJS(list).toJS())
  const ClientMenuTreeProps = {
    dataSource : treeData,
    onChangeDelflag:function(id,delFlag){
      if(delFlag==0){
        delFlag=1
      }else{
        delFlag=0
      }
      const currentItem = getFormItem(list,id)
      currentItem.delFlag = delFlag
      dispatch({
        type: 'client/update',
        payload: {
          data:{
            ...currentItem
          }
        }
      })
    },
    onDelete:function(id,delFlag){
      dispatch({
        type: 'client/update',
        payload: {
          data:{
            id:id,
            delFlag:3
          }
        }
      })
    }
  }
  return (
    <div className='content-inner'>
      <ClientMenuTree {...ClientMenuTreeProps} />
    </div>
  );
}
function mapStateToProps ({ client }) {
  return { client }
}

export default connect(mapStateToProps)(ClientList)
