package mall.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.admin.param.BatchIdParam;
import mall.api.admin.param.GoodsAddParam;
import mall.api.admin.param.GoodsEditParam;
import mall.common.Constants;
import mall.common.ServiceResultEnum;
import mall.config.annotation.TokenToAdminUser;
import mall.entity.AdminUserToken;
import mall.entity.GoodsCategory;
import mall.entity.ZhongHeMallGoods;
import mall.entity.excel.ExampleGoods;
import mall.entity.excel.ImportError;
import mall.entity.excel.ImportGoods;
import mall.service.AdminLogService;
import mall.service.ZhongHeMallCategoryService;
import mall.service.ZhongHeMallGoodsService;
import mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@RestController
@Api(value = "v1", tags = "8-3.后台管理系统商品模块接口")
@RequestMapping("/manage-api/v1")
public class ZhongHeAdminGoodsInfoAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeAdminGoodsInfoAPI.class);

    @Resource
    private ZhongHeMallGoodsService zhongHeMallGoodsService;
    @Resource
    private ZhongHeMallCategoryService zhongHeMallCategoryService;
    @Resource
    private AdminLogService adminLogService;

    /**
     * 列表
     */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ApiOperation(value = "商品列表", notes = "可根据名称和上架状态筛选")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码，最小1 ") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "商品名称") String goodsName,
                       @RequestParam(required = false) @ApiParam(value = "上架状态 0-上架 1-下架") Integer goodsSellStatus,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("商品列表接口  adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        logger.info("列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (!StringUtils.isEmpty(goodsName)) {
            params.put("goodsName", goodsName);
        }
        if (goodsSellStatus != null) {
            params.put("goodsSellStatus", goodsSellStatus);
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        adminLogService.addSuccessLog(adminUser,"商品列表接口",params.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(zhongHeMallGoodsService.getZhongHeMallGoodsPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/goods", method = RequestMethod.POST)
    @ApiOperation(value = "新增商品信息", notes = "新增商品信息")
    public Result save(@RequestBody @Valid GoodsAddParam goodsAddParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("新增商品信息接口  adminUser:{},goodsAddParam:{}", adminUser.toString(), goodsAddParam.toString());
        ZhongHeMallGoods zhongHeMallGoods = new ZhongHeMallGoods();
        BeanUtil.copyProperties(goodsAddParam, zhongHeMallGoods);
        String result = zhongHeMallGoodsService.saveZhongHeMallGoods(zhongHeMallGoods, adminUser);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"新增商品信息接口",goodsAddParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/goods", method = RequestMethod.PUT)
    @ApiOperation(value = "修改商品信息", notes = "修改商品信息")
    public Result update(@RequestBody @Valid GoodsEditParam goodsEditParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("修改商品信息接口  adminUser:{},GoodsEditParam:{}", adminUser.toString(), goodsEditParam.toString());
        ZhongHeMallGoods zhongHeMallGoods = new ZhongHeMallGoods();
        BeanUtil.copyProperties(goodsEditParam, zhongHeMallGoods);
        String result = zhongHeMallGoodsService.updateZhongHeMallGoods(zhongHeMallGoods,adminUser);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"修改商品信息接口",goodsEditParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/goods/{id}")
    @ApiOperation(value = "获取单条商品信息", notes = "根据id查询")
    public Result info(@PathVariable("id") Long id, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("获取单条商品信息接口  adminUser:{},id:{}", adminUser.toString(), id.toString());
        Map goodsInfo = new HashMap(8);
        ZhongHeMallGoods goods = zhongHeMallGoodsService.getZhongHeMallGoodsById(id);
        if (goods == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        goodsInfo.put("goods", goods);
        GoodsCategory thirdCategory;
        GoodsCategory secondCategory;
        GoodsCategory firstCategory;
        thirdCategory = zhongHeMallCategoryService.getGoodsCategoryById(goods.getGoodsCategoryId());
        if (thirdCategory != null) {
            goodsInfo.put("thirdCategory", thirdCategory);
            secondCategory = zhongHeMallCategoryService.getGoodsCategoryById(thirdCategory.getParentId());
            if (secondCategory != null) {
                goodsInfo.put("secondCategory", secondCategory);
                firstCategory = zhongHeMallCategoryService.getGoodsCategoryById(secondCategory.getParentId());
                if (firstCategory != null) {
                    goodsInfo.put("firstCategory", firstCategory);
                }
            }
        }
        adminLogService.addSuccessLog(adminUser,"获取单条商品信息接口","id="+id.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(goodsInfo);
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ApiOperation(value = "批量修改销售状态", notes = "批量修改销售状态")
    public Result delete(@RequestBody BatchIdParam batchIdParam, @PathVariable("sellStatus") int sellStatus, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("批量修改销售状态接口  adminUser:{},batchIdParam:{}", adminUser.toString(), batchIdParam.toString());
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (zhongHeMallGoodsService.batchUpdateSellStatus(batchIdParam.getIds(), sellStatus)) {
            adminLogService.addSuccessLog(adminUser,"批量修改销售状态接口",
                    "ids:"+batchIdParam.toString()+",status:"+sellStatus,"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    /**
     * 用户导入
     */
    @PostMapping(value = "/goods/import")
    @ApiOperation(value = "商品导入", notes = "商品导入")
    public Result userImport(@RequestPart("file") MultipartFile file, @TokenToAdminUser AdminUserToken adminUser) throws Exception {
        logger.info("商品导入接口 ,adminUser={}", adminUser.toString());
        List<ImportGoods> goodsList = ExcelUtils.readMultipartFile(file, ImportGoods.class);
//        logger.info("goodsList{}",goodsList.toString());
        List<ImportError> errors = new ArrayList<>();
        for(ImportGoods goods :goodsList){
            if (goods.getRowTips().equals("")) {//通过导入格式校验
//                logger.info(goods.toString());
//                ZhongHeMallGoods zhongHeMallGoods = new ZhongHeMallGoods();
                String addResult = zhongHeMallGoodsService.importGoods(goods,adminUser );

//                logger.info(addResult.toString());
                if (!ServiceResultEnum.SUCCESS.getResult().equals(addResult)) {
                    //新增错误
                    errors.add(ExcelUtils.newError(goods.getRowNum(),addResult));
                }
            }else {
                //新增错误
                errors.add(ExcelUtils.newError(goods.getRowNum(),goods.getRowTips()));

            }
        }
//        adminLogService.addSuccessLog(adminUser,"商品导入接口","","SUCCESS");

        logger.info(errors.toString());
        adminLogService.addSuccessLog(adminUser,"商品导入接口","","SUCCESS");
        return ResultGenerator.genSuccessResult(errors);
    }

    /**
     * 下载商品导入模板
     */
    @GetMapping(value = "/goods/template")
    @ApiOperation(value = "下载商品导入模板", notes = "下载商品导入模板")
    public void template(HttpServletResponse response){
        // 导出数据
        ExcelUtils.exportTemplate(response, "商品导入模板", ExampleGoods.class,true);
    }

}