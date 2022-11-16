package mall.util;

import mall.api.admin.param.BatchIdParam;
import mall.entity.PointDTO;
import mall.entity.ZhongHeMallGoods;
import org.apache.commons.lang3.ArrayUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lpz
 */
public class UserUtils {

    //新获得字符串添加到最后
    public static String addString(String s, String sList){
        if (sList == null) {//原字符串为空，直接返回新增字符串
            return s;
        }
        if (!sList.equals("")) {
            sList += ",";
        }
        sList += s;
        return sList;
    }
    public static void addString(String s, StringBuilder sList){
        if (!sList.toString().equals("")) {
            sList.append(",");
        }
        sList.append(s);
    }

    //ID组转换为字符串
    public static String batchIdtoString( BatchIdParam batchIdParam ){
        Long[] ids = batchIdParam.getIds();
        Long[] idCheck = new Long[ids.length];
        StringBuilder sList = new StringBuilder();
//        String sList="";
        for (int i = 0; i <ids.length ; i++) {
            Long id = ids[i];
            if (!ArrayUtils.contains(idCheck,id)) {//不重复
                idCheck[i]=id;
                addString(id.toString(), sList);
//                sList.append(id.toString());
            }
        }
        return sList.toString();
    }

    //DTO转换为积分字符串
    public static String DTOtoString(PointDTO pointDTO){
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String point = "("+pointDTO.getPoint().toString()+":"+format.format(pointDTO.getExpiretime())+")";
        return point;
    }

    //积分字符串转换为DTO列表
    public static List<PointDTO> toDTOList(String points){

        List<PointDTO> pointDTOS = new ArrayList<>();
        if(points==null || points.equals("")){
            return null;
        }
        String[] pointList = points.split(",");
        for (String s : pointList) {
            PointDTO tempPointDTO = new PointDTO();
            String temp =s.substring(1, s.length() - 1);
            String[] pts = temp.split(":");
            Integer point = Integer.valueOf(pts[0]);
            DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            Date expiretime;
            try {
                expiretime = format.parse(pts[1]);
            } catch (ParseException e) {
                e.printStackTrace();
                expiretime=null;
            }
            tempPointDTO.setExpiretime(expiretime);
            tempPointDTO.setPoint(point);
            pointDTOS.add(tempPointDTO);
        }
        return pointDTOS;
    }

    // 积分DTO列表按过期时间排序,合并同一个过期日期的积分
    public static List<PointDTO> sortPoints(List<PointDTO> oldPointDTOs){
        oldPointDTOs = oldPointDTOs.stream().sorted(Comparator.comparing(PointDTO::getExpiretime)).collect(Collectors.toList());
        for (int i = 1; i < oldPointDTOs.size(); i++) {
            Date nowExpiretime = oldPointDTOs.get(i).getExpiretime();
            Date lastExpiretime = oldPointDTOs.get(i-1).getExpiretime();
            if (nowExpiretime.equals(lastExpiretime)) {
                //过期日期重复，合并到本条，上一条清空
                oldPointDTOs.get(i).setPoint(oldPointDTOs.get(i).getPoint() + oldPointDTOs.get(i-1).getPoint());
                oldPointDTOs.get(i-1).setPoint(0);
            }
        }
        List<PointDTO> newPointDTOs = new ArrayList<>();
        for (PointDTO pointDOT : oldPointDTOs){
            if (pointDOT.getPoint() > 0) {
                newPointDTOs.add(pointDOT);
            }
        }
        return newPointDTOs;
    }

    //收藏字符串转换为id列表
    public static List<ZhongHeMallGoods> toIdList(String collect){

        List<ZhongHeMallGoods> pointDTOS = new ArrayList<>();
        String[] collectList = collect.split(",");
        for (String s : collectList) {
            ZhongHeMallGoods tempPointDTO = new ZhongHeMallGoods();
            String temp =s.substring(1, s.length() - 1);
            String[] pts = temp.split(":");
            Integer point = Integer.valueOf(pts[0]);
            DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            Date expiretime;
            try {
                expiretime = format.parse(pts[1]);
            } catch (ParseException e) {
                e.printStackTrace();
                expiretime=null;
            }
//            tempPointDTO.setExpiretime(expiretime);
//            tempPointDTO.setPoint(point);
            pointDTOS.add(tempPointDTO);
        }
        return pointDTOS;
    }


}
