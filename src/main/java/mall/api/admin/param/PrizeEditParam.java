 
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PrizeEditParam {

    @ApiModelProperty("待修改奖品id")
    @NotNull(message = "奖品id不能为空")
    @Min(value = 1, message = "奖品id不能为空")
    private Long prizeId;

    @ApiModelProperty("奖品名称")
    @NotEmpty(message = "奖品名称不能为空")
    @Length(max = 128,message = "奖品名称内容过长")
    private String prizeName;

    @ApiModelProperty("奖品简介")
    @NotEmpty(message = "奖品简介不能为空")
    @Length(max = 200,message = "奖品简介内容过长")
    private String prizeIntro;

    @ApiModelProperty("分类id")
    @NotNull(message = "奖品分类不能为空")
    @Min(value = 1, message = "奖品分类最低为1")
    private Long prizeCategoryId;

    @ApiModelProperty("奖品种类")
    @NotNull(message = "奖品种类不能为空")
    private Byte prizeType;

    @ApiModelProperty("奖品主图")
    @NotEmpty(message = "奖品主图不能为空")
    private String prizeCoverImg;

    @ApiModelProperty("奖品值")
    @Min(value = 0, message = "奖品值最低为0")
    @Max(value = 1000000, message = "奖品值最高为1000000")
    private Integer prizeValue;

    @ApiModelProperty("初始库存")
    @NotNull(message = "初始库存不能为空")
    @Min(value = 0, message = "初始库存最低为0")
    @Max(value = 1000000, message = "初始库存最高为1000000")
    private Integer originalStock;

    @ApiModelProperty("剩余库存")
    @NotNull(message = "剩余库存不能为空")
    @Min(value = 0, message = "剩余库存最低为0")
    @Max(value = 1000000, message = "剩余库存最高为1000000")
    private Integer stockNum;

    @ApiModelProperty("奖品标签")
    @Length(max = 16,message = "奖品标签内容过长")
    private String tag;

    @ApiModelProperty("奖品是否上架")
    private Byte prizeSellStatus;

    @ApiModelProperty("抽奖权重")
    @NotNull(message = "抽奖权重不能为空")
    @Min(value = 0, message = "抽奖权重最低为0")
    @Max(value = 100000, message = "抽奖权重最高为100000")
    private Integer prizeWeight;

    @ApiModelProperty("奖品等级 0：未中奖，1：一等奖，2：二等奖")
    @NotNull(message = "奖品等级不能为空")
    private Byte prizeLevel;

    @ApiModelProperty("中奖间隔（小时）")
    @NotNull(message = "中奖间隔不能为空")
    @Min(value = 0, message = "中奖间隔（小时）最低为0")
    @Max(value = 100000, message = "中奖间隔（小时）最高为100000")
    private Integer prizeInterval;

    @ApiModelProperty("奖品详情")
    @NotEmpty(message = "奖品详情不能为空")
    private String prizeDetailContent;
}