 
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class GoodsAddParam {

    @ApiModelProperty("商品名称")
    @NotEmpty(message = "商品名称不能为空")
    @Length(max = 128,message = "商品名称内容过长")
    private String goodsName;

    @ApiModelProperty("商品简介")
    @NotEmpty(message = "商品简介不能为空")
    @Length(max = 200,message = "商品简介内容过长")
    private String goodsIntro;

    @ApiModelProperty("分类id")
    @NotNull(message = "商品分类不能为空")
    @Min(value = 1, message = "分类id最低为1")
    private Long goodsCategoryId;

    @ApiModelProperty("商品主图")
    @NotEmpty(message = "商品主图不能为空")
    private String goodsCoverImg;

    @ApiModelProperty("原价")
    @NotNull(message = "原价不能为空")
    @Min(value = 0, message = "原价最低为0")
    @Max(value = 1000000, message = "原价最高为1000000")
    private Integer originalPrice;

    @ApiModelProperty("售价")
    @NotNull(message = "售价不能为空")
    @Min(value = 0, message = "售价最低为0")
    @Max(value = 1000000, message = "售价最高为1000000")
    private Integer sellingPrice;

    @ApiModelProperty("积分售价")
    @NotNull(message = "积分售价不能为空")
    private String sellingPoint;

    @ApiModelProperty("库存")
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存最低为0")
    @Max(value = 100000, message = "库存最高为100000")
    private Integer stockNum;

    @ApiModelProperty("商品标签")
    @Length(max = 16,message = "商品标签内容过长")
    private String tag;

    private Byte goodsSellStatus;

    @ApiModelProperty("商品详情")
    @NotEmpty(message = "商品详情不能为空")
    private String goodsDetailContent;
}