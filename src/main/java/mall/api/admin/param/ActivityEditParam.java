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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class ActivityEditParam {

    @ApiModelProperty("待修改活动id")
    @NotNull(message = "活动id不能为空")
    @Min(value = 1, message = "活动id不能为空")
    private Long activityId;

    @ApiModelProperty("待修改活动名称")
    @NotEmpty(message = "活动名称不能为空")
    @Length(max = 128,message = "活动名称内容过长")
    private String activityName;

    @ApiModelProperty("分行id")
    @NotNull(message = "分行id不能为空")
    private Long organizationId;

    @ApiModelProperty("待修改活动简介")
    @NotEmpty(message = "活动简介不能为空")
    @Length(max = 200,message = "活动简介内容过长")
    private String activityIntro;

    @ApiModelProperty("待修改活动主图")
    @NotEmpty(message = "活动主图不能为空")
    private String activityCoverImg;

    @ApiModelProperty("待修改活动主图")
    @NotEmpty(message = "活动主图不能为空")
    private String activityCarousel;

    @ApiModelProperty("待修改起始时间")
    @NotNull(message = "起始时间不能为空")
    private Date starttime;

    @ApiModelProperty("待修改结束时间")
    @NotNull(message = "结束时间不能为空")
    private Date endtime;

    @ApiModelProperty("待修改奖品过期时间")
    @NotNull(message = "奖品过期时间不能为空")
    private Date expiretime;

    @ApiModelProperty("待修改奖品列表")
    @NotEmpty(message = "奖品列表不能为空")
    private String prizes;//活动奖品池:[1,2,5]

    @ApiModelProperty("待修改规则id")
    @NotNull(message = "规则id不能为空")
    private Long rule;

    @ApiModelProperty("待修改模板id")
    @NotNull(message = "模板id不能为空")
    private Long template;

    @ApiModelProperty("待修改活动是否上架")
    private Byte status;


    @ApiModelProperty("待修改活动详情")
    @NotEmpty(message = "活动详情不能为空")
    private String activityDetailContent;
}