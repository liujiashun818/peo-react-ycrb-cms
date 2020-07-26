import { Tree } from 'antd';
import {Jt} from "../../utils";

const TreeNode = Tree.TreeNode;

class Demo extends React.Component {
    constructor(props){
        super(props)
        this.onSelect = this.onSelect.bind(this)
        this.state = {
            expList :[]
        }
    }

    onExpand(id){
       
       this.setState({'expList':id})
    }
    onSelect(id, e){
        const modelId = e.node.props.modelId
       
        sessionStorage.modelId = modelId

        const { formData, searchProps, changeCatgId, changeModelId} = this.props;
        if (formData) {
            formData.categoryId = id;
        }
        searchProps(formData, 'fromTree');
        changeCatgId(id[0]);
        changeModelId(modelId)
    }
    render() {
        let {catgs,formData,categoryId} = this.props;
        catgs = Jt.tree.format(catgs);
        let maping = {};
        let  loop = [] ;
        let all = []
        loop = (data) =>{
           return  data.map((item) => {
            all.push(item.id+"")
                maping[item.id] = item;
                if (item.id === 1) {
                    return <TreeNode disabled key={item.id} title={item.name}>{loop(item.children)}</TreeNode>;
                }
                if (item.children && item.children.length) {
                    
                    return <TreeNode modelId={item.modelId} disabled={item.disabled || (categoryId==item.id)} key={item.id} title={item.name}>{loop(item.children)}</TreeNode>;
                }
               
                return <TreeNode modelId={item.modelId} disabled={item.disabled || (categoryId==item.id)} key={item.id} title={item.name} />;
            });
        }
      
        const findParent = (data,id)=>{
            let res = [];
            Object.keys(data).forEach((item,index)=>{
                    if(item+'' == id+''){
                        res.unshift(item);
                        if(item!=1){
                            res.splice(0,0,...( findParent(data,data[item].parentId)))
                        }

                    }
            });
            return res;
        }
        let treenode = loop(catgs);
        let expanderkeys1 = findParent(maping,categoryId);
        expanderkeys1 = this.state.expList.length?this.state.expList:expanderkeys1
        return (
            <Tree
                className="draggable-tree"
                defaultSelectedKeys={[categoryId]}
                selectedKeys={[categoryId]}
                expandedKeys={all}
                draggable
                defaultExpandAll={true}
                onSelect={this.onSelect}
                onExpand={this.onExpand.bind(this)}
            >
                {treenode}
            </Tree>
        );
    }
}
export default Demo
