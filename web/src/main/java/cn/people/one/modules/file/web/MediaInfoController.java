package cn.people.one.modules.file.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.file.model.MediaInfo;
import cn.people.one.modules.file.service.IMediaInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.sql.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;

/**
 * 媒体信息Controller
 *
 * @author zxz
 */
@Api(description = "媒体资源管理")
@RestController
@RequestMapping("/api/file/media/info/")
@Slf4j
public class MediaInfoController {

	@Autowired
	private IMediaInfoService mediaInfoService;

    @ApiOperation("媒体资源列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, paramType = "query"),
        @ApiImplicitParam(name = "keyword", value = "关键词", required = false, paramType = "query"),
        @ApiImplicitParam(name = "type", value = "类型", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
	@RequestMapping(method = RequestMethod.GET)
	public Result<QueryResultVO<MediaInfo>> list(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String startTime,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String endTime,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam String type,
                                      @RequestParam Integer pageNumber,
                                      @RequestParam Integer pageSize) {
		Criteria criteria = Cnd.cri();
		criteria.where().and("status", "=", 1).and("del_flag", "=", 0).and("type", "=", type);
		if (StringUtils.isNotBlank(keyword)) {
			criteria.where().and("name", "like", "%" + keyword + "%").or("keyword", "like", "%" + keyword + "%");
		}
		if (null != startTime && null == endTime) {
			criteria.where().andBetween("trans_time", startTime, new Date());
		}
		if (null != startTime && null != endTime) {
			criteria.where().andBetween("trans_time", startTime, endTime);
		}
		if (null == startTime && null != endTime) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(0, 0, 0);
			criteria.where().andBetween("trans_time", calendar.getTime(), endTime);
		}
		criteria.getOrderBy().desc("create_at");
		return Result.success(mediaInfoService.listPage(pageNumber, pageSize, criteria));
	}

    @ApiOperation("媒体资源详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "媒体资源ID", required = true, paramType = "path")
    })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Result<MediaInfo> view(@PathVariable Long id) {
		return Result.success(mediaInfoService.fetch(id));
	}

    @ApiOperation("保存媒体资源")
	@RequestMapping(method = RequestMethod.POST)
	public Result<MediaInfo> save(@RequestBody MediaInfo mediaInfo) {
		mediaInfoService.save(mediaInfo);
		return Result.success(mediaInfo);
	}

    @ApiOperation("删除媒体资源")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "媒体资源ID", required = true, paramType = "path")
    })
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Result delete(@PathVariable Long id) {
		return Result.success(mediaInfoService.delete(id));
	}

}
