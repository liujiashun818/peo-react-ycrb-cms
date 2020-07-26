import React,{Component} from 'react';
import { EditorState, Modifier } from 'draft-js';
import intend from '../../image/intend.svg';

class CustomOption extends Component {

    addIndent = () => {
      const { editorState, onChange } = this.props;
      const contentState = Modifier.replaceText(
        editorState.getCurrentContent(),
        editorState.getSelection(),
        '    ',
        editorState.getCurrentInlineStyle(),
      );
      onChange(EditorState.push(editorState, contentState, 'insert-characters'));
    };

    render() {
      return (
        <div className="rdw-option-wrapper" onClick={this.addIndent} title={'Indent'}>
            <img src={intend} alt=""/>
        </div>
      );
    }
  }

  export default CustomOption;

