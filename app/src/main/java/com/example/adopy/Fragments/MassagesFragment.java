package com.example.adopy.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.adopy.Adapters.MassagesListAdapter;
import com.example.adopy.ChatActivity;
import com.example.adopy.Interfaces.CardListener;
import com.example.adopy.Interfaces.ItemTouchHelperConnector;
import com.example.adopy.Models.MassageListModel;
import com.example.adopy.Models.PetModel;
import com.example.adopy.MyItemTouchHelper;
import com.example.adopy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;


public class MassagesFragment extends Fragment implements CardListener, ItemTouchHelperConnector {

    private List<MassageListModel>  massageListModels;
    private MassagesListAdapter massagesListAdapter;



    public MassagesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_massages, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_massages);
        massageListModels = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        massagesListAdapter = new MassagesListAdapter(massageListModels,this,getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(massagesListAdapter);
        recyclerView.setHasFixedSize(true);
        SetAllMassagesInRecycle();



        ItemTouchHelper.Callback callback = new MyItemTouchHelper(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        massagesListAdapter.setmItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void SetAllMassagesInRecycle() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MassagesListPage").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                massageListModels.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MassageListModel model = snapshot.getValue(MassageListModel.class);
                    massageListModels.add(model);
                }
                massagesListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ///read from database
    }


    @Override
    public void onCardClicked(int position, View view) {

        //open chat
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("user_id", massageListModels.get(position).getUserId());
        intent.putExtra("photoUrl",massageListModels.get(position).getUrl());
        intent.putExtra("name",massageListModels.get(position).getName());
        startActivity(intent);


    }

    @Override
    public void onCardLongClicked(int position, View view) {

        //show quick massage

    }



    @Override
    public void onItemMove(int lastPosition, int nextPosition) {
        MassageListModel model = massageListModels.get(lastPosition);
        massageListModels.remove(model);
        massageListModels.add(nextPosition,model);
        massagesListAdapter.notifyItemMoved(lastPosition,nextPosition);
    }

    @Override
    public void onItemSwiped(int position) {
        MassageListModel model = massageListModels.get(position);
        PopUpWindow(model,position);
    }

    private void PopUpWindow(final MassageListModel massageListModel, final Integer index) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(),R.style.AlertTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_alert, null);
        dialogBuilder.setView(dialogView);
        TextView Title = dialogView.findViewById(R.id.title);
        TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        TextView btn_ok = dialogView.findViewById(R.id.btn_ok);
        Title.setGravity(Gravity.CENTER);
        Title.setText(getResources().getString(R.string.title_delete)+" "+ massageListModel.getName()+"?");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                massagesListAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("MassagesListPage").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(massageListModel.getMassageListId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        massageListModels.remove(massageListModel);
                        massagesListAdapter.notifyItemRemoved(index);
                        Toast.makeText(getContext(),getResources().getString( R.string.removed), Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialog.dismiss();
            }
        });


    }
}
