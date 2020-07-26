import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import SearchTable from '../../../components/client/searchKeywords';
import { routerPath } from '../../../constants';
import { Jt, Immutable } from '../../../utils';

function SearchKeywords({ location, dispatch, searchKeywords },context) {
	  const stConfig = {
	  	dataSource: searchKeywords.keywordsList,
	  	showModal: searchKeywords.showModal,
	  	handleShowModal() {
			dispatch({
  				type: 'searchKeywords/updateState',
  				payload: {
  					showModal: true
  				}
  			});
	  	},
	  	handleHideModal() {
			dispatch({
  				type: 'searchKeywords/updateState',
  				payload: {
  					showModal: false
  				}
  			});
	  	},
	  	handleSubmit(values, id) {
  			dispatch({
  				type: 'searchKeywords/effects:addKeywords',
  				payload: {...values, id}
  			});
	  	},
	  	handleDelete(id) {
  			dispatch({
  				type: 'searchKeywords/effects:deleteKeywords',
  				payload: {
  					id
  				}
  			});
	  	}
	  }
	  return (
	    <div>
			<SearchTable {...stConfig}/>
	    </div>
	  );
}
SearchKeywords.contextTypes = {
    router: PropTypes.object.isRequired
}
function mapStateToProps ({ searchKeywords }) {
  return { searchKeywords }
}

export default connect(mapStateToProps)(SearchKeywords)