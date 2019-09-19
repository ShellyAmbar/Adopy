package com.example.adopy.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adopy.Adapters.GroupListAdapter;
import com.example.adopy.Adapters.PetsAdapter;
import com.example.adopy.Interfaces.ItemTouchHelperConnector;
import com.example.adopy.Models.GroupModel;
import com.example.adopy.Models.PetModel;
import com.example.adopy.Models.UserModel;
import com.example.adopy.MyItemTouchHelper;
import com.example.adopy.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class GroupsFragment extends Fragment implements ItemTouchHelperConnector {

    private RecyclerView recycler_my_post;
    private GroupListAdapter groupListAdapter;
    private List<GroupModel> groupModelList;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private final static int SELECT_IMAGE = 100;
    private static final int OPEN_CAMERA = 200;
    private Date petBirthday;
    private Bitmap bitmap;
    private Boolean isImmunized;
    private StorageTask uploadTask;
    private Uri ImageUri;
    private String namePet;
    private File file;
    private StorageReference storageReference;
    private String url;
    private boolean isGroupValid;
    private String userName = "";
    private ImageView image;


    public GroupsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),2);
        groupModelList = new ArrayList<>();
        groupListAdapter = new GroupListAdapter(groupModelList, getContext());
        fab = view.findViewById(R.id.fab);
        mAuth = FirebaseAuth.getInstance();
        bitmap = null;
        url = "";
        petBirthday = Calendar.getInstance().getTime();
        storageReference = FirebaseStorage.getInstance().getReference();
        recycler_my_post = view.findViewById(R.id.recycler_home);
        recycler_my_post.setHasFixedSize(true);
        recycler_my_post.setAdapter(groupListAdapter);
        recycler_my_post.setLayoutManager(linearLayoutManager);

        ItemTouchHelper.Callback callback = new MyItemTouchHelper(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        groupListAdapter.setmItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recycler_my_post);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddGroupDialog();
            }
        });

        GetAllGroupsToDashbord();

        return view;
    }

    private void GetAllGroupsToDashbord(){

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy");
        String currentDate = sdf.format(new Date());

        GroupModel groupModel1 = new GroupModel("Food","Adopy","Adopy","1",currentDate,0, url);
        GroupModel groupModel2 = new GroupModel("Games","Adopy","Adopy","2",currentDate,0, url);
        GroupModel groupModel3 = new GroupModel("News","Adopy","Adopy","3",currentDate,0, url);
        GroupModel groupModel4 = new GroupModel("Medicine","Adopy","Adopy","4",currentDate,0, url);
        GroupModel groupModel5 = new GroupModel("Entertainment","Adopy","Adopy","5",currentDate,0, url);
        GroupModel groupModel6 = new GroupModel("Mating","Adopy","Adopy","6",currentDate,0, url);


        groupModelList.add(groupModel1);
        groupModelList.add(groupModel2);
        groupModelList.add(groupModel3);
        groupModelList.add(groupModel4);
        groupModelList.add(groupModel5);
        groupModelList.add(groupModel6);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                groupModelList.clear();
                for(DataSnapshot snapshot :  dataSnapshot.getChildren()){
                    GroupModel groupModel = snapshot.getValue(GroupModel.class);
                    groupModelList.add(groupModel);
                }

                groupListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AddGroupDialog() {
        url = "";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(),R.style.AlertTheme).setCancelable(true);
        final LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_add_group, null);
        dialogBuilder.setView(dialogView);
        final TextView titleOfGroup = dialogView.findViewById(R.id.title);
        final TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        TextView btn_ok = dialogView.findViewById(R.id.btn_ok);
        image = dialogView.findViewById(R.id.image);
        final AutoCompleteTextView name = dialogView.findViewById(R.id.name);
        final AutoCompleteTextView about = dialogView.findViewById(R.id.about);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(getContext());
                CharSequence[] options=new CharSequence[]{
                        "Open Gallery",
                        "Open Camera"
                };
                builder.setTitle("Select option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if(which == 0){
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);

                        }
                        else if (which == 1){


                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(getActivity());


                        }
                    }
                });

                builder.create();
                builder.show();

            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name =titleOfGroup.getText().toString();


                isGroupValid  = true;
                FirebaseDatabase.getInstance().getReference("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            GroupModel model = snapshot.getValue(GroupModel.class);
                            if(name.equals(model.getGroupName()))
                            {
                                isGroupValid = false;
                                break;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if(isGroupValid) {

                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            userName = userModel.getUserName();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy");
                    String currentDate = sdf.format(new Date());

                    String postKey = System.currentTimeMillis() + "";
                    HashMap<String, Object> hashMapGroup = new HashMap<>();
                    hashMapGroup.put("GroupName", name);
                    hashMapGroup.put("OwnerId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMapGroup.put("OwnerName", userName);
                    hashMapGroup.put("GroupId", postKey);
                    hashMapGroup.put("GroupCreatedTime", currentDate );
                    hashMapGroup.put("NumOfParticipants", 1);
                    hashMapGroup.put("PhotoUrl", url );


                    FirebaseDatabase.getInstance().getReference("Groups").child(postKey).setValue(hashMapGroup).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), getResources().getString(R.string.added), Toast.LENGTH_SHORT).show();
                            url = "";


                        }
                    });
                }else{

                    Toast.makeText(getContext(), "This name already exist", Toast.LENGTH_SHORT).show();
                }


                alertDialog.dismiss();

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });


    }


    @Override
    public void onItemMove(int lastPosition, int nextPosition) {
        GroupModel groupModel = groupModelList.get(lastPosition);
        groupModelList.remove(groupModel);
        groupModelList.add(nextPosition,groupModel);
        groupListAdapter.notifyItemMoved(lastPosition,nextPosition);
    }

    @Override
    public void onItemSwiped(int position) {

        GroupModel model = groupModelList.get(position);
        PopUpWindow(model,position);
    }

    private void PopUpWindow(final GroupModel model, int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(),R.style.AlertTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_alert, null);
        dialogBuilder.setView(dialogView);
        TextView Title = dialogView.findViewById(R.id.title);
        TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        TextView btn_ok = dialogView.findViewById(R.id.btn_ok);
        Title.setGravity(Gravity.CENTER);
        Title.setText(getResources().getString(R.string.title_delete)+" "+ model.getGroupName()+"?");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                groupListAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  petModelArrayList.remove(petModel);
                //  petsAdapter.notifyItemRemoved(index);
                FirebaseDatabase.getInstance().getReference("Groups").child(model.getGroupId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getContext(),getResources().getString(R.string.removed_group) , Toast.LENGTH_SHORT).show();

                    }
                });


                alertDialog.dismiss();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    CropImage.ActivityResult result = CropImage.getActivityResult(data);

                    SaveImageToDatabase(result.getUri());

                }
            } else if (resultCode == RESULT_CANCELED)  {
                Toast.makeText(getContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == SELECT_IMAGE){
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    SaveImageToDatabase(data.getData());


                } else if (resultCode == RESULT_CANCELED)  {
                    Toast.makeText(getContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED)  {
                Toast.makeText(getContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void SaveImageToDatabase(final Uri data) {

        url = "";

        if (data!=null) {


            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy");
            String currentDate = sdf.format(new Date());

            storageReference = FirebaseStorage.getInstance().getReference("Groups")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "." + currentDate );

            uploadTask = storageReference.putFile(data);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    url= task.getResult().toString();
                    Glide.with(getContext()).load(url)
                            .placeholder(R.drawable.foot).into(image);
                    // image.setImageURI(Uri.fromFile(new File(url)));

                }
            });


        }
    }
}
