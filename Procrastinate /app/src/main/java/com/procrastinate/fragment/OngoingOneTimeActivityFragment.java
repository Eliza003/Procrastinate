package com.procrastinate.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.procrastinate.R;
import com.procrastinate.activity.NewOneTimeActivity;
import com.procrastinate.adapter.OngoingOneTimeActivityAdapter;
import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.entity.OneTimeActivityEntity;
import com.procrastinate.database.repository.ActivityStageRepository;
import com.procrastinate.database.repository.OneTimeActivityRepository;
import com.procrastinate.utils.Constants;

public class OngoingOneTimeActivityFragment extends Fragment implements OngoingOneTimeActivityAdapter.OnItemClickListener {

    private OngoingOneTimeActivityViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private OngoingOneTimeActivityAdapter mAdapter;

    private TextView mNoDataTextView;

    public static OngoingOneTimeActivityFragment newInstance() {
        return new OngoingOneTimeActivityFragment();
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
        mViewModel = new ViewModelProvider(this).get(OngoingOneTimeActivityViewModel.class);
        // init RecyclerView
        mAdapter = new OngoingOneTimeActivityAdapter(mViewModel.getListData().getValue());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        // init observe
        OneTimeActivityRepository.getInstance(getContext()).loadUserOneTimeActivityListForType(
                getActivity().getIntent().getStringExtra(Constants.USER_NAME), 0)
                .observe(getViewLifecycleOwner(), oneTimeActivityEntities ->
                        mViewModel.setListData(oneTimeActivityEntities));
        mViewModel.getListData().observe(getViewLifecycleOwner(),
                oneTimeActivityEntities -> {
                    if (oneTimeActivityEntities.size() == 0) {
                        mNoDataTextView.setVisibility(View.VISIBLE);
                    }else {
                        mNoDataTextView.setVisibility(View.GONE);
                    }
                    mAdapter.notifyDataSetChanged();
                });
    }

    /**
     * item click finished
     */
    @Override
    public void onFinishedClickListener(OneTimeActivityEntity entity) {
        entity.setType(1);
        updateDatabase(entity);
    }

    /**
     * item click unfinished
     */
    @Override
    public void onUnfinishedClickListener(OneTimeActivityEntity entity) {
        entity.setType(2);
        updateDatabase(entity);
    }

    /**
     * update database
     */
    private void updateDatabase(OneTimeActivityEntity entity) {
        new Thread(() -> {
            boolean isSuccessful = OneTimeActivityRepository.getInstance(getContext()).updateActivity(entity);
            getActivity().runOnUiThread(() -> {    // Switch to the UI thread
                if (isSuccessful) {
                    Toast.makeText(getContext(), getText(R.string.common_succeeded), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getText(R.string.common_error), Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /**
     * item click reset
     */
    @Override
    public void onResetClickListener(OneTimeActivityEntity entity) {
        NewOneTimeActivity.startAction(getContext(),entity);
    }

}