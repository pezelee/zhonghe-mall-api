 
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AdminAddParam implements Serializable {

    @ApiModelProperty("登录名")
    @NotEmpty(message = "登录名不能为空")
    private String loginUserName;

    @ApiModelProperty("用户密码(需要MD5加密)")
    @NotEmpty(message = "密码不能为空")
    private String loginPassword;

    @ApiModelProperty("用户昵称")
    @NotEmpty(message = "用户昵称不能为空")
    private String nickName;

    @ApiModelProperty("所属分行组织")
    @NotNull(message = "分行组织不能为空")
    private Long organizationId;

    @ApiModelProperty("角色")
    @NotNull(message = "角色不能为空")
    private Byte role;

//    @ApiModelProperty("手机号")
//    private String phone;
}
