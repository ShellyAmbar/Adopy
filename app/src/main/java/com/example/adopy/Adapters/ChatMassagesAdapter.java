package com.example.adopy.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adopy.Models.MassageModel;
import com.example.adopy.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ChatMassagesAdapter extends RecyclerView.Adapter<ChatMassagesAdapter.ViewHolder>  {
    private ItemTouchHelper mItemTouchHelper;
    private List<MassageModel>  massageModelList;

    public void setmItemTouchHelper(ItemTouchHelper mItemTouchHelper) {
        this.mItemTouchHelper = mItemTouchHelper;
    }

    public ChatMassagesAdapter(List<MassageModel> massageModelList) {
        this.massageModelList = massageModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_massage,viewGroup,false);
        return new ChatMassagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        MassageModel model = massageModelList.get(position);

        if(model.getWriterId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            viewHolder.time_text_mine.setVisibility(View.VISIBLE);
            viewHolder.massage_text_mine.setVisibility(View.VISIBLE);

            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            viewHolder.time_text_mine.setText(formattedDate);
            viewHolder.massage_text_mine.setText(model.getMassageText());


            viewHolder.massage_text_other.setVisibility(View.GONE);
            viewHolder.time_text_other.setVisibility(View.GONE);


        }
        else {
            viewHolder.time_text_mine.setVisibility(View.GONE);
            viewHolder.massage_text_mine.setVisibility(View.GONE);

            viewHolder.massage_text_other.setVisibility(View.VISIBLE);
            viewHolder.time_text_other.setVisibility(View.VISIBLE);

            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            viewHolder.time_text_other.setText(formattedDate);
            viewHolder.massage_text_other.setText(model.getMassageText());

            }

    }

    @Override
    public int getItemCount() {
        return massageModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements
            View.OnTouchListener, GestureDetector.OnGestureListener{
        private GestureDetector mGestureDetector;
        private TextView time_text_mine,massage_text_mine,time_text_other,massage_text_other;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time_text_mine = itemView.findViewById(R.id.time_text_mine);
            massage_text_mine = itemView.findViewById(R.id.massage_text_mine);
            time_text_other = itemView.findViewById(R.id.time_text_other);
            massage_text_other = itemView.findViewById(R.id.massage_text_other);

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
