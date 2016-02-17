package org.uitagenda.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.uitagenda.R;
import org.uitagenda.fragments.SearchResultFragment;

/**
 * Created by Inneke on 21/08/2015.
 */
public class SearchResultActivity extends AppCompatActivity
{
    public static final String KEY_COMPLETE_QUERY = SearchResultFragment.KEY_COMPLETE_QUERY;
    public static final String KEY_QUERY_CATEGORY = SearchResultFragment.KEY_QUERY_CATEGORY;
    public static final String KEY_QUERY_TERM = SearchResultFragment.KEY_QUERY_TERM;
    public static final String KEY_QUERY_SORT = SearchResultFragment.KEY_QUERY_SORT;
    public static final String KEY_QUERY_CURRENT = SearchResultFragment.KEY_QUERY_CURRENT;
    public static final String KEY_QUERY_DISTANCE = SearchResultFragment.KEY_QUERY_DISTANCE;
    public static final String KEY_QUERY_ZIP_CODE = SearchResultFragment.KEY_QUERY_ZIP_CODE;
    public static final String KEY_QUERY_DATE = SearchResultFragment.KEY_QUERY_DATE;
    public static final String KEY_QUERY_KIDS = SearchResultFragment.KEY_QUERY_KIDS;
    public static final String KEY_QUERY_FREE = SearchResultFragment.KEY_QUERY_FREE;
    public static final String KEY_QUERY_NO_COURSES = SearchResultFragment.KEY_QUERY_NO_COURSES;
    public static final String KEY_OFFER_SAVE = SearchResultFragment.KEY_OFFER_SAVE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(!this.getResources().getBoolean(R.bool.tablet))
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_search_result);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(this.getIntent().getExtras());

        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }
}
