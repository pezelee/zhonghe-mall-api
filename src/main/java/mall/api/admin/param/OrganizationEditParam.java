 
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class OrganizationEditParam {

    @ApiModelProperty("待修改分行组织id")
    @NotNull(message = "分行组织id不能为空")
    @Min(1)
    private Long organizationId;

    @ApiModelProperty("分行组织名称")
    @NotEmpty(message = "分行组织名称不能为空")
    private String orgName;

    @ApiModelProperty("分行组织描述")
    @NotEmpty(message = "分行组织描述不能为空")
    private String description;

}
