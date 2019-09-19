package com.example.adopy;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adopy.Adapters.ChatMassagesAdapter;
import com.example.adopy.Interfaces.ItemTouchHelperConnector;
import com.example.adopy.Models.MassageModel;
import com.example.adopy.Models.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupActivity extends AppCompatActivity implements View.OnClickListener, ItemTouchHelperConnector {

    private ImageView send,mic;
    private CircleImageView photoUser;
    private EditText textMasssage;
    private TextView userName;
    private RecyclerView recyclerView;
    private String massage;
    private String userId;
    private String photoUrl;
    private String name;
    private ChatMassagesAdapter chatMassagesAdapter;
    private List<MassageModel> massageModelList;
    private String group_id = "";
    private String group_name;
    private String group_photo_url;
    private String group_NumOfParticipants;
    private String group_OwnerId;
    private String group_OwnerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        group_id = getIntent().getStringExtra("group_id");
        group_name = getIntent().getStringExtra("group_name");;
        group_photo_url=getIntent().getStringExtra("group_photo_url");
        group_NumOfParticipants=getIntent().getStringExtra("group_NumOfParticipants");;
        group_OwnerId=getIntent().getStringExtra("group_OwnerId");;
        group_OwnerName=getIntent().getStringExtra("group_OwnerName");;
        userName = findViewById(R.id.name);
        send = findViewById(R.id.send);
        mic = findViewById(R.id.microphone);
        textMasssage = findViewById(R.id.text_massage);
        recyclerView = findViewById(R.id.recycler_chat);
        photoUser = findViewById(R.id.photo);
        userId = getIntent().getStringExtra("user_id");
        photoUrl = getIntent().getStringExtra("photoUrl");
        name = getIntent().getStringExtra("name");


        massageModelList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        chatMassagesAdapter = new ChatMassagesAdapter(massageModelList);
        recyclerView.setAdapter(chatMassagesAdapter);

        ItemTouchHelper.Callback callback = new MyItemTouchHelper(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        chatMassagesAdapter.setmItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        userName.setText(name);
        Glide.with(this).load(photoUrl).placeholder(ContextCompat.getDrawable(GroupActivity.this,R.drawable.pet_foot)).into(photoUser);


        send.setOnClickListener(this);
        mic.setOnClickListener(this);
        photoUser.setOnClickListener(this);

        SetAllMassagesInRecycler();

    }

    private void SetAllMassagesInRecycler() {
        FirebaseDatabase.getInstance().getReference("Groups").child(group_id)
                .child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                massageModelList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    MassageModel model = snapshot.getValue(MassageModel.class);
                    massageModelList.add(model);
                }
                chatMassagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.send:
                massage = textMasssage.getText().toString();
                SendMassage(massage);
                break;
            case R.id.microphone:
                break;
            case R.id.photo:
                break;
        }
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
                map.put("ReceiverId",FirebaseAuth.getInstance().getCurrentUser().getUid());

                FirebaseDatabase.getInstance().getReference("Groups").child(group_id)
                        .child("Chat").child(massageId).setValue(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public void onItemMove(int lastPosition, int nextPosition) {

    }

    @Override
    public void onItemSwiped(int position) {
        MassageModel model = massageModelList.get(position);
        PopUpWindow(model,position);
    }

    private void PopUpWindow(final MassageModel massageModel, final Integer index) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GroupActivity.this,R.style.AlertTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_alert, null);
        dialogBuilder.setView(dialogView);
        TextView Title = dialogView.findViewById(R.id.title);
        TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        TextView btn_ok = dialogView.findViewById(R.id.btn_ok);
        Title.setGravity(Gravity.CENTER);
        Title.setText(getResources().getString(R.string.title_delete)+"?");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chatMassagesAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(massageModel.getWriterId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    FirebaseDatabase.getInstance().getReference("Groups").child(group_id)
                        .child("Chat").child(massageModel.getMassageId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                        }
                    });
                }


                alertDialog.dismiss();
            }
        });


    }
}

