package com.smartbuilders.smartsales.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.ChatContact;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class ChatContactsListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ChatContact> mDataset;
    private boolean mLoadRecentConversations;

    public ChatContactsListAdapter(Context context, ArrayList<ChatContact> data, boolean loadRecentConversations) {
        mContext = context;
        mDataset = data;
        mLoadRecentConversations = loadRecentConversations;
    }

    @Override
    public int getCount() {
        if (mDataset!=null) {
            return mDataset.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataset.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if(view==null){//si la vista es null la crea sino la reutiliza
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_contact_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.internalCode.setText(mDataset.get(position).getInternalCode());
        viewHolder.name.setText(mDataset.get(position).getName());
        if (mLoadRecentConversations) {
            viewHolder.maxChatMessageCreateTime.setText(mDataset.get(position).getMaxChatMessageCreateTimeStringFormat());
            viewHolder.maxChatMessageCreateTime.setVisibility(View.VISIBLE);
        } else {
            viewHolder.maxChatMessageCreateTime.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView internalCode;
        public TextView maxChatMessageCreateTime;

        public ViewHolder(View v) {
            name = (TextView) v.findViewById(R.id.chat_contact_name);
            internalCode = (TextView) v.findViewById(R.id.chat_contact_internal_code);
            maxChatMessageCreateTime = (TextView) v.findViewById(R.id.max_chat_message_create_time);
        }
    }

    public void setData(ArrayList<ChatContact> chatContacts) {
        mDataset = chatContacts;
        notifyDataSetChanged();
    }
}