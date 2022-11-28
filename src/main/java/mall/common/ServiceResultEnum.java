package mall.common;

public enum ServiceResultEnum {
    ERROR("error"),

    SUCCESS("success"),

    DATA_NOT_EXIST("未查询到记录！"),

    PARAM_ERROR("参数错误！"),

    SAME_CATEGORY_EXIST("已存在同级同名的分类！"),

    SAME_LOGIN_NAME_EXIST("用户名已存在！"),

    SAME_ORG_NAME_EXIST("组织名已存在！"),

    LOGIN_NAME_NULL("请输入登录名！"),

    LOGIN_NAME_IS_NOT_PHONE("请输入正确的手机号！"),

    LOGIN_PASSWORD_NULL("请输入密码！"),

    PASSWORD_ERROR("密码错误！"),

    LOGIN_VERIFY_CODE_NULL("请输入验证码！"),

    LOGIN_VERIFY_CODE_ERROR("验证码错误！"),

    SAME_INDEX_CONFIG_EXIST("已存在相同的首页配置项！"),

    GOODS_CATEGORY_ERROR("分类数据异常！"),

    SAME_GOODS_EXIST("已存在相同的商品信息！"),

    SAME_PRIZE_EXIST("已存在相同的奖品信息！"),

    SAME_ACTIVITY_EXIST("已存在相同的活动信息！"),

    GOODS_NOT_EXIST("商品不存在！"),

    PRIZE_NOT_EXIST("奖品不存在！"),

    NOTICE_NOT_EXIST("通知不存在！"),

    ACTIVITY_NOT_EXIST("活动不存在！"),

    DRAW_NOT_EXIST("抽奖记录不存在！"),

    RULE_NOT_EXIST("规则不存在！"),

    SAVE_RULE_ERROR("规则保存失败！"),

    RULE_PERIOD_ERROR("规则时段错误！"),

    GOODS_PUT_DOWN("商品已下架！"),

    PRIZE_PUT_DOWN("奖品已下架！"),

    ACTIVITY_PUT_DOWN("活动已下架！"),

    PRIZE_USED("奖品已在其他活动！"),

    PRIZE_OTHER_ORG("奖品不属于活动分行！"),

    PRIZE_PUT_UP("活动中奖品不可修改！"),

    PRIZE_OUT_TIME("奖品已过期！"),

    PRIZE_GETED("奖品已领取！"),

    SAME_MAIL_NO("已存在相同的邮寄单号！"),

    SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR("超出单个商品的最大购买数量！"),

    SHOPPING_CART_ITEM_NUMBER_ERROR("商品数量不能小于 1 ！"),

    SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR("超出购物车最大容量！"),

    SHOPPING_CART_ITEM_EXIST_ERROR("已存在！无需重复添加！"),

    LOGIN_ERROR("登录失败！"),

    LOGIN_LOCKED("账号已锁定！"),

    NOT_LOGIN_ERROR("未登录！"),

    ADMIN_NOT_LOGIN_ERROR("管理员未登录！"),

    TOKEN_EXPIRE_ERROR("无效认证！请重新登录！"),

    ADMIN_TOKEN_EXPIRE_ERROR("管理员登录过期！请重新登录！"),

    USER_NOT_EXIST("用户不存在！"),

    USER_NULL_ERROR("无效用户！请重新登录！"),

    OTHER_USER("用户不匹配！"),

    LOGIN_USER_LOCKED_ERROR("用户已被禁止登录！"),

    SPONSOR_NOT_EXIST_ERROR("客户经理不存在！"),

    PERMISSION_DENIED("管理员权限不足！"),

    OTHER_ORG("权限不足，非所选分行成员！"),

    ORDER_NOT_EXIST_ERROR("订单不存在！"),

    ORDER_ITEM_NOT_EXIST_ERROR("订单项不存在！"),

    NULL_ADDRESS_ERROR("地址不能为空！"),

    SAVE_ADDRESS_ERROR("地址保存失败！"),

    LOTTERY_STATUS_ERROR("奖品状态异常！"),

    PRIZE_TYPE_ERROR("奖品类型异常！"),

    MAIL_NO_ERROR("运单号输入异常！"),

    POINT_NOT_ENOUGH("积分不足！"),

    ORDER_PRICE_ERROR("订单价格异常！"),

    ORDER_ITEM_NULL_ERROR("订单项异常！"),

    ORDER_GENERATE_ERROR("生成订单异常！"),

    SHOPPING_ITEM_ERROR("购物车数据异常！"),

    SHOPPING_ITEM_COUNT_ERROR("库存不足！"),

    CANCEL_GOODS_COUNT_ERROR("取消订单恢复库存错误！"),

    ORDER_STATUS_ERROR("订单状态异常！"),

    OPERATE_ERROR("操作失败！"),

    REQUEST_FORBIDEN_ERROR("禁止该操作！"),

    NO_PERMISSION_ERROR("无权限！"),

    OUT_DRAW_TIME("非抽奖时段！"),

    PRIZES_NOT_EXIST("空奖池！"),

    ERROR_DRAW("抽奖错误！"),

    ERROR_DRAW_TIMES("没有抽奖次数！"),

    ERROR_DRAW_MAX("达到最大抽奖错误次数！"),

    ERROR_DRAW_STOCK("抽奖库存错误！"),

    DB_ERROR("数据错误");

    private String result;

    ServiceResultEnum(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
