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
public class UploadServiceImpl implements UploadService {
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

        if(cell == null){
            value = "";
        }else {
            switch (cell.getCellType()){
                case Cell.CELL_TYPE_FORMULA:
                    value = cell.getCellFormula();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    value = (int)cell.getNumericCellValue() + "";
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    value = cell.getBooleanCellValue() + "";
                    break;
                case Cell.CELL_TYPE_ERROR:
                    value = cell.getErrorCellValue() + "";
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

        //첫 번째 시트
        Sheet sheet = workbook.getSheetAt(0);

        //시트에서 유효한(데이터가 있는) 행의 개수를 가져옴
        int rowNum = sheet.getPhysicalNumberOfRows();
        int cellNum = 0;

        Row row = null;
        Cell cell = null;

        String cellName = "";

        //하나의 Row는 하나의 Map
        Map<String, String> map = null;
        //List에는 모든 Row가 포함됨
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        for(int rowIndex = excelReadOption.getStartRow() - 1; rowIndex < rowNum; rowIndex++ ){
            row = sheet.getRow(rowIndex);
            if(row != null){
                cellNum = row.getLastCellNum();
                map = new HashMap<String, String>();

                for(int cellIndex = 0; cellIndex < cellNum; cellIndex++){
                    cell = row.getCell(cellIndex);
                    cellName = getName(cell, cellIndex);
                    if(!excelReadOption.getOutputColumns().contains(cellName)){
                        continue;
                    }
                    map.put(cellName, getValue(cell));
                }
                map.put("rowNum", String.valueOf(rowIndex+1));
                result.add(map);
            }
        }
        return result;
    }


}