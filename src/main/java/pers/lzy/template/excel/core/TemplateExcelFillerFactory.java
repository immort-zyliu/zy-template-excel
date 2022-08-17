package pers.lzy.template.excel.core;

import pers.lzy.template.excel.filler.DefaultTemplateExcelFiller;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/23  18:25
 */
public class TemplateExcelFillerFactory {


    /**
     * 获取默认 excel 导入工具的 构造器
     */
    public static DefaultTemplateExcelFiller.Builder defaultTemplateExcelFillerBuilder() {
        return new DefaultTemplateExcelFiller.Builder();
    }


}
