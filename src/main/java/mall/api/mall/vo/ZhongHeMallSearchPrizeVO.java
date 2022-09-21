/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.api.mall.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 搜索列表页商品VO
 */
@Data
public class ZhongHeMallSearchPrizeVO implements Serializable {

    @ApiModelProperty("奖品id")
    private Long prizeId;

    @ApiModelProperty("奖品名称")
    private String prizeName;

    @ApiModelProperty("奖品简介")
    private String prizeIntro;

    @ApiModelProperty("奖品图片地址")
    private String prizeCoverImg;

    @ApiModelProperty("奖品初始数量")
    private Integer originalStock;

    @ApiModelProperty("奖品剩余数量")
    private Integer stockNum;

}
