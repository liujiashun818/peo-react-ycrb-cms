import React from 'react';
import { connect } from 'dva';
import FieldGroupList from '../../../components/fields/fieldGroupList';
import { message } from 'antd';

function FieldGroupListPage({ location, dispatch, fieldGroup }) {
    const props = {
        dataSource : fieldGroup.list,
        pagination: fieldGroup.pagination,
        selectedRowKeys: fieldGroup.selectedRowKeys,
        onPageChange: function(page) {
            dispatch({
                type: 'field/effect:query:fieldGroups',
                payload: {
                    pageNumber: page.current,
                    pageSize: page.pageSize
                }
            });
        },
        onBatchDelete:function(){
            const ids = fieldGroup.selectedRowKeys;
            if(!ids || ids.length===0){
                message.error('请选择要删除的条目');
                return
            }
            dispatch({
                type:'fieldGroup/effect:delete:batchDelete',
                payload:{
                  ids
                }
            })
        },
        onSelectedRowKeys:function(selectedRowKeys){
            dispatch({
                type: 'fieldGroup/reducer:update',
                payload: {
                    selectedRowKeys
                }
            })
        },
        onDelete:function(id){
            dispatch({
                type: 'fieldGroup/effect:delete:fieldGroup',
                payload: {
                    item:{
                        id:id
                    },
                    pager
                }
            })
        }
    }
    return (
        <div className='content-inner'>
            <FieldGroupList {...props} />
        </div>
    );
}
function mapStateToProps ({ fieldGroup }) {
    return { fieldGroup }
}

export default connect(mapStateToProps)(FieldGroupListPage)
