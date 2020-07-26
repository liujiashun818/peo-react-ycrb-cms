import React from 'react';
import { Button, Popconfirm,Row,Col } from 'antd';

class Submiter extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: props.saveLoading || false
    }
  }
  componentWillReceiveProps(nextProps) {
    // Should be a controlled component.
    if (nextProps.saveLoading) {
      this.setState(
          {saveLoading:nextProps.saveLoading || false}
        );
    }
  }
  render(){
    const { onSave,onBack,onDelete } = this.props;
    return(
        <div className="cus-buttons-wrap">
        <Popconfirm title="确定删除吗？" onConfirm={onDelete}>
          <Button type="danger">删除</Button>
        </Popconfirm>
        <Button onClick={onBack}>返回</Button>
        <Button type="primary" onClick={(e)=>{onSave(e);}} loading={this.state.saveLoading} className="cus-btn-right">保存</Button>
      </div>
      )
  }
}

export default Submiter;