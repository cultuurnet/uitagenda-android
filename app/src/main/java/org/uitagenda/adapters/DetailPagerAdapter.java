package org.uitagenda.adapters;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.uitagenda.fragments.DetailFragment;
import org.uitagenda.model.UitEvent;

/**
 * Created by Inneke on 18/08/2015.
 */
public class DetailPagerAdapter extends FragmentStatePagerAdapter
{
    private Cursor cursor;

    public DetailPagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
    }

    public void swapCursor(Cursor cursor)
    {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return this.cursor != null ? this.cursor.getCount() : 0;
    }

    @Override
    public Fragment getItem(int position)
    {
        this.cursor.moveToPosition(position);
        UitEvent event = UitEvent.constructFromCursor(this.cursor);

        Bundle bundle = new Bundle();
        bundle.putSerializable(DetailFragment.KEY_EVENT, event);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
