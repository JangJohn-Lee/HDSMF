package com.hdsmf.upload;

import java.util.ArrayList;
import java.util.List;

public class ExcelReadOption {

    public String filePath;
    public List<String> outputColumns;
    public int startRow;

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath){
        this.filePath = filePath;
    }
    public List<String> getOutputColumns(){
        List<String> temp = new ArrayList<String>();
        temp.addAll(outputColumns);
        return temp;
    }

    public void setOutputColumns(List<String> outputColumns){
        List<String> temp = new ArrayList<String>();
        temp.addAll(outputColumns);
        this.outputColumns = temp;
    }

    public void setOutputColumns(String ... outputColumns){
        if(this.outputColumns == null){
            this.outputColumns = new ArrayList<String>();
        }
        for(String outputColumn : outputColumns){
            this.outputColumns.add(outputColumn);
        }
    }

    public int getStartRow() {
        return startRow;
    }
    public void setStartRow(int startRow){
        this.startRow = startRow;
    }

}
