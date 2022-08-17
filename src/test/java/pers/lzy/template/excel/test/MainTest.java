package pers.lzy.template.excel.test;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import pers.lzy.template.excel.calculator.Jxel3ExpressionCalculator;
import pers.lzy.template.excel.core.TemplateExcelFiller;
import pers.lzy.template.excel.core.TemplateExcelFillerFactory;
import pers.lzy.template.excel.utils.ExcelUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/24  10:18
 */
public class MainTest {

    @Test
    public void test01() throws IOException {

        // 构建导出着
        TemplateExcelFiller templateExcelFiller = TemplateExcelFillerFactory.defaultTemplateExcelFillerBuilder()
                .expressionCacheSize(500)
                .functions("math", Math.class)
                .functions("StringUtils", StringUtils.class)
                .build();

        //获取工作簿
        XSSFWorkbook book = new XSSFWorkbook("C:\\Users\\immortal\\Desktop\\test1.xlsx");
        //获取工作表
        XSSFSheet sheet = book.getSheetAt(0);
        Map<String, Object> objectObjectHashMap = new HashMap<>();
        Out out = new Out();
        out.setCode(123123);
        out.setName("哈哈哈");
        objectObjectHashMap.put("out", out);

        List<Out> outList = new ArrayList<>();
        outList.add(new Out("zyliu", 123));
        outList.add(new Out("lc", 123));
        Map<Object, Object> innerMap = new HashMap<>();
        innerMap.put("outList", outList);
        objectObjectHashMap.put("details", outList);

        templateExcelFiller.fillData(sheet, objectObjectHashMap);

        ExcelUtil.writeFile(book, new File("C:\\Users\\immortal\\Desktop\\test1Copy.xlsx"));
    }

    /**
     * 测试获取表达式中，数组的真实长度
     */
    @Test
    public void test02() {
        Map<String, Object> objectObjectHashMap = new HashMap<>();
        Out out = new Out();
        out.setCode(123123);
        out.setName("哈哈哈");
        objectObjectHashMap.put("out", out);

        List<Out> outList = new ArrayList<>();
        outList.add(new Out("zyliu", 123));
        outList.add(new Out("lc", 123));
        Map<Object, Object> innerMap = new HashMap<>();
        innerMap.put("outList", outList);
        objectObjectHashMap.put("details", innerMap);

        Jxel3ExpressionCalculator jxel3ExpressionCalculator = new Jxel3ExpressionCalculator(new JexlBuilder().create().createJxltEngine(), 1000);
        Collection<?> objects = jxel3ExpressionCalculator.parseObjArrInParamMap("afdds123${details.outList[].name + '123'}afdas", objectObjectHashMap);

        for (Object object : objects) {
            System.out.println(object);
        }
    }
    /**
     * 测试获取表达式中，数组的真实长度
     */
    @Test
    public void test03() {
        Map<String, Object> objectObjectHashMap = new HashMap<>();
        Out out = new Out();
        out.setCode(123123);
        out.setName("哈哈哈");
        objectObjectHashMap.put("out", out);

        List<Out> outList = new ArrayList<>();
        outList.add(new Out("zyliu", 123));
        outList.add(new Out("lc", 123));
        objectObjectHashMap.put("details", outList);
        List<Out> outList2 = new ArrayList<>();
        outList2.add(new Out("zyliu", 123));
        objectObjectHashMap.put("details2", outList2);
        Jxel3ExpressionCalculator jxel3ExpressionCalculator = new Jxel3ExpressionCalculator(new JexlBuilder().create().createJxltEngine(), 1000);
        Collection<?> objects = jxel3ExpressionCalculator.parseObjArrInParamMap("afdds123${details[].name + '123' + details2[].name}afdas${details2[].code}", objectObjectHashMap);

        for (Object object : objects) {
            System.out.println(object);
        }
    }

    @Test
    public void test05() {
        System.out.println(String.format("[%d]", 5));
    }

    public static class Out {


        private String name;
        private Integer code;

        public Out(String name, Integer code) {
            this.name = name;
            this.code = code;
        }

        public Out() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "Out{" +
                    "name='" + name + '\'' +
                    ", code=" + code +
                    '}';
        }
    }



}
