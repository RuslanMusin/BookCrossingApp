package com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.MemberAdapter;
import com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.reader.ReaderListView;
import com.example.ruslan.curs2project.ui.widget.EmptyStateRecyclerView;

import java.util.ArrayList;

import static com.example.ruslan.curs2project.utils.Const.FRIEND_LIST;
import static com.example.ruslan.curs2project.utils.Const.READER_LIST;
import static com.example.ruslan.curs2project.utils.Const.REQUEST_LIST;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class ReaderListFragment extends Fragment {

    private ProgressBar progressBar;
    private EmptyStateRecyclerView recyclerView;
    private TextView tvEmpty;

    private boolean isLoading = false;

    private MemberAdapter adapter;

    private String type;

    private ReaderListView parentView;

    private boolean isLoaded = false;

    public static Fragment newInstance(String type,ReaderListView parentView) {
        ReaderListFragment fragment =  new ReaderListFragment();
     /*   Bundle args = new Bundle();
        fragment.setArguments(args);*/
        fragment.type = type;
        fragment.parentView = parentView;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_recycler_list, container, false);
        Log.d(TAG_LOG, "create view = " + this.type);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG_LOG, "on view created = " + this.type);
        initViews(view);
        initRecycler();

        if(!isLoaded && type.equals(READER_LIST)) {
            parentView.changeAdapter(0);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.pg_comics_list);
        recyclerView = view.findViewById(R.id.rv_comics_list);
        tvEmpty = view.findViewById(R.id.tv_empty);

        parentView.setProgressBar(progressBar);
    }

    private void initRecycler() {
        adapter = new MemberAdapter(new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setEmptyView(tvEmpty);
        adapter.attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(parentView);
        recyclerView.setAdapter(adapter);
//        recyclerView.setHasFixedSize(true);
       /* recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int currentPage = 0;
            private boolean isLastPage = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = manager.getChildCount();
                int totalItemCount = manager.getItemCount();
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= 20) {
                        isLoading = true;
                        parentView.loadNextElements(++currentPage);
                    }
                }
            }
        });*/
    }

    public void loadPeople(){
        switch (type) {

            case FRIEND_LIST :
                parentView.loadFriends(UserRepository.getCurrentId());
                break;

            case REQUEST_LIST :
                parentView.loadRequests(UserRepository.getCurrentId());
                break;

            default:
                parentView.loadReaders();
                break;
        }
        isLoaded = true;
    }

    public void changeDataInAdapter(){
        parentView.setCurrentType(type);
        parentView.setAdapter(adapter);
        loadPeople();
    }

}
