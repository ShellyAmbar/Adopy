package com.example.adopy.Adapters;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.adopy.HomeActivity;
import com.example.adopy.Interfaces.CardListener;
import com.example.adopy.Interfaces.ItemTouchHelperConnector;
import com.example.adopy.Models.PetModel;
import com.example.adopy.PetPageActivity;
import com.example.adopy.R;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.ViewHolder>  {


    List<PetModel> petModelList;
    private Context context;
    private ItemTouchHelper mItemTouchHelper;

    public void setmItemTouchHelper(ItemTouchHelper mItemTouchHelper) {
        this.mItemTouchHelper = mItemTouchHelper;
    }


    public PetsAdapter(List<PetModel> petModelList, Context context) {
        this.petModelList = petModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_pet,viewGroup,false);
        return new PetsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        PetModel petModel = petModelList.get(i);
        if(!petModel.getPhoto().isEmpty()){
            Glide.with(context).load(petModel.getPhoto()).placeholder(R.drawable.foot).into(viewHolder.petImage);
        }

        viewHolder.petName.setText(petModel.getName());
        viewHolder.publish_Date.setText(petModel.getDate());


    }

    @Override
    public int getItemCount() {
        return petModelList.size();
    }

    public void setList(List<PetModel> petModelArrayList) {
        petModelList = petModelArrayList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnTouchListener, GestureDetector.OnGestureListener
    {
        private TextView petName;
        private CircleImageView petImage;
        private TextView publish_Date;
        private TextView pet_price;
        private GestureDetector mGestureDetector;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            petImage = itemView.findViewById(R.id.pet_image);
            petName = itemView.findViewById(R.id.pet_name);
            publish_Date = itemView.findViewById(R.id.publish_date);
            mGestureDetector = new GestureDetector(itemView.getContext(),this);

            itemView.setOnTouchListener(this);

        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Intent intent = new Intent(context, PetPageActivity.class);
            intent.putExtra("model",(Serializable)petModelList.get(getAdapterPosition()));

            context.startActivity(intent);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

            mItemTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }



        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mGestureDetector.onTouchEvent(event);
            return true;
        }
    }

}
