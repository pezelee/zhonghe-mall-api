 
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateAdminInfoParam {


    @ApiModelProperty("管理员ID")
    @NotNull(message = "管理员ID不能为空")
    private Long adminUserId;

    @ApiModelProperty("所属分行组织")
    @NotNull(message = "分行组织不能为空")
    private Long organizationId;

    @NotEmpty(message = "管理员登录名不能为空")
    private String loginUserName;

    @NotEmpty(message = "管理员nickName不能为空")
    private String nickName;
}
