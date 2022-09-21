/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class RuleAddParam {

    @ApiModelProperty("待修改活动id")
    @NotNull(message = "活动id不能为空")
    @Min(value = 1, message = "活动id不能为空")
    private Long activityId;

    @ApiModelProperty("未中奖词")
    @NotEmpty(message = "未中奖词 不能为空")
    @Length(max = 200,message = "未中奖词 内容过长")
    private String noticeEmpty;

    @ApiModelProperty("一等奖词")
    @NotEmpty(message = "一等奖词 不能为空")
    @Length(max = 200,message = "一等奖词 内容过长")
    private String noticeLv1;

    @ApiModelProperty("二等奖词")
    @Length(max = 200,message = "二等奖词 内容过长")
    private String noticeLv2;

    @ApiModelProperty("三等奖词")
    @Length(max = 200,message = "三等奖词 内容过长")
    private String noticeLv3;

    @ApiModelProperty("其他奖词")
    @Length(max = 200,message = "其他奖词 内容过长")
    private String noticeOther;

    @ApiModelProperty("抽到空缺奖品时：0：重新抽取  1：顺延下一等奖品  2：未中奖")
    private Byte vacancy;

    @ApiModelProperty("一等奖中奖间隔（小时）")
    @Min(value = 0, message = "一等奖中奖间隔（小时） 最低为0")
    @Max(value = 1000000, message = "一等奖中奖间隔（小时） 最高为1000000")
    private Integer intervalLv1;

    @ApiModelProperty("二等奖中奖间隔（小时）")
    @Min(value = 0, message = "二等奖中奖间隔（小时） 最低为0")
    @Max(value = 1000000, message = "二等奖中奖间隔（小时） 最高为1000000")
    private Integer intervalLv2;

    @ApiModelProperty("三等奖中奖间隔（小时）")
    @Min(value = 0, message = "三等奖中奖间隔（小时） 最低为0")
    @Max(value = 1000000, message = "三等奖中奖间隔（小时） 最高为1000000")
    private Integer intervalLv3;

    @ApiModelProperty("个人抽奖间隔（小时）")
    @Min(value = 0, message = "个人抽奖间隔（小时） 最低为0")
    @Max(value = 1000000, message = "个人抽奖间隔（小时） 最高为1000000")
    private Integer intervalDraw;

    @ApiModelProperty("个人一等奖中奖间隔（小时）")
    @Min(value = 0, message = "个人一等奖中奖间隔（小时） 最低为0")
    @Max(value = 1000000, message = "个人一等奖中奖间隔（小时） 最高为1000000")
    private Integer personIntervalLv1;

    @ApiModelProperty("个人二等奖中奖间隔（小时）")
    @Min(value = 0, message = "个人二等奖中奖间隔（小时） 最低为0")
    @Max(value = 1000000, message = "个人二等奖中奖间隔（小时） 最高为1000000")
    private Integer personIntervalLv2;

    @ApiModelProperty("个人三等奖中奖间隔（小时）")
    @Min(value = 0, message = "个人三等奖中奖间隔（小时） 最低为0")
    @Max(value = 1000000, message = "个人三等奖中奖间隔（小时） 最高为1000000")
    private Integer personIntervalLv3;

    @ApiModelProperty("一等奖保底次数")
    @Min(value = 0, message = "一等奖保底次数 最低为0")
    @Max(value = 1000000, message = "一等奖保底次数 最高为1000000")
    private Integer guaranteeLv1;

    @ApiModelProperty("二等奖保底次数")
    @Min(value = 0, message = "二等奖保底次数 最低为0")
    @Max(value = 1000000, message = "二等奖保底次数 最高为1000000")
    private Integer guaranteeLv2;

    @ApiModelProperty("三等奖保底次数")
    @Min(value = 0, message = "三等奖保底次数 最低为0")
    @Max(value = 1000000, message = "三等奖保底次数 最高为1000000")
    private Integer guaranteeLv3;

    @ApiModelProperty("第一阶段结束日期")
    private Date period1stDate;

    @ApiModelProperty("第一阶段一等奖数量")
    @Min(value = 0, message = "第一阶段一等奖数量 最低为0")
    private Integer period1stNum1;

    @ApiModelProperty("第一阶段二等奖数量")
    @Min(value = 0, message = "第一阶段二等奖数量 最低为0")
    private Integer period1stNum2;

    @ApiModelProperty("第一阶段三等奖数量")
    @Min(value = 0, message = "第一阶段三等奖数量 最低为0")
    private Integer period1stNum3;

    @ApiModelProperty("第二阶段结束日期")
    private Date period2ndDate;

    @ApiModelProperty("第二阶段一等奖数量")
    @Min(value = 0, message = "第二阶段一等奖数量 最低为0")
    private Integer period2ndNum1;

    @ApiModelProperty("第二阶段二等奖数量")
    @Min(value = 0, message = "第二阶段二等奖数量 最低为0")
    private Integer period2ndNum2;

    @ApiModelProperty("第二阶段三等奖数量")
    @Min(value = 0, message = "第二阶段三等奖数量 最低为0")
    private Integer period2ndNum3;

    @ApiModelProperty("领奖过期时间类型：0 在活动中统一配置，1 每个奖品单独配置，2  抽奖后X天")
    private Byte expireType;

    @ApiModelProperty("中奖后过期天数,领奖过期类型为2时有效")
    @Min(value = 1, message = "中奖后过期天数 最低为1")
    private Integer expireDays;
}