package com.example.simplechat.mocou.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.simplechat.mocou.ChatApplication;
import com.example.simplechat.mocou.DialogActivity;
import com.example.simplechat.mocou.MainActivity;
import com.example.simplechat.mocou.R;
import com.example.simplechat.mocou.sync.SocketService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class DialogAdapter extends BaseAdapter {

    private Context context;
    private List<DialogEntity> list;
    private LayoutInflater inflater;

    public DialogAdapter(Context context, List<DialogEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup root) {
        final DialogEntity dialog = this.list.get(position);

        TextView contentText;
        if(dialog.isMine()){
            if(dialog.getType().equals("image")){
                if(dialog.getSent()){
                    convertView = this.inflater.inflate(R.layout.imageview_right, null);
                }else{
                    convertView = this.inflater.inflate(R.layout.imageview_right_offline, null);
                    ImageView offlineButton=(ImageView) convertView.findViewById(R.id.image_offline);
                    offlineButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }

                ImageView imageView=(ImageView) convertView.findViewById(R.id.chat_imageview_right);
                //String fileName=dialog.getDialogId()+".png";
                //File file = new File(context.getFilesDir()+"/images/", fileName);
                File file=new File(dialog.getContent());
                Bitmap bm = null;
                try {
                    bm = BitmapFactory.decodeStream(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bm);
            }else {
                if(dialog.getSent()){
                    convertView = this.inflater.inflate(R.layout.textview_dialog_right, null);
                }else {
                    convertView=this.inflater.inflate(R.layout.textview_dialog_right_offline, null);
                    ImageView offlineButton=(ImageView) convertView.findViewById(R.id.dialog_offline);
                    offlineButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((DialogActivity)context).sendOfflineMsg(dialog);
                        }
                    });
                }
                contentText = (TextView) convertView.findViewById(R.id.dialog_content_right);
                contentText.setText(dialog.getContent());
            }
        }else{
            if(dialog.getType().equals("image")){
                convertView = this.inflater.inflate(R.layout.imageview_left, null);
                ImageView imageView=(ImageView) convertView.findViewById(R.id.chat_imageview_left);
                File file=new File(dialog.getContent());
                Bitmap bm = null;
                try {
                    bm = BitmapFactory.decodeStream(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bm);
            }else {
                convertView = this.inflater.inflate(R.layout.textview_dialog_left, null);
                contentText = (TextView) convertView.findViewById(R.id.dialog_content_left);
                contentText.setText(dialog.getContent());
            }
        }

        return convertView;
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return this.list.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}
