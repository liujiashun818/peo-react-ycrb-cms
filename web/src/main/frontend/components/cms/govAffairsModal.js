import React from 'react'
import PropTypes from 'prop-types';
import {
    Modal,
    Table,
    Form,
    Input,
    Button,
    Row,
    Col
} from 'antd';
import {artTypes as types} from '../../constants';
import CiteArtSearch from './citeArtSearch';
import {Immutable} from '../../utils';

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 1 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 },
  },
};

class GovAffairsModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            selArts: [],
            titleValue: '',
            flag :false
        };
    }

    componentWillReceiveProps(nextProps) {
        this.setState({flag:false})
        if (!nextProps.visible) {
            this.state.selArts = [];
        }
    }

    onOk() {

        if(this.state.flag == false){
            this.state.flag = true;
            this.setState({flag:true},()=>{
                const {catgId, onCiteGa, gaq} = this.props;
                const obj = {
                    categoryId: catgId,
                    articleIds: this.state.selArts.map((item) => {return item.id}).join(','),
                }
                onCiteGa(obj);
            })

        }

    }

    getColumns() {
        const {artTypes} = this.props;
        return [
            {
                title: '机构',
                dataIndex: 'orgname',
                key: 'orgname'
            }, {
                title: '标题',
                dataIndex: 'title',
                key: 'title'

            }, {
                title: '发布时间',
                dataIndex: 'publishDate',
                key: 'publishDate',
                width: 250,
                render: (text, record) => {
                    if (text) {
                        return (new Date(text)).format('yyyy-MM-dd hh:mm:ss')
                    } else {
                        return ''
                    }
                }
            }
        ];
    }

    selectArt(selArts) {
        const cloneData = Immutable.fromJS(selArts).toJS()
        cloneData.map((item,index)=>{
            item.weight = 0
        })
        this.setState({selArts:cloneData});
    }
    onSearch() {
    	this.props.onSearch({title: this.state.titleValue})
    }
    render() {
        const {
            catgs,
            visible,
            loading,
            pagination,
            dataSource,
            onCancel,
            onPageChange
        } = this.props;
        const {selArts, titleValue} = this.state;

        const modalProps = {
            visible,
            title: '引用政务',
            width: '80%',
            onCancel,
            onOk: this.onOk.bind(this)
        };


        const rowSelection = {
            selectedRowKeys: selArts.map(art => art.id),
            onChange: (ids, arts) => {
                this.selectArt(arts)
            }
        };
        return (
            <Modal {...modalProps}>
            	<Form.Item
            	  {...formItemLayout}
		          label="标题"
		        >
		          <Row gutter={8}>
		            <Col span={8}>
		                <Input value={titleValue} onChange={(e) => {this.setState({titleValue: e.target.value})}}/>
		            </Col>
		            <Col span={8}>
		              <Button type="primary" onClick={this.onSearch.bind(this)}>查询</Button>
		            </Col>
		          </Row>
		        </Form.Item>
                <Table
                    simple
                    bordered
                    scroll={{y: 400}}
                    columns={this.getColumns()}
                    dataSource={dataSource}
                    pagination={pagination}
                    rowKey={record => record.id}
                    rowSelection={rowSelection}
                    loading={loading}
                    onChange={onPageChange}
                />
            </Modal>
        );
    }
}

export default GovAffairsModal;
