package com.procrastinate.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.procrastinate.R;
import com.procrastinate.activity.LongTermDetailsActivity;
import com.procrastinate.adapter.OngoingLongTermActivityAdapter;
import com.procrastinate.database.entity.LongTermActivityList;
import com.procrastinate.database.repository.LongTermActivityRepository;
import com.procrastinate.utils.Constants;

public class OngoingLongTermActivityFragment extends Fragment implements OngoingLongTermActivityAdapter.OnItemClickListener {

    private OngoingLongTermActivityViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private OngoingLongTermActivityAdapter mAdapter;

    private TextView mNoDataTextView;

    public static OngoingLongTermActivityFragment newInstance() {
        return new OngoingLongTermActivityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ongoing_activity, container, false);
        mRecyclerView = rootView.findViewById(R.id.mRecyclerView);
        mNoDataTextView = rootView.findViewById(R.id.prompt_no_data);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OngoingLongTermActivityViewModel.class);
        // init RecyclerView
        mAdapter = new OngoingLongTermActivityAdapter(mViewModel.getListData().getValue());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        // init observe
        LongTermActivityRepository.getInstance(getContext()).loadUserLongTermActivityList(
                getActivity().getIntent().getStringExtra(Constants.USER_NAME))
                .observe(getViewLifecycleOwner(), oneTimeActivityEntities ->
                        mViewModel.setListData(oneTimeActivityEntities));
        mViewModel.getListData().observe(getViewLifecycleOwner(),
                longTermActivityLists -> {
                    if (longTermActivityLists.size() == 0) {
                        mNoDataTextView.setVisibility(View.VISIBLE);
                    }else {
                        mNoDataTextView.setVisibility(View.GONE);
                    }
                    mAdapter.notifyDataSetChanged();
                });
    }

    /**
     * item click details
     */
    @Override
    public void onDetailsClickListener(LongTermActivityList entity) {
        LongTermDetailsActivity.startAction(getContext(),
                entity.getActivity().getActivityName(),entity.getActivity().getActivityId());
    }

    @Override
    public void onItemClickListener(LongTermActivityList entity) {
        LongTermDetailsActivity.startAction(getContext(),
                entity.getActivity().getActivityName(),entity.getActivity().getActivityId());
    }

}