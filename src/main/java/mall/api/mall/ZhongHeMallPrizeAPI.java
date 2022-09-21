
package mall.api.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.mall.vo.ZhongHeMallPrizeDetailVO;
import mall.common.Constants;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallException;
import mall.config.annotation.TokenToMallUser;
import mall.entity.MallUser;
import mall.entity.ZhongHeMallPrize;
import mall.service.LotterydrawService;
import mall.service.ZhongHeMallPrizeService;
import mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "6-0.众鹤商城奖品相关接口")
@RequestMapping("/api/v1")
public class ZhongHeMallPrizeAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeMallPrizeAPI.class);

    @Resource
    private ZhongHeMallPrizeService zhongHeMallPrizeService;

    @Resource
    private LotterydrawService lotterydrawService;

    @GetMapping("/prize/detail/{prizeId}")
    @ApiOperation(value = "奖品详情接口", notes = "传参为奖品id")
    public Result<ZhongHeMallPrizeDetailVO> prizeDetail(@ApiParam(value = "奖品id") @PathVariable("prizeId") Long prizeId,
                                                        @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户奖品详情接口    prizeId={},userId={}", prizeId, loginMallUser.getUserId());
        if (prizeId < 1) {
            return ResultGenerator.genFailResult("参数异常");
        }
        ZhongHeMallPrize prize = zhongHeMallPrizeService.getZhongHeMallPrizeById(prizeId);
        if (Constants.SELL_STATUS_UP != prize.getPrizeSellStatus()) {
            ZhongHeMallException.fail(ServiceResultEnum.PRIZE_PUT_DOWN.getResult());
        }
        if (!prize.getOrganizationId().equals(loginMallUser.getOrganizationId())) {
            ZhongHeMallException.fail(ServiceResultEnum.PRIZE_NOT_EXIST.getResult());
        }
        ZhongHeMallPrizeDetailVO prizeDetail = new ZhongHeMallPrizeDetailVO();
        BeanUtil.copyProperties(prize, prizeDetail);
        prizeDetail.setPrizeCarouselList(prize.getPrizeCarousel().split(","));
        return ResultGenerator.genSuccessResult(prizeDetail);
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/prize/list", method = RequestMethod.GET)
    @ApiOperation(value = "抽奖记录列表", notes = "可根据活动名称、客户经理号和上架状态筛选")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码，最小1 ") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "状态 0.待抽奖 1.已抽奖 2.待送货 3:送货中 4.送货完成 -1.抽奖失败 -2.用户关闭 -3.商家关闭") Integer status,
                       @TokenToMallUser MallUser loginMallUserr) {
//        logger.info("抽奖记录：status:{},loginMallUserr:{}", status.toString(),loginMallUserr.toString());
        logger.info("用户抽奖记录接口   loginMallUserr:{}", loginMallUserr.toString());
        if (pageNumber ==null || pageSize ==null || pageNumber < 1 || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        logger.info("列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        Long userId = loginMallUserr.getUserId();
        if (userId != null) {
            params.put("userId", userId);
        }
        if (status != null) {
            params.put("status", status);
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(lotterydrawService.getLotteryDrawByUserId(pageUtil));
    }

}
