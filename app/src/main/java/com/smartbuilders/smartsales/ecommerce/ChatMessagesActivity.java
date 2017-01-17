package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.data.ChatContactDB;
import com.smartbuilders.smartsales.ecommerce.model.ChatContact;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 *
 */
public class ChatMessagesActivity extends AppCompatActivity implements ChatMessagesFragment.Callback {

    public static final String KEY_CHAT_CONTACT_ID = "KEY_CHAT_CONTACT_ID";
    private static final String STATE_CHAT_CONTACT_ID = "STATE_CHAT_CONTACT_ID";

    private int mChatContactId;
    private ChatContact mChatContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        if (savedInstanceState!=null) {
            if (savedInstanceState.containsKey(STATE_CHAT_CONTACT_ID)) {
                mChatContactId = savedInstanceState.getInt(STATE_CHAT_CONTACT_ID);
            }
        } else if(getIntent()!=null && getIntent().getExtras()!=null) {
            if(getIntent().getExtras().containsKey(KEY_CHAT_CONTACT_ID)){
                mChatContactId = getIntent().getExtras().getInt(KEY_CHAT_CONTACT_ID);
            }
        }

        if (mChatContactId>0) {
            mChatContact = (new ChatContactDB(this, Utils.getCurrentUser(this)))
                    .getContactById(mChatContactId);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null) {
            toolbar.setLogo(null);
            toolbar.findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            ((TextView) toolbar.findViewById(R.id.chat_contact_name)).setText(mChatContact.getName());
            ((TextView) toolbar.findViewById(R.id.chat_contact_internal_code)).setText(mChatContact.getInternalCode());
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_messages, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int i = menuItem.getItemId();
        if (i == R.id.search) {

            return true;
        } else if (i == android.R.id.home) {
            onBackPressed();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void chatMessagesDetailLoaded() {

    }

    @Override
    public boolean isFragmentMenuVisible() {
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CHAT_CONTACT_ID, mChatContactId);
        super.onSaveInstanceState(outState);
    }
}
