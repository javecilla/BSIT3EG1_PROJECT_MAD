package com.example.project;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Adapter for ViewPager2 to manage Playground and My Activity fragments
 */
public class ViewPagerAdapter extends FragmentStateAdapter {
    
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PlaygroundFragment();
            case 1:
                return new MyActivityFragment();
            default:
                return new PlaygroundFragment();
        }
    }
    
    @Override
    public int getItemCount() {
        return 2; // Two tabs: PlayGround and My Activity
    }
}

