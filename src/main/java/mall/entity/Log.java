package mall.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Log {
    private Long id;

    private Long adminUserId;

    private String loginUserName;

    private Long organizationId;

    private Byte role;

    private Date updateTime;

    private String api;

    private String detail;

    private String result;
}