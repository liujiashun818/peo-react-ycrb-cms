import React from 'react';
import { connect } from 'dva';
import MenusList from '../../../components/menus/list';
import MenusModal from '../../../components/menus/modal';
import { Jt, Immutable } from '../../../utils'

function formatTreeData(data = []) {
  for(let i = 0; i < data.length; i++) {
    data[i].key = data[i].value = data[i].id + ''
    if(!!data[i].show){
      data[i].show = '可见'
    }else{
      data[i].show = '隐藏'
    }
    // data[i].label = data[i].name
    if(!Jt.array.isEmpty(data[i].child)) {
      data[i].children = data[i].child
      delete data[i].child
      formatTreeData(data[i].children);
    }
  }
  return data;
}

function Menus({ location, dispatch, menus }) {
	const { list, currentItem, modalVisible, modalType } = menus
  const listData = formatTreeData(Immutable.fromJS(list).toJS())
	const menusListProps = {
  	dataSource:listData,
    onDeleteItem (id) {
      dispatch({
        type: 'menus/delete',
        payload: id
      })
    },
    onEditItem (item) {
      item.parentMenu = '顶级菜单'
      function getArray(data,id){
        for (var i in data) {
            if (data[i].id == id) {
                item.parentMenu = data[i].name;
                break;
            } else {
                getArray(data[i].children, id);
            }
        }
      }
      getArray(listData,item.parentId)
      dispatch({
        type: 'menus/showModal',
        payload: {
          modalType: 'update',
          currentItem: item 
        }
      })
    }
  }
  return (
    <div className='content-inner'>
   		<MenusList {...menusListProps} />
    </div>
  );
}
function mapStateToProps ({ menus }) {
  return { menus }
}

export default connect(mapStateToProps)(Menus)