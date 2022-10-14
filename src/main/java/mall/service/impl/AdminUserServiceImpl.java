package mall.service.impl;

import mall.common.ServiceResultEnum;
import mall.dao.AdminUserMapper;
import mall.dao.ZhongHeAdminUserTokenMapper;
import mall.entity.AdminUser;
import mall.entity.AdminUserToken;
import mall.service.AdminUserService;
import mall.util.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Resource
    private AdminUserMapper adminUserMapper;

    @Resource
    private ZhongHeAdminUserTokenMapper zhongHeAdminUserTokenMapper;

    @Override
    public String login(String userName, String password) {
        AdminUser loginAdminUser = adminUserMapper.login(userName, password);
        if (loginAdminUser != null) {
            if (loginAdminUser.getLocked() == 1) {
                //账号已锁定
                return ServiceResultEnum.LOGIN_LOCKED.getResult();
            }
            //登录后即执行修改token的操作
            String token = getNewToken(System.currentTimeMillis() + "", loginAdminUser.getAdminUserId());
            AdminUserToken adminUserToken = zhongHeAdminUserTokenMapper.selectByPrimaryKey(loginAdminUser.getAdminUserId());
            //当前时间
            Date now = new Date();
            //过期时间
            Date expireTime = new Date(now.getTime() + 2 * 24 * 3600 * 1000);//过期时间 48 小时
            if (adminUserToken == null) {
                adminUserToken = new AdminUserToken();
                adminUserToken.setAdminUserId(loginAdminUser.getAdminUserId());
                adminUserToken.setRole(loginAdminUser.getRole());
                adminUserToken.setOrganizationId(loginAdminUser.getOrganizationId());
                adminUserToken.setToken(token);
                adminUserToken.setUpdateTime(now);
                adminUserToken.setExpireTime(expireTime);
                //新增一条token数据
                if (zhongHeAdminUserTokenMapper.insertSelective(adminUserToken) > 0) {
                    //新增成功后返回
                    return token;
                }
            } else {
                adminUserToken.setToken(token);
                adminUserToken.setUpdateTime(now);
                adminUserToken.setExpireTime(expireTime);
                //更新
                if (zhongHeAdminUserTokenMapper.updateByPrimaryKeySelective(adminUserToken) > 0) {
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
        String src = timeStr + userId + NumberUtil.genRandomNum(6);
        return SystemUtil.genToken(src);
    }


    @Override
    public AdminUser getUserDetailById(Long loginUserId) {
        return adminUserMapper.selectByPrimaryKey(loginUserId);
    }

    @Override
    public String updatePassword(Long loginUserId, String originalPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        //当前用户非空才可以进行更改
        if (adminUser != null) {
            //比较原密码是否正确
            if (originalPassword.equals(adminUser.getLoginPassword())) {
                //设置新密码并修改
                adminUser.setLoginPassword(newPassword);
                if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0 && zhongHeAdminUserTokenMapper.deleteByPrimaryKey(loginUserId) > 0) {
                    //修改成功且清空当前token则返回true
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            }else return ServiceResultEnum.PASSWORD_ERROR.getResult();
        }
        return ServiceResultEnum.USER_NOT_EXIST.getResult();
    }

    @Override
    public String updateName(Long id, String loginUserName, String nickName) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(id);
        //登录名重复
        AdminUser temp =adminUserMapper.selectByLoginName(loginUserName);
        if ( temp!= null && !id.equals(temp.getAdminUserId())) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        //当前用户非空才可以进行更改
        if (adminUser != null) {
            //设置新名称并修改
            adminUser.setLoginUserName(loginUserName);
            adminUser.setNickName(nickName);
            if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0) {
                //修改成功则返回true
                return ServiceResultEnum.SUCCESS.getResult();
            }
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Boolean logout(Long adminUserId) {
        return zhongHeAdminUserTokenMapper.deleteByPrimaryKey(adminUserId) > 0;
    }

    @Override
    public String deleteById(Long id) {
        AdminUser temp = adminUserMapper.selectByPrimaryKey(id);
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setLocked((byte) 1);
//        temp.setUpdateTime(new Date());
        if (adminUserMapper.lockByPrimaryKey(id) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Boolean deleteBatch(Long[] ids, Byte lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        if (lockStatus == 1) {
            //删除数据
            adminUserMapper.lockUserBatch(ids, lockStatus);
            zhongHeAdminUserTokenMapper.deleteBatch(ids);
            return true;
        }
        return adminUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }

    @Override
    public PageResult getZhongHeMallAdminsPage(PageQueryUtil pageUtil) {

        List<AdminUser> adminUsers;
        int total;

        byte role = (byte) pageUtil.get("role");
        if (role == 0) {
            //后台总管理员
            adminUsers = adminUserMapper.findMallAdminList(pageUtil);
            total = adminUserMapper.getTotalMallAdmins(pageUtil);
        }else if(role == 1) {
            //组织管理员
            adminUsers = adminUserMapper.findMallAdminListByOrg(pageUtil);
            total = adminUserMapper.getTotalMallAdminsByOrg(pageUtil);
        }else{
            //客户经理
            adminUsers = adminUserMapper.findMallAdminListByOrg(pageUtil);
            total = adminUserMapper.getTotalMallAdminsByOrg(pageUtil);
        }
        PageResult pageResult = new PageResult(adminUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveAdmin(AdminUser adminUser) {
        if (adminUserMapper.selectByLoginName(adminUser.getLoginUserName()) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        adminUser.setCreateTime(new Date());
        adminUser.setUpdateTime(new Date());

        //密码
        String password=adminUser.getLoginPassword();
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        adminUser.setLoginPassword(passwordMD5);

        //客户经理号，自动填入用户名手机号
        String sponsor;
        sponsor=adminUser.getLoginUserName();
        adminUser.setSponsor(sponsor);
        adminUser.setPhone(sponsor);

        if (adminUserMapper.insertSelective(adminUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }
}
