package com.example.adopy.EnterPages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adopy.HomeActivity;
import com.example.adopy.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private AutoCompleteTextView InputEmail;
    private AutoCompleteTextView InputPass;
    private ProgressDialog mDialog;
    private Button signUp;
    private FirebaseAuth mAuth;
    private AutoCompleteTextView UserNameText;
    private JSONObject Person_Data_Object;
    private DatabaseReference UserDataBase;
    private final String API_TOKEN_KEY = "AAAArO5oaF0:APA91bEsiO2oDruU5BnT9EaNrcj0axAgSTpTidEXLcJGzfJsXorx7bqEm0AUqaepmqhhksdhmpYMPmvE7Cnfag5BP7FW0YOn9pEzWIxhnnB1cTnzkBQz1LKPeJCrep5r3fmHp-Uz0TDe";


    FirebaseUser user;

    private LoginButton loginButton;

    private SeekBar seekBar_Age;
    private String UserGender;

    private String UserAge;
    private RadioButton radio_female;
    private RadioButton radio_male;
    private RadioGroup radio_group;
    private RadioButton radioButton;
    //private RadioGroup radio_group_interested;

    private int Progress;
    private TextView progressAge;



    URL Profile_picture;

    CallbackManager mCallbackManager;
    private static String TAG= "FacebookLogin";
    private static final String EMAIL = "email";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        InputEmail=findViewById(R.id.email);
        InputPass=findViewById(R.id.password);
        UserNameText=findViewById(R.id.UserNameText);
        signUp=findViewById(R.id.sign_up_button);
        loginButton=findViewById(R.id.login_button);
        radio_group=findViewById(R.id.radio_group);
        mAuth = FirebaseAuth.getInstance();
        UserGender="Male";
        UserAge="";




        seekBar_Age=findViewById(R.id.seekBar_Age);


        progressAge=findViewById(R.id.progressAge);


        Progress=24;

        seekBar_Age.setMax(100);
        seekBar_Age.setProgress(Progress);

        seekBar_Age.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Progress=progress;
                seekBar_Age.setProgress(Progress);
                progressAge.setText("" + Progress );


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //listeners


        signUp.setOnClickListener(this);


        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        mCallbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                mDialog= new ProgressDialog(SignUpActivity.this);
                mDialog.setMessage("Retrieving Data..");
                mDialog.show();
                String AccessToken = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {


                        Person_Data_Object = object;
                        mDialog.dismiss();
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

                Toast.makeText(SignUpActivity.this,getResources().getString(R.string.canceled),Toast.LENGTH_LONG).show();
                // ...
            }

            @Override
            public void onError(FacebookException error) {

                Toast.makeText(SignUpActivity.this,getResources().getString(R.string.error),Toast.LENGTH_LONG).show();
                // ...
            }
        });


    }

    private void handleFacebookAccessToken(final AccessToken token) {


        mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information



                            user = mAuth.getCurrentUser();

                            String Uid=user.getUid();

                            final ProgressDialog progressDialog=new ProgressDialog(SignUpActivity.this);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();




                            try {
                                Profile_picture = new URL("https://graph.facebook.com/" +  Person_Data_Object.getString("id") + "/picture?width=250&height=250");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            HashMap<String,String> hashMapUsr=new HashMap<>();
                            hashMapUsr.put("UserName", user.getDisplayName() );
                            hashMapUsr.put("Image",Profile_picture.toString());
                            hashMapUsr.put("Email",user.getEmail());
                            hashMapUsr.put("Id",user.getUid() );
                            hashMapUsr.put("UserAge","25");
                            hashMapUsr.put("UserCity","");
                            hashMapUsr.put("UserGender",UserGender);
                            hashMapUsr.put("messaging_token",API_TOKEN_KEY);

                            UserDataBase= FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
                            UserDataBase.setValue(hashMapUsr).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(SignUpActivity.this,"Entered with Success ",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this,HomeActivity.class));

                                }
                            });




                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.failed),
                                    Toast.LENGTH_SHORT).show();

                            user=null;

                        }

                        // ...
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


    }

    public void CheckClick(View view) {
        radio_group=findViewById(R.id.radio_group);
        int radioId = radio_group.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        UserGender=radioButton.getText().toString();



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {




            case R.id.sign_up_button:
                if(InputEmail.getText().toString().equals("")||InputPass.getText().toString().equals("") ){
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.blank),
                            Toast.LENGTH_SHORT).show();

                }else{
                    signupUser( InputEmail.getText().toString(),InputPass.getText().toString());
                }


                break;



        }

    }


    private void signupUser(final String email, final String pass) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    if(pass.length()<6 ) {
                        Toast toast= Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error_incorrect_password),Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    if(!email.contains("@")){
                        Toast toast=  Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error_invalid_password),Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }else{
//if person didnt add gender

                    int radioId = radio_group.getCheckedRadioButtonId();
                    radioButton = findViewById(radioId);

                    UserGender=radioButton.getText().toString();

                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();

                    user = task.getResult().getUser();

                    String Uid=user.getUid();


                    HashMap<String,String> hashMapUsr=new HashMap<>();
                    hashMapUsr.put("PhotoUrl"," ");
                    hashMapUsr.put("Email",user.getEmail());
                    hashMapUsr.put("Id",user.getUid() );
                    hashMapUsr.put("UserName",UserNameText.getText().toString());
                    hashMapUsr.put("UserAge",String.valueOf(Progress));
                    hashMapUsr.put("UserCity","");
                    hashMapUsr.put("UserGender",UserGender);
                    hashMapUsr.put("messaging_token", FirebaseInstanceId.getInstance().getToken());


                    UserDataBase= FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
                    UserDataBase.setValue(hashMapUsr).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(SignUpActivity.this,"Entered with Success ",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this,HomeActivity.class));

                        }
                    });




                }
            }
        });
    }
}
