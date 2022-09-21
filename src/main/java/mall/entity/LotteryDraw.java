/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class LotteryDraw {
    private Long lotteryDrawId;//抽奖ID

    private Long userId;

    private String nickName;

    private String sponsor;//客户经理号

    private Long organizationId;//所属组织

    private String orgName;//所属组织名称

    private Long activityId;//活动ID

    private String activityName;//活动名称

    private Date drawTime;//抽奖时间

    private Date expireTime;//过期时间

    private Byte status;//状态:0.待抽奖 1.已抽奖 2.待发送 3:发送中 4.发送完成 5.已接收 -1.抽奖失败 -2.用户关闭 -3.商家关闭'

    private Long prizeId;//抽到奖品

    private String prizeName;

    private Integer prizeValue;

    private Byte prizeType;//奖品种类  0：实物快递  1：实物现场  2： 积分 3：会员卡

    private String mailNo;//邮寄单号

    private Long createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Long updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}