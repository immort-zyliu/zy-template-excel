package pers.lzy.template.excel.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import pers.lzy.template.excel.utils.ExcelUtil;
import pers.lzy.template.excel.utils.ReUtils;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/24  11:06
 */
public class TagParser {

    /**
     * 判断内容中是否含有指定的标签
     *
     * @return true：是，false：不是
     */
    public static String parseParamByTag(String content, String tagName) {
        // 构造正则
        String regStr = String.format("<%s>(.+)</%s>", tagName, tagName);
        // 只匹配第一个，因为我们只认第一个
        return ReUtils.get(regStr, content, 1);
    }

    public static String parseCellTagContent(Cell cell, String tagName) {
        if (cell == null) {
            return null;
        }

        // 获取单元格中的所有内容
        String cellValue = ExcelUtil.getCellValue(cell);
        // 获取指定标签的内容
        String parsedCellValue = TagParser.parseParamByTag(cellValue, tagName);
        // 如果是空，说明不是 simple-eval标签，则不处理
        if (StringUtils.isEmpty(parsedCellValue)) {
            return null;
        }
        return parsedCellValue;
    }

}
