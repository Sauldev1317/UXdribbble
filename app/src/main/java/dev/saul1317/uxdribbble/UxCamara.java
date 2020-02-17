package dev.saul1317.uxdribbble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UxCamara extends AppCompatActivity implements View.OnClickListener {

    Toolbar camare_toolbar;
    Button button_choose_picture;
    ImageView img_new_picture;
    Uri filePath;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    //FIREBASE
    private FirebaseStorage firebaseStorage;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux_camara);

        camare_toolbar = (Toolbar) findViewById(R.id.camera_toolbar);
        setSupportActionBar(camare_toolbar);
        getSupportActionBar().setTitle(null);


        camare_toolbar.setNavigationIcon(R.drawable.delete);
        camare_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UxCamara.this, Home.class));
            }
        });

        img_new_picture = (ImageView) findViewById(R.id.img_new_picture);
        button_choose_picture = (Button) findViewById(R.id.button_choose_picture);
        button_choose_picture.setOnClickListener(this);

        firebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = firebaseStorage.getReference();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ux_camare_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_done) {
            uploadImage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_choose_picture:
                choosePicture();
                break;
            default:
                break;
        }
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            ImageDecoder.Source source = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                source = ImageDecoder.createSource(this.getContentResolver(), filePath);
                Bitmap bitmap = null;

                try {
                    bitmap = ImageDecoder.decodeBitmap(source);
                    img_new_picture.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }else{
            Log.e("onActivityResult", "error");
        }
    }

    private void uploadImage(){
        if(filePath != null){

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Saving picture...");
            progressDialog.show();

            final StorageReference reference = mStorageRef.child("gallery/" + UUID.randomUUID().toString());
            reference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    registrarNuevaImagen(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getBytesTransferred());
                            progressDialog.setMessage("uploaded " + (int)progress + "%");
                        }
                    });
        }
    }

    private void registrarNuevaImagen(String url_img) {
        String id_user = mAuth.getCurrentUser().getUid();
        String id_image = mDatabase.push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("url", url_img);
        mDatabase.child("Users").child(id_user).child("pictures").child(id_image).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if (task2.isSuccessful()){
                    Toast.makeText(UxCamara.this, "Picture unploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
