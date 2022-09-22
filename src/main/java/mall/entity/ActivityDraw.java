package mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

//活动用户抽奖次数
@Data
public class ActivityDraw {
    private Long id;//

    private Long userId;

    private Long organizationId;//所属组织

    private String nickName;

    private String loginName;

    private String sponsor;

    private Long activityId;//活动ID

    private Integer draws;//抽奖次数

    private Long createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Long updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}