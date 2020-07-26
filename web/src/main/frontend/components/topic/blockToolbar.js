import React from 'react'
import PropTypes from 'prop-types';
import { Row, Col, Menu, Dropdown, message } from 'antd';
import styles from './blockToolbar.less'

const createTypes = [
	{value: 'add', label: '添加'},
	{value: 'cite', label: '引用'}
]

function BlockToolbar ({
	selArts,
	wgtChgObj,
	curBlock,
    loading,
	saveWeight,
	onOffArts,
	createArt
}) {
	function batchMenuClick(menu) {
		const key = menu.key
		if(key === 'saveWeight') {
			const arts = []
			for(let i in wgtChgObj) {
				arts.push({
					id: i,
					weight: wgtChgObj[i]
				})
			}
			if(arts.length === 0) {
				message.error('请修改后再保存')
				return
			}
			saveWeight(arts)
		}
		if(key === 'on' || key === 'off') {
			if(selArts.length === 0) {
				message.error('请选择需要执行批量操作的文章')
				return
			}
			const onIds = [];
			const offIds = [];
			for(let i = 0; i < selArts.length; i++) {
				if(selArts[i].delFlag === 0) {
					offIds.push(selArts[i].id)
				}
				else {
					onIds.push(selArts[i].id)
				}
			}
			if(key === 'on' && offIds.length > 0) {
				message.error('选择的文章中包含上线文章')
				return
			}
			else if(key === 'off' && onIds.length > 0) {
				message.error('审核中的文章不能下线')
				return
			}
			const ids = onIds.length > 0 ? onIds : offIds

			onOffArts(ids.join(','), key === 'on' ? '上线' : '下线')
		}
	}

	function createArtHandle(e) {
		if(!curBlock.id) {
			message.error('请先添加区块');
			return;
		}
		createArt(e.key || 'add');
	}

	const batchMenu = (
		<Menu onClick={batchMenuClick}>
			<Menu.Item disabled={loading?true:false} key="on">批量上线</Menu.Item>
			<Menu.Item disabled={loading?true:false} key="off">批量下线</Menu.Item>
			<Menu.Item disabled={loading?true:false} key="saveWeight">保存权重</Menu.Item>
		</Menu>
	)
	const createMenu = (
		<Menu onClick={createArtHandle}>
			{
				createTypes.map((item, index) => {
					return <Menu.Item key={item.value}>{item.label}</Menu.Item>
				})
			}
		</Menu>
	)
	return (
		<div>
			<Row gutter={24} className={styles.blockToolbar}>
				<Col lg={12} md={12} sm={12} xs={24}>
					<Dropdown.Button overlay={batchMenu}>
						批量操作
					</Dropdown.Button>
				</Col>
				<Col lg={12} md={12} sm={12} xs={24} className="right-col">
					<Dropdown.Button disabled={loading?true:false} onClick={createArtHandle} overlay={createMenu} type="primary">
						新建文章
					</Dropdown.Button>
				</Col>
			</Row>
		</div>
	)
}

export default BlockToolbar;
