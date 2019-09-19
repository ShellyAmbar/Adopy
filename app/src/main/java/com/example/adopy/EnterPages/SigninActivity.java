package com.example.adopy.EnterPages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.cardemulation.HostNfcFService;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adopy.HomeActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;

import com.example.adopy.R;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    private AutoCompleteTextView InputEmail;
    private AutoCompleteTextView InputPass;
    private CircleImageView circleImageView;
    private boolean ENTER_SIGN=false;
    private TextView btn_forgot_pass;
    private TextView btn_signup;
    Button mEmailSignInButton;
    FirebaseUser user;
    private LoginButton loginButton;
    private SignInButton signInButton_google;

    CallbackManager mCallbackManager;

    GoogleApiClient googleApiClient;
    private final static int RC_SIGN_IN=9001;
    private static String TAG= "FacebookLogin";
    private static final String EMAIL = "email";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        loginButton = findViewById(R.id.login_button);
        InputEmail = (AutoCompleteTextView) findViewById(R.id.email);
        InputPass = (AutoCompleteTextView) findViewById(R.id.password);


        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        btn_forgot_pass=findViewById(R.id.btn_forgot_password);
        btn_signup=findViewById(R.id.btn_layout_signup);
        loginButton=findViewById(R.id.login_button);





        mAuth = FirebaseAuth.getInstance();
        //set permissions

        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        mCallbackManager = CallbackManager.Factory.create();
        AccessToken accessToken=AccessToken.getCurrentAccessToken();
        if (mAuth != null || accessToken!=null) {

            mAuth.signOut();
            LoginManager.getInstance().logOut();

        }



        mEmailSignInButton.setOnClickListener(this);

        btn_forgot_pass.setOnClickListener(this);

        btn_signup.setOnClickListener(this);





        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                mDialog= new ProgressDialog(SigninActivity.this);
                mDialog.setMessage(getResources().getString(R.string.load_data));
                mDialog.show();
                String AccessToken = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {



                    }
                });

                Bundle Parameters = new Bundle();
                Parameters.putString("fields","email");
                request.setParameters(Parameters);
                request.executeAsync();



                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

                Toast.makeText(SigninActivity.this,getResources().getString( R.string.canceled),Toast.LENGTH_LONG).show();
                // ...
            }

            @Override
            public void onError(FacebookException error) {

                Toast.makeText(SigninActivity.this,getResources().getString(R.string.error),Toast.LENGTH_LONG).show();
                // ...
            }
        });




    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            mDialog.dismiss();
                            Toast.makeText(SigninActivity.this,"Entered with Success ",Toast.LENGTH_SHORT).show();
                            updateUI(user);
                            Intent intent=new Intent(SigninActivity.this, HomeActivity.class);
                            intent.putExtra("publisherId", mAuth.getCurrentUser().getUid());
                            startActivity(intent);








                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(SigninActivity.this,getResources().getString( R.string.failed),
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"facebook login "+task.getException());
                            user=null;
                            updateUI(user);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        AccessToken accessToken=AccessToken.getCurrentAccessToken();
        if (mAuth != null || accessToken!=null) {

            mAuth.signOut();
            LoginManager.getInstance().logOut();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


    }


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser== null){
            ENTER_SIGN=false;
        }else{
            // getData(currentUser);
            ENTER_SIGN=true;
            //entering to app
            //startActivity(new Intent(LoginActivity.this,MainActivity.class));


        }

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()){

            case R.id.btn_forgot_password:

                startActivity(new Intent(SigninActivity.this, ResetPassword.class));

                break;

            case R.id.btn_layout_signup:

                Intent intent=new Intent(SigninActivity.this,SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                break;

            case R.id.email_sign_in_button:

                if( InputEmail.getText().toString().equals("") || InputPass.getText().toString().equals("")){
                    Toast.makeText(SigninActivity.this, getResources().getString(R.string.blank),
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }else{
                    loginUser(InputEmail.getText().toString(), InputPass.getText().toString());

                }
                break;



        }

    }


    private void loginUser(final String Email, final String Pass) {
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(Email, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            Toast.makeText(SigninActivity.this, getResources().getString(R.string.success),Toast.LENGTH_SHORT).show();
                            updateUI(user);
                            Intent intent=new Intent(SigninActivity.this,HomeActivity.class);
                            intent.putExtra("publisherId", mAuth.getCurrentUser().getUid());
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            // updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.

                            if(Pass.length()<6 ) {
                                Toast toast= Toast.makeText(SigninActivity.this, getResources().getString(R.string.error_incorrect_password),Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            if(!Email.contains("@")){
                                Toast toast=  Toast.makeText(SigninActivity.this, getResources().getString(R.string.error_invalid_password),Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            Toast.makeText(SigninActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }
}
