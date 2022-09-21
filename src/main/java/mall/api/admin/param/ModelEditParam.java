/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ModelEditParam {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("待修改活动id")
    @NotNull(message = "活动id不能为空")
    private Long activityId;

    @ApiModelProperty("模板id")
    @NotNull(message = "模板id不能为空")
    private Long modelId;

    @ApiModelProperty("模板名称")
    @NotEmpty(message = "模板名称不能为空")
    @Length(max = 128,message = "模板名称内容过长")
    private String modelName;
}