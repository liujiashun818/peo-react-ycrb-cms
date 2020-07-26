import { Tree } from 'antd';
import {Jt} from "../../utils";

const TreeNode = Tree.TreeNode;

class AskTree extends React.Component {
    constructor(props){
        super(props)
        this.onSelect = this.onSelect.bind(this)
    }
    onSelect(id){
        const {formData,onSelect,searchProps} = this.props;
        console.log(this.props.formData,'slect接口');
        
        // if (formData) {
        //     formData.categoryId = id;
        // }
       // formData.categoryId = id;
    //    searchProps(formData)
        onSelect(id[0]);
        // window.history.back(1);
        // console.log("你好")
    }
    render() {
        let {catgs,formData,categoryId} = this.props;
        catgs  = formData.length>0?Jt.tree.format(formData):[];
        let  loop = [] ;
       let all = []
        loop = data => data.map((item) => {
            all.push(item.id+"")
            if (item.children && item.children.length) {
                return <TreeNode key={item.id} title={item.name}>{loop(item.children)}</TreeNode>;
            }
            return <TreeNode key={item.id} title={item.name} />;
        });
        let lp = loop(catgs)
        
        return (
            <Tree
                className="draggable-tree"
                defaultSelectedKeys={[categoryId]}
                selectedKeys={[categoryId]}
                expandedKeys={all}
             
                onSelect={this.onSelect}
            >
                {lp}
            </Tree>
        );
    }
}
export default AskTree
