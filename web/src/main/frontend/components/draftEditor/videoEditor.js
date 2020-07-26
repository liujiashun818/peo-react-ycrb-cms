import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { EditorState, Modifier } from 'draft-js';
import { Editor } from 'react-draft-wysiwyg';


class CustomOption extends Component {
    constructor(props){
        super(props)
       this.addStar = this.addStar.bind(this)
    }
  static propTypes = {
    onChange: PropTypes.func,
    editorState: PropTypes.object,
  };

  addStar() {
    const { editorState, onChange } = this.props;
    const contentState = Modifier.replaceText(
      editorState.getCurrentContent(),
      editorState.getSelection(),
      '⭐⭐⭐video⭐⭐⭐\n',
      editorState.getCurrentInlineStyle(),
    );
    onChange(EditorState.push(editorState, contentState, 'insert-characters'));
  };

  render() {
    return (
      <div className='rdw-option-wrapper' onClick={this.addStar} title={'视频标记'}>插入视频标记</div>
    );
  }
}
export default CustomOption
