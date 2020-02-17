package dev.saul1317.uxdribbble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import dev.saul1317.uxdribbble.model.Gallery;

public class DetailPicture extends AppCompatActivity {

    ImageView img_detail;
    TextView txt_picture_name, txt_picture_description;
    String id_picture;


    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_detail_picture);
        img_detail = (ImageView) findViewById(R.id.img_detail);
        txt_picture_name = (TextView) findViewById(R.id.txt_picture_name);
        txt_picture_description = (TextView) findViewById(R.id.txt_picture_description);
        id_picture = getIntent().getExtras().getString("id_picture");
        Log.e("Id_picture", id_picture);
        getDetailPicture(id_picture);
    }

    private void getDetailPicture(String id_picture) {
        String id_user = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(id_user).child("pictures").child(id_picture);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                //String url = dataSnapshot.child("url").getValue(String.class);
                Gallery gallery = dataSnapshot.getValue(Gallery.class);
                updateUI(gallery);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Gallery gallery) {
        if(gallery != null){
            Picasso.get().load(gallery.getUrl()).into(img_detail);
            txt_picture_name.setText(gallery.getPictureName());
            txt_picture_description.setText(gallery.getPictureDescription());
        }else{
            Toast.makeText(this, "SIN IMAGEN", Toast.LENGTH_SHORT).show();
        }
    }


}
