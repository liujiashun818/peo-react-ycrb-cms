import React from 'react';
import {connect} from 'dva';
import FieldList from '../../../components/fields/fieldList';
import FieldEditModal from '../../../components/fields/fieldEditModal';

function FieldListPage({location, dispatch, field}) {
	
	function updateState(obj) {
		dispatch({
			type: 'field/reducer:update',
			payload: obj
		});
	}

	const listProps = {
		loading: field.loading,
		dataSource: field.list,
		pagination: field.pagination,
		selectedIds: field.selectedIds,
		updateState,
		onPageChange: (page) => {
			dispatch({
				type: 'field/effect:query:fields',
				payload: {
					pageNumber: page.current,
					pageSize: page.pageSize
				}
			});
		},
		deleteHandle: (id) => {
			dispatch({
				type: 'field/effect:delete:field',
				payload: {
					id
				}
			});
		},
		batchDeleteHandle: (ids) => {
			dispatch({
				type: 'field/effect:delete:fields',
				payload: {
					ids
				}
			});
		}
	};

	const femProps = {
		visible: field.femv,
		curField: field.curField,
		updateState,
		updateField: (field) => {
			dispatch({
				type: 'field/effect:update:field',
				payload: field
			});
		}
	}
	return (
		<div className="content-inner">
			<FieldList {...listProps}/>
			<FieldEditModal {...femProps}/>
		</div>
	);
}

function mapStateToProps({field}) {
	return {field};
}

export default connect(mapStateToProps)(FieldListPage);

