package io.toka.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<ChatMessage> objects;

    private class ViewHolder {
        TextView Text;
        TextView Username;
        TextView Timestamp;
    }

    public HistoryAdapter(Context context, ArrayList<ChatMessage> objects) {
        inflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    public int getCount() {
        return objects.size();
    }

    public ChatMessage getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.chatmessage, null);
            holder.Text = (TextView) convertView.findViewById(R.id.text);
            holder.Username = (TextView) convertView.findViewById(R.id.username);
            holder.Timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.Text.setText(objects.get(position).getText());
        holder.Username.setText(objects.get(position).getUsername());
        holder.Timestamp.setText(objects.get(position).getTimestamp());
        return convertView;
    }
}