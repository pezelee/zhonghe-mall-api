
package mall.service.impl;

import mall.api.admin.param.BatchIdParam;
import mall.api.admin.param.UserAddParam;
import mall.api.mall.param.MallUserUpdateParam;
import mall.api.mall.param.PasswordUpdateParam;
import mall.api.mall.vo.ZhongHeMallUserVO;
import mall.common.Constants;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallException;
import mall.dao.AdminUserMapper;
import mall.dao.MallUserMapper;
import mall.dao.ZhongHeMallGoodsMapper;
import mall.dao.ZhongHeMallUserTokenMapper;
import mall.entity.*;
import mall.service.ZhongHeMallUserService;
import mall.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ZhongHeMallUserServiceImpl implements ZhongHeMallUserService {

    @Autowired
    private MallUserMapper mallUserMapper;
    @Autowired
    private AdminUserMapper adminUserMapper;
    @Autowired
    private ZhongHeMallUserTokenMapper zhongHeMallUserTokenMapper;
    @Autowired
    private ZhongHeMallGoodsMapper goodsMapper;

    @Override
    public ZhongHeMallUserVO info(Long userId){
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        if (user == null ) {
            ZhongHeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        ZhongHeMallUserVO userVO = new ZhongHeMallUserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public String register(String loginName, String password, String sponsor) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        registerUser.setIntroduceSign(Constants.USER_INTRO);
        //密码
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        //根据客户经理号，获得客户经理信息-所属分行
        if (sponsor == null) {
            registerUser.setOrganizationId(1L);//不填时默认为1
        }else {
            AdminUser adminUser = adminUserMapper.selectBySponsor(sponsor);
            if (adminUser == null) {
                //客户经理不存在
                return ServiceResultEnum.SPONSOR_NOT_EXIST_ERROR.getResult();
            }
            registerUser.setOrganizationId(adminUser.getOrganizationId());
            registerUser.setSponsor(sponsor);
        }
        registerUser.setCreateTime(new Date());

        if (mallUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String addUser(UserAddParam userAddParam, AdminUserToken adminUser) {
        if (mallUserMapper.selectByLoginName(userAddParam.getLoginUserName()) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(userAddParam.getLoginUserName());
        registerUser.setNickName(userAddParam.getNickName());
        registerUser.setIntroduceSign(Constants.USER_INTRO);
        //密码
        String passwordMD5 = MD5Util.MD5Encode(userAddParam.getLoginPassword(), "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (userAddParam.getOrganizationId() == null) {
            //分行未填写，默认为管理员自己的分行
            registerUser.setOrganizationId(adminUser.getOrganizationId());
        }else {
            registerUser.setOrganizationId(userAddParam.getOrganizationId());
        }
        registerUser.setPhone(userAddParam.getLoginUserName());
        registerUser.setCreateTime(new Date());

        if (mallUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String login(String loginName, String passwordMD5) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED_ERROR.getResult();
            }
            //登录后即执行修改token的操作
            String token = getNewToken(System.currentTimeMillis() + "", user.getUserId());
            Long organizationId = user.getOrganizationId();
            MallUserToken mallUserToken = zhongHeMallUserTokenMapper.selectByPrimaryKey(user.getUserId());
            //当前时间
            Date now = new Date();
            //过期时间
            Date expireTime = new Date(now.getTime() + 5 * 24 * 3600 * 1000);//过期时间 48 小时
            if (mallUserToken == null) {
                mallUserToken = new MallUserToken();
                mallUserToken.setUserId(user.getUserId());
                mallUserToken.setToken(token);
                mallUserToken.setOrganizationId(organizationId);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);
                //新增一条token数据
                if (zhongHeMallUserTokenMapper.insertSelective(mallUserToken) > 0) {
                    //新增成功后返回
                    return token;
                }
            } else {
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);
                //更新
                if (zhongHeMallUserTokenMapper.updateByPrimaryKeySelective(mallUserToken) > 0) {
                    //修改成功后返回
                    return token;
                }
            }

        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    /**
     * 获取token值
     *
     * @param timeStr
     * @param userId
     * @return
     */
    private String getNewToken(String timeStr, Long userId) {
        String src = timeStr + userId + NumberUtil.genRandomNum(4);
        return SystemUtil.genToken(src);
    }

    @Override
    public Boolean updateUserInfo(MallUserUpdateParam mallUser, Long userId) {
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            ZhongHeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        user.setNickName(mallUser.getNickName());
        user.setIntroduceSign(mallUser.getIntroduceSign());
        if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean updatePassword(PasswordUpdateParam password, Long userId) {
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            ZhongHeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        if (!user.getPasswordMd5().equals(password.getPasswordMd5Old())){
            ZhongHeMallException.fail(ServiceResultEnum.PASSWORD_ERROR.getResult());
        }
        user.setPasswordMd5(password.getPasswordMd5New());
        //若密码为空字符，则表明用户不打算修改密码，使用原密码保存
//        if (!MD5Util.MD5Encode("", "UTF-8").equals(mallUser.getPasswordMd5())){
//            user.setPasswordMd5(mallUser.getPasswordMd5());
//        }
        if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean logout(Long userId) {
        return zhongHeMallUserTokenMapper.deleteByPrimaryKey(userId) > 0;
    }

    @Override
    public PageResult getZhongHeMallUsersPage(PageQueryUtil pageUtil) {
        List<MallUser> mallUsers = mallUserMapper.findMallUserList(pageUtil);
        int total = mallUserMapper.getTotalMallUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean lockUsers(Long[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return mallUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }

    @Override
    public Boolean addPoint(Long userId, PointDTO pointDTO) {

        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        String pointList = user.getPoint();
        //新获得积分添加到最后
        pointList= UserUtils.addString(UserUtils.DTOtoString(pointDTO),pointList);
        user.setPoint(pointList);
        if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
            return true;
        }
        return false;
    }

    //查看并更新用户积分
    @Override
    public TotalPoint getTotalPoint(Long userId){
        TotalPoint totalPoint = new TotalPoint();
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        String points = user.getPoint();
        StringBuilder newPoints = new StringBuilder();//更新积分列表
        //获得积分列表
        List<PointDTO> tempDTOS = UserUtils.toDTOList(points);

        int total = 0;//总积分
        Integer expirePoint = 0;//过期积分
        Date nextExpiretime = new Date();//最近过期时间
        int nextPoint = 0;//最近过期积分
        List<PointDTO> newpointDTOS = new ArrayList<>();

        if (tempDTOS != null) {
            //按过期时间排序,合并同一个过期日期的积分
            List<PointDTO> pointDTOS = UserUtils.sortPoints(tempDTOS);

            int nowPoint = 0;//
            Date lastExpiretime = new Date();//上一条过期时间
            for(PointDTO pointDTO : pointDTOS){
                Date expiretime = pointDTO.getExpiretime();
                Date now = new Date();
                if (now.before(expiretime)) {
                    //未过期
                    Integer tempPoint =pointDTO.getPoint();
                    if (tempPoint > 0) {
                        total += tempPoint;
                        if (nextExpiretime.before(expiretime)) {//最近过期积分
                            nextPoint = tempPoint;
                            nextExpiretime=expiretime;
                        }else if(expiretime.equals(nextExpiretime)){//最近过期时间相同
                            nextPoint += tempPoint;
                        }
                        if (!newPoints.toString().equals("")) {
                            newPoints.append(",");
                        }
                        newPoints.append(UserUtils.DTOtoString(pointDTO));
                        newpointDTOS.add(pointDTO);
                    }
                }else {
                    //已过期
                    expirePoint += pointDTO.getPoint();
                }
            }
            //更新去掉过期积分之后的新列表
            user.setPoint(newPoints.toString());
            mallUserMapper.updateByPrimaryKeySelective(user);

        }
        //返回总积分
        totalPoint.setTotalPoint(total);
        totalPoint.setExpirePoint(expirePoint);
        totalPoint.setNextExpiretime(nextExpiretime);
        totalPoint.setNextPoint(nextPoint);
        totalPoint.setPointDTOs(newpointDTOS);
        return totalPoint;
    }


    //支付积分
    @Override
    public String payPoint(Long userId, Integer payPoint) {

        TotalPoint totalPoint= getTotalPoint(userId);
        if (totalPoint.getTotalPoint() < payPoint) {
            //积分不足
            return ServiceResultEnum.POINT_NOT_ENOUGH.getResult();
        }

        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        String points = user.getPoint();
        StringBuilder newPoints = new StringBuilder();//更新积分列表
        //获得积分列表
        List<PointDTO> pointDTOS = UserUtils.toDTOList(points);
        int surPoint = payPoint;//剩余未支付积分
        for(PointDTO pointDTO : pointDTOS){
            Integer point = pointDTO.getPoint();
            if (point >= surPoint) {//本条积分足够支付
                point -= surPoint;
                surPoint = 0;
                PointDTO newPointDTO = new PointDTO();
                newPointDTO.setExpiretime(pointDTO.getExpiretime());
                newPointDTO.setPoint(point);
                if (!newPoints.toString().equals("")) {
                    newPoints.append(",");
                }
                newPoints.append(UserUtils.DTOtoString(newPointDTO));
            }else {//本条积分不够支付
                surPoint -= point;
                point = 0;
            }
        }
        //更新支付积分之后的新列表
        user.setPoint(newPoints.toString());

        if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //付费获得积分
    @Override
    public String payCash(Long userId, Integer addPoint) {
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        String points = user.getPoint();
        PointDTO pointDTO = new PointDTO();
        Date now = new Date();
        Date expiretime = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        //开始时间加100年,最终结果 2022-02-28 12:12:12
        c.add(Calendar.YEAR, 100);
        expiretime=c.getTime();
        //新获得积分添加到最后
        pointDTO.setPoint(addPoint);
        pointDTO.setExpiretime(expiretime);
        points= UserUtils.addString(UserUtils.DTOtoString(pointDTO),points);
        user.setPoint(points);
        if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    //获取用户收藏列表
    @Override
    public List<ZhongHeMallGoods> getUserCollect(Long userId, String collect) {
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        List<ZhongHeMallGoods> collectGoodsList = new ArrayList<>();
        String newCollect="";//更新收藏字符串
        if (collect == null) {
            return null;
        }
        String[] collectIdList = collect.split(",");
        for (String s : collectIdList) {
            ZhongHeMallGoods tempGoods = goodsMapper.selectByPrimaryKey(Long.valueOf(s));
            if (tempGoods != null) {
                collectGoodsList.add(tempGoods);
                newCollect= UserUtils.addString(s,newCollect);//只留下有效的商品ID
            }
        }
        user.setCollect(newCollect);
        try{
            mallUserMapper.updateByPrimaryKeySelective(user);
        } catch (Exception e) {e.printStackTrace();}
        return collectGoodsList;
    }

    //更改用户收藏列表
    @Override
    public String updateUserCollect(Long userId, BatchIdParam batchIdParam) {
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        String newCollect = UserUtils.batchIdtoString(batchIdParam);
        user.setCollect(newCollect);
        if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }
}
