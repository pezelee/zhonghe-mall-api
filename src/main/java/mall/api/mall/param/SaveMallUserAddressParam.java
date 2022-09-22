package mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 添加收货地址param
 */
@Data
public class SaveMallUserAddressParam {

    @ApiModelProperty("收件人名称")
    @NotEmpty(message = "收件人名称不能为空")
    private String userName;

    @ApiModelProperty("收件人联系方式")
    @NotEmpty(message = "收件人联系方式不能为空")
    private String userPhone;

    @ApiModelProperty("是否默认地址 0-不是 1-是")
    private Byte defaultFlag;

    @ApiModelProperty("省")
    @NotEmpty(message = "省不能为空")
    private String provinceName;

    @ApiModelProperty("市")
    @NotEmpty(message = "市不能为空")
    private String cityName;

    @ApiModelProperty("区/县")
    @NotEmpty(message = "区/县不能为空")
    private String regionName;

    @ApiModelProperty("详细地址")
    @NotEmpty(message = "详细地址不能为空")
    private String detailAddress;
}
