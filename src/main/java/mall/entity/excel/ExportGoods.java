package mall.entity.excel;

import lombok.Data;
import mall.util.ExcelExport;

@Data
public class ExportGoods {

    @ExcelExport(value = "商品名称",example = "导入商品1")
    private String goodsName;

    @ExcelExport(value = "商品简介" ,example = "导入商品1简介")
    private String goodsIntro;

    @ExcelExport(value = "分类名称" ,example = "电脑")
    private String categoryName;

    @ExcelExport(value = "原价" ,example = "0")
    private Integer originalPrice;

    @ExcelExport(value = "售价" ,example = "0")
    private Integer sellingPrice;

    @ExcelExport(value = "积分售价-现金" ,example = "0")
    private Integer sellingPointC;

    @ExcelExport(value = "积分售价-积分" ,example = "100")
    private Integer sellingPointP;

    @ExcelExport(value = "库存" ,example = "100")
    private Integer stockNum;

    @ExcelExport(value = "商品标签" ,example = "电脑")
    private String tag;

    @ExcelExport(value = "商品详情" ,example = "电脑")
    private String goodsDetailContent;
}
