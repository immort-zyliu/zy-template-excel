package pers.lzy.template.excel.processor;

import com.google.auto.service.AutoService;
import org.apache.poi.ss.usermodel.Sheet;
import pers.lzy.template.excel.constant.CommonDataNameConstant;
import pers.lzy.template.excel.core.ExpressionCalculator;
import pers.lzy.template.excel.core.OperateExcelPostProcessor;
import pers.lzy.template.excel.pojo.MergeArrInfo;
import pers.lzy.template.excel.pojo.MergeRegion;
import pers.lzy.template.excel.utils.ExcelUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/28  13:41
 * 最后，需要合并某些指定的单元格，所以这里就做了合并单元格的操作
 */
@AutoService(OperateExcelPostProcessor.class)
public class OperateExcelMergeRegionPostProcessor implements OperateExcelPostProcessor {

    /**
     * 对Excel进行个性化处理的方法
     *
     * @param sheet                要操作的sheet
     * @param params               需要的参数列表 (当然，此数据可以在整个handler中流转)
     * @param expressionCalculator 表达式计算器
     */
    @Override
    public void operatePostProcess(Sheet sheet, Map<String, Object> params, ExpressionCalculator expressionCalculator) {

        @SuppressWarnings("unchecked")
        List<MergeArrInfo> mergeArrInfoList = (List<MergeArrInfo>) params.get(CommonDataNameConstant.MERGE_ARR_INFO);

        for (MergeArrInfo mergeArrInfo : mergeArrInfoList) {
            // 进行单元格的合并操作
            mergeCell(mergeArrInfo, sheet);
        }

    }

    /**
     * 进行单元格的合并草错
     */
    private void mergeCell(MergeArrInfo mergeArrInfo, Sheet sheet) {

        List<MergeRegion> mergeRegionList = new ArrayList<>();
        Integer startRow = mergeArrInfo.getStartRow();
        Integer endRow = mergeArrInfo.getEndRow();
        Integer columnNumber = mergeArrInfo.getColumnNumber();

        // 首先默认第一个为 上一个单元格的值
        String preValue = ExcelUtil.getCellValue(sheet, startRow, columnNumber);

        // region默认是第一个单元格所在位置
        MergeRegion tempMergeRegion = new MergeRegion(
                startRow, startRow, columnNumber, columnNumber, preValue
        );

        for (int rowNumber = startRow; rowNumber < endRow; rowNumber++) {
            String currentValue = ExcelUtil.getCellValue(sheet, rowNumber, columnNumber);

            // 如果当前的 value 跟上一个不同,说明是新的开始
            if ("".equals(currentValue) && "".equals(preValue) || !currentValue.equals(preValue)) {
                // currentValue是空串且preValue是空串，那么我们就认为他不同（虽然他是相同的） true，则进来了
                // 如果到了|| 后面，说明前两个有一个不是空串，那么我们就有可比性了

                // 则结束封装并放入list，同时新来一个继续封装

                // 如果发现是多个单元格，才放入最终的集合中
                if (tempMergeRegion.getFirstRow() != tempMergeRegion.getLastRow()) {
                    mergeRegionList.add(tempMergeRegion);
                }

                // 创建一个新的，继续寻找
                tempMergeRegion = new MergeRegion(
                        rowNumber, rowNumber, columnNumber, columnNumber, currentValue
                );

                // 设置preValue 为 当前新的Value
                preValue = currentValue;
            } else {
                // 说明跟上个一样，则改变当前 region的范围
                tempMergeRegion.setLastRow(rowNumber);
            }

        }


        // 如果发现是多个单元格，才放入最终的集合中（最后一次合并的判断处理）
        if (tempMergeRegion.getFirstRow() != tempMergeRegion.getLastRow()) {
            mergeRegionList.add(tempMergeRegion);
        }

        // 进行真正的合并
        doMergeCellByMergeRegionInfo(sheet, mergeRegionList);

    }

    private void doMergeCellByMergeRegionInfo(Sheet sheet, List<MergeRegion> mergeRegionList) {

        // 遍历进行合并
        for (MergeRegion mergeRegion : mergeRegionList) {

            // 调用方法进行合并
            ExcelUtil.mergedRegionNoRtn(
                    sheet,
                    mergeRegion.getFirstRow(),
                    mergeRegion.getLastRow(),
                    mergeRegion.getFirstCol(),
                    mergeRegion.getLastCol()
            );
        }
    }
}
