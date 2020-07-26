import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { routerPath } from '../../../constants';
import FieldGroupEdit from '../../../components/fields/fieldGroupEdit';

class FieldGroupEditPage extends React.Component {
    constructor(props, context) {
        super(props,context);
        this.state = {
            saveLoading:false
        }
    }
  
    render(){
        const {location, dispatch, fieldGroup} = this.props;
        const context = this.context;
        const { curItem,catgsTree,catgModels,fieldList } = fieldGroup;
        const submitFail = () => {
            this.setState({saveLoading: false});
        };
        const submitBefore = ()=> {
            this.setState({saveLoading: true});
        };
        const formProps = {
            item:curItem,
            catgsTree,
            catgModels,
            fieldList,
            save(item) {
                dispatch({
                    type: 'fieldGroup/effect:save:fieldGroup',
                    payload: {
                        item,
                        success: () => {
                            context.router.goBack()
                        },
                        submitFail
                    }
                });
            },
            del(){
                dispatch({
                    type: 'fieldGroup/effect:delete:fieldGroup',
                    payload: {
                        item:{
                            id:curItem.id
                        },
                        success: () => {
                            context.router.goBack()
                        },
                        submitFail
                    }
                });
            },
            onBack(){
                context.router.goBack()
            },
            submitBefore,
            submitFail
        };
        return (
            <div className='content-inner'>
                <FieldGroupEdit {...formProps} saveLoading={this.state.saveLoading} />
            </div>
        );
    }
}

FieldGroupEditPage.contextTypes = {
    router: PropTypes.object.isRequired
};

FieldGroupEditPage.propTypes = {
    location: PropTypes.object,
    dispath: PropTypes.func,
    fieldGroup: PropTypes.object
};

function mapStateToProps ({ fieldGroup }) {
    return { fieldGroup };
}

export default connect(mapStateToProps)(FieldGroupEditPage);
