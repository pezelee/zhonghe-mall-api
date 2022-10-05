package mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Notice {

    private Long id;

    private Long userId;

    private String nickName;

    private String loginName;

    private Long organizationId;

    private String phone;

    private String title;

    private String sender;

    private String notice;

    private Byte noticeType;

    private Byte readFlag;

    private Byte isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date readTime;
}