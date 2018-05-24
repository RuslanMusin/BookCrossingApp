package com.example.ruslan.curs2project.ui.base;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ruslan.curs2project.R;


/**
 * Created by Ruslan on 18.02.2018.
 */

public abstract class FragmentHostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager.findFragmentById(getContainerId()) == null){
            fragmentManager.beginTransaction()
                    .add(getContainerId(),getFragment())
                    .commit();
        }
    }

    protected abstract Fragment getFragment();

    private int getContainerId(){
        return R.id.fragment_container;
    }
}
