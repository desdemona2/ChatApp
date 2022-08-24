package com.redheadhammer.friendschat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private static final String TAG = "MessagesAdapter";
    private final List<ModelClass> list;
    private final String username;
    private boolean status;
    private static final int RECEIVE = 2;
    private static final int SEND = 1;

    public MessagesAdapter(List<ModelClass> list, String username) {
        this.list = list;
        this.username = username;
        this.status = false;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SEND) {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.card_send, parent, false
            );
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.card_receive, parent, false
            );
        }return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            if (status) {
                textView = itemView.findViewById(R.id.messageSent);
            } else {
                textView = itemView.findViewById(R.id.messageReceive);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: " + list.get(position).getUsername());
        if (list.get(position).getUsername().equals(username)) {
            status = true;
            return SEND;
        } else {
            status = false;
            return RECEIVE;
        }
    }
}
