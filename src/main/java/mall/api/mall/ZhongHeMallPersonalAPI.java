package mall.api.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.admin.param.BatchIdParam;
import mall.api.mall.param.MallUserLoginParam;
import mall.api.mall.param.MallUserUpdateParam;
import mall.api.mall.param.PasswordUpdateParam;
import mall.api.mall.vo.ZhongHeMallNoticeVO;
import mall.api.mall.vo.ZhongHeMallUserVO;
import mall.common.Constants;
import mall.common.ServiceResultEnum;
import mall.config.annotation.TokenToMallUser;
import mall.entity.MallUser;
import mall.entity.TotalPoint;
import mall.entity.ZhongHeMallGoods;
import mall.service.NoticeService;
import mall.service.ZhongHeMallShoppingCartService;
import mall.service.ZhongHeMallUserService;
import mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "2-0.众鹤商城用户操作相关接口")
@RequestMapping("/api/v1")
public class ZhongHeMallPersonalAPI {

    @Resource
    private ZhongHeMallUserService zhongHeMallUserService;
    @Resource
    private ZhongHeMallShoppingCartService zhongHeMallShoppingCartService;
    @Resource
    private NoticeService noticeService;

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeMallPersonalAPI.class);

    @PostMapping("/user/login")
    @ApiOperation(value = "商城登录接口", notes = "返回token")
    public Result<String> login(@RequestBody @Valid MallUserLoginParam mallUserLoginParam) {
        if (!NumberUtil.isPhone(mallUserLoginParam.getLoginName())){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        String loginResult = zhongHeMallUserService.login(mallUserLoginParam.getLoginName(), mallUserLoginParam.getPasswordMd5());

        logger.info("用户商城登录接口  loginName={},loginResult={}", mallUserLoginParam.getLoginName(), loginResult);

        //登录成功
        if (!StringUtils.isEmpty(loginResult) && loginResult.length() == Constants.TOKEN_LENGTH) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(loginResult);
            return result;
        }
        //登录失败
        return ResultGenerator.genFailResult(loginResult);
    }


    @PostMapping("/user/logout")
    @ApiOperation(value = "商城登出接口", notes = "清除token")
    public Result<String> logout(@TokenToMallUser MallUser loginMallUser) {
        logger.info("用户商城登出接口   loginMallUser={}", loginMallUser.getUserId());

        Boolean logoutResult = zhongHeMallUserService.logout(loginMallUser.getUserId());
        //登出成功
        if (logoutResult) {
//            logger.info("登出成功");
            return ResultGenerator.genSuccessResult();
        }
        //登出失败
//        logger.info("登出失败");
        return ResultGenerator.genFailResult("logout error");
    }


//    @PostMapping("/user/register")
//    @ApiOperation(value = "用户注册", notes = "")
//    public Result register(@RequestBody @Valid MallUserRegisterParam mallUserRegisterParam) {
//        if (!NumberUtil.isPhone(mallUserRegisterParam.getLoginName())){
//            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
//        }
//        String registerResult = zhongHeMallUserService.register(mallUserRegisterParam.getLoginName(), mallUserRegisterParam.getPassword(),mallUserRegisterParam.getSponsor());
//
//        logger.info("用户注册 api,loginName={},loginResult={}", mallUserRegisterParam.getLoginName(), registerResult);
//
//        //注册成功
//        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
//            return ResultGenerator.genSuccessResult();
//        }
//        return ResultGenerator.genFailResult(registerResult);
//    }

    @PutMapping("/user/info")
    @ApiOperation(value = "修改用户信息", notes = "")
    public Result updateInfo(@RequestBody @ApiParam("用户信息") MallUserUpdateParam mallUserUpdateParam,
                             @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户修改用户信息接口   MallUser:{},UpdateParam:{}", loginMallUser.toString(), mallUserUpdateParam.toString());
//        if (mallUserUpdateParam.getPasswordMd5() == null || mallUserUpdateParam.getPasswordMd5().equals("")) {
//            Result result = ResultGenerator.genFailResult("新密码不能为空");
//            return result;
//        }
        Boolean flag = zhongHeMallUserService.updateUserInfo(mallUserUpdateParam, loginMallUser.getUserId());
        if (flag) {
            //返回成功
            Result result = ResultGenerator.genSuccessResult();
            return result;
        } else {
            //返回失败
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        }
    }

    @PutMapping("/user/password")
    @ApiOperation(value = "修改用户密码", notes = "")
    public Result updatePassword(@RequestBody @ApiParam("用户信息") PasswordUpdateParam passwordUpdateParam,
                             @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户修改密码接口   MallUser:{},UpdateParam:{}", loginMallUser.toString(), passwordUpdateParam.toString());
//        if (mallUserUpdateParam.getPasswordMd5() == null || mallUserUpdateParam.getPasswordMd5().equals("")) {
//            Result result = ResultGenerator.genFailResult("新密码不能为空");
//            return result;
//        }
        Boolean flag = zhongHeMallUserService.updatePassword(passwordUpdateParam, loginMallUser.getUserId());
        if (flag) {
            //返回成功
            Result result = ResultGenerator.genSuccessResult();
            return result;
        } else {
            //返回失败
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        }
    }

    @GetMapping("/user/info")
    @ApiOperation(value = "获取用户信息", notes = "")
    public Result<ZhongHeMallUserVO> getUserDetail(@TokenToMallUser MallUser loginMallUser) {
        logger.info("获取用户信息接口  MallUser:{}", loginMallUser.toString());
        ZhongHeMallUserVO mallUserVO = new ZhongHeMallUserVO();
        BeanUtil.copyProperties(loginMallUser, mallUserVO);
        return ResultGenerator.genSuccessResult(mallUserVO);
    }


    @GetMapping("/user/noticelist")
    @ApiOperation(value = "获取用户通知列表", notes = "")
    public Result<ZhongHeMallUserVO> getUserNotice(
            @ApiParam(value = "页码") @RequestParam(required = false) Integer pageNumber,
            @TokenToMallUser MallUser loginMallUser) {
        logger.info("获取用户通知列表接口  MallUser:{}", loginMallUser.toString());
        if (pageNumber == null || pageNumber < 1 ) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
//        logger.info("列表参数 pageNumber:{}", pageNumber.toString());

        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", 10);
        params.put("userId", loginMallUser.getUserId());
        //封装分页请求参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        PageResult result = noticeService.getUserNoticeList(pageUtil);
        return ResultGenerator.genSuccessResult(result);
    }

    @GetMapping("/user/noticeinfo/{id}")
    @ApiOperation(value = "获取通知详情", notes = "")
    public Result<ZhongHeMallUserVO> getNoticeDetail(@PathVariable("id") Long id,
            @TokenToMallUser MallUser loginMallUser) {
        logger.info("获取通知详情接口  id:{}, MallUser:{}", id.toString(), loginMallUser.toString());
        ZhongHeMallNoticeVO noticeVO = noticeService.getNoticeDetailById(id);
        noticeService.readed(id);//标记为已读
        return ResultGenerator.genSuccessResult(noticeVO);
    }

    @GetMapping("/user/point")
    @ApiOperation(value = "获取用户积分", notes = "")
    public Result getUserPoint(@TokenToMallUser MallUser loginMallUser) {
        logger.info("获取用户积分接口  MallUser:{}", loginMallUser.toString());
        TotalPoint totalPoint;
        totalPoint = zhongHeMallUserService.getTotalPoint(loginMallUser.getUserId());
//        logger.info("用户积分信息：{}", totalPoint.toString());
        return ResultGenerator.genSuccessResult(totalPoint);
    }

    @GetMapping("/user/flagcount")
    @ApiOperation(value = "获取用户信息标记数量", notes = "未读通知数量，购物车数量")
    public Result<ZhongHeMallUserVO> getUserFlag(@TokenToMallUser MallUser loginMallUser) {
        logger.info("获取用户信息接口  MallUser:{}", loginMallUser.toString());
        Integer noticeCount = noticeService.getNoticeNoReadCount(loginMallUser.getUserId());
        Integer cartCount = zhongHeMallShoppingCartService.getMyShoppingCartItems(loginMallUser.getUserId()).size();
        Map params = new HashMap(8);
        params.put("noticeCount", noticeCount);
        params.put("cartCount", cartCount);
        return ResultGenerator.genSuccessResult(params);
    }

//    @GetMapping("/user/payPoint")
//    @ApiOperation(value = "积分支付", notes = "")
//    public Result payPoint(@TokenToMallUser MallUser loginMallUser,int point) {
//        logger.info("积分支付接口  MallUser:{},point:{}", loginMallUser.toString(),point);
//
//        String Result = zhongHeMallUserService.payPoint(loginMallUser.getUserId(),point);
//        if (!Result.equals("success")) {
//            return ResultGenerator.genFailResult(Result);
//        }
//        return ResultGenerator.genSuccessResult(Result);
//    }

    @GetMapping("/user/payCash")
    @ApiOperation(value = "付费获得积分", notes = "")
    public Result payCash(@TokenToMallUser MallUser loginMallUser,int point) {
        logger.info("用户付费获得积分接口  MallUser:{},point:{}", loginMallUser.toString(),point);

        String Result = zhongHeMallUserService.payCash(loginMallUser.getUserId(),point);
        if (!Result.equals("success")) {
            return ResultGenerator.genFailResult(Result);
        }
        return ResultGenerator.genSuccessResult(Result);
    }

    @GetMapping("/user/collectGoods")
    @ApiOperation(value = "获取用户收藏列表", notes = "")
    public Result getUserCollectGoods(@TokenToMallUser MallUser loginMallUser) {
        logger.info("获取用户收藏列表  MallUser:{}", loginMallUser.toString());
        String collect = loginMallUser.getCollect();
        //获得收藏列表
        List<ZhongHeMallGoods> collectGoodsList=zhongHeMallUserService.getUserCollect(loginMallUser.getUserId(),collect);
        return ResultGenerator.genSuccessResult(collectGoodsList);
    }

    @GetMapping("/user/collectIds")
    @ApiOperation(value = "获取用户收藏列表-id数组", notes = "")
    public Result getUserCollectIds(@TokenToMallUser MallUser loginMallUser) {
        logger.info("获取用户收藏列表-id数组  MallUser:{}", loginMallUser.toString());
        String collect = loginMallUser.getCollect();
        //获得收藏列表
        List<ZhongHeMallGoods> collectGoodsList=zhongHeMallUserService.getUserCollect(loginMallUser.getUserId(),collect);
        if (collectGoodsList == null) {
            return ResultGenerator.genSuccessResult(null);
        }
        Long[] ids = new Long[collectGoodsList.size()];
        for (int i = 0; i < collectGoodsList.size(); i++) {
            Long id = collectGoodsList.get(i).getGoodsId();
            ids[i]=id;
        }
        return ResultGenerator.genSuccessResult(ids);
    }

    @PutMapping("/user/collect")
    @ApiOperation(value = "更改用户收藏列表", notes = "")
    public Result updateUserCollect(@RequestBody BatchIdParam batchIdParam, @TokenToMallUser MallUser loginMallUser) {
        logger.info("更改用户收藏列表接口  MallUser:{}", loginMallUser.toString());
//        String collect = loginMallUser.getCollect();
        //获得收藏列表
        String result =zhongHeMallUserService.updateUserCollect(loginMallUser.getUserId(),batchIdParam);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }
}
