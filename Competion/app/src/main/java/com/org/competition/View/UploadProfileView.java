package com.org.competition.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.org.competition.Base.BaseActivity;
import com.org.competition.Firebase.Firebase;
import com.org.competition.databinding.ActivityRegisterBinding;
import com.org.competition.databinding.ActivityUploadProfileBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class UploadProfileView extends BaseActivity {

    ActivityUploadProfileBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityUploadProfileBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }
    private void initView(){
        bind.closeBtn.setOnClickListener(this);
        bind.uploadProfileBtn.setOnClickListener(this);
        bind.goHome.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == bind.closeBtn || view == bind.goHome){
            startIntentAsCleanMode(HomeView.class);
        }else if(view == bind.uploadProfileBtn){
            CropImage.activity()
                    .setFixAspectRatio(true)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                uploadProfile(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                showMessage("Fail upload profile image.");
                startIntentAsCleanMode(HomeView.class);
            }
        }
    }
    @Override
    public void onBackPressed() {
        startIntentAsCleanMode(HomeView.class);
    }
    //upload image to storage
    private void uploadProfile(Uri uri){
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child(Firebase.PROFILE);
        final StorageReference imageFilePath = mStorage.child(uri.getLastPathSegment());
        dialogHelper.showProgressDialog();
        imageFilePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        updateUser(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                dialogHelper.closeDialog();
                showMessage("Fail upload profile image.");
                startIntentAsCleanMode(HomeView.class);
            }
        });
    }
    private void updateUser(String profile_path){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put("image", profile_path);
        dialogHelper.showProgressDialog();
        database.getReference(Firebase.USER).child(currentUser.getUid()).updateChildren(taskMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                dialogHelper.closeDialog();
                showMessage("Success to update user !");
                startIntentAsCleanMode(HomeView.class);
            }
        });
    }
}
