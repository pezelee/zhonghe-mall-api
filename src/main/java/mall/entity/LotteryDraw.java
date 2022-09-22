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