import React from 'react'
import PropTypes from 'prop-types'
import { Row, Col, Menu, Dropdown, message ,Modal} from 'antd'
import styles from './toolbar.less'
import VisibleWrap from '../ui/visibleWrap'

const createTypes = [
	{value: 'normal', label: '新闻'},
	{value: 'live', label: '直播'},
	{value: 'topic', label: '专题'},
	{value: 'cite', label: '引用'},
	{value: 'govAffairs', label: '政务'}
]

const createTypesBang = [
	// {value: 'normal', label: '新闻'},
	// {value: 'live', label: '直播'},
	// {value: 'topic', label: '专题'},
	// {value: 'cite', label: '引用'},
	{value: 'help', label: '公益'},
	{value: 'cite', label: '引用'},
]
const confirm = Modal.confirm;
function toggleLine(selArts,onOffArts,key){
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
function Toolbar ({
	selArts,
	wgtChgObj,
	saveWeight,
	onOffArts,
	createArt,
	listType,
	relase,
	relaseCancel,
	restore,
	createtype
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
		if(key =='release'){
			if(selArts.length === 0) {
                message.error('请选择需要执行批量操作的文章')
                return
			}
			Modal.confirm({
				title:"确定提前发布吗",
				onOk:()=>{
					console.log("取消发布");
					relase(selArts);
				}
			})
			
		}

		if(key =='releaseCancel'){
			if(selArts.length === 0) {
                message.error('请选择需要执行批量操作的文章')
                return
			}
			Modal.confirm({
				title:"确定取消发布吗",
				onOk:()=>{
					relaseCancel(selArts);
				}
			})
			
		}

		if(key =='restore'){
			if(selArts.length === 0) {
                message.error('请选择需要执行批量操作的文章')
                return
			}
			Modal.confirm({
				title:"确定还原吗",
				onOk:()=>{
					console.log("确定还原吗?");
					restore(selArts)
				}
			})
			
		}

        if(key === 'off'){
            if(selArts.length === 0) {
                message.error('请选择需要执行批量操作的文章')
                return
            }
            confirm({
                title: '批量下线',
                content: '确定批量下线',
                onOk() {
                    toggleLine(selArts,onOffArts,key)
                },
                onCancel() {
                    console.log('关闭批量下线');
                },
            });
        }else if(key === 'on'){
            if(selArts.length === 0) {
                message.error('请选择需要执行批量操作的文章')
                return
            }
            toggleLine(selArts,onOffArts,key);
        }
	}

	function createArtHandle(e) {
		createArt(e.key || 'normal');
	}

	const batchMenu = (
		<Menu onClick={batchMenuClick}>
			<Menu.Item key="on">批量上线</Menu.Item>
			<Menu.Item key="off">批量下线</Menu.Item>
			<Menu.Item key="saveWeight">保存权重</Menu.Item>
		</Menu>
	)
	const batchMenuTime = (
		<Menu onClick={batchMenuClick}>
			<Menu.Item key="release">提前发布</Menu.Item>
			<Menu.Item key="releaseCancel">取消发布</Menu.Item>
		</Menu>
	)
	const batchMenuRetore = (
		<Menu onClick={batchMenuClick}>
			<Menu.Item key="restore">批量还原</Menu.Item>
			
		</Menu>
	)
	const articleMenu = (
		<Menu onClick={createArtHandle}>
			{
				(createtype=='8'?createTypesBang:createTypes).map((item, index) => {
					return <Menu.Item key={item.value}>{item.label}</Menu.Item>
				})
			}
		</Menu>
	)
	return (
		<div>
			<Row gutter={24} className={styles.toolbar}>
				<Col lg={12} md={12} sm={12} xs={24}>
					<VisibleWrap permis="edit:online">
						<Dropdown.Button overlay={listType!=3&&listType!=5?batchMenu:(listType==5?batchMenuTime:batchMenuRetore)}>
							批量操作
						</Dropdown.Button>
					</VisibleWrap>
				</Col>
				<Col lg={12} md={12} sm={12} xs={24} className={styles.rightCol}>
					{
						listType!=3&&listType!=5?
						<Dropdown.Button onClick={createArtHandle} overlay={articleMenu} type="primary">
						新建文章
					</Dropdown.Button>:
					''
					}
					
				</Col>
			</Row>
		</div>
	)
}

Toolbar.propTypes = {
	selArts: PropTypes.array,
	wgtChgObj: PropTypes.object,
	saveWeight: PropTypes.func,
	onOffArts: PropTypes.func,
	createArt: PropTypes.func
}

export default Toolbar
