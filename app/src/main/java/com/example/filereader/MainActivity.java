package com.example.filereader;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.lang.Object;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList <String> names;
    private ArrayList <String> paths;
    private ArrayList <FileData> filesList;
    private static final int PERMISSION_REQUEST_STORAGE=30;
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String ROOT_PATH_NAME ="內部記憶體";
    private static final String ROOT_NAME = "回根目錄";
    private static final String PRE_LEVEL ="回上一層";
    private static final String copy_dest_path= Environment.getExternalStorageDirectory()+"/file_reader/";
    private String target="";//目前位置
    private String nowPath="";
    private FileData fileData;
    private File[] files;
    private FileAdapter adapter;
    private String[] pictures_type={"jpg","jpeg","gif","png"};
    private String[] video_type={"mp4","mpg4","3gp","avi","asf","m4v","m4u","mpe","mpeg","mpg"};
    private String[] music_type={"mp2","mp3","mpga","ogg","wav","wma","wmv"};

    private ListView listView;
    private TextView tvNowPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_STORAGE);
            }else{
                initData();
                initView();
            }
        }



    }

    private void initData(){

        filesList = new ArrayList<>();//放入listview資料結構
        names = new ArrayList<>();
        paths = new ArrayList<>();

        tvNowPath=(TextView)findViewById(R.id.nowPath) ;


        getFileDirectory(ROOT_PATH);
        tvNowPath.setText(ROOT_PATH_NAME);


    }

    private void getFileDirectory(String path){
        filesList.clear();
        paths.clear();
        if(!path.equals(ROOT_PATH)){
            //第0個位置放root
            names.add(ROOT_NAME);
            paths.add(ROOT_PATH);
            fileData=new FileData(ROOT_NAME,ROOT_PATH,false,4);
            filesList.add(fileData);

            //第1個位置放pre_level//前一個位置
            names.add(PRE_LEVEL);
            paths.add(new File(path).getParent());
            fileData=new FileData(PRE_LEVEL,new File(path).getParent(),false,4);
            filesList.add(fileData);
        }

        files = new File(path).listFiles();
        for(int i=0;i<files.length;i++){
            if(!files[i].getName().substring(0,1).equals(".")){
//                names.add(files[i].getName());
//                paths.add(files[i].getPath());
                if(files[i].isDirectory()){//如果為資料夾
                    fileData=new FileData(files[i].getName(),files[i].getPath(),false,4);
                }else{
                    fileData=new FileData(files[i].getName(),files[i].getPath(),true,file_type(files[i].getName()));
                }
                filesList.add(fileData);
            }

        }

        //排序資料夾與檔案，排除 根目錄 與 上一層 後，先判斷種類後(資料夾、音檔、圖片、影片)，再按照名稱排序
        Collections.sort( filesList, new Comparator<FileData>(){
            @Override
            public int compare(FileData o1, FileData o2) {
                if(!o1.equals(filesList.get(0)) && !o1.equals(filesList.get(1)) && !o2.equals(filesList.get(1)) && !o2.equals((filesList.get(1)))  ){
                    if(o1.file_ty == o2.file_ty){
                        return o1.fileName.compareTo(o2.fileName) ;
                    }
                    return  o2.file_ty-o1.file_ty ;//資料夾排在前面，檔案排在後面
                }
                return 0;

            }
        });

    }

    private int file_type(String fileName){
        String[] tmp=fileName.split("\\.");
        Log.e("fileName",fileName);
        if(tmp.length==0)   return 4;
        else{
            String type=tmp[tmp.length-1];
            for(String i: music_type){
                if(type.equals(i))  return 0;
            }
            for(String i : video_type){
                if(type.equals(i))  return 1;
            }
            for(String i :pictures_type){
                if(type.equals(i))  return 2;
            }
            return 3;
        }
    }

    private void initView(){
        adapter=new FileAdapter(MainActivity.this,filesList,false);
        listView=(ListView)findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setMultiChoiceModeListener(new MultiChoiceModeListener(listView));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                target = filesList.get(position).filePath;
                if(target.equals(ROOT_PATH)){
                    nowPath =paths.get(position);
                    getFileDirectory(ROOT_PATH);
                    adapter.notifyDataSetChanged();
                    tvNowPath.setText(ROOT_PATH_NAME);
                }else if (target.equals(PRE_LEVEL)){
                    nowPath = paths.get(position);
                    getFileDirectory(new File(nowPath).getParent());
                    adapter.notifyDataSetChanged();
                    tvNowPath.setText(target);
                }else{
                    File file = new File(target);
                    if(file.canRead()){
                        if(file.isDirectory()){
                            nowPath = filesList.get(position).getFilePath();
                            getFileDirectory( filesList.get(position).getFilePath());
                            adapter.notifyDataSetChanged();
                            tvNowPath.setText(target);
                        }else{
                            Toast.makeText(MainActivity.this,R.string.is_not_directory,Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(MainActivity.this,R.string.can_not_read,Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



    }





    private class MultiChoiceModeListener implements AbsListView.MultiChoiceModeListener{
        private  ListView mListView;
        private  List<FileData> selectItems= new ArrayList<>();
        private TextView mTitleTextView;

        private MultiChoiceModeListener(ListView listView){
            mListView=listView;

        }



        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if(adapter.getItem(position).getIsFile()){
                selectItems.add(adapter.getItem(position));
                Log.e("選取到的項目NAME",selectItems.get(selectItems.size()-1).fileName);
                Log.e("選取到的項目path",selectItems.get(selectItems.size()-1).filePath);

                mTitleTextView.setText("已選擇 " + mListView.getCheckedItemCount() + " 項");
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(MainActivity.this,R.string.directory_can_not_copy,Toast.LENGTH_LONG).show();
            }



        }

        //建立一個actionbar
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.check_task_priority2,menu);

            @SuppressLint("ResourceType")
            View multiSelectActionBarView = LayoutInflater.from(MainActivity.this).inflate(R.layout.action_mode_bar,null);
            actionMode.setCustomView(multiSelectActionBarView);
            selectItems.clear();
            mTitleTextView=(TextView)multiSelectActionBarView.findViewById(R.id.title);
            mTitleTextView.setText("已選擇0項");
            Log.e("長按住~~","按住了~~~~");
            adapter.setCheckable(true);
            adapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {

            return false;
        }

        @Override
        public boolean onActionItemClicked( ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.up:{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<FileData> select = selectItems;
                            Log.e("複製檔案數量", String.valueOf(select.size()));

                            for( FileData items:select) {
                                try {
                                    Log.e("起點檔案位址", items.filePath);
                                    Log.e("目的檔案位址", copy_dest_path + items.getFileName());
                                    File file_sourse=new File(items.filePath);
                                    File file_dest = new File(copy_dest_path + items.getFileName());
                                    if(!file_dest.exists()){
                                        file_dest.createNewFile();
                                    }

                                    copyFileUsingFileStreams(file_sourse,file_dest);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            Message msg=new Message();
                            handler.sendMessage(msg);

                        }
                    }).start();

                    actionMode.finish();

                    break;

                }


            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            //selectItems.clear();
            adapter.setCheckable(false);
            adapter.notifyDataSetChanged();

        }
    }

    private static void copyFileUsingFileStreams(File source, File dest)        //複製檔案
            throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            if(input!=null)     input.close();
            if(output!=null)    output.close();
        }
    }

    Handler handler= new Handler(){
        @Override
        public void handleMessage( Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MainActivity.this,R.string.copy_file_finish,Toast.LENGTH_LONG).show();


        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST_STORAGE:{
                initData();
                initView();
            }
        }
    }






}
