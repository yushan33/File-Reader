package com.example.filereader;

public class FileData {
    String fileName;
    String filePath;
    Boolean isFile;
    int file_ty;//0為音樂，1為影片，2為圖片,3為其他檔案,4為資料夾


    FileData(String fileName, String filePath,Boolean isFile,int type){
        this.fileName=fileName;
        this.filePath=filePath;
        this.isFile=isFile;
        this.file_ty=type;
    }

    public void setFileName(String fileName){
        this.fileName=fileName;
    }

    public void setFilePath(String filePath){
        this.filePath=filePath;
    }

    public void setIsFile(Boolean isFile){
        this.isFile=isFile;
    }

    public void setFile_ty(int type){this.file_ty=type;}

    public String getFileName(){
        return this.fileName;
    }

    public String getFilePath(){
        return this.filePath;
    }

    public Boolean getIsFile(){
        return this.isFile;
    }

    public int getFile_ty(){
        return this.file_ty;
    }

}
