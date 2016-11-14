package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartbuilders.smartsales.ecommerce.adapters.NotificationsListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.NotificationHistoryDB;
import com.smartbuilders.smartsales.ecommerce.model.NotificationHistory;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class NotificationsListFragment extends Fragment {

    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION = "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";

    private boolean mIsInitialLoad;
    private User mUser;
    private NotificationsListAdapter mNotificationsListAdapter;
    private NotificationHistoryDB mNotificationHistoryDB;
    private int mRecyclerViewCurrentFirstPosition;
    private View mBlankScreenView;
    private RecyclerView recyclerView;

    public NotificationsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notifications_list, container, false);
        mIsInitialLoad = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)){
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
                        }
                    }
                    mUser = Utils.getCurrentUser(getContext());
                    mNotificationHistoryDB = new NotificationHistoryDB(getContext(), mUser);
                    mNotificationsListAdapter = new NotificationsListAdapter(getContext(), mNotificationHistoryDB.getNotifications(), mUser);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mBlankScreenView = view.findViewById(R.id.empty_layout_wallpaper);
                                recyclerView = (RecyclerView) view.findViewById(R.id.notifications_list);

                                if (view.findViewById(R.id.settings_fab)!=null) {
                                    view.findViewById(R.id.settings_fab).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(getContext(), SettingsNotifications.class));
                                        }
                                    });
                                }

                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);

                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(mNotificationsListAdapter);

                                if (mRecyclerViewCurrentFirstPosition!=0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (recyclerView==null || recyclerView.getAdapter()==null
                                        || recyclerView.getAdapter().getItemCount()==0) {
                                    mBlankScreenView.setVisibility(View.VISIBLE);
                                } else {
                                    recyclerView.setVisibility(View.VISIBLE);
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
            reloadNotificationsList();
        }
        super.onStart();
    }

    public void reloadNotificationsList(){
        if (mNotificationHistoryDB !=null) {
            reloadNotificationsList(mNotificationHistoryDB.getNotifications());
        }
    }

    public void reloadNotificationsList(ArrayList<NotificationHistory> notificationHistories){
        if (recyclerView!=null && recyclerView.getAdapter()!=null) {
            ((NotificationsListAdapter) recyclerView.getAdapter()).setData(notificationHistories);
            if (notificationHistories == null || notificationHistories.size() == 0) {
                mBlankScreenView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                mBlankScreenView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
        } catch (Exception e) {
            outState.putInt(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mNotificationHistoryDB.updateNotificationsStatus(NotificationHistory.STATUS_SEEN);
        super.onDestroy();
    }

}
