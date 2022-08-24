package com.redheadhammer.friendschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.redheadhammer.friendschat.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private String username;
    private String otherUser;
    private List<ModelClass> list;
    MessagesAdapter adapter;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference reference = database.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        otherUser = intent.getStringExtra("otherUser");


        list = new ArrayList<>();
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));


        binding.userName.setText(username);
        binding.imageBack.setOnClickListener(this::onBackClick);
        binding.fab.setOnClickListener(this::onFabClick);

        getMessages();
    }

    private void onBackClick(View view) {
        super.onBackPressed();
    }

    private void onFabClick(View view) {
        String message = String.valueOf(binding.message.getText());
        if (!message.isEmpty()) {
            sendMessage(message);
            binding.message.setText("");
        }
    }

    private void sendMessage(String message) {
        final String key = reference.child("Messages").child(username).child(otherUser).push().getKey();
        final Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("username", username);
        messageMap.put("message", message);

        // save the message for current user
        if (key == null) {
            Toast.makeText(this, "Sending Message Failed", Toast.LENGTH_SHORT).show();
            return;
        }
        reference.child("Messages").child(username).child(otherUser).child(key).setValue(messageMap)
                .addOnCompleteListener(
                        // save the message for the receiver
                        task -> reference.child("Messages").child(otherUser)
                                .child(username).child(key).setValue(messageMap)
                        );
    }

    private void getMessages() {
        reference.child("Messages").child(username).child(otherUser).addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot,
                                             @Nullable String previousChildName) {
                        ModelClass modelClass = snapshot.getValue(ModelClass.class);
                        list.add(modelClass);
                        adapter.notifyItemInserted(list.size()-1);
                        binding.recycler.scrollToPosition(list.size()-1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot,
                                               @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot,
                                             @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
        adapter = new MessagesAdapter(list, username);
        binding.recycler.setAdapter(adapter);
    }
}