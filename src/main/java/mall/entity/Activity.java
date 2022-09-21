
package mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Activity {
    private Long activityId;

    private String activityName;

    private String activityIntro;

    private Long organizationId;

    private String orgName;

    private String activityCoverImg;

    private String activityCarousel;

    private String activityDetailContent;

    private Date starttime;

    private Date endtime;

    private Date expiretime;//奖品到期时间

    private String prizes;//活动奖品池:[1,2,5]

    private Long rule;

    private Long template;

    private Byte status;

    private Long createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Long updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}