import React from 'react'
import PropTypes from 'prop-types';
import { Modal, Row, Col, Tabs } from 'antd';

class BlockTabs extends React.Component {
	constructor(props) {
		super(props);
	}

	onEdit(targetKey, action) {
		if(action === 'add') {
			this.props.addBlock();
		}
		else if(action === 'remove') {
			Modal.confirm({
                content: '确认删除？',
                onOk: () => {
 					this.props.deleteBlock(targetKey);
                }
            })
			
		}
	}

	onChange(activeKey) {
		this.props.switchBlock(activeKey);
	}

	getTabTt(title='') {
		if(title.length <= 15) {
			return title;
		}
		else {
			return title.substring(0, 15) + '...';
		}
	}

	render() {
		let {blocks, curBlock} = this.props;
		return (
			<div>
				<Tabs
					type="editable-card"
					activeKey={curBlock.id+''}
					onEdit={this.onEdit.bind(this)}
					onChange={this.onChange.bind(this)}
				>
					{
						blocks.map(block => {
							return (
								<Tabs.TabPane key={block.id} closable={true} tab={this.getTabTt(block.title)} />
							);
						})
					}
  				</Tabs>
			</div>
		);
	}
}

export default BlockTabs;