package com.procrastinate.adapter;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.procrastinate.R;

import java.util.List;


public class FinishedActivityAdapter extends RecyclerView.Adapter<FinishedActivityAdapter.ViewHolder> {

    List<Pair<Long, String>> mPairList;

    public FinishedActivityAdapter(List<Pair<Long, String>> mPairList) {
        this.mPairList = mPairList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_finished_activity, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = mPairList.get(position).second;
        holder.mTitleTextView.setText(title);
    }


    @Override
    public int getItemCount() {
        return mPairList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleTextView;
        Button mDeleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.item_title);
            mDeleteButton = itemView.findViewById(R.id.action_delete);
            mDeleteButton.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    Pair<Long, String> pair = mPairList.get(getAdapterPosition());
                    mOnItemClickListener.onDeleteClickListener(pair.first, pair.second);
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onDeleteClickListener(Long id, String title);
    }

    OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}