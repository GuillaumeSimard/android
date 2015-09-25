package com.dinfogarneau.cours03e.ecotrajet.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dinfogarneau.cours03e.ecotrajet.fragment.DemandeFragment;
import com.dinfogarneau.cours03e.ecotrajet.fragment.DepartFragment;

/**
 * Created by Guillaume on 2015-09-11.
 */
public class OngletPagerAdapterConducteur extends FragmentPagerAdapter {

    public OngletPagerAdapterConducteur(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                // Fragment pour Artistes.
                return new DepartFragment();
            case 1:
                // Fragment pour Albums.
                return new DemandeFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // Return le nombre d'onglets.
        return 2;
    }
}