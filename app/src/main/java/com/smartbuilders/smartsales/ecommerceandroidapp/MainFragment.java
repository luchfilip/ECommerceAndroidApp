package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.MainActivityAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.MainPageSectionsDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 22/3/2016.
 */
public class MainFragment extends Fragment {

    private static final String STATE_LISTVIEW_INDEX = "STATE_LISTVIEW_INDEX";
    private static final String STATE_LISTVIEW_TOP = "STATE_LISTVIEW_TOP";

    // save index and top position
    private int mListViewIndex;
    private int mListViewTop;

    private ListView mListView;
    private User mCurrentUser;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        final ArrayList<Object> mainPageObjects = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_LISTVIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LISTVIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LISTVIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LISTVIEW_TOP);
                        }
                    }
                    mCurrentUser = Utils.getCurrentUser(getActivity());
                    mainPageObjects.addAll((new MainPageSectionsDB(getActivity(), mCurrentUser)).getActiveMainPageSections());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mListView = (ListView) view.findViewById(R.id.main_categories_list);
                                mListView.setAdapter(new MainActivityAdapter(getActivity(), mainPageObjects, mCurrentUser));
                                mListView.setVisibility(View.VISIBLE);
                                mListView.setSelectionFromTop(mListViewIndex, mListViewTop);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                view.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                view.findViewById(R.id.main_categories_list).setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        }.start();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            outState.putInt(STATE_LISTVIEW_INDEX, mListView.getFirstVisiblePosition());
        } catch (Exception e) {
            outState.putInt(STATE_LISTVIEW_INDEX, mListViewIndex);
        }
        try {
            outState.putInt(STATE_LISTVIEW_TOP, (mListView.getChildAt(0) == null) ? 0 :
                    (mListView.getChildAt(0).getTop() - mListView.getPaddingTop()));
        } catch (Exception e) {
            outState.putInt(STATE_LISTVIEW_TOP, mListViewTop);
        }
        super.onSaveInstanceState(outState);
    }

}
