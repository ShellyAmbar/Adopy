package com.example.adopy.EnterPages;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.adopy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private ImageView image;
    private Button btn_reset;
    private EditText InputEmail;

    private Button back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth=FirebaseAuth.getInstance();

        image=findViewById(R.id.imageView);
        btn_reset=findViewById(R.id.reset_pass_button);
        InputEmail=findViewById(R.id.reset_email);

        btn_reset.setOnClickListener(this);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){


            case R.id.reset_pass_button:
                resetPassword(InputEmail.getText().toString());
                break;
            case R.id.back_button:
                finish();
                break;

        }
    }

    private void resetPassword(final String Email) {

        mAuth.sendPasswordResetEmail(Email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(ResetPassword.this, "We sent a new password to your mail:" + Email, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(ResetPassword.this, "An error occurred while we tried to send you the new password. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
