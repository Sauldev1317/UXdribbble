package dev.saul1317.uxdribbble;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dev.saul1317.uxdribbble.Adapter.AdapterRecyclerviewImgPost;
import dev.saul1317.uxdribbble.Adapter.ImgPostOnItemClickListener;
import dev.saul1317.uxdribbble.model.Gallery;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabPost extends Fragment {


    RecyclerView recyclerView_tab_post;
    AdapterRecyclerviewImgPost adapterRecyclerviewImgPost;
    List<Gallery> url_img;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String id_user;


    public TabPost() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_post, container, false);

        recyclerView_tab_post = (RecyclerView) view.findViewById(R.id.recyclerView_tab_post);
        recyclerView_tab_post.setHasFixedSize(true);
        recyclerView_tab_post.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        url_img = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        id_user = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(id_user).child("pictures");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Gallery gallery = postSnapshot.getValue(Gallery.class);
                    url_img.add(gallery);
                }

                adapterRecyclerviewImgPost = new AdapterRecyclerviewImgPost(R.layout.cardview_img_post, url_img, getContext(), new ImgPostOnItemClickListener() {
                    @Override
                    public void OnImageClickListener(Gallery gallery, View view) {
                        getSelectPictureId(gallery.getUrl(), view);
                    }
                });

                recyclerView_tab_post.setAdapter(adapterRecyclerviewImgPost);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void getSelectPictureId(String url, final View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(id_user).child("pictures");
        mDatabase.orderByChild("url").equalTo(url).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String keys = "";
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    keys=datas.getKey();
                }

                Intent intent = new Intent(getActivity(), DetailPicture.class);
                intent.putExtra("id_picture", keys);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String> (view, "detail_picture");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);
                    startActivity(intent, options.toBundle());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
