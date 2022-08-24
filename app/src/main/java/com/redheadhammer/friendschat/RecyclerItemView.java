package com.redheadhammer.friendschat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RecyclerItemView extends RecyclerView.Adapter<RecyclerItemView.MyViewHolder>{

    private final List<String> usersList;
    private final String username;
    private final Context context;

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();

    public RecyclerItemView(List<String> usersList, String username, Context context) {
        this.usersList = usersList;
        this.username = username;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerItemView.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_item, parent, false
        );
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerItemView.MyViewHolder holder, int position) {
        databaseReference.child("Users").child(usersList.get(position)).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nameOther = String.valueOf(snapshot.child("username").getValue());
                        String imageURL = String.valueOf(snapshot.child("image").getValue());

                        holder.username.setText(nameOther);

                        if (! "null".equals(imageURL)) {
                            Glide.with(context).load(imageURL).into(holder.imageView);
                        }

                        holder.cardView.setOnClickListener(view -> {
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("otherUser", nameOther);
                            context.startActivity(intent);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                }
        );
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView username;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            username = itemView.findViewById(R.id.userName);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
