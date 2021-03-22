package com.teama.dacosclient.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teama.dacosclient.R;
import com.teama.dacosclient.data.model.Chat;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Chat}.
 */
public class ChatRecyclerViewAdapter
        extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>
        implements ISearchableAdapter {

    /**
     * mValues serves as interlayer between Adapter and Chat.CHATS static list.
     */
    private List<Chat> mValues;
    private List<Chat> currentChats;
    private String query;
    private OnChatListener mOnChatListener;

    private final String MESSAGE_HISTORY_IS_EMPTY = "Message history is empty";

    public ChatRecyclerViewAdapter(List<Chat> items, OnChatListener onChatListener) {
        this.mValues = items;
        this.mOnChatListener = onChatListener;
        query = "";
        updateCurrentChats();
    }

    public void setChats(List<Chat> chatList) {
        // TODO: update setChats with DiffUtil to reduce time and resources for updating adapter:
        // https://stackoverflow.com/questions/44489235/update-recyclerview-with-android-livedata
        // https://ideone.com/tslCYG
        mValues = chatList;
        updateCurrentChats();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat, parent, false);
        return new ViewHolder(view, mOnChatListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = currentChats.get(position);
        holder.mUsernameTextView.setText(currentChats.get(position).getUsername());
        holder.mLastMessageTextView.setText(
                currentChats.get(position).getMessages().isEmpty() ?
                        MESSAGE_HISTORY_IS_EMPTY :
                        currentChats.get(position).getLastMessage().getText());
    }

    @Override
    public int getItemCount() {
        return currentChats.size();
    }

    @Override
    public void setQuery(String query) {
        this.query = query;
        updateCurrentChats();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mUsernameTextView;
        public final TextView mLastMessageTextView;
        public Chat mItem;
        OnChatListener onChatListener;

        public ViewHolder(View view, OnChatListener onChatListener) {
            super(view);
            mView = view;
            mUsernameTextView = view.findViewById(R.id.username);
            mLastMessageTextView = view.findViewById(R.id.last_message);
            this.onChatListener = onChatListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onChatListener.onChatClick(mItem.getNumericId());
        }
    }

    private void updateCurrentChats() {
        currentChats = new ArrayList<>();
        if (query.equals(""))
            for (Chat chat : mValues) {
                if (!chat.getMessages().isEmpty())
                    currentChats.add(chat);
            }
        else
            for (Chat chat : mValues) {
                if (chat.getUsername().toLowerCase().contains(query.toLowerCase()))
                    currentChats.add(chat);
            }
        notifyDataSetChanged();
    }

    public interface OnChatListener {
        void onChatClick(int chatId);
    }
}