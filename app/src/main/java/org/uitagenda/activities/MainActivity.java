package org.uitagenda.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import org.uitagenda.BuildConfig;
import org.uitagenda.R;
import org.uitagenda.adapters.DrawerAdapter;
import org.uitagenda.fragments.AboutFragment;
import org.uitagenda.fragments.CurrentFragment;
import org.uitagenda.fragments.FavoritesFragment;
import org.uitagenda.fragments.SearchFragment;
import org.uitagenda.fragments.SearchHistoryFragment;

/**
 * Created by Inneke on 11/08/2015.
 */
public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemClickListener
{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int fragmentPosition;
    private static final String KEY_POSITION = "selected_position";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());
        if(!this.getResources().getBoolean(R.bool.tablet))
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.drawerLayout = (DrawerLayout) this.findViewById(R.id.drawerLayout);
        RecyclerView recyclerViewDrawer = (RecyclerView) this.findViewById(R.id.recyclerView_drawer);

        this.setSupportActionBar(toolbar);

        if (this.drawerLayout != null)
        {
            this.drawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            this.drawerLayout.setDrawerListener(this.drawerToggle);
            this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
        }

        DrawerAdapter drawerAdapter = new DrawerAdapter(this, this);
        recyclerViewDrawer.setAdapter(drawerAdapter);
        recyclerViewDrawer.setLayoutManager(new LinearLayoutManager(this));

        int selectedPosition = savedInstanceState != null ? savedInstanceState.getInt(KEY_POSITION, DrawerAdapter.POSITION_CURRENT) : DrawerAdapter.POSITION_CURRENT;

        Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment == null)
            this.switchFragment(selectedPosition, this.getResources().getStringArray(R.array.drawer_items)[selectedPosition]);
        else
            this.switchFragment(fragment, selectedPosition, this.getResources().getStringArray(R.array.drawer_items)[selectedPosition]);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(KEY_POSITION, this.fragmentPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        if (this.drawerLayout != null)
            this.drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem favorites = menu.findItem(R.id.action_favorites);
        MenuItem search = menu.findItem(R.id.action_search);

        if(favorites != null)
            favorites.setVisible(this.fragmentPosition != DrawerAdapter.POSITION_FAVORITES);

        if(search != null)
            search.setVisible(this.fragmentPosition != DrawerAdapter.POSITION_SEARCH);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (this.drawerLayout != null && this.drawerToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId())
        {
            case R.id.action_favorites:
                this.switchFragment(DrawerAdapter.POSITION_FAVORITES, this.getResources().getStringArray(R.array.drawer_items)[DrawerAdapter.POSITION_FAVORITES]);
                return true;

            case R.id.action_search:
                this.switchFragment(DrawerAdapter.POSITION_SEARCH, this.getResources().getStringArray(R.array.drawer_items)[DrawerAdapter.POSITION_SEARCH]);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (this.drawerLayout != null)
            this.drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed()
    {
        if (this.drawerLayout != null && this.drawerLayout.isDrawerOpen(Gravity.LEFT))
        {
            this.drawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onDrawerItemClick(int position, String title)
    {
        this.switchFragment(position, title);

        if (this.drawerLayout != null)
        {
            this.drawerLayout.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    MainActivity.this.drawerLayout.closeDrawer(Gravity.LEFT);
                }
            }, 250);
        }
    }

    private void switchFragment(int position, String title)
    {
        Fragment fragment;

        switch (position)
        {
            case DrawerAdapter.POSITION_CURRENT:
                fragment = new CurrentFragment();
                break;
            case DrawerAdapter.POSITION_FAVORITES:
                fragment = new FavoritesFragment();
                break;
            case DrawerAdapter.POSITION_SEARCH:
                fragment = new SearchFragment();
                break;
            case DrawerAdapter.POSITION_SEARCH_HISTORY:
                fragment = new SearchHistoryFragment();
                break;
            case DrawerAdapter.POSITION_ABOUT:
                fragment = new AboutFragment();
                break;
            default:
                fragment = null;
                break;
        }

        this.switchFragment(fragment, position, title);
    }

    private void switchFragment(Fragment fragment, int position, String title)
    {
        if (fragment != null)
        {
            if(this.getSupportActionBar() != null)
                this.getSupportActionBar().setTitle(title);

            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();

            this.fragmentPosition = position;
            this.supportInvalidateOptionsMenu();
        }
    }
}
