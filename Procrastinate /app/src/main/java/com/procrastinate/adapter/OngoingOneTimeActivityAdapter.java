package com.procrastinate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.procrastinate.R;
import com.procrastinate.database.entity.OneTimeActivityEntity;

import java.util.List;


public class OngoingOneTimeActivityAdapter extends RecyclerView.Adapter<OngoingOneTimeActivityAdapter.ViewHolder> {

    List<OneTimeActivityEntity> mOneTimeActivityEntityList;

    public OngoingOneTimeActivityAdapter(List<OneTimeActivityEntity> mOneTimeActivityEntityList) {
        this.mOneTimeActivityEntityList = mOneTimeActivityEntityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ongoing_one_time_activity, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTitleTextView.setText(mOneTimeActivityEntityList.get(position).getActivityName());
    }


    @Override
    public int getItemCount() {
        return mOneTimeActivityEntityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleTextView;
        Button mFinishedButton;
        Button mUnfinishedButton;
        Button mResetButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.item_title);
            mFinishedButton = itemView.findViewById(R.id.action_finished);
            mUnfinishedButton = itemView.findViewById(R.id.action_unfinished);
            mResetButton = itemView.findViewById(R.id.action_reset);
            mFinishedButton.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onFinishedClickListener(mOneTimeActivityEntityList.get(getAdapterPosition()));
                }
            });
            mUnfinishedButton.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onUnfinishedClickListener(mOneTimeActivityEntityList.get(getAdapterPosition()));
                }
            });
            mResetButton.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onResetClickListener(mOneTimeActivityEntityList.get(getAdapterPosition()));
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onFinishedClickListener(OneTimeActivityEntity entity);

        void onUnfinishedClickListener(OneTimeActivityEntity entity);

        void onResetClickListener(OneTimeActivityEntity entity);
    }

    OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}