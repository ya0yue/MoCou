package com.example.simplechat.mocou.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import com.example.simplechat.mocou.R;

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private List<ChatEntity> list;
    LayoutInflater inflater;

    public ChatAdapter(Context context, List<ChatEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup root) {
        ChatEntity ce = this.list.get(position);
        TextView contentName;
        TextView contentText;
        //int id;
        convertView = this.inflater.inflate(R.layout.textview_inlist, null);
        contentName = (TextView)convertView.findViewById(R.id.chat_chatToName);
        contentText = (TextView)convertView.findViewById(R.id.chat_chatContent);
        contentName.setText(ce.getChatToName());

        if(ce.getType().equals("text")) {
            contentText.setText(ce.getchatContent());
        }else if(ce.getType().equals("image")){
            contentText.setText("[image]");
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
}
