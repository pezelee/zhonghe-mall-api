package mall.api.mall.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ZhongHeMallNoticeTitleVO implements Serializable {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("发送者")
    private String sender;

    @ApiModelProperty("通知类型")
    private Byte noticeType;

    @ApiModelProperty("是否已读")
    private Byte readFlag;
}
