package com.example.adopy.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.example.adopy.Adapters.PetsAdapter;
import com.example.adopy.Interfaces.ItemTouchHelperConnector;
import com.example.adopy.Models.PetModel;
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


public class MyPostsFragment extends Fragment implements ItemTouchHelperConnector {

    private RecyclerView recycler_my_post;
    private PetsAdapter petsAdapter;
    private List<PetModel> petModelArrayList;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private final static int SELECT_IMAGE = 100;
    private static final int OPEN_CAMERA = 200;
    private ImageView image;
    private Date petBirthday;
    private Bitmap bitmap;
    private Boolean isImmunized;
    private StorageTask uploadTask;
    private Uri ImageUri;
    private String namePet;
    private File file;
    private StorageReference storageReference;
    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback;
    private final int IMAGE_CAPTURE=1;


    private FirebaseFirestore FB;
    private String url;

    public MyPostsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_posts, container, false);

        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),2);
        petModelArrayList = new ArrayList<>();
        petsAdapter = new PetsAdapter(petModelArrayList, getContext());
        fab = view.findViewById(R.id.fab);
        mAuth = FirebaseAuth.getInstance();
        FB = FirebaseFirestore.getInstance();
        bitmap = null;
        url = "";
        petBirthday = Calendar.getInstance().getTime();
        storageReference = FirebaseStorage.getInstance().getReference();
        recycler_my_post = view.findViewById(R.id.recycler_home);
        recycler_my_post.setHasFixedSize(true);
        recycler_my_post.setAdapter(petsAdapter);
        recycler_my_post.setLayoutManager(linearLayoutManager);

        ItemTouchHelper.Callback callback = new MyItemTouchHelper(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        petsAdapter.setmItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recycler_my_post);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddDialog();
            }
        });

        GetAllPetsToDashbord();
        return view;
    }



    @Override
    public void onItemMove(int lastPosition, int nextPosition) {
        PetModel petModel = petModelArrayList.get(lastPosition);
        petModelArrayList.remove(petModel);
        petModelArrayList.add(nextPosition,petModel);
        petsAdapter.notifyItemMoved(lastPosition,nextPosition);
    }

    @Override
    public void onItemSwiped(int position) {

        PetModel petModel = petModelArrayList.get(position);
        PopUpWindow(petModel,position);
    }


    private void PopUpWindow(final PetModel petModel, final Integer index) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(),R.style.AlertTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_alert, null);
        dialogBuilder.setView(dialogView);
        TextView Title = dialogView.findViewById(R.id.title);
        TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        TextView btn_ok = dialogView.findViewById(R.id.btn_ok);
        Title.setGravity(Gravity.CENTER);
        Title.setText(getResources().getString(R.string.title_delete)+" "+ petModel.getName()+"?");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                petsAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  petModelArrayList.remove(petModel);
              //  petsAdapter.notifyItemRemoved(index);

                RemoveModelFromList(petModel);
                alertDialog.dismiss();
            }
        });


    }

    private void RemoveModelFromList(final PetModel petModel) {
        FirebaseDatabase.getInstance().getReference("Pets").child(petModel.getPostId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("User_Posts").child(petModel.getPostId()).removeValue();


            }
        });


    }

    private void AddDialog(){
        url="";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(),R.style.AlertTheme).setCancelable(true);
        final LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_add_pet, null);
        dialogBuilder.setView(dialogView);
        TextView Title = dialogView.findViewById(R.id.title);
        final TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        TextView btn_ok = dialogView.findViewById(R.id.btn_ok);
        image = dialogView.findViewById(R.id.image);
        final AutoCompleteTextView name = dialogView.findViewById(R.id.name);
        RadioGroup radio_group = dialogView.findViewById(R.id.radio_group);
        final DatePicker age_picker = dialogView.findViewById(R.id.age_picker);
        final AutoCompleteTextView price = dialogView.findViewById(R.id.price);
        final AutoCompleteTextView about = dialogView.findViewById(R.id.about);
        final AutoCompleteTextView location = dialogView.findViewById(R.id.location);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            age_picker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    petBirthday  = new Date(year,monthOfYear,dayOfMonth);
                }
            });
        }


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

        isImmunized = false;
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == 0){
                    isImmunized = true;
                }else if(checkedId == 1){
                    isImmunized = false;
                }
            }
        });
        Title.setText(getResources().getString(R.string.title_post));
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

                user = mAuth.getCurrentUser();


                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy");
                String currentDateandTime = sdf.format(new Date());
                Date currentDate= Calendar.getInstance().getTime();
                long ageOfPet = currentDate.getTime() - petBirthday.getTime();
                long seconds = ageOfPet / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                long years = (days>=360) ? days/360 : 0;
                long months = years >0 ? ((days%360)>= 30 ? (days%360) /30 : 0 ): ((days)>= 30? days/30 : 0 );
                long leftDays = years > 0 ? (months >0?((days%360)%30) : days%360) : months > 0?(days%30) : days;



                namePet = name.getText().toString();


                String Uid=user.getUid();
                String postKey = System.currentTimeMillis()+"";
                HashMap<String,Object> hashMapUsr=new HashMap<>();
                hashMapUsr.put("Name",namePet);
                hashMapUsr.put("Photo",url);
                if(about.getText().equals(""))
                {
                    hashMapUsr.put("Info",(getResources().getString(R.string.adopt_me_now_and_save_me_from_the_street)));
                }
                else{
                    hashMapUsr.put("Info",about.getText().toString());
                }
                hashMapUsr.put("Date",currentDateandTime);
                hashMapUsr.put("Location",location.getText().toString());
                hashMapUsr.put("Immunized",isImmunized);
                hashMapUsr.put("Age",years +" years"+ months +" months"+ leftDays+" days");
                hashMapUsr.put("Price",price.getText().toString());
                hashMapUsr.put("PostOwnerId",FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMapUsr.put("PostId",postKey);

                FirebaseDatabase.getInstance().getReference("Pets").child(postKey).setValue(hashMapUsr).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),getResources().getString(R.string.added),Toast.LENGTH_SHORT).show();
                        url="";


                    }
                });

                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("User_Posts").child(postKey).setValue(hashMapUsr).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),getResources().getString(R.string.added),Toast.LENGTH_SHORT).show();


                    }
                });



                alertDialog.dismiss();
            }
        });

    }

    private void GetAllPetsToDashbord(){

       DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("User_Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                petModelArrayList.clear();
                for(DataSnapshot snapshot :  dataSnapshot.getChildren()){
                    PetModel petModel = snapshot.getValue(PetModel.class);
                    petModelArrayList.add(petModel);
                }

                petsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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


            storageReference = FirebaseStorage.getInstance().getReference("Pets")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "." + namePet);

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
