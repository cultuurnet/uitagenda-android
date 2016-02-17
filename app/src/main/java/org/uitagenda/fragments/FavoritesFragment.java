package org.uitagenda.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.uitagenda.R;
import org.uitagenda.activities.DetailActivity;
import org.uitagenda.adapters.EventAdapter;
import org.uitagenda.api.ApiHelper;
import org.uitagenda.asynctasks.EventDatabaseAsyncTask;
import org.uitagenda.contentproviders.EventContentProvider;
import org.uitagenda.model.Event;
import org.uitagenda.model.RootObject;
import org.uitagenda.model.UitEvent;
import org.uitagenda.utils.FavoritesHelper;
import org.uitagenda.utils.TrackerHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Inneke on 11/08/2015.
 */
public class FavoritesFragment extends Fragment implements EventAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
        FavoritesHelper.FavoritesListener, LoaderManager.LoaderCallbacks<Cursor>
{
    private RecyclerView recyclerViewEvents;
    private ScrollView layoutEmpty;
    private TextView textViewEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EventAdapter eventAdapter;
    private LinearLayoutManager layoutManager;
    private boolean loadEventsFromDatabase;
    private boolean eventsLoaded;

    private static final String KEY_ROTATED = "rotated";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        this.recyclerViewEvents = (RecyclerView) view.findViewById(R.id.recyclerView_events);
        this.layoutEmpty = (ScrollView) view.findViewById(R.id.layout_empty);
        this.textViewEmpty = (TextView) view.findViewById(R.id.textView_empty);
        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_events);

        this.swipeRefreshLayout.setOnRefreshListener(this);

        this.eventAdapter = new EventAdapter(view.getContext(), true);
        this.eventAdapter.setListener(this);
        this.layoutManager = new LinearLayoutManager(view.getContext());
        this.recyclerViewEvents.setAdapter(this.eventAdapter);
        this.recyclerViewEvents.setLayoutManager(this.layoutManager);
        this.recyclerViewEvents.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                int totalItemCount = FavoritesFragment.this.layoutManager.getItemCount();
                int firstVisiblePosition = FavoritesFragment.this.layoutManager.findFirstVisibleItemPosition();
                int topRowVerticalPosition = (recyclerView == null || totalItemCount == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                FavoritesFragment.this.swipeRefreshLayout.setEnabled(firstVisiblePosition == 0 && topRowVerticalPosition >= 0);
            }
        });

        this.loadEventsFromDatabase = savedInstanceState != null && savedInstanceState.getBoolean(KEY_ROTATED);
        if(!this.loadEventsFromDatabase)
        {
            EventDatabaseAsyncTask task = new EventDatabaseAsyncTask(view.getContext(), null, true);
            task.execute();

            String favorites = FavoritesHelper.getAllFavorites(this.getActivity());
            if(TextUtils.isEmpty(favorites))
            {
                this.updateView(null, R.string.no_events_favorites, true, true);
            }
            else
            {
                this.swipeRefreshLayout.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        FavoritesFragment.this.swipeRefreshLayout.setRefreshing(true);
                    }
                });
                this.getEvents(favorites);
            }
        }
        else
        {
            this.swipeRefreshLayout.post(new Runnable()
            {
                @Override
                public void run()
                {
                    FavoritesFragment.this.swipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        FavoritesHelper.addListener(this);

        return view;
    }

    @Override
    public void onDestroyView()
    {
        FavoritesHelper.removeListener(this);

        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putBoolean(KEY_ROTATED, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if(this.getActivity() != null && !this.getActivity().isFinishing())
            TrackerHelper.trackScreen(this.getActivity(), TrackerHelper.SCREEN_FAVORITES);
    }

    @Override
    public void onStop()
    {
        this.swipeRefreshLayout.setRefreshing(false);

        super.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(this.loadEventsFromDatabase)
        {
            this.updateView(null, 0, false, true);
            this.getLoaderManager().restartLoader(1, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new CursorLoader(this.getView().getContext(), EventContentProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if(!this.eventsLoaded)
        {
            List<UitEvent> events = new ArrayList<>();

            if (data != null && data.moveToFirst())
            {
                do
                {
                    events.add(UitEvent.constructFromCursor(data));
                }
                while (data.moveToNext());
            }

            this.updateView(events, R.string.no_events, true, false);
            this.eventsLoaded = true;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    @Override
    public void onRefresh()
    {
        if(this.getActivity() != null && !this.getActivity().isFinishing())
        {
            EventDatabaseAsyncTask task = new EventDatabaseAsyncTask(this.getActivity(), null, true);
            task.execute();

            String favorites = FavoritesHelper.getAllFavorites(this.getActivity());
            if(TextUtils.isEmpty(favorites))
            {
                this.updateView(null, R.string.no_events_favorites, true, true);
            }
            else
            {
                this.getEvents(favorites);
            }
        }
        else
        {
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void getEvents(String favorites)
    {
        this.updateView(null, 0, false, true);

        ApiHelper.getServiceOAuth().getFavoriteEvents("cdbid:" + favorites, new Callback<RootObject>()
        {
            @Override
            public void success(RootObject rootObject, Response response)
            {
                if(FavoritesFragment.this.getActivity() != null && !FavoritesFragment.this.getActivity().isFinishing())
                {
                    List<UitEvent> events = new ArrayList<>();

                    if (rootObject != null && rootObject.getEvents() != null)
                    {
                        for (Event event : rootObject.getEvents())
                            if (event != null && event.getEvent() != null)
                                events.add(new UitEvent(event.getEvent()));
                        EventDatabaseAsyncTask task = new EventDatabaseAsyncTask(FavoritesFragment.this.getActivity(), events, false);
                        task.execute();
                    }

                    FavoritesFragment.this.updateView(events, R.string.no_events, true, false);
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                FavoritesFragment.this.updateView(null, R.string.no_events_internet, true, false);
            }
        });
    }

    private void updateView(List<UitEvent> events, int errorMessageResource, boolean stopRefreshing, boolean clearList)
    {
        if(clearList)
        {
            this.eventAdapter.clearList();
        }
        else
        {
            this.eventAdapter.addEvents(events);
        }

        if(stopRefreshing)
        {
            this.swipeRefreshLayout.setRefreshing(false);
        }

        boolean empty = this.eventAdapter.getItemCount() == 0;
        this.recyclerViewEvents.setVisibility(empty ? View.GONE : View.VISIBLE);
        this.layoutEmpty.setVisibility(empty && errorMessageResource != 0 ? View.VISIBLE : View.GONE);
        if(errorMessageResource != 0)
            this.textViewEmpty.setText(errorMessageResource);
    }

    @Override
    public void onEventClick(View caller, int position)
    {
        switch (caller.getId())
        {
            case R.id.imageView_favorite:
                String cdbid = this.eventAdapter.getCdbid(position);
                boolean favorite = !TextUtils.isEmpty(cdbid) && FavoritesHelper.isFavorite(caller.getContext(), cdbid);
                if(favorite)
                    FavoritesHelper.removeFavorite(caller.getContext(), cdbid);
                else
                    FavoritesHelper.addFavorite(caller.getContext(), cdbid);
                break;

            default:
                Intent intent = new Intent(caller.getContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.KEY_SELECTED_POSITION, position);
                this.startActivity(intent);
                break;
        }
    }

    @Override
    public void onFavoriteAdded(String cdbid)
    {
        this.eventAdapter.updateEvent(cdbid);
    }

    @Override
    public void onFavoriteRemoved(String cdbid)
    {
        this.eventAdapter.updateEvent(cdbid);
    }
}
