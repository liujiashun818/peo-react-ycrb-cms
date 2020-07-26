import React from 'react'
import PropTypes from 'prop-types';
import {Tabs, Form, Input, Collapse, Button, Modal, Tooltip} from 'antd';
import { FIELD_TYPE } from '../../constants';
import FieldEdit from './fieldEdit';
import styles from './fieldsModule.less';
import { queryField } from '../../modules/fields/services/fields'
const TabPane = Tabs.TabPane;

class FieldsModule extends React.Component {
	
	constructor(props) {
		super(props);
	    this.state = {
	       fields : props.value || [],
	       toValidate:'1', //触发子组件表单验证
	       collapse:[],
	       modalVisible: false
	    }
	    this.toValiConut = 0; //待验证子组件数量
    	this.valiedConut = 0; //已验证子组件数量
    	this.receivevali = props.toValidate;
    	this.added_conut = 0; //已经添加了新增字段的数量
    	this.addToPos = 'top'; //添加到列表上边还是下边
	}

	triggerChange = (data) => {
	    // Should provide an event to pass value to Form.
	    const onChange = this.props.onChange;
	    if (onChange) {
	    	onChange([].concat(data));
	    }
	}

	validateChildren = () => {
	    // 当触发保存的时候，重置valiedConut， 改变传入子组件的props的属性值， 保证只传一次，然后传入onValidated，等待回调，重构
	    const keysArray = Object.values(this.state.fields).map(function (item) { //展开全部子组件面板，以保证Props有效传递
			return (item['id_key'] || item['id']) + '';
		});

	    this.setState({collapse:keysArray},function(){
	    	this.valiedConut = 0;
		    this.setState({
		    	toValidate: this.state.toValidate === '1'?'2':'1'
		    });
	    });
	}
	onValidated = () => {
	    this.valiedConut++;
	    if(this.valiedConut < this.toValiConut){ //已验证的子组件尚未达到总数，不提交
	    	return;
	    }
	    const { validateFields, getFieldsValue } = this.props.form;
	    const { onValidated } = this.props;
	    validateFields((err, values) => {
			if (err) {
				return
			}
	    	const dataForm = {
				...getFieldsValue(),
			}
			const data = [];
			const fields = this.state.fields;
			fields.forEach(field => {
				data.push(dataForm[field.id || field.id_key]);
			})
			this.triggerChange(data);
	    	onValidated();
	    });
	}

	showModal = (pos) => {
	    this.setState({
	    	modalVisible: true,
	    });
	    this.addToPos = pos;
	}

	modalCancel = (e) => {
	    this.setState({
	    	modalVisible: false,
	    });
	}

	getFieldTypes = (e) => {
		const fieldTypes = [];
		for(let key of Object.keys(FIELD_TYPE)) {
		    fieldTypes.push(key)				
		}
		return fieldTypes;
	}

	addFieldRend =(fieldData) => {
		const fields = this.state.fields;
		if(this.addToPos=='top'){
			fields.unshift(fieldData)
		}else{
			fields.push(fieldData)
		}
		const collapse = this.state.collapse;
		collapse.push('' + (fieldData.id || fieldData.id_key));
		this.setState({
			collapse
		});
		this.triggerChange(fields);
		this.modalCancel();
	}

	addField =(typeOrId,isDef=true) => {
		if(!isDef){
			queryField({id:typeOrId}).then((data) => {
      			if(data.code===0 && data.data){
      				const fieldData = data.data;
      				this.addFieldRend(fieldData);
			    }
		    });
		}else{
			this.added_conut++;
			const fieldData = {id_key:'-'+this.added_conut,type:typeOrId};
			this.addFieldRend(fieldData);
		}
	}

	removeField = (id) => {
		const fields = this.state.fields;
		const index = fields.findIndex((value)=>{
			return id == value.id || value.id_key;
		});
		fields.splice(index,1);
		this.triggerChange(fields);
	}
	componentWillReceiveProps(nextProps) {
		// Should be a controlled component.
	    if (nextProps.value !== this.state.fields) {
	      this.setState( {fields:nextProps.value} );
	    }
	    if( nextProps.toValidate != this.receivevali) {
	    	this.receivevali = nextProps.toValidate;
	    	this.validateChildren();
	    }
	}
	render() {
		const { getFieldDecorator } = this.props.form;
		this.toValiConut = this.state.fields.length;
		const FieldEditProps = {
	      toValidate:this.state.toValidate,
	      onValidated:this.onValidated,
	      removeField:this.removeField
	    }
		return (
			<div className="ant-form">
				{
					this.state.fields && this.state.fields.length > 0 
					?	<div>
							<Button icon="plus" className="cus-button-orange mb10" onClick={()=>{this.showModal('top')}}>添加新字段</Button>
							<Collapse bordered={true} activeKey={this.state.collapse} onChange={(key)=>{ this.setState({collapse:key}) }} >
							{
								this.state.fields.map((item, index) => {
									const key = (item.id_key||item.id) +'';
									return(
										<Collapse.Panel key={key} header={item.name} >
											<Form.Item>
												{getFieldDecorator(key, {
									                initialValue: item || {}
									            })(<FieldEdit {...FieldEditProps} />)}
											</Form.Item>
										</Collapse.Panel>
									)
								})
							}
							</Collapse>
						</div>
              		:   ''
              	}
              	<Button icon="plus" className="cus-button-orange mt10" onClick={()=>{this.showModal('bot')}}>添加新字段</Button>
              	<Modal
					title="添加新字段"
					visible={this.state.modalVisible}
					onCancel={this.modalCancel}
					footer={false}
					width={600}
		        >
		        	<Tabs defaultActiveKey="1">
					    <TabPane tab="默认字段" key="1">
					    	 <ul className={styles.fieldList}>
					         	{
					         		this.getFieldTypes().map((item, index) => {
						         		return(
						         			<li key={index}><Button icon={FIELD_TYPE[item].icon} onClick={(e)=>{this.addField(item)}}>{FIELD_TYPE[item].name}</Button></li>
						         		)
							        })
						        }
					        </ul>
					    </TabPane>
					    <TabPane tab="已有字段" key="2">
					    	 <ul className={styles.fieldList}>
					         	{
					         		this.props.fieldList.map((item, index) => {
						         		const isAdded = this.state.fields.findIndex(function(value, i, arr) {
						         			return item.id == value.id
						         		});
					         			return(
					         				isAdded >= 0 
					         				?	''
					         				: 	<li key={index}>
					         						<Tooltip title={item.name}>
					         							<Button 
						         							className="f-toe1" 
						         							title={item.name} 
						         							onClick={(e)=>{this.addField(item.id,false)}}
						         						>
						         							{item.name}
						         						</Button>
				         							</Tooltip>
					         					</li>
					         			)
						         	})
						        }
					        </ul>
					    </TabPane>
					 </Tabs>
		        </Modal>
			</div>
		);
	}
}

export default Form.create()(FieldsModule);

