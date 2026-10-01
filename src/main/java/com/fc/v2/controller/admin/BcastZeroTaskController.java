package com.fc.v2.controller.admin;

import java.util.Date;
import java.util.List;

import com.fc.v2.common.base.BaseController;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.common.domain.ResultTable;
import com.fc.v2.common.log.Log;
import com.fc.v2.model.auto.TBcastZeroChannel;
import com.fc.v2.model.auto.TBcastZeroRound;
import com.fc.v2.model.auto.TBcastZeroTask;
import com.fc.v2.service.ITBcastZeroTaskService;
import com.fc.v2.service.bcast.ZeroRoundResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 零报告催办到期单 Controller（scheduling-job 形状：系统卡着日子自走一轮，人只看落下的结果）。
 *
 * @author fuce
 * @date 2026-10-01
 */
@Api(value = "零报告催办到期单")
@Controller
@RequestMapping("/bcastZeroTask")
public class BcastZeroTaskController extends BaseController {

    private final String prefix = "admin/bcastZeroTask";

    @Autowired
    private ITBcastZeroTaskService bcastZeroTaskService;

    @ApiOperation(value = "催办台账跳转", notes = "催办台账跳转")
    @GetMapping("/view")
    @RequiresPermissions("bcast:bcastZeroTask:view")
    public String view(ModelMap model) {
        return prefix + "/list";
    }

    @Log(title = "零报告催办台账", action = "list")
    @ApiOperation(value = "现行清单", notes = "现行清单(候催/催到/催不动都挂着)")
    @GetMapping("/list")
    @RequiresPermissions("bcast:bcastZeroTask:list")
    @ResponseBody
    public ResultTable list() {
        // 清单照挂催不动的条目，不藏不掖；剩余工作日、是否到档由同一把尺回填
        List<TBcastZeroTask> data = bcastZeroTaskService.selectActiveList();
        return pageTable(data, data.size());
    }

    @Log(title = "零报告催办跑一轮", action = "run")
    @ApiOperation(value = "跑一轮", notes = "按此刻开口，逐条办到底，回库点出收口三数")
    @PostMapping("/run")
    @RequiresPermissions("bcast:bcastZeroTask:run")
    @ResponseBody
    public AjaxResult run() {
        ZeroRoundResult result = bcastZeroTaskService.runOnce(new Date());
        AjaxResult ok = AjaxResult.success(result.getMessage());
        ok.put("data", result);
        return ok;
    }

    @Log(title = "零报告两路送达痕迹", action = "channels")
    @ApiOperation(value = "两路送达痕迹", notes = "某条目站内/手机两路痕迹")
    @GetMapping("/channels")
    @RequiresPermissions("bcast:bcastZeroTask:list")
    @ResponseBody
    public AjaxResult channels(Long id) {
        List<TBcastZeroChannel> data = bcastZeroTaskService.listChannels(id);
        return AjaxResult.successData(200, data);
    }

    @Log(title = "零报告催办历轮收口", action = "rounds")
    @ApiOperation(value = "历轮收口", notes = "历轮收口三数")
    @GetMapping("/rounds")
    @RequiresPermissions("bcast:bcastZeroTask:list")
    @ResponseBody
    public AjaxResult rounds() {
        List<TBcastZeroRound> data = bcastZeroTaskService.listRounds();
        return AjaxResult.successData(200, data);
    }
}
