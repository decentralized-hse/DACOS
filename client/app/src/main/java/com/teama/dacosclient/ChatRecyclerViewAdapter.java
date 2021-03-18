package com.teama.dacosclient;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teama.dacosclient.data.model.Chat;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Chat}.
 */
public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    /**
     * mValues serves as interlayer between Adapter and Chat.CHATS static list.
     */
    private List<Chat> mValues;

    private final String MESSAGE_HISTORY_IS_EMPTY = "Message history is empty";

    public ChatRecyclerViewAdapter(List<Chat> items) {
        mValues = items;
    }

    public void setChats(List<Chat> chatList) {
        // TODO: update setChats with DiffUtil to reduce time and resources for updating adapter:
        // https://stackoverflow.com/questions/44489235/update-recyclerview-with-android-livedata
        // https://ideone.com/tslCYG
        mValues = chatList;
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mUsernameTextView.setText(mValues.get(position).getUsername());
        holder.mLastMessageTextView.setText(
                mValues.get(position).getMessages().isEmpty() ?
                        MESSAGE_HISTORY_IS_EMPTY :
                        mValues.get(position).getLastMessage().getText());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mUsernameTextView;
        public final TextView mLastMessageTextView;
        public Chat mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUsernameTextView = (TextView) view.findViewById(R.id.username);
            mLastMessageTextView = (TextView) view.findViewById(R.id.last_message);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUsernameTextView.getText() + "'";
        }
    }
}