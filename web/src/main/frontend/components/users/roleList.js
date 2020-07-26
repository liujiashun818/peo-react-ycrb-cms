import React from 'react'
import PropTypes from 'prop-types';
import { Table, Popconfirm, Row, Col, Button } from 'antd';
import { Link } from 'dva/router';
import VisibleWrap from '../ui/visibleWrap'
import { routerPath,fieldMap } from '../../constants';

class RoleList extends React.Component {
	constructor(props) {
		super(props);
	}

	getColumns() {
		const {dataScopes, deleteRole} = this.props;
		return [{
			title: fieldMap.ROLE_NAME,
			dataIndex: 'name',
			key: 'name'
		},{
			title: fieldMap.OFFICE_NAME,
			dataIndex: 'officeName',
			key: 'officeName'
		},{
			title: fieldMap.DATASCOPE,
			dataIndex: 'dataScope',
			key: 'dataScope',
			render: (text, record) => {
				for(let i = 0, len = dataScopes.length; i < len; i++) {
					if(dataScopes[i].value == record.dataScope) {
						return dataScopes[i].label;
					}
				}
				return text;
			}
		},{
			title: fieldMap.ACTION,
			key: 'action',
			render: (text, record) => {
				return (
					<p>
						<VisibleWrap permis="view">
							<Link to={{pathname: routerPath.ROLE_EDIT, query: {id: record.id, action: 'update'}}}>修改</Link>
						</VisibleWrap>
						<span className="ant-divider"></span>
						<VisibleWrap permis="edit:delete">
							<Popconfirm title="确定删除吗？" onConfirm={() => deleteRole(record.id)}>
	                    		<a>删除</a>
	                		</Popconfirm>
                		</VisibleWrap>
					</p>
				);
			}
		}];
	}

	render() {
		const {loading, dataSource, addRole} = this.props;
		return (
			<div>
				<Row gutter={24} style={{ marginBottom: '10px' }}>
					<Col style={{ textAlign: 'right' }}>
						<VisibleWrap permis="edit:modify">
							<Button type="primary" size="large" onClick={addRole}>添加角色</Button>
						</VisibleWrap>
					</Col>	
				</Row>
				<Table
					simple
		            bordered
		            scroll={{ x: 1200 }}
		            columns={this.getColumns()}
		            dataSource={dataSource}
		            loading={loading}
		            rowKey={record => record.id}
		            pagination={false}
				/>
			</div>
		);
	}
}

export default RoleList;