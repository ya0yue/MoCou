package com.example.simplechat.mocou.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.simplechat.mocou.R;

import java.util.List;

/**
 * Created by Larry on 4/7/17.
 */

public class ContactAdapter extends BaseAdapter {

    private Context context;
    private List<ContactEntity> list;
    LayoutInflater inflater;

    public ContactAdapter(Context context, List<ContactEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactEntity entity = this.list.get(position);
        TextView contentName;
        //int id;
        convertView = this.inflater.inflate(R.layout.textview_contact, null);
        contentName = (TextView)convertView.findViewById(R.id.contact_left);
        contentName.setText(entity.username);
        return convertView;
    }
}
