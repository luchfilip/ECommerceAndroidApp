package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.adapters.NotificationsListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.NotificationHistoryDB;
import com.smartbuilders.smartsales.ecommerce.model.NotificationHistory;
import com.smartbuilders.smartsales.ecommerce.utils.BadgeUtils;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class NotificationsListFragment extends Fragment implements NotificationsListAdapter.Callback {

    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION = "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";

    private boolean mIsInitialLoad;
    private User mUser;
    private NotificationsListAdapter mNotificationsListAdapter;
    private NotificationHistoryDB mNotificationHistoryDB;
    private int mRecyclerViewCurrentFirstPosition;
    private View mBlankScreenView;
    private RecyclerView recyclerView;
    private View mMainLayout;

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
                    mNotificationsListAdapter = new NotificationsListAdapter(NotificationsListFragment.this,
                            getContext(), mNotificationHistoryDB.getNotifications(), mUser);
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
                                mMainLayout = view.findViewById(R.id.main_layout);

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

                                ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                                        new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT /*| ItemTouchHelper.DOWN | ItemTouchHelper.UP*/) {

                                    @Override
                                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                                        return false;
                                    }

                                    @Override
                                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                                        //Remove swiped item from list and notify the RecyclerView
                                        final int itemPosition = viewHolder.getAdapterPosition();
                                        final NotificationHistory notificationHistory = mNotificationsListAdapter.getItem(itemPosition);

                                        String result = mNotificationHistoryDB.deleteNotification(notificationHistory.getId());
                                        if(result == null){
                                            //viewHolder.setIsRecyclable(false);
                                            mNotificationsListAdapter.removeItem(itemPosition);
                                            Snackbar.make(mMainLayout, R.string.notification_removed, Snackbar.LENGTH_LONG)
                                                    .setAction(R.string.undo, new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            String result = mNotificationHistoryDB.restoreNotification(notificationHistory.getId());
                                                            if(result == null){
                                                                mNotificationsListAdapter.addItem(itemPosition, notificationHistory);
                                                                Snackbar.make(mMainLayout, R.string.notification_restored, Snackbar.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }).show();
                                        } else {
                                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                                            RecyclerView.ViewHolder viewHolder, float dX,
                                                            float dY, int actionState, boolean isCurrentlyActive) {
                                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                                            // Get RecyclerView item from the ViewHolder
                                            View itemView = viewHolder.itemView;

                                            Paint p = new Paint();
                                            p.setColor(Utils.getColor(getContext(), R.color.on_swipe_bg_color));

                                            Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_highlight_off_white_48dp);
                                            if (dX > 0) {
                                                // Draw Rect with varying right side, equal to displacement dX
                                                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                                        (float) itemView.getBottom(), p);

                                                // Set the image icon for Right swipe
                                                c.drawBitmap(icon,
                                                        (float) itemView.getLeft() + Utils.convertDpToPixel(16, getContext()),
                                                        (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                                                        p);
                                            } else {
                                                // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                                                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                                        (float) itemView.getRight(), (float) itemView.getBottom(), p);

                                                //Set the image icon for Left swipe
                                                c.drawBitmap(icon,
                                                        (float) itemView.getRight() - Utils.convertDpToPixel(16, getContext()) - icon.getWidth(),
                                                        (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                                                        p);
                                            }
                                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                        }
                                    }

                                };
                                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                                itemTouchHelper.attachToRecyclerView(recyclerView);
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
            if (mNotificationHistoryDB !=null) {
                reloadNotificationsList(mNotificationHistoryDB.getNotifications(), true);
            }
        }
        if (getActivity()!=null) {
            BadgeUtils.clearBadge(getActivity());
        }
        if (mNotificationHistoryDB!=null) {
            mNotificationHistoryDB.updateNotificationsStatus(NotificationHistory.STATUS_SEEN);
        }
        super.onStart();
    }

    @Override
    public void reloadNotificationsList(ArrayList<NotificationHistory> notificationHistories, boolean setData){
        if (recyclerView!=null && recyclerView.getAdapter()!=null) {
            if (setData) {
                ((NotificationsListAdapter) recyclerView.getAdapter()).setData(notificationHistories);
            }
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

}
