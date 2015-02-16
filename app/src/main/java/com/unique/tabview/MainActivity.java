package com.unique.tabview;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.unique.library.TabView;
import com.unique.library.TabViewAdapter;


public class MainActivity extends ActionBarActivity {
    private Integer[] imagesIds = {R.mipmap.clock_icon, R.mipmap.emotion_icon, R.mipmap.memo_icon,
            R.mipmap.worldtime_icon, R.mipmap.stopwatch_icon, R.mipmap.timer_icon,
            R.mipmap.memorial_icon, R.mipmap.calendar_icon};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TabView tabView = (TabView) findViewById(R.id.my_tab);
        ExampleAdapter adapter = new ExampleAdapter(imagesIds, this);
        tabView.setOnItemClickListener(new TabViewAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position) {

            }
        });
        tabView.setAdapter(adapter);
        tabView.setReflectionEnabled(false);
        tabView.setUnselectedAlpha(0.3f);
        tabView.setUnselectedSaturation(1f);
        tabView.setMaxRotation(0);
        tabView.setReflectionGap(5);
        tabView.setUnselectedScale(0.4f);
        tabView.setScaleDownGravity(0.5f);
        tabView.setActionDistance(TabView.ACTION_DISTANCE_AUTO);

    }
}
