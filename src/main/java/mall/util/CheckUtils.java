package mall.util;

import mall.common.ServiceResultEnum;
import mall.entity.Activity;
import mall.entity.AdminUserToken;
import mall.entity.Rule;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lpz
 */
public class CheckUtils {

    //判定权限是否符合--总管理员或相应的分行管理员
    public static String isAdmin(AdminUserToken adminUser,Long id){
        byte role = adminUser.getRole();
        if (role != 0) {//非总管理员
            if (role != 1) {//非分行管理员
                return ServiceResultEnum.PERMISSION_DENIED.getResult();
            }
            if (!isSameId(adminUser.getOrganizationId(), id)) {//组织不符合
                return ServiceResultEnum.OTHER_ORG.getResult();
            }
        }
        return ServiceResultEnum.SUCCESS.getResult();
    }
    //判定权限是否符合--总管理员
    public static String isChiefAdmin(AdminUserToken adminUser){
        byte role = adminUser.getRole();
        if (role != 0) {//非总管理员
            return ServiceResultEnum.PERMISSION_DENIED.getResult();
        }
        return ServiceResultEnum.SUCCESS.getResult();
    }
    //判定ID是否符合
    public static boolean isSameId(Long Id1, Long Id2){
        return Id2.equals(Id1);
    }

    //判定是否抽奖时段
    public static boolean isTime(Activity activity){
        Date starttime = activity.getStarttime();
        Date endtime = activity.getEndtime();
        Date now = new Date();
        //非抽奖时段
        return now.getTime() >= starttime.getTime() && now.getTime() <= endtime.getTime();
    }

    //判定抽奖规则是否合规
    public static String isRule(Rule rule,Activity activity){
        String period1 = isRulePeriod(rule.getPeriod1st(),activity.getStarttime(),activity.getEndtime());
        if (!period1.equals("success")) {
            return period1;
        }
        String period2 = isRulePeriod(rule.getPeriod2nd(),activity.getStarttime(),activity.getEndtime());
        if (!period2.equals("success")) {
            return period2;
        }
        if (!rule.getActivityId().equals(activity.getActivityId())) {
            return "活动不匹配";
        }
        if (rule.getIntervalLv1() != null && rule.getIntervalLv1()<0) {
            return "中奖间隔不能小于0";
        }
        if (rule.getIntervalLv2() != null && rule.getIntervalLv2()<0) {
            return "中奖间隔不能小于0";
        }
        if (rule.getIntervalLv3() != null && rule.getIntervalLv3()<0) {
            return "中奖间隔不能小于0";
        }
        if (rule.getIntervalDraw() != null && rule.getIntervalDraw()<0) {
            return "抽奖间隔不能小于0";
        }
        if (rule.getPersonIntervalLv1() != null && rule.getPersonIntervalLv1()<0) {
            return "中奖间隔不能小于0";
        }
        if (rule.getPersonIntervalLv2() != null && rule.getPersonIntervalLv2()<0) {
            return "中奖间隔不能小于0";
        }
        if (rule.getPersonIntervalLv3() != null && rule.getPersonIntervalLv3()<0) {
            return "中奖间隔不能小于0";
        }
        if (rule.getGuaranteeLv1() != null && rule.getGuaranteeLv1()<0) {
            return "保底次数不能小于0";
        }
        if (rule.getGuaranteeLv2() != null && rule.getGuaranteeLv2()<0) {
            return "保底次数不能小于0";
        }
        if (rule.getGuaranteeLv3() != null && rule.getGuaranteeLv3()<0) {
            return "保底次数隔不能小于0";
        }
        if (rule.getExpireDays() != null && rule.getExpireDays()<0) {
            return "过期天数不能小于0";
        }
        return "success";
    }

    //判定抽奖规则时段是否合规
    public static String isRulePeriod(String period,Date starttime,Date endtime){
        if ("".equals(period) || period == null) {
            return "success";
        }
        //【结束日期，一等奖数量，二等奖数量，三等奖数量】
        String[] period1List = period.split(",");
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Date periodtime;
        try {
            periodtime = format.parse(period1List[0]);
        } catch (ParseException e) {
            return "阶段分界日期格式错误";
        }
        if (periodtime.after(endtime) || periodtime.before(starttime)) {
            return "阶段分界日期不在活动时段内";
        }
        if (period1List[1] == null) {
            return "阶段数量不存在";
        }else if(!period1List[1].matches("^[0-9]+$")){
            return "阶段一等奖数量格式错误";
        }
        if (period1List[2] != null && !period1List[2].matches("^[0-9]+$")) {
            return "阶段二等奖数量格式错误";
        }
        if (period1List[3] != null && !period1List[3].matches("^[0-9]+$")) {
            return "阶段三等奖数量格式错误";
        }
        return "success";
    }

    public static URI getHost(URI uri) {
        URI effectiveURI = null;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (Throwable var4) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    public static String cleanString(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        value = value.toLowerCase();
        value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        value = value.replaceAll("'", "& #39;");
        value = value.replaceAll("onload", "0nl0ad");
        value = value.replaceAll("xml", "xm1");
        value = value.replaceAll("window", "wind0w");
        value = value.replaceAll("click", "cl1ck");
        value = value.replaceAll("var", "v0r");
        value = value.replaceAll("let", "1et");
        value = value.replaceAll("function", "functi0n");
        value = value.replaceAll("return", "retu1n");
        value = value.replaceAll("$", "");
        value = value.replaceAll("document", "d0cument");
        value = value.replaceAll("const", "c0nst");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "scr1pt");
        value = value.replaceAll("insert", "1nsert");
        value = value.replaceAll("drop", "dr0p");
        value = value.replaceAll("create", "cre0ate");
        value = value.replaceAll("update", "upd0ate");
        value = value.replaceAll("alter", "a1ter");
        value = value.replaceAll("from", "fr0m");
        value = value.replaceAll("where", "wh1re");
        value = value.replaceAll("database", "data1base");
        value = value.replaceAll("table", "tab1e");
        value = value.replaceAll("tb", "tb0");
        return value;
    }
}
