package com.example.adopy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adopy.Models.PetModel;
import com.example.adopy.Models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetPageActivity extends AppCompatActivity {
    private PetModel petModel;
    private String  ownerUrl;
    private  String ownerName;
    private  String mineUrl;
    private String mineName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_page);
        TextView Info = findViewById(R.id.Info);
        TextView Date = findViewById(R.id.Date);
        TextView Location = findViewById(R.id.Location);
        TextView Immunized = findViewById(R.id.Immunized);
        TextView Age = findViewById(R.id.Age);
        TextView Price = findViewById(R.id.Price);
        TextView title = findViewById(R.id.title);
        ImageView image = findViewById(R.id.image);
        FloatingActionButton fab = findViewById(R.id.fab);
        EditText text_massage = findViewById(R.id.text_massage);

        petModel = (PetModel) getIntent().getExtras().getSerializable("model");
        if(!petModel.getPhoto().isEmpty()){
            Glide.with(PetPageActivity.this).load(petModel.getPhoto()).placeholder(R.drawable.foot).into(image);
        }
        title.setText(petModel.getName());
        Price.setText(petModel.getPrice());
        Age.setText(petModel.getAge());
        Immunized.setText(petModel.getImmunized() +"");
        Location.setText(petModel.getLocation());
        Date.setText(petModel.getDate());
        Info.setText(petModel.getInfo());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopUpWindow();
            }
        });
    }


    private void PopUpWindow() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.AlertTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_massage, null);
        dialogBuilder.setView(dialogView);
        final AutoCompleteTextView text_title =dialogView.findViewById(R.id.text_title);
        final AutoCompleteTextView text_massage =dialogView.findViewById(R.id.text_massage);
        TextView Title = dialogView.findViewById(R.id.title);
        TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        TextView btn_ok = dialogView.findViewById(R.id.btn_ok);
        Title.setGravity(Gravity.CENTER);
        Title.setText(getResources().getString(R.string.send_massage));
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textMassage = text_massage.getText().toString();
                String textTitle = text_title.getText().toString();

                Toast.makeText(PetPageActivity.this,   "Your massage was sent successfully.", Toast.LENGTH_SHORT).show();


                String OwnerName;
                String OwnerUrl;

                FirebaseDatabase.getInstance().getReference("Users").child(petModel.getPostOwnerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class) ;
                        ownerUrl = userModel.getPhotoUrl();
                        ownerName = userModel.getUserName();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class) ;
                        mineUrl = userModel.getPhotoUrl();
                        mineName = userModel.getUserName();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // open brodcast
                SendMassage(textMassage);
                String massageId = System.currentTimeMillis()+"";
                //owner details
                Map<String,Object> map1 = new HashMap<>();
                map1.put("Url",ownerUrl);
                map1.put("name",ownerName);
                map1.put("MassageTitle",textTitle);
                map1.put("MassageListId",massageId);
                map1.put("UserId",petModel.getPostOwnerId());

                //mine details
                Map<String,Object> map2 = new HashMap<>();
                map2.put("Url",mineUrl);
                map2.put("name",mineName);
                map2.put("MassageTitle",textTitle);
                map2.put("MassageListId",massageId);
                map2.put("UserId",FirebaseAuth.getInstance().getCurrentUser().getUid());


                FirebaseDatabase.getInstance().getReference("MassagesListPage").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(massageId).setValue(map1);
                FirebaseDatabase.getInstance().getReference("MassagesListPage").child(petModel.getPostOwnerId()).child(massageId).setValue(map2);
                alertDialog.dismiss();

            }
        });



    }

    private void SendMassage(final String massage) {

        final Map<String,Object> map = new HashMap<>();
        final String massageId = System.currentTimeMillis()+"";

        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                map.put("MassageText",massage);
                map.put("UserName",userModel.getUserName());
                map.put("MassageId",massageId);
                map.put("WriterId",FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.put("ReceiverId",petModel.getPostOwnerId());

                FirebaseDatabase.getInstance().getReference("Chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(petModel.getPostOwnerId()).child(massageId).setValue(map);

                FirebaseDatabase.getInstance().getReference("Chats").child(petModel.getPostOwnerId())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(massageId).setValue(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}
