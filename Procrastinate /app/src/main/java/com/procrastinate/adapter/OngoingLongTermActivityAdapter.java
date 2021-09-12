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
import com.procrastinate.database.entity.LongTermActivityList;

import java.text.DecimalFormat;
import java.util.List;


public class OngoingLongTermActivityAdapter extends RecyclerView.Adapter<OngoingLongTermActivityAdapter.ViewHolder> {

    List<LongTermActivityList> mLongTermActivityLists;

    boolean isDelete = false;

    public OngoingLongTermActivityAdapter(List<LongTermActivityList> mLongTermActivityLists) {
        this.mLongTermActivityLists = mLongTermActivityLists;
    }

    public OngoingLongTermActivityAdapter(List<LongTermActivityList> mLongTermActivityLists,boolean isDelete) {
        this.mLongTermActivityLists = mLongTermActivityLists;
        this.isDelete = isDelete;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ongoing_long_term_activity, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LongTermActivityList activityList = mLongTermActivityLists.get(position);
        holder.mTitleTextView.setText(activityList.getActivity().getActivityName());
        int count = 0;
        for (ActivityStageEntity stageEntity : activityList.getActivityStageList()) {
            if (stageEntity.getType() == 1) {
                count++;
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("##.##%");
        String percentage = decimalFormat.format(count * 1f / activityList.getActivityStageList().size());
        holder.mPercentageTextView.setText(percentage);
    }

    @Override
    public int getItemCount() {
        return mLongTermActivityLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleTextView;
        TextView mPercentageTextView;
        Button mDetailsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.item_title);
            mPercentageTextView = itemView.findViewById(R.id.item_percentage);
            mDetailsButton = itemView.findViewById(R.id.action_details);
            if (isDelete) {
                mDetailsButton.setText(itemView.getContext().getText(R.string.action_delete));
            }
            mDetailsButton.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onDetailsClickListener(mLongTermActivityLists.get(getAdapterPosition()));
                }
            });
            itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClickListener(mLongTermActivityLists.get(getAdapterPosition()));
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onDetailsClickListener(LongTermActivityList entity);
        void onItemClickListener(LongTermActivityList entity);
    }

    OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}