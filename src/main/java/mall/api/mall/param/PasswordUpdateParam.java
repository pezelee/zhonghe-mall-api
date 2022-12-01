package mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 用户修改param
 */
@Data
public class PasswordUpdateParam implements Serializable {

    @ApiModelProperty("用户新密码(需要MD5加密)")
    @NotEmpty(message = "密码不能为空")
    private String passwordMd5Old;

    @ApiModelProperty("用户旧密码(需要MD5加密)")
    @NotEmpty(message = "密码不能为空")
    private String passwordMd5New;

}
