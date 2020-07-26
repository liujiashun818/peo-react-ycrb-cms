import React from 'react';
import { connect } from 'dva';
import OfficeTree from '../../../components/users/officeList';
import { Jt, Immutable } from '../../../utils'

function OfficeList({ location, dispatch, office }) {
  const officeTreeProps = {
    dataSource : office.tree,
    onDelete:function(id){
      dispatch({
        type: 'office/delete',
        payload: {
          office:{
            id:id
          }
        }
      })
    }
  }
  return (
    <div className='content-inner'>
      <OfficeTree {...officeTreeProps} />
    </div>
  );
}
function mapStateToProps ({ office }) {
  return { office }
}

export default connect(mapStateToProps)(OfficeList)
