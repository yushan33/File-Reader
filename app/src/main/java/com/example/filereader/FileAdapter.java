package com.example.filereader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class FileAdapter extends BaseAdapter {

    private List<FileData> FileList;//資料
    private LayoutInflater inflater;//加載layout
    private Boolean mCheckable;

    private class ViewHolder{
        ImageView image;
        TextView tv;
        CheckBox checkBox;

        public ViewHolder(TextView t,ImageView i,CheckBox c){
            this.image=i;
            this.tv=t;
            this.checkBox=c;
        }
    }

    public FileAdapter(Context context , List<FileData>FileList, Boolean check ){
        this.inflater =LayoutInflater.from(context);
        this.FileList =FileList;
        this.mCheckable=check;

    }


    //取得數量
    @Override
    public int getCount() {
        return FileList.size();
    }

    //取得項目
    @Override
    public FileData getItem(int position) {
        return FileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return FileList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int[] image={R.drawable.ic_music_green_24dp,R.drawable.ic_video_24dp,R.drawable.ic_photo_24dp,
                R.drawable.ic_file_24dp,R.drawable.ic_folder_green_24dp};
        if(convertView==null){
            convertView = inflater.inflate(R.layout.activity_adapter, null);
            holder = new ViewHolder(
                    (TextView)convertView.findViewById(R.id.textView),
                    (ImageView)convertView.findViewById(R.id.imageView),
                    (CheckBox)convertView.findViewById(R.id.selected_check_box)

            );
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final FileData fileData=(FileData)getItem(position);

        holder.tv.setText(fileData.getFileName());

        //可見性選擇狀態
        if (mCheckable==true) {
            if(fileData.getIsFile())
                holder.checkBox.setVisibility(View.VISIBLE);
            else
                holder.checkBox.setVisibility(View.INVISIBLE);
        } else {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }

        holder.checkBox.setChecked(((ListView) parent).isItemChecked(position));

        if(fileData.getFile_ty()==0)
            holder.image.setImageResource(image[0]);
        else if(fileData.getFile_ty()==1)
            holder.image.setImageResource(image[1]);
        else if(fileData.getFile_ty()==2)
            holder.image.setImageResource(image[2]);
        else if(fileData.getFile_ty()==3)
            holder.image.setImageResource(image[3]);
        else if(!fileData.getIsFile())
            holder.image.setImageResource(image[4]);
        return convertView;
    }

    //用来设置是否CheckBox可见
    public void setCheckable( boolean checkable) {
        mCheckable = checkable;
    }



}
