package mall.api.mall.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 奖品详情页VO
 */
@Data
public class ZhongHeMallPrizeDetailVO implements Serializable {

    @ApiModelProperty("奖品id")
    private Long prizeId;

    @ApiModelProperty("奖品名称")
    private String prizeName;

    @ApiModelProperty("奖品简介")
    private String prizeIntro;

    @ApiModelProperty("奖品图片地址")
    private String prizeCoverImg;

    @ApiModelProperty("奖品图片")
    private String[] prizeCarouselList;

    @ApiModelProperty("奖品初始数量")
    private Integer originalStock;

    @ApiModelProperty("奖品剩余数量")
    private Integer stockNum;

    @ApiModelProperty("奖品标签")
    private String tag;

    @ApiModelProperty("奖品详情字段")
    private String prizeDetailContent;
}
