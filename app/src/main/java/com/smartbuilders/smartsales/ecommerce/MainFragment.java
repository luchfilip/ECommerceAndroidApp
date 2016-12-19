package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartbuilders.smartsales.ecommerce.adapters.MainActivityStaggeredLayoutAdapter;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.data.MainPageSectionsDB;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 22/3/2016.
 */
public class MainFragment extends Fragment {

    private static final String STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION = "STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION";

    private boolean mIsInitialLoad;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private int[] mRecyclerViewCurrentFirstPosition;
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
                        if(savedInstanceState.containsKey(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION)){
                            mRecyclerViewCurrentFirstPosition = savedInstanceState.getIntArray(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION);
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

                                mRecyclerView = (RecyclerView) view.findViewById(R.id.main_categories_list);
                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                mRecyclerView.setHasFixedSize(true);
                                mStaggeredGridLayoutManager =
                                        new StaggeredGridLayoutManager(
                                                getResources().getInteger(R.integer.number_of_cards_in_staggered_grid_layout),
                                                StaggeredGridLayoutManager.VERTICAL);
                                mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
                                mRecyclerView.setAdapter(new MainActivityStaggeredLayoutAdapter(getContext(),
                                        getActivity(), mainPageObjects, mCurrentUser));
                                if (mRecyclerViewCurrentFirstPosition!=null) {
                                    mRecyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition[0]);
                                }
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
            if (mRecyclerView!=null) {
                mRecyclerView.setVisibility(View.GONE);
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
                                    if(mRecyclerView!=null) {
                                        if (mRecyclerView.getAdapter()!=null) {
                                            ((MainActivityStaggeredLayoutAdapter) mRecyclerView.getAdapter()).setData(mainPageObjects);
                                        } else {
                                            mRecyclerView.setAdapter(new MainActivityStaggeredLayoutAdapter(getContext(), getActivity(), mainPageObjects, mCurrentUser));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (mProgressContainer!=null) {
                                        mProgressContainer.setVisibility(View.GONE);
                                    }
                                    if (mRecyclerView!=null) {
                                        mRecyclerView.setVisibility(View.VISIBLE);
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
            int[] into = new int[getResources().getInteger(R.integer.number_of_cards_in_staggered_grid_layout)];
            outState.putIntArray(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION,
                    ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPositions(into));
        } catch (Exception e) {
            outState.putIntArray(STATE_RECYCLER_VIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }

}
