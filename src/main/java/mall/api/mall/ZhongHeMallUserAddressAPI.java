package mall.api.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mall.api.mall.param.SaveMallUserAddressParam;
import mall.api.mall.param.UpdateMallUserAddressParam;
import mall.api.mall.vo.ZhongHeMallUserAddressVO;
import mall.common.ServiceResultEnum;
import mall.config.annotation.TokenToMallUser;
import mall.entity.MallUser;
import mall.entity.MallUserAddress;
import mall.service.ZhongHeMallUserAddressService;
import mall.util.BeanUtil;
import mall.util.Result;
import mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(value = "v1", tags = "2-1.众鹤商城个人地址相关接口")
@RequestMapping("/api/v1")
public class ZhongHeMallUserAddressAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeMallUserAddressAPI.class);

    @Resource
    private ZhongHeMallUserAddressService mallUserAddressService;

    @GetMapping("/address")
    @ApiOperation(value = "我的收货地址列表", notes = "")
    public Result<List<ZhongHeMallUserAddressVO>> addressList(@TokenToMallUser MallUser loginMallUser) {
        logger.info("我的收货地址列表接口     loginMallUser:{}", loginMallUser.toString());
        return ResultGenerator.genSuccessResult(mallUserAddressService.getMyAddresses(loginMallUser.getUserId()));
    }

    @PostMapping("/address")
    @ApiOperation(value = "添加地址", notes = "")
    public Result<Boolean> saveUserAddress(@RequestBody SaveMallUserAddressParam saveMallUserAddressParam,
                                           @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户添加地址接口     loginMallUser:{},saveMallUserAddressParam:{}", loginMallUser.toString(),saveMallUserAddressParam.toString());
        MallUserAddress userAddress = new MallUserAddress();
        BeanUtil.copyProperties(saveMallUserAddressParam, userAddress);
        userAddress.setUserId(loginMallUser.getUserId());
        Boolean saveResult = mallUserAddressService.saveUserAddress(userAddress);
        //添加成功
        if (saveResult) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult("添加失败");
    }

    @PutMapping("/address")
    @ApiOperation(value = "修改地址", notes = "")
    public Result<Boolean> updateMallUserAddress(@RequestBody UpdateMallUserAddressParam updateMallUserAddressParam,
                                                 @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户修改地址接口     loginMallUser:{},AddressParam:{}", loginMallUser.toString(),updateMallUserAddressParam.toString());
        MallUserAddress mallUserAddressById = mallUserAddressService.getMallUserAddressById(updateMallUserAddressParam.getAddressId());
        if (!loginMallUser.getUserId().equals(mallUserAddressById.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        MallUserAddress userAddress = new MallUserAddress();
        BeanUtil.copyProperties(updateMallUserAddressParam, userAddress);
        userAddress.setUserId(loginMallUser.getUserId());
        Boolean updateResult = mallUserAddressService.updateMallUserAddress(userAddress);
        //修改成功
        if (updateResult) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult("修改失败");
    }

    @GetMapping("/address/{addressId}")
    @ApiOperation(value = "获取收货地址详情", notes = "传参为地址id")
    public Result<ZhongHeMallUserAddressVO> getMallUserAddress(@PathVariable("addressId") Long addressId,
                                                               @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户获取收货地址详情接口     loginMallUser:{},AddressParam:{}", loginMallUser.toString(),addressId.toString());
        MallUserAddress mallUserAddressById = mallUserAddressService.getMallUserAddressById(addressId);
        ZhongHeMallUserAddressVO zhongHeMallUserAddressVO = new ZhongHeMallUserAddressVO();
        BeanUtil.copyProperties(mallUserAddressById, zhongHeMallUserAddressVO);
        if (!loginMallUser.getUserId().equals(mallUserAddressById.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        return ResultGenerator.genSuccessResult(zhongHeMallUserAddressVO);
    }

    @GetMapping("/address/default")
    @ApiOperation(value = "获取默认收货地址", notes = "无传参")
    public Result getDefaultMallUserAddress(@TokenToMallUser MallUser loginMallUser) {
        logger.info("用户获取默认收货地址接口     loginMallUser:{}", loginMallUser.toString());
        MallUserAddress mallUserAddressById = mallUserAddressService.getMyDefaultAddressByUserId(loginMallUser.getUserId());
        return ResultGenerator.genSuccessResult(mallUserAddressById);
    }

    @DeleteMapping("/address/{addressId}")
    @ApiOperation(value = "删除收货地址", notes = "传参为地址id")
    public Result deleteAddress(@PathVariable("addressId") Long addressId,
                                @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户获取收货地址详情接口     loginMallUser:{},AddressParam:{}", loginMallUser.toString(),addressId.toString());
        MallUserAddress mallUserAddressById = mallUserAddressService.getMallUserAddressById(addressId);
        if (!loginMallUser.getUserId().equals(mallUserAddressById.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        Boolean deleteResult = mallUserAddressService.deleteById(addressId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }
}
