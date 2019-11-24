package com.example.visualmath;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.gun0912.tedpermission.TedPermission.TAG;

public class VM_StorageHandler {
    private StorageReference storageReference;

    public VM_StorageHandler(){
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void storageFileLoad(String filePath, Uri uri){

        StorageReference riversRef = storageReference.child(filePath);

        riversRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Log.i(TAG,"성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Log.i(TAG,"실패...");
                    }
                });
    }


    public void storageFileGet(){

    }
}
