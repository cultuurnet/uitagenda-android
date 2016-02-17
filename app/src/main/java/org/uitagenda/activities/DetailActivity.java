package org.uitagenda.activities;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.uitagenda.R;
import org.uitagenda.adapters.DetailPagerAdapter;
import org.uitagenda.contentproviders.EventContentProvider;
import org.uitagenda.database.EventTable;
import org.uitagenda.utils.Constants;
import org.uitagenda.utils.TrackerHelper;

/**
 * Created by Inneke on 18/08/2015.
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener
{
    private Button buttonPrevious;
    private Button buttonNext;
    private ViewPager viewPager;
    private LinearLayout layoutNavigation;
    private DetailPagerAdapter pagerAdapter;
    private boolean positionSet;
    private Cursor cursor;

    public static final String KEY_SELECTED_POSITION = "selected_position";
    public static final String KEY_NOT_STARTED_FROM_MAIN = "not_started_from_main";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(!this.getResources().getBoolean(R.bool.tablet))
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.viewPager = (ViewPager) this.findViewById(R.id.viewPager_detail);
        this.buttonPrevious = (Button) this.findViewById(R.id.button_previous);
        this.buttonNext = (Button) this.findViewById(R.id.button_next);
        this.layoutNavigation = (LinearLayout) this.findViewById(R.id.layout_navigation);

        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.buttonPrevious.setOnClickListener(this);
        this.buttonNext.setOnClickListener(this);

        this.pagerAdapter = new DetailPagerAdapter(this.getSupportFragmentManager());
        this.viewPager.setAdapter(this.pagerAdapter);
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                DetailActivity.this.updateButtons(position);
                if(DetailActivity.this.cursor != null && DetailActivity.this.cursor.moveToPosition(position))
                    DetailActivity.this.setTitle(DetailActivity.this.cursor.getString(DetailActivity.this.cursor.getColumnIndex(EventTable.COLUMN_TITLE)));
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });

        this.getSupportLoaderManager().restartLoader(Constants.LOADER_EVENTS, null, this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        TrackerHelper.trackScreen(this, TrackerHelper.SCREEN_DETAIL);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        switch (id)
        {
            case Constants.LOADER_EVENTS:
                return new CursorLoader(this, EventContentProvider.CONTENT_URI, null, null, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        switch (loader.getId())
        {
            case Constants.LOADER_EVENTS:
                this.cursor = data;
                this.pagerAdapter.swapCursor(data);
                if(!this.positionSet)
                {
                    int position = this.getIntent().getExtras().getInt(KEY_SELECTED_POSITION);
                    this.viewPager.setCurrentItem(position, false);
                    this.updateButtons(position);
                    if(this.cursor != null && this.cursor.moveToPosition(position))
                        this.setTitle(this.cursor.getString(this.cursor.getColumnIndex(EventTable.COLUMN_TITLE)));
                    this.positionSet = true;
                }
                this.layoutNavigation.setVisibility(data != null && data.getCount() > 1 ? View.VISIBLE : View.GONE);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        switch (loader.getId())
        {
            case Constants.LOADER_EVENTS:
                this.cursor = null;
                this.pagerAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_previous:
                this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() - 1);
                break;

            case R.id.button_next:
                this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() + 1);
                break;
        }
    }

    private void updateButtons(int position)
    {
        this.buttonPrevious.setVisibility(position > 0 ? View.VISIBLE : View.INVISIBLE);
        this.buttonNext.setVisibility(position < this.viewPager.getAdapter().getCount() - 1 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (this.getIntent().getBooleanExtra(KEY_NOT_STARTED_FROM_MAIN, false))
                    this.finish();
                else
                    NavUtils.navigateUpFromSameTask(this);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
