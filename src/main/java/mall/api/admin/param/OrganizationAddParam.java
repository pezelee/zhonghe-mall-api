 
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class OrganizationAddParam {

    @ApiModelProperty("分行组织名称")
    @NotEmpty(message = "分行组织名称不能为空")
    private String orgName;

    @ApiModelProperty("分行组织描述")
    @NotEmpty(message = "分行组织描述不能为空")
    private String description;

}
