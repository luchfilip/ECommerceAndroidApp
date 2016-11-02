package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.adapters.MainActivityAdapter;
import com.smartbuilders.smartsales.ecommerce.data.MainPageSectionsDB;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 22/3/2016.
 */
public class MainFragment extends Fragment {

    private static final String STATE_LIST_VIEW_INDEX = "STATE_LIST_VIEW_INDEX";
    private static final String STATE_LIST_VIEW_TOP = "STATE_LIST_VIEW_TOP";

    // save index and top position
    private int mListViewIndex;
    private int mListViewTop;

    private boolean mIsInitialLoad;
    private ListView mListView;
    private User mCurrentUser;
    private View mProgressContainer;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        mIsInitialLoad = true;

        final ArrayList<Object> mainPageObjects = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    if(savedInstanceState != null) {
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_INDEX)){
                            mListViewIndex = savedInstanceState.getInt(STATE_LIST_VIEW_INDEX);
                        }
                        if(savedInstanceState.containsKey(STATE_LIST_VIEW_TOP)){
                            mListViewTop = savedInstanceState.getInt(STATE_LIST_VIEW_TOP);
                        }
                    }
                    mCurrentUser = Utils.getCurrentUser(getActivity());
                    mainPageObjects.addAll((new MainPageSectionsDB(getContext(), mCurrentUser)).getActiveMainPageSections());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(getActivity()!=null && getContext()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mProgressContainer = view.findViewById(R.id.progressContainer);
                                mListView = (ListView) view.findViewById(R.id.main_categories_list);
                                mListView.setAdapter(new MainActivityAdapter(getContext(), getActivity(), mainPageObjects, mCurrentUser));
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
    public void onStart() {
        if(mIsInitialLoad){
            mIsInitialLoad = false;
        }else{
            if (mProgressContainer!=null) {
                mProgressContainer.setVisibility(View.VISIBLE);
            }
            if (mListView!=null) {
                mListView.setVisibility(View.GONE);
            }
            final ArrayList<Object> mainPageObjects = new ArrayList<>();
            new Thread() {
                @Override
                public void run() {
                    try {
                        mCurrentUser = Utils.getCurrentUser(getActivity());
                        mainPageObjects.addAll((new MainPageSectionsDB(getContext(), mCurrentUser)).getActiveMainPageSections());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(getActivity()!=null && getContext()!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(mListView!=null) {
                                        if (mListView.getAdapter()!=null) {
                                            ((MainActivityAdapter) mListView.getAdapter()).setData(mainPageObjects);
                                        } else {
                                            mListView.setAdapter(new MainActivityAdapter(getContext(), getActivity(), mainPageObjects, mCurrentUser));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (mProgressContainer!=null) {
                                        mProgressContainer.setVisibility(View.GONE);
                                    }
                                    if (mListView!=null) {
                                        mListView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                    }
                }
            }.start();
        }
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
        super.onSaveInstanceState(outState);
    }

}
