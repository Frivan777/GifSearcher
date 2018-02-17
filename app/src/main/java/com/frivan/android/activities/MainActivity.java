package com.frivan.android.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.frivan.android.fragments.GifListFragment;
import com.frivan.android.gifsearcher.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * Инициализирует используемые view1
     */
    private void initView() {
        showFragment();
    }

    /**
     * Отображает фрагмент
     */
    private void showFragment() {
        FragmentManager fm = getSupportFragmentManager();

        Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
        if (currentFragment == null) {
            fm.beginTransaction()
                    .add(R.id.fragment_container, GifListFragment.newInstance())
                    .commit();
        }
    }
}
