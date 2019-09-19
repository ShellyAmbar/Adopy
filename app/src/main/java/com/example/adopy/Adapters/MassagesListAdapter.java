package com.example.adopy.Adapters;

import android.content.Context;
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
import com.example.adopy.Interfaces.CardListener;
import com.example.adopy.Models.MassageListModel;
import com.example.adopy.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MassagesListAdapter extends RecyclerView.Adapter<MassagesListAdapter.ViewHolder>  {

    private List<MassageListModel> massageListModels;
    private CardListener cardListener;
    private Context context;
    private ItemTouchHelper mItemTouchHelper;

    public void setmItemTouchHelper(ItemTouchHelper mItemTouchHelper) {
        this.mItemTouchHelper = mItemTouchHelper;
    }

    public MassagesListAdapter(List<MassageListModel> massageListModels, CardListener cardListener, Context context) {
        this.massageListModels = massageListModels;
        this.cardListener = cardListener;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_massage,viewGroup,false);
        return new MassagesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        MassageListModel model = massageListModels.get(i);
        Glide.with(context).load(model.getUrl()).into( viewHolder.image);
        viewHolder.name.setText(model.getName());
        viewHolder.massage_title.setText(model.getMassageTitle());


    }

    @Override
    public int getItemCount() {
        return massageListModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnTouchListener, GestureDetector.OnGestureListener
    {
        private GestureDetector mGestureDetector;
        private TextView massage_title, name;
        private CircleImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            massage_title = itemView.findViewById(R.id.massage_title);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardListener.onCardClicked(getAdapterPosition(),v);

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    cardListener.onCardLongClicked(getAdapterPosition(),v);
                    return true;
                }
            });

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
