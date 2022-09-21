/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.api.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mall.api.mall.vo.IndexInfoVO;
import mall.api.mall.vo.ZhongHeMallIndexCarouselVO;
import mall.api.mall.vo.ZhongHeMallIndexConfigGoodsVO;
import mall.common.Constants;
import mall.common.IndexConfigTypeEnum;
import mall.service.ZhongHeMallCarouselService;
import mall.service.ZhongHeMallIndexConfigService;
import mall.util.Result;
import mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(value = "v1", tags = "1.众鹤商城首页接口")
@RequestMapping("/api/v1")
public class ZhongHeMallIndexAPI {
    private static final Logger logger = LoggerFactory.getLogger(ZhongHeMallIndexAPI.class);

    @Resource
    private ZhongHeMallCarouselService zhongHeMallCarouselService;

    @Resource
    private ZhongHeMallIndexConfigService zhongHeMallIndexConfigService;

    @GetMapping("/index-infos")
    @ApiOperation(value = "用户获取首页数据", notes = "轮播图、新品、推荐等")
    public Result<IndexInfoVO> indexInfo() {
        logger.info("用户获取首页数据接口");
        IndexInfoVO indexInfoVO = new IndexInfoVO();
        List<ZhongHeMallIndexCarouselVO> carousels = zhongHeMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<ZhongHeMallIndexConfigGoodsVO> hotGoodses = zhongHeMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<ZhongHeMallIndexConfigGoodsVO> newGoodses = zhongHeMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<ZhongHeMallIndexConfigGoodsVO> recommendGoodses = zhongHeMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        indexInfoVO.setCarousels(carousels);
        indexInfoVO.setHotGoodses(hotGoodses);
        indexInfoVO.setNewGoodses(newGoodses);
        indexInfoVO.setRecommendGoodses(recommendGoodses);
        logger.info("首页数据：carousels:{},hot:{},new:{},recommend:{}", carousels.toString(), hotGoodses.toString(), newGoodses.toString(), recommendGoodses.toString());
        return ResultGenerator.genSuccessResult(indexInfoVO);
    }
}
