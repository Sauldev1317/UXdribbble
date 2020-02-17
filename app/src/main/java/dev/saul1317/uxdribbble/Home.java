package dev.saul1317.uxdribbble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.saul1317.uxdribbble.Adapter.PageAdapter;
import dev.saul1317.uxdribbble.model.Gallery;
import dev.saul1317.uxdribbble.model.User;

public class Home extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HOME";

    TextView txt_userName;
    TabLayout tablayout_home;
    TabItem tab_post, tab_board;
    ViewPager viewPager_home;
    Toolbar toolbar;
    CircleImageView profile_image;
    BottomNavigationView bottomNavigationView_home;
    PageAdapter pageAdapter;

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializedView();
        initializeFirebase();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(mAuth.getCurrentUser() != null){
            FirebaseUser user = mAuth.getCurrentUser();
            if(user != null){
                getUserData(user.getUid());
            }
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initializedView() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        txt_userName = (TextView) findViewById(R.id.txt_userName);

        tablayout_home = (TabLayout) findViewById(R.id.tablayout_home);
        tab_post = (TabItem) findViewById(R.id.tab_post);
        tab_board = (TabItem) findViewById(R.id.tab_board);
        viewPager_home = (ViewPager) findViewById(R.id.viewPager_home);

        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);

        bottomNavigationView_home = (BottomNavigationView) findViewById(R.id.bottomNavigationView_home);
        bottomNavigationView_home.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.add_picture:
                        startActivity(new Intent(Home.this, UxCamara.class));
                        break;
                }
                return false;
            }
        });

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tablayout_home.getTabCount());
        viewPager_home.setAdapter(pageAdapter);

        tablayout_home.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager_home.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0){
                    pageAdapter.notifyDataSetChanged();
                }else if(tab.getPosition() == 0){
                    pageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager_home.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout_home));
    }

    private void getUserData(String userKey) {
        mDatabase.child("Users").child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String userID = dataSnapshot.child("user_id").getValue(String.class);
                User user = dataSnapshot.getValue(User.class);
                updateUI(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void updateUI(User user) {
        if(user != null){
            txt_userName.setText(user.getName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ux_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:
                mAuth.signOut();
                startActivity(new Intent(Home.this, Login.class));
                finish();
                break;
            case R.id.action_profile:
                Toast.makeText(this, "Mi cuenta", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.profile_image:
                Intent intent = new Intent(this, ProfilePicture.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String> (profile_image, "profile_picture");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Home.this, pairs);
                    startActivity(intent, options.toBundle());
                }
                break;
            default:
                break;
        }
    }
}
