package com.example.delighter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private EditText mNameField,mAboutField;
    private Button mBack,mConfirm;
    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private String userId,name,about,profileImageUrl;
    private Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        String userSex=getIntent().getExtras().getString("userSex");
        mNameField=(EditText)findViewById(R.id.name);
        mAboutField=(EditText)findViewById(R.id.about);
        mBack=(Button)findViewById(R.id.back);
        mConfirm=(Button)findViewById(R.id.confirm);
        mProfileImage=(ImageView) findViewById(R.id.profileImage);
        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        mCustomerDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userId);

        getUserInfo();
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });


        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saverUserInformation();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
    }

    private void getUserInfo() {
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.getChildrenCount()>0){
                    Map<String,Object> map=(Map<String,Object>)snapshot.getValue();
                    if(map.get("name")!=null){
                        name=map.get("name").toString();
                        mNameField.setText(name);

                    }
                    if(map.get("about")!=null){
                        about=map.get("about").toString();
                        mAboutField.setText(about);

                    }
                    Glide.clear(mProfileImage);
                    if(map.get("profileImageUrl")!=null){
                        profileImageUrl = map.get("profileImageUrl").toString();

                        switch(profileImageUrl){
                            case "default":
                                Glide.with(getApplication()).load(R.drawable.profile).into(mProfileImage);
                                break;
                            default:Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
                                break;
                        }

                        Glide.with(getApplicationContext()).load(profileImageUrl).into(mProfileImage);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saverUserInformation() {
        name= mNameField.getText().toString();
        about=mAboutField.getText().toString();

        Map userInfo=new HashMap();
        userInfo.put("name",name);
        userInfo.put("about",about);
        mCustomerDatabase.updateChildren(userInfo);
        if(resultUri!=null){
            //StorageReference filepath= FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);

            final StorageReference filepath= FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);

            Bitmap bitmap=null;

            try {
                if(Build.VERSION.SDK_INT < 28) {
                     bitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),resultUri
                    );

                } else {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), resultUri);
                     bitmap = ImageDecoder.decodeBitmap(source);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("error","bitmap");
            }
            ByteArrayOutputStream boas=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,boas);
            byte[] data=boas.toByteArray();
            UploadTask uploadTask=filepath.putBytes(data);
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.i("profile picture","falied");
//                    finish();
//                }
//            });
//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Task<Uri> task=taskSnapshot.getMetadata().getReference().getDownloadUrl();
//                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        Map userInfo=new HashMap();
//                        userInfo.put("profileImageUri",uri.toString());
//                        mCustomerDatabase.updateChildren(userInfo);
//                        finish();
//                        return;
//                    }
//                });
//                }
//            });


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map userInfo=new HashMap();
                        userInfo.put("profileImageUrl",uri.toString());
                      mCustomerDatabase.updateChildren(userInfo);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.i("Profile Image","failed");
                            finish();
                            return;
                        }
                    });
                }
            });



        }else{
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode== Activity.RESULT_OK){
             Uri imageUri=data.getData();
            resultUri=imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}