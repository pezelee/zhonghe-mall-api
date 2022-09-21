/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.service;

import mall.api.admin.param.BatchIdParam;
import mall.api.admin.param.UserAddParam;
import mall.api.mall.param.MallUserUpdateParam;
import mall.api.mall.vo.ZhongHeMallUserVO;
import mall.entity.*;
import mall.util.PageQueryUtil;
import mall.util.PageResult;

import java.util.List;

public interface ZhongHeMallUserService {

    /**
     * 获得用户信息
     *
     * @param userId
     * @return
     */
    ZhongHeMallUserVO info(Long userId);

    /**
     * 用户注册
     *
     * @param loginName
     * @param password
     * @param sponsor
     * @return
     */
    String register(String loginName, String password, String sponsor);

    /**
     * 管理员新增用户注册
     *
     *
     * @param userAddParam
     * @param adminUser
     * @return
     */
    String addUser(UserAddParam userAddParam, AdminUserToken adminUser);

    /**
     * 登录
     *
     * @param loginName
     * @param passwordMD5
     * @return
     */
    String login(String loginName, String passwordMD5);

    /**
     * 用户信息修改
     *
     * @param mallUser
     * @return
     */
    Boolean updateUserInfo(MallUserUpdateParam mallUser, Long userId);

    /**
     * 登出接口
     * @param userId
     * @return
     */
    Boolean logout(Long userId);

    /**
     * 用户禁用与解除禁用(0-未锁定 1-已锁定)
     *
     * @param ids
     * @param lockStatus
     * @return
     */
    Boolean lockUsers(Long[] ids, int lockStatus);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getZhongHeMallUsersPage(PageQueryUtil pageUtil);

    /**
     * 积分获得
     *
     * @param userId
     * @param pointDTO
     * @return
     */
    Boolean addPoint(Long userId, PointDTO pointDTO);

    /**
     * 查看用户积分
     *
     * @param userId
     * @return
     */
    TotalPoint getTotalPoint(Long userId);

    /**
     * 支付积分
     *
     * @param userId
     * @param payPoint 待支付积分
     * @return
     */
    String payPoint(Long userId, Integer payPoint);

    /**
     * 付费获得积分
     *
     * @param userId
     * @param addPoint 付费获得积分
     * @return
     */
    String payCash(Long userId, Integer addPoint);

    /**
     * 获取用户收藏列表
     *
     *
     * @param userId
     * @param collect
     * @return
     */
    List<ZhongHeMallGoods> getUserCollect(Long userId, String collect);

    /**
     * 更改用户收藏列表
     *
     *
     * @param userId
     * @param batchIdParam
     * @return
     */
    String updateUserCollect(Long userId, BatchIdParam batchIdParam);
}
