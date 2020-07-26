"use strict";
/**
 * 改动了/static/ueditor/ueditor.all.js 源文件，请主要不要替换成新版
 * @type {[type]}
 */
import {URL_PREFIX} from '../../constants';
const __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
import React from 'react'
const UEditor = (function (_super) {
    __extends(UEditor, _super);
    function UEditor() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    UEditor.prototype.componentWillMount = function () {
        
        if (typeof UE != 'undefined' && !!UE.getEditor && !!UE.delEditor) {
            // 如果UE已经是全局变量了，则说明已经加载了UEditor相应的<script>
            return;
        }
        else {
            var scriptConfig = document.querySelector("script[src='" + this.props.uconfigSrc + "']");
            if (!scriptConfig) {
                scriptConfig = document.createElement("script");
                const uconfigSrc = this.props.uconfigSrc;
                scriptConfig.src = uconfigSrc.indexOf('http') >= 0 ? uconfigSrc : URL_PREFIX + uconfigSrc;
                document.body.appendChild(scriptConfig);
            }
            var scriptEditor = document.querySelector("script[src='" + this.props.ueditorSrc + "']");
            if (!scriptEditor) {
                scriptEditor = document.createElement("script");
                const ueditorSrc = this.props.ueditorSrc;
                scriptEditor.src = ueditorSrc.indexOf('http') >= 0 ? ueditorSrc : URL_PREFIX + ueditorSrc;
                document.body.appendChild(scriptEditor);
            }
        }
    };
    UEditor.prototype.componentDidMount = function () {
        function waitUntil(props) {
            try {
                
                
                var ue_1 = UE.getEditor(props._id, {
                    initialFrameWidth: props.width,
                    initialFrameHeight: props.height,
                    autoFloatEnabled: false,
                });
                ue_1.ready(function () {
                    // ue_1.setDisabled();
                    ue_1.setContent(props.initialContent);
                    // ue_1.setEnabled();
                    props.afterInit(ue_1);
                });
            }
            catch (err) {
                // console.log('暂时无UE对象可用，等待500ms', err);
                setTimeout(function () { waitUntil(props); }, 500);
            }
        }
        waitUntil(this.props);
    };
    UEditor.prototype.componentWillUnmount = function () {
        if (typeof UE != 'undefined'  && !!UE.getEditor && !!UE.delEditor) {
            UE.delEditor(this.props._id)
        };
        // 不再卸载<script> :
        //     <script type="text/javascript" src="/static/ueditor/ueditor.config.js"></script>
        //     <script type="text/javascript" src="/static/ueditor/ueditor.all.min.js"></script>
    };
    UEditor.prototype.render = function () {
        return (<div style={{lineHeight:'20px'}}>{React.createElement("script", { id: this.props._id, name: this.props.name, type: "text/plain" })}</div>);
    };
    return UEditor;
}(React.Component));
UEditor.defaultProps = {
    _id: 'ueditorcontainer',
    name: 'uecontent',
    height: 600,
    width: 600,
    initialContent: '',
    afterInit: function (ue) { },
    uconfigSrc: "./ueditor/ueditor.config.js",
    ueditorSrc: "./ueditor/ueditor.all.js",
    langSrc: "./ueditor/lang/zh-cn/zh-cn.js",
};
export default UEditor;
