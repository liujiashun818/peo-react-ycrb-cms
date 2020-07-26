import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import DictForm from '../../../components/sys/dictForm';

function DictEdit ({location, dispatch, dict },context){
	const {currentItem={}} = dict
	const formProps = {
		currentItem,
		goBack(){
			context.router.goBack()
		},
		handleSubmit(data){
			if(location.query.type == 'update'){
				data.id = currentItem.id
				dispatch({
					type:'dict/update',
					payload:{
						data,
			            callback:() => {
			              context.router.goBack()
			            }
					},
				})	
			}else{
				dispatch({
					type:'dict/create',
					payload:{
						data,
			            callback:() => {
			              context.router.goBack()
			            }
					},
				})
			}
		}
	}
	return (
		<div className="content-inner">
			<DictForm {...formProps}></DictForm>
		</div>
	)
}
DictEdit.contextTypes = {
    router: PropTypes.object.isRequired
}
function mapStateToProps ({ dict }) {
  return { dict }
}

export default connect(mapStateToProps)(DictEdit)