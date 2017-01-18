package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.smartbuilders.smartsales.ecommerce.adapters.ChatMessagesAdapter;
import com.smartbuilders.smartsales.ecommerce.data.ChatContactDB;
import com.smartbuilders.smartsales.ecommerce.data.ChatMessageDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesRepDB;
import com.smartbuilders.smartsales.ecommerce.model.ChatContact;
import com.smartbuilders.smartsales.ecommerce.model.ChatMessage;
import com.smartbuilders.smartsales.ecommerce.services.SendChatMessageService;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatMessagesFragment extends Fragment {

    private static final String STATE_CHAT_CONTACT_ID = "STATE_CHAT_CONTACT_ID";
    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION = "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";

    private User mUser;
    private boolean mIsInitialLoad;
    private int mSenderChatContactId;
    private int mReceiverChatContactId;
    private ChatContact mReceiverChatContact;
    private ArrayList<ChatMessage> mChatMessages;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;

    public interface Callback{
        void chatMessagesDetailLoaded();
        boolean isFragmentMenuVisible();
    }

    public ChatMessagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chat_messages, container, false);
        setMenuVisibility(((Callback) getActivity()).isFragmentMenuVisible());

        mIsInitialLoad = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    if (savedInstanceState!=null) {
                        if (savedInstanceState.containsKey(STATE_CHAT_CONTACT_ID)) {
                            mReceiverChatContactId = savedInstanceState.getInt(STATE_CHAT_CONTACT_ID);
                        }
                        if (savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)) {
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                    }

                    if (getArguments() != null) {
                        if (getArguments().containsKey(ChatMessagesActivity.KEY_CHAT_CONTACT_ID)) {
                            mReceiverChatContactId = getArguments().getInt(ChatMessagesActivity.KEY_CHAT_CONTACT_ID);
                        }
                    } else if (getActivity().getIntent() != null && getActivity().getIntent().getExtras() != null) {
                        if (getActivity().getIntent().getExtras().containsKey(ChatMessagesActivity.KEY_CHAT_CONTACT_ID)) {
                            mReceiverChatContactId = getActivity().getIntent().getExtras().getInt(ChatMessagesActivity.KEY_CHAT_CONTACT_ID);
                        }
                    }

                    mUser = Utils.getCurrentUser(getContext());

                    if (mReceiverChatContactId >0) {
                        mReceiverChatContact = (new ChatContactDB(getContext(), mUser)).getContactById(mReceiverChatContactId);
                    }

                    if (mReceiverChatContact != null) {
                        mChatMessages = (new ChatMessageDB(getContext(), mUser)).getMessagesFromContact(mReceiverChatContactId);
                    }

                    mSenderChatContactId = new SalesRepDB(getContext(), mUser).getSalesRepId();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.chat_messages);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);
                                mLinearLayoutManager = new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(new ChatMessagesAdapter(getContext(), mChatMessages, mUser, mSenderChatContactId));

                                if (mRecyclerViewCurrentFirstPosition!=0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }

                                view.findViewById(R.id.chat_send_message_imageView).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            String messageToSend = ((EditText) view.findViewById(R.id.chat_message_to_send_editText))
                                                    .getText().toString();
                                            if (!TextUtils.isEmpty(messageToSend)) {
                                                ((EditText) view.findViewById(R.id.chat_message_to_send_editText)).setText(null);

                                                ChatMessage chatMessage = new ChatMessage();
                                                chatMessage.setSenderChatContactId(mSenderChatContactId);
                                                chatMessage.setReceiverChatContactId(mReceiverChatContactId);
                                                chatMessage.setMessage(messageToSend);
                                                chatMessage.setCreated(new Date());
                                                ((ChatMessagesAdapter) recyclerView.getAdapter()).addChatMessage(chatMessage);

                                                Intent msgIntent = new Intent(getContext(), SendChatMessageService.class);
                                                msgIntent.putExtra(SendChatMessageService.KEY_USER_ID, mUser.getUserId());
                                                msgIntent.putExtra(SendChatMessageService.KEY_CHAT_MESSAGE, chatMessage);
                                                getContext().startService(msgIntent);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (mReceiverChatContact !=null) {
                                    view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                                    view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                } else {
                                    view.findViewById(R.id.main_layout).setVisibility(View.GONE);
                                    view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                }
                                if(getActivity()!=null){
                                    ((Callback) getActivity()).chatMessagesDetailLoaded();
                                }
                            }
                        }
                    });
                }
            }
        }.start();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart() {
        if (mIsInitialLoad) {
            mIsInitialLoad = false;
        } else {
            //reload messages
        }
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CHAT_CONTACT_ID, mReceiverChatContactId);
        try {
            if (mLinearLayoutManager instanceof GridLayoutManager) {
                outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstVisibleItemPosition());
            } else {
                outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
            }
        } catch (Exception e) {
            outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
