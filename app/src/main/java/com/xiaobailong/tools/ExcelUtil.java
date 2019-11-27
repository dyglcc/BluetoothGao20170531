package com.xiaobailong.tools;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtil {
    private final static String TAG = "ExcelUtil";

    public static List<List<Object>> read(String file_name) {
        String extension = file_name.lastIndexOf(".") == -1 ? "" : file_name
                .substring(file_name.lastIndexOf(".") + 1);
        if ("xls".equals(extension)) {// 2003
            Log.d(TAG, "read2003XLS, extension:" + extension);
            return read2003XLS(file_name);
        } else if ("xlsx".equals(extension)) {
            Log.d(TAG, "read2007XLSX, extension:" + extension);
            return read2007XLSX(file_name);
        } else {
            Log.d(TAG, "不支持的文件类型, extension:" + extension);
            return null;
        }
    }

    public static List<List<Object>> read2003XLS(String path) {
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        try {
            Workbook book = Workbook.getWorkbook(new File(path));
            // book.getNumberOfSheets();  //获取sheet页的数目
            // 获得第一个工作表对象
            Sheet sheet = book.getSheet(0);
            int Rows = sheet.getRows();
            int Cols = sheet.getColumns();
            Log.d(TAG, "当前工作表的名字:" + sheet.getName());
            Log.d(TAG, "总行数:" + Rows + ", 总列数:" + Cols);

            List<Object> objList = new ArrayList<Object>();
            String val = null;
            for (int i = 0; i < Rows; i++) {
                boolean null_row = true;
                for (int j = 0; j < Cols; j++) {
                    // getCell(Col,Row)获得单元格的值，注意getCell格式是先列后行，不是常见的先行后列
                    Log.d(TAG, (sheet.getCell(j, i)).getContents() + "\t");
                    val = (sheet.getCell(j, i)).getContents();
                    if (val == null || val.equals("")) {
                        val = "null";
                    } else {
                        null_row = false;
                    }
                    objList.add(val);
                }
                Log.d(TAG, "\n");
                if (null_row != true) {
                    dataList.add(objList);
                    null_row = true;
                }
                objList = new ArrayList<Object>();
            }
            book.close();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return dataList;
    }

    public static List<List<Object>> read2007XLSX(String path) {
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        String str_c = "";
        String v = null;
        boolean flat = false;
        List<String> ls = new ArrayList<String>();
        try {
            ZipFile xlsxFile = new ZipFile(new File(path));
            ZipEntry sharedStringXML = xlsxFile.getEntry("xl/sharedStrings.xml");
            if (sharedStringXML == null) {
                Log.d(TAG, "空文件:" + path);
                return dataList;
            }
            InputStream inputStream = xlsxFile.getInputStream(sharedStringXML);
            XmlPullParser xmlParser = Xml.newPullParser();
            xmlParser.setInput(inputStream, "utf-8");
            int evtType = xmlParser.getEventType();
            while (evtType != XmlPullParser.END_DOCUMENT) {
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        String tag = xmlParser.getName();
                        if (tag.equalsIgnoreCase("t")) {
                            ls.add(xmlParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                evtType = xmlParser.next();
            }
            ZipEntry sheetXML = xlsxFile.getEntry("xl/worksheets/sheet1.xml");
            InputStream inputStreamsheet = xlsxFile.getInputStream(sheetXML);
            XmlPullParser xmlParsersheet = Xml.newPullParser();
            xmlParsersheet.setInput(inputStreamsheet, "utf-8");
            int evtTypesheet = xmlParsersheet.getEventType();
            List<Object> objList = new ArrayList<Object>();
            String val = null;
            boolean null_row = true;

            while (evtTypesheet != XmlPullParser.END_DOCUMENT) {
                switch (evtTypesheet) {
                    case XmlPullParser.START_TAG:
                        String tag = xmlParsersheet.getName();
                        if (tag.equalsIgnoreCase("row")) {
                        } else if (tag.equalsIgnoreCase("c")) {
                            String t = xmlParsersheet.getAttributeValue(null, "t");
                            if (t != null) {
                                flat = true; // 字符串型
                                // Log.d(TAG, flat + "有");
                            } else { // 非字符串型，可能是整型
                                // Log.d(TAG, flat + "没有");
                                flat = false;
                            }
                        } else if (tag.equalsIgnoreCase("v")) {
                            v = xmlParsersheet.nextText();
                            if (v != null) {
                                if (flat) {
                                    str_c += ls.get(Integer.parseInt(v)) + "  ";
                                    val = ls.get(Integer.parseInt(v));
                                    null_row = false;
                                } else {
                                    str_c += v + "  ";
                                    val = v;
                                }
                                objList.add(val);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xmlParsersheet.getName().equalsIgnoreCase("row") && v != null) {
                            str_c += "\n";
                            if (null_row != true) {
                                dataList.add(objList);
                                null_row = true;
                            }
                            objList = new ArrayList<Object>();
                        }
                        break;
                }
                evtTypesheet = xmlParsersheet.next();
            }
            Log.d(TAG, str_c);
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        if (str_c == null) {
            str_c = "解析文件出现问题";
            Log.d(TAG, str_c);
        }

        return dataList;
    }

    public static int writeExcel(String file_name, List<List<Object>> data_list) {
        try {
            WritableWorkbook book = Workbook.createWorkbook(new File(file_name));
            WritableSheet sheet1 = book.createSheet("sheet1", 0);
            for (int i = 0; i < data_list.size(); i++) {
                List<Object> obj_list = data_list.get(i);
                for (int j = 0; j < obj_list.size(); j++) {
                    String value = obj_list.get(j) == null ? "" : obj_list.get(j).toString();
                    Label label = new Label(j, i, value);
                    sheet1.addCell(label);
                }
            }
            book.write();
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public static int writeExcelWithTitleColumn(String file_name, List<List<Object>> data_list, String[] columns, String sheetname) {
        try {
            WritableWorkbook book = Workbook.createWorkbook(new File(file_name));
            WritableSheet sheet1 = book.createSheet(sheetname, 0);
            WritableFont font1 = new WritableFont(WritableFont.createFont("微软雅黑"), 10, WritableFont.BOLD);
            WritableFont font2 = new WritableFont(WritableFont.createFont("微软雅黑"), 9, WritableFont.NO_BOLD);
            WritableCellFormat wcf = new WritableCellFormat(font1);
            WritableCellFormat wcf2 = new WritableCellFormat(font2);
            // 用于表头样式
            wcf.setBackground(jxl.format.Colour.AQUA); // 背景颜色
            wcf.setAlignment(Alignment.CENTRE); // 水平居中
            wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直居中
            wcf.setBorder(Border.ALL, BorderLineStyle.MEDIUM);// 设置边框 Border静态字段 设置边框样式BorderLineStyle 静态字段


//            wcf2.setBackground(Colour.GRAY_25);
            wcf2.setAlignment(Alignment.CENTRE);
            wcf2.setVerticalAlignment(VerticalAlignment.CENTRE);
            wcf2.setBorder(Border.ALL, BorderLineStyle.MEDIUM);

            // 循环写入表头
            for (int i = 0; i < columns.length; i++) {
                    /*
                     * 添加单元格(Cell)内容addCell() 添加Label对象Label()
                     * 数据的类型有很多种、在这里你需要什么类型就导入什么类型 如：jxl.write.DateTime
                     * 、jxl.write.Number、jxl.write.Label Label(i, 0, columns[i],
                     * wcf) 其中i为列、0为行、columns[i]为数据、wcf为样式
                     * 合起来就是说将columns[i]添加到第一行(行、列下标都是从0开始)第i列、样式为什么"色"内容居中
                     */
                sheet1.addCell(new Label(i, 0, columns[i], wcf));
            }

            for (int i = 0; i < data_list.size(); i++) {
                List<Object> obj_list = data_list.get(i);
                for (int j = 0; j < obj_list.size(); j++) {
                    String value = obj_list.get(j) == null ? "" : obj_list.get(j).toString();
                    Label label = new Label(j, i + 1, value, wcf2);
                    sheet1.addCell(label);
                }
            }
            book.write();
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public static int writeExcelWithTitleColumnAndMergeTitleCell(String file_name, List<List<Object>> data_list, String[] columns, String title, String sheetname) {
        try {
            WritableWorkbook book = Workbook.createWorkbook(new File(file_name));
            WritableSheet sheet1 = book.createSheet(sheetname, 0);
            WritableFont font1 = new WritableFont(WritableFont.createFont("微软雅黑"), 10, WritableFont.BOLD);
            WritableFont font2 = new WritableFont(WritableFont.createFont("微软雅黑"), 9, WritableFont.NO_BOLD);
            WritableCellFormat wcf = new WritableCellFormat(font1);
            WritableCellFormat wcf2 = new WritableCellFormat(font2);
            // 用于表头样式
            wcf.setBackground(jxl.format.Colour.AQUA); // 背景颜色
            wcf.setAlignment(Alignment.CENTRE); // 水平居中
            wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直居中
            wcf.setBorder(Border.ALL, BorderLineStyle.MEDIUM);// 设置边框 Border静态字段 设置边框样式BorderLineStyle 静态字段


//            wcf2.setBackground(Colour.GRAY_25);
            wcf2.setAlignment(Alignment.CENTRE);
            wcf2.setVerticalAlignment(VerticalAlignment.CENTRE);
            wcf2.setBorder(Border.ALL, BorderLineStyle.MEDIUM);

//            // 循环写入表头
//            for (int i = 0; i < columns.length; i++) {
//                    /*
//                     * 添加单元格(Cell)内容addCell() 添加Label对象Label()
//                     * 数据的类型有很多种、在这里你需要什么类型就导入什么类型 如：jxl.write.DateTime
//                     * 、jxl.write.Number、jxl.write.Label Label(i, 0, columns[i],
//                     * wcf) 其中i为列、0为行、columns[i]为数据、wcf为样式
//                     * 合起来就是说将columns[i]添加到第一行(行、列下标都是从0开始)第i列、样式为什么"色"内容居中
//                     */
//            }

            // 第一行
            sheet1.mergeCells(0, 0, columns.length-1, 0);
            sheet1.addCell(new Label(0, 0, title, wcf));

            // 循环写入表头
            for (int i = 0; i < columns.length; i++) {
                    /*
                     * 添加单元格(Cell)内容addCell() 添加Label对象Label()
                     * 数据的类型有很多种、在这里你需要什么类型就导入什么类型 如：jxl.write.DateTime
                     * 、jxl.write.Number、jxl.write.Label Label(i, 0, columns[i],
                     * wcf) 其中i为列、0为行、columns[i]为数据、wcf为样式
                     * 合起来就是说将columns[i]添加到第一行(行、列下标都是从0开始)第i列、样式为什么"色"内容居中
                     */
                sheet1.addCell(new Label(i, 1, columns[i], wcf));
            }

            for (int i = 0; i < data_list.size(); i++) {
                List<Object> obj_list = data_list.get(i);
                for (int j = 0; j < obj_list.size(); j++) {
                    String value = obj_list.get(j) == null ? "" : obj_list.get(j).toString();
                    Label label = new Label(j, i + 2, value, wcf2);
                    sheet1.addCell(label);
                }
            }
            book.write();
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

}
