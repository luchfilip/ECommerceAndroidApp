package com.smartbuilders.smartsales.ecommerce;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartbuilders.smartsales.ecommerce.adapters.NotificationsListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.NotificationDB;
import com.smartbuilders.smartsales.ecommerce.model.Notification;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
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
    private NotificationDB mNotificationDB;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private View mBlankScreenView;
    private RecyclerView recyclerView;
    private ArrayList<Notification> mNotifications;

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
                    mNotificationDB = new NotificationDB(getContext(), mUser);

                    mNotifications = mNotificationDB.getNotifications();

                    mNotificationsListAdapter = new NotificationsListAdapter(getContext(), mNotifications, mUser);
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

                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);

                                mLinearLayoutManager = new LinearLayoutManager(getContext());

                                recyclerView.setLayoutManager(mLinearLayoutManager);
                                recyclerView.setAdapter(mNotificationsListAdapter);

                                if (mRecyclerViewCurrentFirstPosition!=0) {
                                    recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                if (mNotifications.isEmpty()) {
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
        if (mNotificationDB!=null) {
            reloadNotificationsList(mNotificationDB.getNotifications());
        }
    }

    public void reloadNotificationsList(ArrayList<Notification> notifications){
        mNotifications = notifications;
        mNotificationsListAdapter.setData(notifications);
        if (notifications==null || notifications.size()==0) {
            mBlankScreenView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            mBlankScreenView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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

    @Override
    public void onDestroy() {
        mNotificationDB.updateNotificationsStatus();
        super.onDestroy();
    }

}
