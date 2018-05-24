package com.example.ruslan.curs2project.utils;

import android.app.Fragment;

import com.example.ruslan.curs2project.R;


/**
 * Created by Ruslan on 23.02.2018.
 */

public class FragmentHelper {

    public static void changeFragment(Fragment lastFragment, Fragment nextFragment){
        lastFragment.getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, nextFragment)
                .commit();
    }
}
