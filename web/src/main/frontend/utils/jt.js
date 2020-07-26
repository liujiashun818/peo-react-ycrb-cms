import { message } from 'antd';

const Jt = {};

Jt.string = {
	trim(str) {
		return String(str).replace(/^\s+|\s+$/g, "");
	},
	isEmpty(str) {
		return str === undefined || str === null || this.trim(str) === "";
	}
};

Jt.array = {
	isEmpty(arr) {
		return arr === undefined || arr === null || arr.length === 0;
	},
	isArray(arr) {
		return Object.prototype.toString.call(arr) === "[object Array]";
	},
	listToMap(arr) {
		return arr.map(function(v,i){
		    return {id:v};
		});
	}
};

Jt.object = {
	isEmpty(obj) {
		let name;
		for(name in obj) {
			return false;
		}
		return true;
	}
};

Jt.catg = {
	format(catgs) {
		for(let i = 0, len = catgs.length; i < len; i++) {
			catgs[i].value = catgs[i].id + '';
			catgs[i].label = catgs[i].name;
			if(catgs[i].child) {
				catgs[i].children = catgs[i].child;
				delete catgs[i].child;
				this.format(catgs[i].children);
			}
		}
	},
	getInitId(catgs) {
		if(Jt.array.isEmpty(catgs)) {
			return '';
		}
		let catg = catgs[0];
	    if(catg.children) {
	        return this.getInitId(catg.children);
	    }
	    return catg.id;
	},
	getInitCatg(catgs) {
		if(Jt.array.isEmpty(catgs)) {
			return {};
		}
		const catg = catgs[0];
		if(catg.children) {
			return this.getInitCatg(catg.children);
		}
		return catg;
	},
	getCatg(catgs, id) {
		for(let i = 0, len = catgs.length; i < len; i++) {
			if(catgs[i].id == id) {
				return catgs[i];
			}
			if(catgs[i].children) {
				var catg = this.getCatg(catgs[i].children, id);
				if(catg) {
					return catg;
				}
			}
		}
	}
};

Jt.field = {
	combine(fieldGroups=[]) {
		const res = [];
	    fieldGroups.forEach(({fields=[]}) => {
	        res.push(...fields);
	    });
	    return res;
	}
};

//通用树形结构数据转换工具
Jt.tree = {
	format(data){
		for(let i = 0, len = data.length; i < len; i++) {
			data[i].key = data[i].value = data[i].id + '';
			data[i].label = data[i].name;
			if(data[i].child) {
				data[i].children = this.format(data[i].child);
				delete data[i].child;
			}
		}
		return data;
	},
	isLeaf(data, id) {
		for(let i = 0, len = data.length; i < len; i++) {
			if(data[i].id == id) {
				if(data[i].children) {
					return false;
				}
				return true;
			}
			else if(data[i].children) {
				const flag =  this.isLeaf(data[i].children, id);
				if(flag) {
					return true;
				}
			}
		}
	},
	getIds(data,toObj=false){
		const idsArray = [];
		data.forEach(function(itme) {
			const idStr = toObj && {value:itme.id + ''} ||  itme.id + '';
			idsArray.push(idStr);
		});
		return idsArray;
	}
};

Jt.validate = {
	isEmail(val) {
		if(val.search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/) !== -1){
            return true;
        }
        return false;
	},
	isUrl(val) {
		if(/^(?:ht|f)tp(?:s)?\:\/\/(?:[\w\-\.]+)\.\w+/i.test(val)) {
			return true;
		}
		return false;
	},
	isTel(val) {
		if(/^1\d{10}$|^(\(\d{3,4}\)|\d{3,4}-|\s)?\d{7,14}$/.test(val)) {
			return true;
		}
		return false;
	},
	isNumber(val) {
		if(/^[0-9]*$/.test(val)) {
			return true;
		}
		return false;
	}
};

//全局错误处理函数
Jt.onError = (e,dispatch) => {
	message.config({
		top: 8,
		duration: 3
	});
    e.message = e.message || '系统错误，请联系管理';
    if(e.message === 'logout'){
    	dispatch({type: 'app/onLogout'})
    }else {
    	message.error(e.message, 3);
    }
};

module.exports = exports = Jt;
