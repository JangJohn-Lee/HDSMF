package com.hdsmf.upload;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;


public interface UploadService {

    Workbook getWorkbook(String filePath);
    String getName(Cell cell, int cellIndex);
    String getValue(Cell cell);
    List<Map<String, String>> excelRead(ExcelReadOption excelReadOption);
}

