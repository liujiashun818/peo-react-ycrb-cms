import React from 'react';
import { Editor,bold,italic,underline,strikethrough,monospace,superscript,subscript,fontSize,unordered,ordered,indent,outdent,left,center,right,justify,color,link,unlink,emoji,image,eraser,undo,redo} from 'react-draft-wysiwyg';
import 'react-draft-wysiwyg/dist/react-draft-wysiwyg.css';
import styles from './editor.less';
import { request } from '../../utils';
import {
		CompositeDecorator,
        ContentBlock,
        ContentState,
        EditorState,
        convertFromHTML,
        convertToRaw,
        convertFromRaw
} from 'draft-js';
import draftToHtml from 'draftjs-to-html';
import htmlToDraft from 'html-to-draftjs';
import ImageDragger from '../draftEditor/image-dragger';
import List from '../draftEditor/list-component';
import TextIndent from '../draftEditor/text-indent';
import ImageDrag from '../draftEditor/image-drag';
import VideoEditor from '../draftEditor/videoEditor';

import { connect } from 'dva'

const uploadCallback = function(file) {
	let formData = new FormData();
    formData.append("file", file);
	return new Promise(
		(resolve, reject) => {
			const res = request('/api/upload/file', {
			    method: 'post',
			    data: formData
			},true).then((data) => {
                console.log(data,"返回数据")
		        resolve({ data: { link: data.data.fileUrl } });
		    }).catch((data)=> {
		        reject(data);
            });
            //这是调接口的地方
		}
	)
};

const styleMap = {
    'fontsize-8': {'fontSize': '8px'},
    'fontsize-9': {'fontSize': '9px'},
    'fontsize-10': {'fontSize': '10px'},
    'fontsize-11': {'fontSize': '11px'},
    'fontsize-12': {'fontSize': '12px'},
    'fontsize-14': {'fontSize': '14px'},
    'fontsize-16': {'fontSize': '16px'},
    'fontsize-18': {'fontSize': '18px'},
    'fontsize-24': {'fontSize': '24px'},
    'fontsize-30': {'fontSize': '30px'},
    'fontsize-36': {'fontSize': '36px'},
    'fontsize-48': {'fontSize': '48px'},
    'fontsize-60': {'fontSize': '60px'},
    'fontsize-72': {'fontSize': '72px'},
    'color-rgb(97,189,109)': {'color': 'rgb(97,189,109)'},
    'color-rgb(26,188,156)': {'color': 'rgb(26,188,156)'},
    'color-rgb(84,172,210)': {'color': 'rgb(84,172,210)'},
    'color-rgb(44,130,201)': {'color': 'rgb(44,130,201)'},
    'color-rgb(147,101,184)': {'color': 'rgb(147,101,184)'},
    'color-rgb(71,85,119)': {'color': 'rgb(71,85,119)'},
    'color-rgb(204,204,204)': {'color': 'rgb(204,204,204)'},
    'color-rgb(65,168,95)': {'color': 'rgb(65,168,95)'},
    'color-rgb(0,168,133)': {'color': 'rgb(0,168,133)'},
    'color-rgb(61,142,185)': {'color': 'rgb(61,142,185)'},
    'color-rgb(41,105,176)': {'color': 'rgb(41,105,176)'},
    'color-rgb(85,57,130)': {'color': 'rgb(85,57,130)'},
    'color-rgb(40,50,78)': {'color': 'rgb(40,50,78)'},
    'color-rgb(0,0,0)': {'color': 'rgb(0,0,0)'},
    'color-rgb(247,218,100)': {'color': 'rgb(247,218,100)'},
    'color-rgb(251,160,38)': {'color': 'rgb(251,160,38)'},
    'color-rgb(235,107,86)': {'color': 'rgb(235,107,86)'},
    'color-rgb(226,80,65)': {'color': 'rgb(226,80,65)'},
    'color-rgb(163,143,132)': {'color': 'rgb(163,143,132)'},
    'color-rgb(239,239,239)': {'color': 'rgb(239,239,239)'},
    'color-rgb(226,80,65)': {'color': 'rgb(226,80,65)'},
    'color-rgb(255,255,255)': {'color': 'rgb(255,255,255)'},
    'color-rgb(250,197,28)': {'color': 'rgb(250,197,28)'},
    'color-rgb(243,121,52)': {'color': 'rgb(243,121,52)'},
    'color-rgb(209,72,65)': {'color': 'rgb(209,72,65)'},
    'color-rgb(184,49,47)': {'color': 'rgb(184,49,47)'},
    'color-rgb(124,112,107)': {'color': 'rgb(124,112,107)'},
    'color-rgb(209,213,216)': {'color': 'rgb(209,213,216)'},
    'bgcolor-rgb(97,189,109)': {'backgroundColor': 'rgb(97,189,109)'},
    'bgcolor-rgb(26,188,156)': {'backgroundColor': 'rgb(26,188,156)'},
    'bgcolor-rgb(84,172,210)': {'backgroundColor': 'rgb(84,172,210)'},
    'bgcolor-rgb(44,130,201)': {'backgroundColor': 'rgb(44,130,201)'},
    'bgcolor-rgb(147,101,184)': {'backgroundColor': 'rgb(147,101,184)'},
    'bgcolor-rgb(71,85,119)': {'backgroundColor': 'rgb(71,85,119)'},
    'bgcolor-rgb(204,204,204)': {'backgroundColor': 'rgb(204,204,204)'},
    'bgcolor-rgb(65,168,95)': {'backgroundColor': 'rgb(65,168,95)'},
    'bgcolor-rgb(0,168,133)': {'backgroundColor': 'rgb(0,168,133)'},
    'bgcolor-rgb(61,142,185)': {'backgroundColor': 'rgb(61,142,185)'},
    'bgcolor-rgb(41,105,176)': {'backgroundColor': 'rgb(41,105,176)'},
    'bgcolor-rgb(85,57,130)': {'backgroundColor': 'rgb(85,57,130)'},
    'bgcolor-rgb(40,50,78)': {'backgroundColor': 'rgb(40,50,78)'},
    'bgcolor-rgb(0,0,0)': {'backgroundColor': 'rgb(0,0,0)'},
    'bgcolor-rgb(247,218,100)': {'backgroundColor': 'rgb(247,218,100)'},
    'bgcolor-rgb(251,160,38)': {'backgroundColor': 'rgb(251,160,38)'},
    'bgcolor-rgb(235,107,86)': {'backgroundColor': 'rgb(235,107,86)'},
    'bgcolor-rgb(226,80,65)': {'backgroundColor': 'rgb(226,80,65)'},
    'bgcolor-rgb(163,143,132)': {'backgroundColor': 'rgb(163,143,132)'},
    'bgcolor-rgb(239,239,239)': {'backgroundColor': 'rgb(239,239,239)'},
    'bgcolor-rgb(226,80,65)': {'backgroundColor': 'rgb(226,80,65)'},
    'bgcolor-rgb(255,255,255)': {'backgroundColor': 'rgb(255,255,255)'},
    'bgcolor-rgb(250,197,28)': {'backgroundColor': 'rgb(250,197,28)'},
    'bgcolor-rgb(243,121,52)': {'backgroundColor': 'rgb(243,121,52)'},
    'bgcolor-rgb(209,72,65)': {'backgroundColor': 'rgb(209,72,65)'},
    'bgcolor-rgb(184,49,47)': {'backgroundColor': 'rgb(184,49,47)'},
    'bgcolor-rgb(124,112,107)': {'backgroundColor': 'rgb(124,112,107)'},
    'bgcolor-rgb(209,213,216)': {'backgroundColor': 'rgb(209,213,216)'}
};

const toolbar = {
    options: ['inline', 'blockType', 'fontSize', 'fontFamily', 'list', 'textAlign', 'colorPicker',
        'link', 'embedded', 'image', 'remove', 'history'],
        // , 'emoji' 表情隐藏
    inline: {
        inDropdown: false,
        className: undefined,
        options: ['bold', 'italic', 'underline', 'strikethrough', 'monospace', 'superscript', 'subscript'],
        bold: { icon: bold, className: undefined },
        italic: { icon: italic, className: undefined },
        underline: { icon: underline, className: undefined },
        strikethrough: { icon: strikethrough, className: undefined },
        monospace: { icon: monospace, className: undefined },
        superscript: { icon: superscript, className: undefined },
        subscript: { icon: subscript, className: undefined },
    },
    blockType: {
        inDropdown: true,
        options: [ 'Normal', 'H1', 'H2', 'H3', 'H4', 'H5', 'H6', 'Blockquote'],
        className: undefined,
        dropdownClassName: undefined,
    },
    fontSize: {
        icon: fontSize,
        options: [8, 9, 10, 11, 12, 14, 16, 18, 24, 30, 36, 48, 60, 72, 96],
        className: undefined,
        dropdownClassName: undefined,
    },
    fontFamily: {
        options: ['Arial', 'Georgia', 'Impact', 'Tahoma', 'Times New Roman', 'Verdana'],
        className: undefined,
        dropdownClassName: undefined,
    },
    list: {
        inDropdown: false,
        className: undefined,
        options: ['unordered', 'ordered', 'indent', 'outdent'],
        unordered: { icon: unordered, className: undefined },
        ordered: { icon: ordered, className: undefined },
        indent: { icon: indent, className: undefined },
        outdent: { icon: outdent, className: undefined },
        //component: List
    },
    textAlign: {
        inDropdown: false,
        className: undefined,
        options: ['left', 'center', 'right', 'justify'],
        left: { icon: left, className: undefined },
        center: { icon: center, className: undefined },
        right: { icon: right, className: undefined },
        justify: { icon: justify, className: undefined },
    },
    colorPicker: {
        icon: color,
        className: undefined,
        popClassName: undefined,
        colors: ['rgb(97,189,109)', 'rgb(26,188,156)', 'rgb(84,172,210)', 'rgb(44,130,201)',
            'rgb(147,101,184)', 'rgb(71,85,119)', 'rgb(204,204,204)', 'rgb(65,168,95)', 'rgb(0,168,133)',
            'rgb(61,142,185)', 'rgb(41,105,176)', 'rgb(85,57,130)', 'rgb(40,50,78)', 'rgb(0,0,0)',
            'rgb(247,218,100)', 'rgb(251,160,38)', 'rgb(235,107,86)', 'rgb(226,80,65)', 'rgb(163,143,132)',
            'rgb(239,239,239)', 'rgb(255,255,255)', 'rgb(250,197,28)', 'rgb(243,121,52)', 'rgb(209,72,65)',
            'rgb(184,49,47)', 'rgb(124,112,107)', 'rgb(209,213,216)']
    },
    link: {
        inDropdown: false,
        className: undefined,
        popClassName: undefined,
        options: ['link', 'unlink'],
        link: { icon: link, className: undefined },
        unlink: { icon: unlink, className: undefined },
    },
    embedded: { icon: image, className: undefined, popClassName: undefined },
    image: {
        icon: image,
        className: undefined,
        popupClassName: undefined,
        urlEnabled: true,
        uploadEnabled: true,
        alignmentEnabled: true,
        uploadCallback: uploadCallback,
        defaultSize: { height: 'auto', width: 'auto' },
        //component: ImageDragger
    },
    remove: { icon: eraser, className: undefined },
    history: {
        inDropdown: false,
        className: undefined,
        options: ['undo', 'redo'],
        undo: { icon: undo, className: undefined },
        redo: { icon: redo, className: undefined },
    }
}

const covertHtmlData = (data) => {
    const sampleMarkup =  data.html || "<p></p>";
    const blocksFromHTML = htmlToDraft(sampleMarkup);
    const contentBlocks = blocksFromHTML.contentBlocks;
    const state = ContentState.createFromBlockArray(contentBlocks);
    return EditorState.createWithContent(state)
}

class DraftEditor extends React.Component {
	constructor(props){
        super(props)
        this.state = {
    		editorState: covertHtmlData(this.props.value),
            contentState:convertToRaw(covertHtmlData(this.props.value).getCurrentContent())
    	};
    	//this.focus = () => this.refs.editor.focus();
	}

    onEditorStateChange = (editorState) => {
        //console.log(convertToRaw(editorState.getCurrentContent()));
        this.setState({
            editorState,
        });
        // const contentState = this.state.editorState.getCurrentContent();
        // const html = stateToHTML(contentState);
        const contentState = convertToRaw(editorState.getCurrentContent());
        const html = draftToHtml(contentState);
        const onChange = this.props.onChange;
        if (onChange) {
            onChange({
                html,
                innerChange:true
            });
        }
        this.props.onEditChange(html)
    }

    componentWillReceiveProps(nextProps) {
        //当父组件传入Props时，innerChange为false，此时才触发内容重置
        if ('value' in nextProps && !nextProps.value.innerChange) {
            this.setState({
                contentState: convertToRaw(covertHtmlData(nextProps.value).getCurrentContent())
            });
        }
    }

	render() {
        const me = this;
		return (
            <Editor
                ref="editor"
                stripPastedStyles={false}
                editorClassName={styles.wrapperStyle}
                toolbarClassName={styles.toolbarStyle}
                toolbar={toolbar}
                editorState={this.state.editorState}
                contentState={this.state.contentState}
                onEditorStateChange={this.onEditorStateChange}
                localization ={{locale:'zh'}}
                toolbarCustomButtons={[<TextIndent />, <ImageDrag refs={me.refs}/>,<VideoEditor/>]}
            />
        )
	}
}

export default DraftEditor;
