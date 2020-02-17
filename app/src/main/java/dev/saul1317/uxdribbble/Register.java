package dev.saul1317.uxdribbble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import dev.saul1317.uxdribbble.Animations.RevealAnimation;
import dev.saul1317.uxdribbble.Animations.RevealAnimationOnListener;
import dev.saul1317.uxdribbble.model.User;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Register";
    TextView txt_register, text_btn_sign_up;
    TextInputLayout textInputLayout_userEmail_register, textInputLayout_userPassword_register,
            textInputLayout_userName_register, textInputLayout_userPhone_register;
    TextInputEditText textInputEditText_userEmail_register, textInputEditText_userPassword_register,
            textInputEditText_userName_register, textInputEditText_userPhone_register;
    FrameLayout button_sing_up;
    ProgressBar progress_bar_btn_sign_up;
    LinearLayout linearLayout_sign_in;
    View reveal_register;

    //FIREBASE
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    RevealAnimation revealAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loadViews();
    }

    private void loadViews() {

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        txt_register = (TextView) findViewById(R.id.txt_register);

        textInputLayout_userEmail_register = (TextInputLayout) findViewById(R.id.textInputLayout_userEmail_register);
        textInputLayout_userPassword_register = (TextInputLayout) findViewById(R.id.textInputLayout_userPassword_register);
        textInputLayout_userName_register = (TextInputLayout) findViewById(R.id.textInputLayout_userName_register);
        textInputLayout_userPhone_register = (TextInputLayout) findViewById(R.id.textInputLayout_userPhone_register);

        textInputEditText_userEmail_register = (TextInputEditText) findViewById(R.id.textInputEditText_userEmail_register);
        textInputEditText_userPassword_register = (TextInputEditText) findViewById(R.id.textInputEditText_userPassword_register);
        textInputEditText_userName_register = (TextInputEditText) findViewById(R.id.textInputEditText_userName_register);
        textInputEditText_userPhone_register = (TextInputEditText) findViewById(R.id.textInputEditText_userPhone_register);

        progress_bar_btn_sign_up = (ProgressBar) findViewById(R.id.progress_bar_btn_sign_up);
        text_btn_sign_up = (TextView) findViewById(R.id.text_btn_sign_up);

        button_sing_up = (FrameLayout) findViewById(R.id.button_sing_up);
        button_sing_up.setOnClickListener(this);

        linearLayout_sign_in = (LinearLayout) findViewById(R.id.linearLayout_sign_in);
        linearLayout_sign_in.setOnClickListener(this);

        reveal_register = (View) findViewById(R.id.reveal_register);
        revealAnimation = new RevealAnimation(this, button_sing_up, progress_bar_btn_sign_up, text_btn_sign_up, reveal_register);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_sing_up:
                revealAnimation.transformFabButton();
                String userEmail = textInputEditText_userEmail_register.getText().toString();
                String userPassword = textInputEditText_userPassword_register.getText().toString();
                String userPhone = textInputEditText_userPhone_register.getText().toString();
                String userName = textInputEditText_userName_register.getText().toString();

                User user = new User();
                user.setEmail(userEmail);
                user.setPassword(userPassword);
                user.setPhone(userPhone);
                user.setName(userName);

                if(validateRegister(user)){
                    registrarUsuario(user);
                }else{
                    revealAnimation.buttonResetRevealAnimation();
                }

                break;

            case R.id.linearLayout_sign_in:
                openActivityUserRegister();
                break;

            default:
                break;
        }
    }

    private void registrarUsuario(final User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String id = mAuth.getCurrentUser().getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("email", user.getEmail());
                    map.put("password", user.getPassword());
                    map.put("name", user.getName());
                    map.put("phone", user.getPhone());
                    mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()){
                                revealAnimation.revealButton(new RevealAnimationOnListener() {
                                    @Override
                                    public void revealAnimationOnListerner() {
                                        startActivity(new Intent(Register.this, Home.class));
                                        finish();
                                    }
                                });
                            }
                        }
                    });

                }else{
                    Toast.makeText(Register.this, "No se a registrano ni madres", Toast.LENGTH_SHORT).show();
                    revealAnimation.buttonResetRevealAnimation();
                }
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        revealAnimation.buttonResetRevealAnimation();
    }

    private boolean validateRegister(User user) {
        if(user.getEmail().isEmpty()){
            textInputEditText_userEmail_register.setError(getResources().getString(R.string.request));
            textInputEditText_userEmail_register.requestFocus();
            return false;

        }else if(user.getPassword().isEmpty()){
            textInputEditText_userPassword_register.setError(getResources().getString(R.string.request));
            textInputEditText_userPassword_register.requestFocus();
            return false;

        }else if(user.getName().isEmpty()){
            textInputEditText_userName_register.setError(getResources().getString(R.string.request));
            textInputEditText_userName_register.requestFocus();
            return false;

        }else if(user.getPhone().isEmpty()){
            textInputEditText_userPhone_register.setError(getResources().getString(R.string.request));
            textInputEditText_userPhone_register.requestFocus();
            return false;

        }else if(user.getPhone().length() < 10){
            textInputEditText_userPhone_register.setError(getResources().getString(R.string.incomplete_phone));
            textInputEditText_userPhone_register.requestFocus();
            return false;
        }
        return true;
    }

    private void openActivityUserRegister(){
        /*Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        overridePendingTransition( 0, R.anim.fade_out_animation);*/
        onBackPressed();
    }
}
