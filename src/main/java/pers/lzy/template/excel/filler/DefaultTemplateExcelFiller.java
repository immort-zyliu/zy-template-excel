package pers.lzy.template.excel.filler;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JxltEngine;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.lzy.template.excel.anno.CellOperateHandler;
import pers.lzy.template.excel.calculator.Jxel3ExpressionCalculator;
import pers.lzy.template.excel.common.TagParser;
import pers.lzy.template.excel.constant.CommonDataNameConstant;
import pers.lzy.template.excel.core.*;
import pers.lzy.template.excel.exception.OperateExcelCellHandlerInitException;
import pers.lzy.template.excel.pojo.ArrInfo;
import pers.lzy.template.excel.pojo.MergeArrInfo;
import pers.lzy.template.excel.provider.FillDataProvider;
import pers.lzy.template.excel.provider.SheetProvider;
import pers.lzy.template.excel.utils.SpiLoader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/23  18:21
 */
public class DefaultTemplateExcelFiller implements TemplateExcelFiller {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTemplateExcelFiller.class);

    /**
     * 表达式计算器
     */
    private final ExpressionCalculator expressionCalculator;

    /**
     * cell 处理链
     */
    private final List<OperateExcelCellHandler> operateExcelCellHandlerList;

    /**
     * 后置处理链
     */
    private final List<OperateExcelPostProcessor> operateExcelPostProcessorList;

    /**
     * key: cell-handler's tagName
     * value: OperateExcelCellHandler
     */
    private final Map<String, OperateExcelCellHandler> operateExcelCellHandlerTagMap;

    /**
     * cell handler 解析之后的 holder 对象列表
     */
    private final List<OperateExcelCellHandlerHolder> operateExcelCellHandlerHolders;


    public DefaultTemplateExcelFiller(Builder builder) {
        this.operateExcelCellHandlerList = builder.operateExcelCellHandlerList;
        this.operateExcelPostProcessorList = builder.operateExcelPostProcessorList;
        int expressionCacheSize = builder.expressionCacheSize;

        // 设置表达式计算器
        if (builder.expressionCalculator != null) {
            this.expressionCalculator = builder.expressionCalculator;
        } else {
            this.expressionCalculator = new Jxel3ExpressionCalculator(builder.jxltEngine, expressionCacheSize);
        }

        this.operateExcelCellHandlerTagMap = builder.operateExcelCellHandlerTagMap;
        this.operateExcelCellHandlerHolders = builder.operateExcelCellHandlerHolders;
    }

    @Override
    public void fillData(SheetProvider sheetProvider, FillDataProvider dataProvider) {
        Sheet sheet = sheetProvider.getSheet();
        Map<String, Object> paramData = dataProvider.getParamData();

        if (sheet == null || paramData == null) {
            throw new IllegalStateException("sheet and paramData cannot be null");
        }

        verifyAndInitParamData(paramData);

        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Optional.ofNullable(sheet.getRow(rowNum)).ifPresent(curRow -> {
                for (int colNum = 0; colNum < curRow.getLastCellNum(); colNum++) {
                    Optional.ofNullable(curRow.getCell(colNum)).ifPresent(curCell ->
                            this.doFillCell(sheet, curCell, paramData)
                    );
                }
            });
        }
        // 执行 所有的 Excel 的后置处理
        this.operateExcelPostProcessorList.forEach(processor -> processor.operatePostProcess(sheet, paramData, this.expressionCalculator));
    }

    private void doFillCell(Sheet sheet, Cell curCell, Map<String, Object> paramData) {
        // 解析tag
        String tagName = TagParser.findFirstTag(curCell);

        if (tagName == null) {
            return;
        }

        // 通过tag找到handler
        OperateExcelCellHandler operateExcelCellHandler = operateExcelCellHandlerTagMap.get(tagName);
        if (operateExcelCellHandler == null) {
            logger.warn("No tag({}) handler found, skipped", tagName);
            return;
        }

        // 调用handler对cell进行处理
        operateExcelCellHandler.operate(sheet, curCell, paramData, this.expressionCalculator);
    }

    /**
     * 校验并初始化参数列表（将一些公共的流程中的对象放入参数列表）
     *
     * @param paramData 参数
     */
    private void verifyAndInitParamData(Map<String, Object> paramData) {

        // 我们不允许用户使用这个key
        if (paramData.get(CommonDataNameConstant.ARR_HISTORY) != null) {
            logger.error("ARR_HISTORY cannot be used because some other information will be recorded");
            throw new IllegalArgumentException("ARR_HISTORY cannot be used because some other information will be recorded");
        }
        if (paramData.get(CommonDataNameConstant.MERGE_ARR_INFO) != null) {
            logger.error("MERGE_ARR_INFO cannot be used because some other information will be recorded");
            throw new IllegalArgumentException("MERGE_ARR_INFO cannot be used because some other information will be recorded");
        }
        // 初始化记录数组插入历史的全链路变量
        paramData.put(CommonDataNameConstant.ARR_HISTORY, new HashMap<String, ArrInfo>());
        paramData.put(CommonDataNameConstant.MERGE_ARR_INFO, new ArrayList<MergeArrInfo>());
    }


    /**
     * 构建器
     */
    public static class Builder {

        private final JxltEngine jxltEngine;

        private final Map<String, Object> functions;

        private int expressionCacheSize = 1000;

        private List<OperateExcelCellHandler> operateExcelCellHandlerList;

        private List<OperateExcelPostProcessor> operateExcelPostProcessorList;

        private ExpressionCalculator expressionCalculator;

        /**
         * key: cell-handler's tagName
         * value: OperateExcelCellHandler
         */
        private Map<String, OperateExcelCellHandler> operateExcelCellHandlerTagMap;

        private List<OperateExcelCellHandlerHolder> operateExcelCellHandlerHolders;

        public Builder() {
            functions = new HashMap<>();
            functions.putAll(loadFunctions());

            JexlEngine jexlEngine = new JexlBuilder()
                    .namespaces(functions)
                    .create();
            jxltEngine = jexlEngine.createJxltEngine();
            this.operateExcelCellHandlerList = this.loadInstanceByInterface(OperateExcelCellHandler.class);
            this.operateExcelPostProcessorList = this.loadInstanceByInterface(OperateExcelPostProcessor.class);
            // 初始化辅助handler或者是map的信息
            this.initAuxiliaryInfo();

        }

        private void initAuxiliaryInfo() {
            this.initOperateExcelCellHandlerHolders();
            this.initOperateExcelCellHandlerTagMap();
        }

        private void initOperateExcelCellHandlerHolders() {
            logger.info("init operateExcelCellHandlerTagMap");
            this.operateExcelCellHandlerHolders = this.operateExcelCellHandlerList.stream()
                    .map(cellHandler -> {
                        CellOperateHandler cellOperateHandler = cellHandler.getClass().getAnnotation(CellOperateHandler.class);
                        if (cellOperateHandler == null) {
                            throw new OperateExcelCellHandlerInitException("The OperateExcelCellHandler must identify the CellOperateHandler annotation");
                        }
                        return new OperateExcelCellHandlerHolder(cellHandler, cellOperateHandler.tagName());

                    })
                    .collect(Collectors.toList());
        }

        private void initOperateExcelCellHandlerTagMap() {
            logger.info("init operateExcelCellHandlerHolders");
            this.operateExcelCellHandlerTagMap = this.operateExcelCellHandlerHolders.stream()
                    .collect(Collectors.toConcurrentMap(
                            OperateExcelCellHandlerHolder::getCellHandlerTagName,
                            OperateExcelCellHandlerHolder::getOperateExcelCellHandler,
                            (oldV, newV) -> {
                                throw new OperateExcelCellHandlerInitException("There are multiple OperateExcelCellHandler with the same tagName. cell:" + oldV + ";" + newV);
                            }
                    ));
        }


        private Map<String, Object> loadFunctions() {
            List<FunctionProvider> functionProviders = this.loadInstanceByInterface(FunctionProvider.class);
            return functionProviders.stream()
                    .map(FunctionProvider::provideFunctions)
                    .reduce(new HashMap<>(), (root, ele) -> {
                        root.putAll(ele);
                        return root;
                    });
        }


        private <T> List<T> loadInstanceByInterface(Class<T> clazz) {
            return SpiLoader.loadInstanceListSorted(clazz);
        }


        /**
         * 添加自定义函数
         *
         * @param key   自定义函数名称
         * @param clazz 自定义函数的实现类
         * @return builder
         */
        public Builder functions(String key, Class<?> clazz) {
            this.functions.put(key, clazz);
            return this;
        }

        /**
         * 添加 操作excel 的 Operate
         * 需要注意的是：您只能往最后添加，换而言之，您添加的handler总是会在最后执行。
         */
        public Builder operateExcelHandlerList(OperateExcelCellHandler operateExcelCellHandler) {
            this.operateExcelCellHandlerList.add(operateExcelCellHandler);
            return this;
        }

        /**
         * 需要注意的是，您只能往最后添加，换而言之，您添加的handler总是会在最后执行。
         */
        public Builder operateExcelPostProcessor(OperateExcelPostProcessor operateExcelPostProcessor) {
            this.operateExcelPostProcessorList.add(operateExcelPostProcessor);
            return this;
        }

        /**
         * 重新设置（这个的顺序您可以指定）
         *
         * @param operateExcelCellHandlerList 指定好顺序的 handler 链
         */
        public Builder resetOperateExcelHandlerList(List<OperateExcelCellHandler> operateExcelCellHandlerList) {
            this.operateExcelCellHandlerList = operateExcelCellHandlerList;
            this.initAuxiliaryInfo();
            return this;
        }

        public Builder resetOperateExcelPostProcessorList(List<OperateExcelPostProcessor> operateExcelPostProcessorList) {
            this.operateExcelPostProcessorList = operateExcelPostProcessorList;
            return this;
        }

        /**
         * 设置表达式缓存map的容量
         *
         * @param cacheSize 大小
         */
        public Builder expressionCacheSize(int cacheSize) {
            this.expressionCacheSize = cacheSize;
            return this;
        }

        /**
         * 重新设置 表达式计算器
         */
        public Builder resetExpressionCalculator(ExpressionCalculator calculator) {
            this.expressionCalculator = calculator;
            return this;
        }

        /**
         * 构建
         */
        public DefaultTemplateExcelFiller build() {
            return new DefaultTemplateExcelFiller(this);
        }


    }
}