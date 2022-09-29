 
package mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ZhongHeMallPrize {
    private Long prizeId;

    private Long organizationId;

    private String orgName;

    private String prizeName;

    private String prizeIntro;

    private Long prizeCategoryId;

    private Byte prizeType;//发放方式  0：实物快递  1：实物现场  2： 积分 3：会员卡

    private String prizeCoverImg;

    private String prizeCarousel;

    private Integer prizeValue;//奖品值（积分）

    private Integer originalStock;//奖品初始数量

    private Integer stockNum;//奖品库存数量

    private Integer totalNum;//奖品发放总数

    private String tag;

    private Byte prizeSellStatus;

    private Integer prizeWeight;//抽奖权重

    private Byte prizeLevel;//奖品等级，0：参与奖，1：一等奖，2：二等奖。。。

    private Integer prizeInterval;//中奖间隔（小时）

    private Long activityId;//所属活动，默认0，无活动

    private Long createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Long updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String prizeDetailContent;
}