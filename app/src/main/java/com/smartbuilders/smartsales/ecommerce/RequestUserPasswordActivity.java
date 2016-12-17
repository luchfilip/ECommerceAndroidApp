package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * Jesus Sarco, 07.06.2016
 */
public class RequestUserPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_user_password);

        final View activityRootView = findViewById(R.id.parent_layout);
        if (activityRootView!=null) {
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if ((activityRootView.getRootView().getHeight() - activityRootView.getHeight()) > 100) {
                        //se esta mostrando el teclado
                        if (findViewById(R.id.fields_scrollView) != null) {
                            View lastChild = ((ScrollView) findViewById(R.id.fields_scrollView))
                                    .getChildAt(((ScrollView) findViewById(R.id.fields_scrollView)).getChildCount() - 1);
                            int delta = lastChild.getBottom() + findViewById(R.id.fields_scrollView).getPaddingBottom()
                                    - (findViewById(R.id.fields_scrollView).getScrollY() + findViewById(R.id.fields_scrollView).getHeight());
                            ((ScrollView) findViewById(R.id.fields_scrollView)).smoothScrollBy(0, delta);
                        }
                    }
                }
            });
        }
    }

}
