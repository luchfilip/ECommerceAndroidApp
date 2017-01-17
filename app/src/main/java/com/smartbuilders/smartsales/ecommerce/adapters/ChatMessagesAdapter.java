package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.ChatMessage;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;

/**
 * Created by Alberto on 8/4/2016.
 */
public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder> {

    private ArrayList<ChatMessage> mDataset;
    private Context mContext;
    private User mUser;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private View containerLayout;
        private TextView message;
        private TextView created;

        public ViewHolder(View v) {
            super(v);
            containerLayout = v.findViewById(R.id.container_layout);
            message = (TextView) v.findViewById(R.id.message);
            created = (TextView) v.findViewById(R.id.created);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatMessagesAdapter(Context context, ArrayList<ChatMessage> myDataset, User user) {
        mDataset = myDataset;
        mUser = user;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatMessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.message.setText(mDataset.get(position).getMessage());
        holder.created.setText(mDataset.get(position).getCreatedStringFormat());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset!=null) {
            return mDataset.size();
        }
        return 0;
    }
}