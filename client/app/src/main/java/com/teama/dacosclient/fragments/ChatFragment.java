package com.teama.dacosclient.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teama.dacosclient.R;
import com.teama.dacosclient.activities.ChatsActivity;
import com.teama.dacosclient.adapters.ChatRecyclerViewAdapter;
import com.teama.dacosclient.data.model.Chat;

import java.util.List;

/**
 * A fragment representing a list of chats.
 */
public class ChatFragment extends Fragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     * Link to the adapter is setted up here.
     */
    public ChatFragment() {
        adapter = new ChatRecyclerViewAdapter(Chat.getChats(), ChatsActivity.getActivityContext());
    }

    private ChatRecyclerViewAdapter adapter;

    /**
     * Creates instance of ChatFragment.
     * Any parameter args must be setted in here.
     */
    @SuppressWarnings("unused")
    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
            Chat.observeChatsData(getViewLifecycleOwner(),
                    (Observer<List<Chat>>) adapter::setChats);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    mLayoutManager.getOrientation());
            recyclerView.addItemDecoration(mDividerItemDecoration);
        }
        return view;
    }

    public ChatRecyclerViewAdapter getAdapter() {
        return adapter;
    }
}