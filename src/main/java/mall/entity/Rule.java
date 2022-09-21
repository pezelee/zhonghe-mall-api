
package mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Rule {
    private Long id;

    private Long activityId;

    private String activityName;

    private String noticeEmpty;//未中奖词

    private String noticeLv1;//一等奖词

    private String noticeLv2;//二等奖词

    private String noticeLv3;//三等奖词

    private String noticeOther;//其他奖词

    private Byte vacancy;//抽到空缺奖品时：0：重新抽取  1：顺延下一等奖品  2：未中奖

    private Integer intervalLv1;//一等奖中奖间隔（小时）

    private Integer intervalLv2;//二等奖中奖间隔（小时）

    private Integer intervalLv3;//三等奖中奖间隔（小时）

    private Integer intervalDraw;//个人抽奖间隔（小时）

    private Integer personIntervalLv1;//个人一等奖中奖间隔（小时）

    private Integer personIntervalLv2;//个人二等奖中奖间隔（小时）

    private Integer personIntervalLv3;//个人三等奖中奖间隔（小时）

    private Integer guaranteeLv1;//一等奖保底次数

    private Integer guaranteeLv2;//二等奖保底次数

    private Integer guaranteeLv3;//三等奖保底次数

    private String period1st;//第一阶段【结束日期，一等奖数量，二等奖数量，三等奖数量】

    private Date period1stDate;//第一阶段结束日期

    private Integer period1stNum1;//第一阶段一等奖数量

    private Integer period1stNum2;//第一阶段二等奖数量

    private Integer period1stNum3;//第一阶段三等奖数量

    private String period2nd;//第二阶段【结束日期，一等奖数量，二等奖数量，三等奖数量】

    private Date period2ndDate;//第二阶段结束日期

    private Integer period2ndNum1;//第二阶段一等奖数量

    private Integer period2ndNum2;//第二阶段二等奖数量

    private Integer period2ndNum3;//第二阶段三等奖数量

    private Byte expireType;//领奖过期时间类型：0 在活动中统一配置，1 每个奖品单独配置，2  抽奖后X天

    private Integer expireDays;//中奖后过期天数,领奖过期类型为2时有效

    private Byte status;

    private Long createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Long updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

//    id, activity_id, activity_name, notice_empty, notice_lv1, notice_lv2, notice_lv3, notice_other,
//    vacancy, interval_lv1, interval_lv2, interval_lv3, interval_draw,
//    person_interval_lv1, person_interval_lv2, person_interval_lv3,
//    guarantee_lv1, guarantee_lv2, guarantee_lv3, period_1st, period_2nd,
//    expire_type, expire_days, status,
//    create_user, create_time, update_user, update_time

//            #{id,jdbcType=BIGINT}, #{activityId,jdbcType=BIGINT}, #{activityName,jdbcType=VARCHAR},
//            #{noticeEmpty,jdbcType=VARCHAR},#{noticeLv1,jdbcType=VARCHAR},#{noticeLv2,jdbcType=VARCHAR},
//            #{noticeLv3,jdbcType=VARCHAR},#{noticeOther,jdbcType=VARCHAR},  #{vacancy,jdbcType=TINYINT},
//            #{intervalLv1,jdbcType=INTEGER}, #{intervalLv2,jdbcType=INTEGER},
//            #{intervalLv3,jdbcType=INTEGER}, #{intervalDraw,jdbcType=INTEGER},
//            #{personIntervalLv1,jdbcType=INTEGER}, #{personIntervalLv2,jdbcType=INTEGER}, #{personIntervalLv3,jdbcType=INTEGER},
//            #{guaranteeLv1,jdbcType=INTEGER}, #{guaranteeLv2,jdbcType=INTEGER}, #{guaranteeLv3,jdbcType=INTEGER},
//            #{period1st,jdbcType=VARCHAR},#{period2nd,jdbcType=VARCHAR},
//            #{expireType,jdbcType=TINYINT}, #{expireDays,jdbcType=INTEGER}, #{status,jdbcType=TINYINT},
//            #{createUser,jdbcType=BIGINT}, #{createTime,jdbcType=TIMESTAMP},
//            #{updateUser,jdbcType=BIGINT}, #{updateTime,jdbcType=TIMESTAMP},
}