import React from 'react'
import PropTypes from 'prop-types';
import { connect } from 'dva';
import OfficeEditForm from '../../../components/users/officeEditForm';
import { Jt, Immutable } from '../../../utils'

function OfficeEdit({ history, location, dispatch, office }, context) {
	const { tree, curOffice } = office;

	const officeEditFormProps = {
		tree,
    item:curOffice,
    createOffice(office) {
        dispatch({
            type: 'office/create',
            payload: {
                office,
                success: () => {
                    context.router.push('/users/office')
                }
            }
        })
    },
    updateOffice(office) {
        dispatch({
            type: 'office/update',
            payload: {
                office,
                success: () => {
                    context.router.push('/users/office')
                }
            }
        })
    },
    backToList(){
      context.router.push('/users/office')
    }
	}
  return (
    <div className='content-inner'>
      <OfficeEditForm {...officeEditFormProps}/>
    </div>
  );
}
OfficeEdit.contextTypes = {
    router: PropTypes.object.isRequired
}

OfficeEdit.propTypes = {
    location: PropTypes.object,
    dispath: PropTypes.func,
    office: PropTypes.object
}

function mapStateToProps ({ office }) {
  return { office }
}

export default connect(mapStateToProps)(OfficeEdit)
