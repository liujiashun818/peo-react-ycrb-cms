import React from 'react'
import PropTypes from 'prop-types';
import CommentList from '../comment/commentList';
import Search from '../comment/search';
import Reply from './reply';
import {
    Modal,
    Table,
    Popconfirm,
    Form,
    Input,
    InputNumber,
    Radio,
    Button,
    Tag
} from 'antd';

class ArtCommentModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
        };
    }

    componentWillReceiveProps(nextProps) {
    }

    render() {
    	const {visible, onCancel, onHide, listProps, searchProps, handleSubmitReply} = this.props;
    	const modalProps = {
    		visible,
    		onHide,
    		onCancel,
    		footer: null,
    		title: '查看评论',
    		width: 1150
    	}
        return (
            <Modal {...modalProps}>
                <Reply handleSubmitReply={handleSubmitReply}/>
            	<Search {...searchProps}></Search>
            	<CommentList {...listProps}/>
            </Modal>
        );
    }
}

export default ArtCommentModal;
