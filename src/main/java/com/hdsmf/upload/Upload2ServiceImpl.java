package com.hdsmf.upload;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Upload2ServiceImpl implements UploadService {
    public Workbook getWorkbook(String filePath){

        /*
         * FileInputStream은 파일 경로에 있는 파일을 읽어서 Byte로 가져옴
         * 파일이 존재하지 않으면 RuntimeException 발생
         */
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        /*
         * 파일 확장자 .XLS -> HSSFWorkbook
         * 파일 확장자 .XLSX -> XSSFWorkbook 에 각각 초기화
         */
        Workbook workbook = null;
        if(filePath.toUpperCase().endsWith(".XLS")){
            try {
                workbook = new HSSFWorkbook(fileInputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else if(filePath.toUpperCase().endsWith(".XLSX")){
            try{
                workbook = new XSSFWorkbook(fileInputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return workbook;
    }

    /*
     * Cell에 해당하는 Column Name 가져옴
     * 만약 Cell이 널이라면 int cellIndex의 값으로 Column Name 가져옴
     */
    public String getName(Cell cell, int cellIndex){
        int cellNum = 0;
        if(cell != null){
            cellNum = cell.getColumnIndex();
        }else{
            cellNum = cellIndex;
        }
        return CellReference.convertNumToColString(cellNum);
    }

    public String getValue(Cell cell){
        String value = "";

        System.out.println("cell들어왔어어어어" + cell);
        if(cell == null){
            value = "";
        }else {
            switch (cell.getCellType()){
                case Cell.CELL_TYPE_FORMULA:
                    value = cell.getCellFormula();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    value = String.valueOf(cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    value = String.valueOf(cell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    value = "";
                    break;
                default:
                    value = cell.getStringCellValue();
            }
        }

        return value;
    }


    @Override
    public List<Map<String, String>> excelRead(ExcelReadOption excelReadOption) {
        Workbook workbook = getWorkbook(excelReadOption.getFilePath());
        Sheet sheet = workbook.getSheetAt(0);

        int rowNum = sheet.getPhysicalNumberOfRows();
        System.out.println("rowNum" + rowNum);
        int cellNum = 0;

        Row row = null;
        Cell cell = null;
        String cellName = "";

        Map<String, String> map = null;
        List<Map<String, String>> result = new ArrayList<>();

        for(int rowIndex = excelReadOption.getStartRow() - 1; rowIndex < rowNum; rowIndex++ ){

            row = sheet.getRow(rowIndex);
            System.out.println(row + "row");
            System.out.println(rowIndex + "rowIndex");

            if(row != null){
                cellNum = row.getLastCellNum();
                map = new HashMap<String, String>();

                for (int cellIndex = 0; cellIndex < cellNum; cellIndex++) {
                    cell = row.getCell(cellIndex);
                    cellName = getName(cell, cellIndex);
                    System.out.println("cell" + cell);
                    System.out.println("cellName" + cellName);
                    System.out.println("iii" + excelReadOption.getOutputColumns().contains(cellName));

                    String cellValue = getValue(cell);
                    System.out.println(cellValue + "cellValue");

                    map.put(cellName, (String)cellValue);


                    System.out.println(map + "map11");
                }

                int rowIndexPlus = rowIndex + 1;
                map.put("rowNum", String.valueOf(rowIndexPlus));
                result.add(map);

                System.out.println(result + "result22");
                System.out.println(map + "map22");


                System.out.println("rowNum" + rowNum);
                System.out.println("cellNum" + cellNum);
            }
        }
        System.out.println(result + "result");

        return result;
    }
}