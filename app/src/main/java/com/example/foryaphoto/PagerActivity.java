package com.example.foryaphoto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;

/**
 * @author Aleksandr Karpachev
 *         Created on 25.05.18
 */

public class PagerActivity extends FragmentActivity {

    private static final String POSITION = "PagerActivityPhotoIndex";

    public static Intent newIntent(Context context, int position){
        Intent intent = new Intent(context, PagerActivity.class);
        intent.putExtra(POSITION, position);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(PagerFragment.TAG);
        if (fragment == null) {
            PagerFragment pagerFragment = PagerFragment.newInstance(getIntent().getIntExtra(POSITION, 0));
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, pagerFragment, PagerFragment.TAG)
                    .commit();
        }
    }
}
