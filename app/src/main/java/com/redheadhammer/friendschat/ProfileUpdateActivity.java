package com.redheadhammer.friendschat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.redheadhammer.friendschat.databinding.ActivityProfileUpdateBinding;

import java.util.Objects;
import java.util.UUID;

public class ProfileUpdateActivity extends AppCompatActivity {

    private static final String TAG = "ProfileUpdateActivity";
    private ActivityProfileUpdateBinding binding;
    private ActivityResultLauncher<Intent> chooseImageLauncher;
    private Uri imageUri;
    private boolean isImageSelected;
    // save previous image link to delete it on adding new image
    private String image;

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = firebaseStorage.getReference();
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileUpdateBinding.inflate(getLayoutInflater());
        chooseImageLauncherInit();

        setContentView(binding.getRoot());

        updateInit();

        binding.userImage.setOnClickListener(this::setImage);
        binding.signUpUpdate.setOnClickListener(this::onUpdateClick);
    }

    private void onUpdateClick(View view) {
        String username = String.valueOf(binding.userNameUpdate.getText());
        if (username.isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show();
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.signUpUpdate.setClickable(false);
            updateUserName(username);
            uploadUserImage();
        }
    }

    private void updateUserName(String username) {
        databaseReference.child("Users").child(firebaseUser.getUid())
                .child("username").setValue(username);
    }

    private void uploadUserImage() {
        // if image is selected than save it to the storage and
        // save reference of image in database to load later
        if (isImageSelected) {
            UUID fileId = UUID.randomUUID();
            final String filename = String.format("images/%s.jpg", fileId);
            // save image in firebase storage
            saveImageInStorage(filename);
        } else {
            databaseReference.child("Users")
                    .child(firebaseUser.getUid())
                    .child("image")
                    .setValue("null");
        }
        binding.progressBar.setVisibility(View.GONE);
    }

    private void saveImageInStorage(String fileName) {
        storageReference.child(fileName).putFile(imageUri).addOnSuccessListener(
                snapshot -> {
                    // get storage reference for the image file
                    StorageReference imageReference = firebaseStorage.getReference(fileName);
                    // save the image url to the firebase database section to index it later
                    imageReference.getDownloadUrl().addOnSuccessListener(uri ->
                            databaseReference.child("Users")
                                    .child(Objects.requireNonNull(firebaseUser.getUid()))
                                    .child("image").setValue(uri.toString()));
                }
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, R.string.database_upload_success, Toast.LENGTH_SHORT).show();
                // delete previous image
                StorageReference imageReference = firebaseStorage.getReferenceFromUrl(image);
                imageReference.delete();
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthUserCollisionException | FirebaseAuthInvalidCredentialsException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d(TAG, "saveImageInStorage: " + e.getMessage());
                    Toast.makeText(this, R.string.upload_database_fail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateInit() {
        databaseReference.child("Users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.userNameUpdate.setText(
                                String.valueOf(snapshot.child("username").getValue())
                        );
                        image = String.valueOf(snapshot.child("image").getValue());

                        if (! "null".equals(image)) {
                            Glide.with(ProfileUpdateActivity.this).load(image)
                                .centerCrop()
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(
                                            @Nullable GlideException e,
                                            Object model,
                                            Target<Drawable> target,
                                            boolean isFirstResource
                                    ) {
                                        Toast.makeText(ProfileUpdateActivity.this,
                                                R.string.image_load_failed, Toast.LENGTH_SHORT).show();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(
                                            Drawable resource,
                                            Object model,
                                            Target<Drawable> target,
                                            DataSource dataSource,
                                            boolean isFirstResource
                                    ) {
                                        Toast.makeText(ProfileUpdateActivity.this,
                                                R.string.load_image_success, Toast.LENGTH_SHORT).show();
                                        binding.progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(binding.userImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void setImage(View view) {
        imageChooser();
    }

    private void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        chooseImageLauncher.launch(intent);
    }

    private void chooseImageLauncherInit() {
        chooseImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int result_code = result.getResultCode();
                    Intent data = result.getData();

                    if (result_code == RESULT_OK && data != null) {
                        imageUri = data.getData();
                        Glide.with(this)
                                .load(imageUri)
                                .centerCrop()
                                .into(binding.userImage);
                        isImageSelected = true;
                    } else {
                        isImageSelected = false;
                    }
                }
        );
    }
}