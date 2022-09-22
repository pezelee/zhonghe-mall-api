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
