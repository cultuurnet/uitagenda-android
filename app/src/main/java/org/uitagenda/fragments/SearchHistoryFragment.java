package org.uitagenda.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.uitagenda.R;
import org.uitagenda.activities.SearchResultActivity;
import org.uitagenda.adapters.SearchHistoryAdapter;
import org.uitagenda.contentproviders.SearchContentProvider;
import org.uitagenda.database.SearchTable;
import org.uitagenda.utils.Constants;
import org.uitagenda.utils.TrackerHelper;

/**
 * Created by Inneke on 11/08/2015.
 */
public class SearchHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SearchHistoryAdapter.OnItemClickListener
{
    private RecyclerView recyclerViewSearchHistory;
    private LinearLayout layoutEmpty;
    private SearchHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search_history, container, false);

        this.recyclerViewSearchHistory = (RecyclerView) view.findViewById(R.id.recyclerView_searchHistory);
        this.layoutEmpty = (LinearLayout) view.findViewById(R.id.layout_empty);

        this.adapter = new SearchHistoryAdapter();
        this.adapter.setListener(this);
        this.recyclerViewSearchHistory.setAdapter(this.adapter);
        this.recyclerViewSearchHistory.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.recyclerViewSearchHistory.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, 0)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                if (SearchHistoryFragment.this.getActivity() != null && !SearchHistoryFragment.this.getActivity().isFinishing())
                {
                    int searchId = SearchHistoryFragment.this.adapter.getSearchId(viewHolder.getAdapterPosition());
                    String selection = SearchTable.COLUMN_ID + " = " + searchId;
                    SearchHistoryFragment.this.getActivity().getContentResolver().delete(SearchContentProvider.CONTENT_URI, selection, null);
                }
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
            {
                return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
        });
        swipeToDismissTouchHelper.attachToRecyclerView(this.recyclerViewSearchHistory);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.getLoaderManager().restartLoader(Constants.LOADER_SEARCH, null, this);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if(this.getActivity() != null && !this.getActivity().isFinishing())
            TrackerHelper.trackScreen(this.getActivity(), TrackerHelper.SCREEN_SEARCH_HISTORY);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        switch (id)
        {
            case Constants.LOADER_SEARCH:
                return new CursorLoader(this.getView().getContext(), SearchContentProvider.CONTENT_URI, null, null, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        switch (loader.getId())
        {
            case Constants.LOADER_SEARCH:
                this.adapter.setCursor(data);

                boolean empty = data == null || data.getCount() == 0;
                this.recyclerViewSearchHistory.setVisibility(empty ? View.GONE : View.VISIBLE);
                this.layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        switch (loader.getId())
        {
            case Constants.LOADER_SEARCH:
                this.adapter.setCursor(null);
                this.recyclerViewSearchHistory.setVisibility(View.GONE);
                this.layoutEmpty.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onSearchClick(View caller, int position)
    {
        Cursor cursor = this.adapter.getCursor();

        if(cursor != null && cursor.moveToPosition(position))
        {
            String completeQuery = cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_COMPLETE_QUERY));
            String category = cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_CATEGORY));
            String term = cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_TERM));
            String sort = cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_SORT));
            String current = cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_CURRENT));
            String distance = cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_DISTANCE));
            String zipCode = cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_ZIP_CODE));
            String date = cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_DATE));
            String kids = cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_KIDS));
            String free = cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_FREE));
            String noCourses = cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_NO_COURSES));

            Intent intent = new Intent(caller.getContext(), SearchResultActivity.class);
            intent.putExtra(SearchResultActivity.KEY_COMPLETE_QUERY, completeQuery);
            intent.putExtra(SearchResultActivity.KEY_QUERY_CATEGORY, category);
            intent.putExtra(SearchResultActivity.KEY_QUERY_TERM, term);
            intent.putExtra(SearchResultActivity.KEY_QUERY_SORT, sort);
            intent.putExtra(SearchResultActivity.KEY_QUERY_CURRENT, current);
            intent.putExtra(SearchResultActivity.KEY_QUERY_DISTANCE, distance);
            intent.putExtra(SearchResultActivity.KEY_QUERY_ZIP_CODE, zipCode);
            intent.putExtra(SearchResultActivity.KEY_QUERY_DATE, date);
            intent.putExtra(SearchResultActivity.KEY_QUERY_KIDS, kids);
            intent.putExtra(SearchResultActivity.KEY_QUERY_FREE, free);
            intent.putExtra(SearchResultActivity.KEY_QUERY_NO_COURSES, noCourses);
            intent.putExtra(SearchResultActivity.KEY_OFFER_SAVE, false);
            this.startActivity(intent);
        }
    }
}
