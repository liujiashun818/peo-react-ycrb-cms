package cn.people.one.modules.sys.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.sys.model.Dict;
import cn.people.one.modules.sys.model.front.DictVO;
import cn.people.one.modules.sys.service.IDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典Controller
 *
 * @author cuiyukun
 */
@Api(description = "字典管理(sys模块)")
@RestController
@RequestMapping("/api/sys/dict")
@Slf4j
public class DictController {

	@Autowired
	private IDictService dictService;

    @ApiOperation("数据字典列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
	@RequestMapping(method = RequestMethod.GET)
	public Result<QueryResultVO<Dict>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize, DictVO dictVO) {
		return Result.success(dictService.listPage(pageNumber, pageSize,dictVO));
	}

	/**
	 * 添加一条字典记录
	 * 待完善
	 */
    @ApiOperation("保存数据字典")
	@RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("sys:dict:edit")//权限管理;
	public Result add(@RequestBody Dict dict) {
		dictService.save(dict);
		return Result.success();
	}

	/**
	 * 删除一条记录
	 * 待完善
	 */
    @ApiOperation("删除数据字典")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "数据字典ID", required = true, paramType = "path")
    })
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("sys:dict:edit")//权限管理;
	public Result delete(@PathVariable Long id) {
		dictService.vDelete(id);
		return Result.success();
	}

    @ApiOperation("数据字典详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "数据字典ID", required = true, paramType = "path")
    })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Dict fetch(@PathVariable Long id) {
		return dictService.fetch(id);
	}

    @ApiOperation("根据类型查询列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "type", value = "类型", required = false, paramType = "query")
    })
	@RequestMapping(value = "/listData", method = RequestMethod.GET)
	public List<Dict> listData(@RequestParam(required = false) String type) {
		return dictService.getListByType(type);
	}

	/**
	 * 部分更新
	 */
    @ApiOperation("部分更新")
	@RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("sys:dict:edit")//权限管理;
	public Result update(@RequestBody Dict dict) {
		if(dictService.updateIgnoreNull(dict)>0){
            return Result.success();
        }
        return Result.error("更新失败");
	}

    @ApiOperation("获取类型")
    @RequestMapping(value = "types", method = RequestMethod.GET)
	public Result<List> getTypes(){
	     List list = dictService.getTypes();
	     return Result.success(list);
    }
}
