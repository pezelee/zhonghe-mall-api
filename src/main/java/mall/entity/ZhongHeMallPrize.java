/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
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

    private Long createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Long updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String prizeDetailContent;
}