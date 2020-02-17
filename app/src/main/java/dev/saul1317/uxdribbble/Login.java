package dev.saul1317.uxdribbble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import dev.saul1317.uxdribbble.Animations.RevealAnimation;
import dev.saul1317.uxdribbble.Animations.RevealAnimationOnListener;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Login";
    private static final int GOOGLE_SIGN = 777;

    //VIEWS
    TextView txt_login, text_btn_sign_in;
    TextInputLayout textInputLayout_userEmail, textInputLayout_userPassword;
    TextInputEditText textInputEditText_userEmail, textInputEditText_userPassword;
    FrameLayout button_sing_in;
    ProgressBar progress_bar_btn_sign_in;
    View reveal_login;
    LinearLayout linearLayout_sign_up;
    ImageButton imageButton_sign_in_facebook, imageButton_sign_in_google;

    //ANIMATIONS
    RevealAnimation revealAnimation;

    //FIREBASE
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    //GOOGLE

    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeView();
        initializeFirebase();
        initializeGoogleSignIn();
    }

    private void initializeView() {

        txt_login = (TextView) findViewById(R.id.txt_login);

        textInputLayout_userEmail = (TextInputLayout) findViewById(R.id.textInputLayout_userEmail);
        textInputLayout_userPassword = (TextInputLayout) findViewById(R.id.textInputLayout_userPassword);

        textInputEditText_userEmail = (TextInputEditText) findViewById(R.id.textInputEditText_userEmail);
        textInputEditText_userPassword = (TextInputEditText) findViewById(R.id.textInputEditText_userPassword);

        linearLayout_sign_up = (LinearLayout) findViewById(R.id.linearLayout_sign_up);
        linearLayout_sign_up.setOnClickListener(this);

        imageButton_sign_in_facebook = (ImageButton) findViewById(R.id.imageButton_sign_in_facebook);
        imageButton_sign_in_facebook.setOnClickListener(this);

        imageButton_sign_in_google = (ImageButton) findViewById(R.id.imageButton_sign_in_google);
        imageButton_sign_in_google.setOnClickListener(this);

        button_sing_in = (FrameLayout) findViewById(R.id.button_sing_in);
        button_sing_in.setOnClickListener(this);
        progress_bar_btn_sign_in = (ProgressBar) findViewById(R.id.progress_bar_btn_sign_in);
        text_btn_sign_in = (TextView) findViewById(R.id.text_btn_sign_in);

        reveal_login = (View) findViewById(R.id.reveal_login);
        revealAnimation = new RevealAnimation(this, button_sing_in, progress_bar_btn_sign_in ,text_btn_sign_in, reveal_login);
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void initializeGoogleSignIn() {
        // Configure Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            Log.e(TAG, "onAuthStateChanged - Logueado");
            openActivityHome();
            finish();
        } else {
            Log.e(TAG, "Sesión cerrada");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_sing_in:
                revealAnimation.transformFabButton();
                String email = textInputEditText_userEmail.getText().toString();
                String password = textInputEditText_userPassword.getText().toString();

                if(validateUserData(email, password)){
                    signWithEmailAndPassword(email, password);
                }else{
                    revealAnimation.buttonResetRevealAnimation();
                }
                break;

            case R.id.linearLayout_sign_up:
                openActivityUserRegister();
                break;

            case R.id.imageButton_sign_in_google:
                signInGoogle();
                break;

            default:
                break;
        }
    }

    private boolean validateUserData(String email, String password) {
        if (email.isEmpty()){
            textInputEditText_userEmail.setError(getResources().getString(R.string.request));
            textInputEditText_userEmail.requestFocus();
            return false;
        }else if(password.isEmpty()){
            textInputEditText_userPassword.setError(getResources().getString(R.string.request));
            textInputEditText_userPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void signWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.toString());
                revealAnimation.buttonResetRevealAnimation();
            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    authenticationSuccessful();
                }else{
                    Toast.makeText(Login.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, task.getException().toString());
                    revealAnimation.buttonResetRevealAnimation();
                }
            }
        });
    }

    //GOOGLE AUTHENTICATION

    private void signInGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null){
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            registrarUsuario(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registrarUsuario(FirebaseUser user) {
        String id = mAuth.getCurrentUser().getUid();
        Map<String, Object> map = new HashMap<>();
        map.put("email", user.getEmail());
        map.put("name", user.getDisplayName());
        map.put("phone", user.getPhoneNumber());
        mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if (task2.isSuccessful()){
                    authenticationSuccessful();
                }
            }
        });
    }

    private void authenticationSuccessful() {
        revealAnimation.revealButton(new RevealAnimationOnListener() {
            @Override
            public void revealAnimationOnListerner() {
                openActivityHome();
                finish();
            }
        });
    }

    private void openActivityHome(){
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        overridePendingTransition( 0, R.anim.fade_out_animation);
    }

    private void openActivityUserRegister() {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
        overridePendingTransition( 0, R.anim.fade_out_animation);
    }

    @Override
    protected void onStop() {
        super.onStop();
        revealAnimation.buttonResetRevealAnimation();
    }

}
