package mall.util;

import mall.entity.SellPointDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lpz
 */
public class GoodsUtils {

    //积分字符串转换为DTO列表
    public static List<SellPointDTO> toDTOList(String points){
        List<SellPointDTO> pointDTOS = new ArrayList<>();
        String[] pointList = points.split(",");
        for (String s : pointList) {
            SellPointDTO tempPointDTO = new SellPointDTO();
            String temp =s.substring(1, s.length() - 1);
            String[] pts = temp.split("-");
            Integer point = Integer.valueOf(pts[0]);
            Integer price = Integer.valueOf(pts[1]);
            tempPointDTO.setPoint(point);
            tempPointDTO.setPrice(price);
            pointDTOS.add(tempPointDTO);
        }
        return pointDTOS;
    }

    //售价最少的积分
    public static int minPoint(List<SellPointDTO> pointDTOS){
        if (pointDTOS == null) {
            return 0;
        }
        int minPoint = pointDTOS.get(0).getPoint();
        for(SellPointDTO pointDTO : pointDTOS){
            int tempPoint = pointDTO.getPoint();
            if (tempPoint < minPoint) {
                minPoint = tempPoint;
            }
        }
        return minPoint;
    }

//    //新获得字符串添加到最后
//    public static String addString(String s, String sList){
//        if (!sList.equals("")) {
//            sList += ",";
//        }
//        sList += s;
//        return sList;
//    }
//    public static void addString(String s, StringBuilder sList){
//        if (!sList.toString().equals("")) {
//            sList.append(",");
//        }
//        sList.append(s);
//    }
//
//    //ID组转换为字符串
//    public static String batchIdtoString( BatchIdParam batchIdParam ){
//        Long[] ids = batchIdParam.getIds();
//        Long[] idCheck = new Long[ids.length];
//        StringBuilder sList = new StringBuilder();
////        String sList="";
//        for (int i = 0; i <ids.length ; i++) {
//            Long id = ids[i];
//            if (!ArrayUtils.contains(idCheck,id)) {//不重复
//                idCheck[i]=id;
//                addString(id.toString(), sList);
////                sList.append(id.toString());
//            }
//        }
//        return sList.toString();
//    }
//
//    //DTO转换为积分字符串
//    public static String DTOtoString(PointDTO pointDTO){
//        DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
//        String point = "("+pointDTO.getPoint().toString()+":"+format.format(pointDTO.getExpiretime())+")";
//        return point;
//    }



//    //积分DTO列表按过期时间排序
//    public static List<PointDTO> sortPoints(List<PointDTO> oldPointDTOs){
//        oldPointDTOs = oldPointDTOs.stream().sorted(Comparator.comparing(PointDTO::getExpiretime)).collect(Collectors.toList());
//        return oldPointDTOs;
//    }
//
//    //收藏字符串转换为id列表
//    public static List<ZhongHeMallGoods> toIdList(String collect){
//
//        List<ZhongHeMallGoods> pointDTOS = new ArrayList<>();
//        String[] collectList = collect.split(",");
//        for (String s : collectList) {
//            ZhongHeMallGoods tempPointDTO = new ZhongHeMallGoods();
//            String temp =s.substring(1, s.length() - 1);
//            String[] pts = temp.split(":");
//            Integer point = Integer.valueOf(pts[0]);
//            DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
//            Date expiretime;
//            try {
//                expiretime = format.parse(pts[1]);
//            } catch (ParseException e) {
//                e.printStackTrace();
//                expiretime=null;
//            }
////            tempPointDTO.setExpiretime(expiretime);
////            tempPointDTO.setPoint(point);
//            pointDTOS.add(tempPointDTO);
//        }
//        return pointDTOS;
//    }


}
