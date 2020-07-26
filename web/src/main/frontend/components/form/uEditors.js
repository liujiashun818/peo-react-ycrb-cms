import React from 'react';

export default class Ueditor extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      id: this.props.id ? this.props.id : null,
      ueditor: null,
    };
  }
  componentDidMount() {
    // const { id } = this.state;
    const props = this.props;
    const { _id } = props;
    const waitUntil = () => {
      const UE = window.UE;
      // UE.delEditor(_id);
      const ueditor = UE.getEditor(_id, {
        initialFrameWidth: props.width || '100%',
        initialFrameHeight: props.height || 200,
        // autoHeightEnabled: true,
        autoFloatEnabled: false,
      });
      ueditor.ready(() => {
        ueditor.setContent(props.html);
        ueditor.setEnabled();

        if (props.getContent) {
          props.getContent(ueditor.getContent);
        }
      });
    }
    if (_id) {
      const UE = window.UE;
      try {
        /* 加载之前先执行删除操作，否则如果存在页面切换，
        再切回带编辑器页面重新加载时不刷新无法渲染出编辑器*/

        waitUntil()
      } catch (e) {
        waitUntil()
        // e
      }

      // ueditor.setContent(this.props.html || '')
    }
  }
  render() {
    const { _id } = this.props;
    return (
      <div>
        <textarea id={_id} />
      </div>
    );
  }
}
