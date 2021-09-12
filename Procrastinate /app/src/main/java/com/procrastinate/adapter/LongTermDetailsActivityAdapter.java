package com.procrastinate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.procrastinate.R;
import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.entity.OneTimeActivityEntity;

import java.util.List;


public class LongTermDetailsActivityAdapter extends RecyclerView.Adapter<LongTermDetailsActivityAdapter.ViewHolder> {

    List<ActivityStageEntity> mActivityStageEntities;

    public LongTermDetailsActivityAdapter(List<ActivityStageEntity> mActivityStageEntities) {
        this.mActivityStageEntities = mActivityStageEntities;
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
        ActivityStageEntity stageEntity = mActivityStageEntities.get(position);
        // set visibility
        if (stageEntity.getType() == 0 ){
            holder.mFinishedButton.setVisibility(View.VISIBLE);
            holder.mUnfinishedButton.setVisibility(View.VISIBLE);
            holder.mResetButton.setVisibility(View.VISIBLE);
            holder.mFinishedTextView.setVisibility(View.GONE);
        }else {
            holder.mFinishedButton.setVisibility(View.GONE);
            holder.mUnfinishedButton.setVisibility(View.GONE);
            holder.mResetButton.setVisibility(View.GONE);
            holder.mFinishedTextView.setVisibility(View.VISIBLE);
            if (stageEntity.getType() == 1 ){
                holder.mFinishedTextView.setText(holder.itemView.getContext().getText(R.string.action_finished));
            }else {
                holder.mFinishedTextView.setText(holder.itemView.getContext().getText(R.string.action_unfinished));
            }
        }
        // ---
        holder.mTitleTextView.setText(stageEntity.getStageName());
    }


    @Override
    public int getItemCount() {
        return mActivityStageEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleTextView;
        TextView mFinishedTextView;
        Button mFinishedButton;
        Button mUnfinishedButton;
        Button mResetButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.item_title);
            mFinishedTextView = itemView.findViewById(R.id.item_hint);
            mFinishedButton = itemView.findViewById(R.id.action_finished);
            mUnfinishedButton = itemView.findViewById(R.id.action_unfinished);
            mResetButton = itemView.findViewById(R.id.action_reset);
            mFinishedButton.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onFinishedClickListener(mActivityStageEntities.get(getAdapterPosition()));
                }
            });
            mUnfinishedButton.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onUnfinishedClickListener(mActivityStageEntities.get(getAdapterPosition()));
                }
            });
            mResetButton.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onResetClickListener(mActivityStageEntities.get(getAdapterPosition()));
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onFinishedClickListener(ActivityStageEntity entity);

        void onUnfinishedClickListener(ActivityStageEntity entity);

        void onResetClickListener(ActivityStageEntity entity);
    }

    OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}