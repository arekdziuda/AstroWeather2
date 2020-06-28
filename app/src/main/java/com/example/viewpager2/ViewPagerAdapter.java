package com.example.viewpager2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private Bundle bundle;
    private static final int CARD_ITEM_SIZE = 2;


    public void setArguments(Bundle bundle) {
        this.bundle = bundle;
    }

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                MoonFragment moonFragment = MoonFragment.newInstance(position);
                moonFragment.setArguments(bundle);
                return moonFragment;
            case 1:
                SunFragment sunFragment = SunFragment.newInstance(position);
                sunFragment.setArguments(bundle);
                return sunFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}