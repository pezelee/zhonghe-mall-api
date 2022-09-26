package mall.entity.excel;

import lombok.Data;
import mall.util.ExcelImport;

@Data
public class ImportGoods {

    private int rowNum;

    @ExcelImport(value = "商品名称",required = true)
    private String goodsName;

    @ExcelImport(value = "商品简介" ,required = true)
    private String goodsIntro;

    @ExcelImport(value = "分类名称" ,required = true)
    private String categoryName;

//    @ExcelImport(value = "商品主图" ,required = true)
//    private String goodsCoverImg;

    @ExcelImport(value = "原价" ,required = true,min = "0")
    private Integer originalPrice;

    @ExcelImport(value = "售价" ,required = true,min = "0")
    private Integer sellingPrice;

    @ExcelImport(value = "积分售价-现金" ,required = true,min = "0")
    private Integer sellingPointC;

    @ExcelImport(value = "积分售价-积分" ,required = true,min = "0")
    private Integer sellingPointP;

    @ExcelImport(value = "库存" ,required = true,min = "0")
    private Integer stockNum;

    @ExcelImport(value = "商品标签" ,required = true)
    private String tag;

    @ExcelImport(value = "商品详情" ,required = true)
    private String goodsDetailContent;

    private String rowTips;//错误提示
    private String rowData;//原始数据
}
