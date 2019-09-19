package com.example.adopy.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.adopy.ChatActivity;
import com.example.adopy.GroupActivity;
import com.example.adopy.Interfaces.CardListener;
import com.example.adopy.Models.GroupModel;
import com.example.adopy.Models.MassageListModel;
import com.example.adopy.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder>  {

    private List<GroupModel> GroupsListModels;
    private Context context;
    private ItemTouchHelper mItemTouchHelper;

    public void setmItemTouchHelper(ItemTouchHelper mItemTouchHelper) {
        this.mItemTouchHelper = mItemTouchHelper;
    }

    public GroupListAdapter(List<GroupModel> groupsListModels, Context context) {
        this.GroupsListModels = groupsListModels;

        this.context = context;
    }



    @NonNull
    @Override
    public GroupListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group,viewGroup,false);
        return new GroupListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        GroupModel model = GroupsListModels.get(i);
        viewHolder.groupName.setText(model.getGroupName());
        Glide.with(context).load(model.getPhotoUrl()).into(viewHolder.image);




    }

    @Override
    public int getItemCount() {
        return GroupsListModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnTouchListener, GestureDetector.OnGestureListener
    {
        private GestureDetector mGestureDetector;
        private TextView  groupName;
        private CircleImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            groupName = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);


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

            Intent intent = new Intent(context, GroupActivity.class);
            intent.putExtra("group_name",GroupsListModels.get(getAdapterPosition()).getGroupName() );
            intent.putExtra("group_photo_url",GroupsListModels.get(getAdapterPosition()).getPhotoUrl());
            intent.putExtra("group_id",GroupsListModels.get(getAdapterPosition()).getGroupId());
            intent.putExtra("group_NumOfParticipants",GroupsListModels.get(getAdapterPosition()).getNumOfParticipants());
            intent.putExtra("group_OwnerId",GroupsListModels.get(getAdapterPosition()).getOwnerId());
            intent.putExtra("group_OwnerName",GroupsListModels.get(getAdapterPosition()).getOwnerName());

            context.startActivity(intent);


            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
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



