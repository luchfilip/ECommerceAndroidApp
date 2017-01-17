package com.smartbuilders.smartsales.ecommerce;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerce.adapters.ChatContactsListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.ChatContactDB;
import com.smartbuilders.smartsales.ecommerce.model.ChatContact;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatContactsListFragment extends Fragment {

    public static String KEY_LOAD_RECENT_CONVERSATIONS = "KEY_LOAD_RECENT_CONVERSATIONS";
    private static final String STATE_LOAD_RECENT_CONVERSATIONS = "STATE_LOAD_RECENT_CONVERSATIONS";
    private static final String STATE_CURRENT_SELECTED_INDEX = "STATE_CURRENT_SELECTED_INDEX";
    private static final String STATE_LIST_VIEW_INDEX = "STATE_LIST_VIEW_INDEX";
    private static final String STATE_LIST_VIEW_TOP = "STATE_LIST_VIEW_TOP";

    private User mUser;
    private boolean mIsInitialLoad;
    private boolean mLoadRecentConversations;
    private ListView mListView;
    private int mListViewIndex;
    private int mListViewTop;
    private int mCurrentSelectedIndex;

    public interface Callback {
        void onItemSelected(ChatContact chatContact, boolean isRecentConversation);
        void onItemLongSelected(ChatContact chatContact, ListView listView, User user);
        void onListIsLoaded(ListView listView, boolean isRecentConversation);
        void setSelectedIndex(int selectedIndex, ListView listView);
    }

    public ChatContactsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chat_contacts_list, container, false);

        mIsInitialLoad = true;

        final ArrayList<ChatContact> chatContacts = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState!=null){
                        if(savedInstanceState.containsKey(STATE_LOAD_RECENT_CONVERSATIONS)){
                            mLoadRecentConversations = savedInstanceState.getBoolean(STATE_LOAD_RECENT_CONVERSATIONS);
                        }
                        if(savedInstanceState.containsKey(STATE_CURRENT_SELECTED_INDEX)){
                            mCurrentSelectedIndex = savedInstanceState.getInt(STATE_CURRENT_SELECTED_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LIST_VIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LIST_VIEW_TOP);
                        }
                    }

                    if(getArguments()!=null){
                        mLoadRecentConversations = getArguments().containsKey(KEY_LOAD_RECENT_CONVERSATIONS);
                    }

                    mUser = Utils.getCurrentUser(getContext());

                    if (mLoadRecentConversations) {
                        chatContacts.addAll(new ChatContactDB(getContext(), mUser).getContactsWithRecentConversations());
                    } else {
                        chatContacts.addAll(new ChatContactDB(getContext(), mUser).getAvailableContacts());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mListView = (ListView) view.findViewById(R.id.chat_contacts_list);
                                mListView.setAdapter(new ChatContactsListAdapter(getContext(), chatContacts, mLoadRecentConversations));

                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                                        mCurrentSelectedIndex = position;
                                        // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                        // if it cannot seek to that position.
                                        ChatContact chatContact = (ChatContact) adapterView.getItemAtPosition(position);
                                        if (chatContact != null) {
                                            ((Callback) getActivity()).onItemSelected(chatContact, mIsInitialLoad);
                                        }
                                    }
                                });

                                if (mLoadRecentConversations) {
                                    mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                        @Override
                                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                            // CursorAdapter returns a cursor at the correct position for getItem(), or null
                                            // if it cannot seek to that position.
                                            ChatContact chatContact = (ChatContact) parent.getItemAtPosition(position);
                                            if (chatContact != null) {
                                                ((Callback) getActivity()).onItemLongSelected(chatContact, mListView, mUser);
                                            }
                                            return true;
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.chat_contacts_list).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (getActivity()!=null) {
                                    if (savedInstanceState==null || mListView.getAdapter()==null
                                            || mListView.getAdapter().isEmpty()) {
                                        ((Callback) getActivity()).onListIsLoaded(mListView, mLoadRecentConversations);
                                    } else {
                                        ((Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex, mListView);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }.start();
        return view;
    }

    @Override
    public void onStart() {
        if(mIsInitialLoad){
            mIsInitialLoad = false;
        }else{
            if(mListView!=null && mListView.getAdapter()!=null && getContext()!=null
                    && getActivity()!=null){
                int oldListSize = mListView.getAdapter().getCount();
                if (mLoadRecentConversations) {
                    ((ChatContactsListAdapter) mListView.getAdapter()).setData((new ChatContactDB(getContext(),
                            mUser)).getContactsWithRecentConversations());
                }else{
                    ((ChatContactsListAdapter) mListView.getAdapter()).setData((new ChatContactDB(getContext(),
                            mUser)).getAvailableContacts());
                }
                if(mListView.getAdapter().getCount()!=oldListSize){
                    if (getActivity()!=null) {
                        ((Callback) getActivity()).onListIsLoaded(mListView, mLoadRecentConversations);
                    }
                }else{
                    if (getActivity()!=null) {
                        ((Callback) getActivity()).setSelectedIndex(mCurrentSelectedIndex, mListView);
                    }
                }
            }else{
                if (getActivity()!=null) {
                    ((Callback) getActivity()).onListIsLoaded(mListView, mLoadRecentConversations);
                }
            }
        }
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_LOAD_RECENT_CONVERSATIONS, mLoadRecentConversations);
        try {
            outState.putInt(STATE_LIST_VIEW_INDEX, mListView.getFirstVisiblePosition());
        } catch (Exception e) {
            outState.putInt(STATE_LIST_VIEW_INDEX, mListViewIndex);
        }
        try {
            outState.putInt(STATE_LIST_VIEW_TOP, (mListView.getChildAt(0) == null) ? 0 :
                    (mListView.getChildAt(0).getTop() - mListView.getPaddingTop()));
        } catch (Exception e) {
            outState.putInt(STATE_LIST_VIEW_TOP, mListViewTop);
        }
        outState.putInt(STATE_CURRENT_SELECTED_INDEX, mCurrentSelectedIndex);
        super.onSaveInstanceState(outState);
    }
}
