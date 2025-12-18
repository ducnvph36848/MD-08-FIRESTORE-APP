package com.example.md_08_ungdungfivestore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.ChatMessage;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {

    Context context;
    ArrayList<ChatMessage> list;

    public ChatAdapter(Context ctx, ArrayList<ChatMessage> list) {
        this.context = ctx;
        this.list = list;
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int i) { return list.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        ChatMessage msg = list.get(i);

        if (msg.isUser) {
            view = LayoutInflater.from(context).inflate(R.layout.item_user, null);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_notification, null);
        }

        TextView tv = view.findViewById(R.id.txtMessage);
        tv.setText(msg.message);
        return view;
    }
}
