package mall.api.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.mall.vo.ZhongHeMallGoodsDetailVO;
import mall.api.mall.vo.ZhongHeMallSearchGoodsVO;
import mall.common.Constants;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallException;
import mall.config.annotation.TokenToMallUser;
import mall.entity.MallUser;
import mall.entity.TotalPoint;
import mall.entity.ZhongHeMallGoods;
import mall.service.ZhongHeMallGoodsService;
import mall.service.ZhongHeMallUserService;
import mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "4.众鹤商城商品相关接口")
@RequestMapping("/api/v1")
public class ZhongHeMallGoodsAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeMallGoodsAPI.class);

    @Resource
    private ZhongHeMallGoodsService zhongHeMallGoodsService;
    @Resource
    private ZhongHeMallUserService userService;


    @GetMapping("/search")
    @ApiOperation(value = "用户商品搜索接口", notes = "根据关键字和分类id进行搜索")
    public Result<PageResult<List<ZhongHeMallSearchGoodsVO>>> search(
            @RequestParam(required = false) @ApiParam(value = "搜索关键字") String keyword,
            @RequestParam(required = false) @ApiParam(value = "分类id") Long goodsCategoryId,
            @RequestParam(required = false) @ApiParam(value = "orderBy") String orderBy,
            @RequestParam(required = false) @ApiParam(value = "我能兑换") boolean isEnough,
            @RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
            @TokenToMallUser MallUser loginMallUser) {

//        logger.info("商品搜索接口,keyword={},goodsCategoryId={},orderBy={},pageNumber={},userId={}", keyword, goodsCategoryId, orderBy, pageNumber, loginMallUser.getUserId());
        logger.info("用户商品搜索接口    loginMallUser={}", loginMallUser);
        Map params = new HashMap(8);
        //两个搜索参数都为空，直接返回异常
        if (goodsCategoryId == null && StringUtils.isEmpty(keyword) && !isEnough) {
            ZhongHeMallException.fail("请输入要搜索的商品分类或关键字");
        }
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("goodsCategoryId", goodsCategoryId);
        params.put("page", pageNumber);
//        params.put("isEnough", isEnough);
//        params.put("userId", loginMallUser.getUserId());
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        //对keyword做过滤 去掉空格
        if (!StringUtils.isEmpty(keyword)) {
            params.put("keyword", keyword);
        }
        if (!StringUtils.isEmpty(orderBy)) {
            params.put("orderBy", orderBy);
        }
        if (isEnough) {//查询时 勾选 我能兑换
            //获取用户积分
            TotalPoint totalPoint = userService.getTotalPoint(loginMallUser.getUserId());
            int myPoint= totalPoint.getTotalPoint();
            params.put("myPoint", myPoint);
        }
        //搜索上架状态下的商品
        params.put("goodsSellStatus", Constants.SELL_STATUS_UP);
        //封装商品数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(zhongHeMallGoodsService.searchZhongHeMallGoods(pageUtil));
    }

    @GetMapping("/goods/detail/{goodsId}")
    @ApiOperation(value = "用户商品详情接口", notes = "传参为商品id")
    public Result<ZhongHeMallGoodsDetailVO> goodsDetail(@ApiParam(value = "商品id") @PathVariable("goodsId") Long goodsId,
                                                        @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户商品详情接口     goodsId={},userId={}", goodsId, loginMallUser.getUserId());
        if (goodsId < 1) {
            return ResultGenerator.genFailResult("参数异常");
        }
        ZhongHeMallGoods goods = zhongHeMallGoodsService.getZhongHeMallGoodsById(goodsId);
        if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()) {
            ZhongHeMallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        ZhongHeMallGoodsDetailVO goodsDetailVO = new ZhongHeMallGoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        return ResultGenerator.genSuccessResult(goodsDetailVO);
    }

}
