package org.uitagenda.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.uitagenda.R;
import org.uitagenda.activities.DetailActivity;
import org.uitagenda.adapters.EventAdapter;
import org.uitagenda.api.ApiHelper;
import org.uitagenda.asynctasks.EventDatabaseAsyncTask;
import org.uitagenda.contentproviders.EventContentProvider;
import org.uitagenda.model.Event;
import org.uitagenda.model.RootObject;
import org.uitagenda.model.UitEvent;
import org.uitagenda.utils.EventListenerImpl;
import org.uitagenda.utils.FavoritesHelper;
import org.uitagenda.utils.TrackerHelper;
import org.uitagenda.utils.TypefaceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Inneke on 11/08/2015.
 */
public class CurrentFragment extends Fragment implements EventAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, FavoritesHelper.FavoritesListener,
        LoaderManager.LoaderCallbacks<Cursor>
{
    private RelativeLayout layoutContent;
    private RecyclerView recyclerViewEvents;
    private ScrollView layoutEmpty;
    private TextView textViewEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EventAdapter eventAdapter;
    private LinearLayoutManager layoutManager;
    private GoogleApiClient apiClient;
    private LocationRequest locationRequest;
    private Handler handlerLocation;
    private Runnable callbackLocation;
    private int totalEvents;
    private boolean loadingData;
    private Location location;
    private boolean loadEventsFromDatabase;
    private boolean eventsLoaded;
    private boolean snackbarVisible;

    private static final long TIMER_LOCATION = 8000;
    private static final String KEY_ROTATED = "rotated";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_TOTAL_EVENTS = "total_events";
    private static final String KEY_SNACKBAR_VISIBLE = "snackbar_visible";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_current, container, false);

        this.layoutContent = (RelativeLayout) view.findViewById(R.id.layout_content);
        this.recyclerViewEvents = (RecyclerView) view.findViewById(R.id.recyclerView_events);
        this.layoutEmpty = (ScrollView) view.findViewById(R.id.layout_empty);
        this.textViewEmpty = (TextView) view.findViewById(R.id.textView_empty);
        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_events);

        this.loadingData = false;

        this.swipeRefreshLayout.setOnRefreshListener(this);

        this.eventAdapter = new EventAdapter(view.getContext(), false);
        this.eventAdapter.setListener(this);
        this.layoutManager = new LinearLayoutManager(view.getContext());
        this.recyclerViewEvents.setAdapter(this.eventAdapter);
        this.recyclerViewEvents.setLayoutManager(this.layoutManager);
        this.recyclerViewEvents.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                int visibleItemCount = CurrentFragment.this.layoutManager.getChildCount();
                int totalItemCount = CurrentFragment.this.layoutManager.getItemCount();
                int firstVisiblePosition = CurrentFragment.this.layoutManager.findFirstVisibleItemPosition();
                int topRowVerticalPosition = (recyclerView == null || totalItemCount == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                CurrentFragment.this.swipeRefreshLayout.setEnabled(firstVisiblePosition == 0 && topRowVerticalPosition >= 0);

                boolean atEndOfList = (firstVisiblePosition + visibleItemCount) > (totalItemCount - 4);
                boolean lastPage = totalItemCount == CurrentFragment.this.totalEvents;
                if (atEndOfList && !CurrentFragment.this.loadingData && !lastPage)
                    CurrentFragment.this.getEvents();
            }
        });

        this.location = savedInstanceState != null ? (Location) savedInstanceState.getParcelable(KEY_LOCATION) : null;
        this.eventAdapter.setLocation(this.location != null ? this.location.getLatitude() : 0, this.location != null ? this.location.getLongitude() : 0);
        this.totalEvents = savedInstanceState != null ? savedInstanceState.getInt(KEY_TOTAL_EVENTS) : 0;
        this.snackbarVisible = savedInstanceState != null && savedInstanceState.getBoolean(KEY_SNACKBAR_VISIBLE);
        this.loadEventsFromDatabase = savedInstanceState != null && savedInstanceState.getBoolean(KEY_ROTATED);
        if (!this.loadEventsFromDatabase)
        {
            EventDatabaseAsyncTask task = new EventDatabaseAsyncTask(view.getContext(), null, true);
            task.execute();

            this.swipeRefreshLayout.post(new Runnable()
            {
                @Override
                public void run()
                {
                    CurrentFragment.this.swipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        if (this.snackbarVisible)
            this.showLocationSnackbar(view.getContext());

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(view.getContext()) == ConnectionResult.SUCCESS)
        {
            this.apiClient = new GoogleApiClient.Builder(view.getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            this.locationRequest = new LocationRequest();
            this.locationRequest.setInterval(10000);
            this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        this.handlerLocation = new Handler();
        this.callbackLocation = new Runnable()
        {
            @Override
            public void run()
            {
                if (CurrentFragment.this.apiClient != null && CurrentFragment.this.apiClient.isConnected())
                {
                    LocationServices.FusedLocationApi.removeLocationUpdates(CurrentFragment.this.apiClient, CurrentFragment.this);
                    CurrentFragment.this.getEvents();
                }
            }
        };

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
        outState.putParcelable(KEY_LOCATION, this.location);
        outState.putInt(KEY_TOTAL_EVENTS, this.totalEvents);
        outState.putBoolean(KEY_SNACKBAR_VISIBLE, this.snackbarVisible);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (this.getActivity() != null && !this.getActivity().isFinishing())
            TrackerHelper.trackScreen(this.getActivity(), TrackerHelper.SCREEN_CURRENT);

        if (this.apiClient != null)
        {
            this.apiClient.connect();
        } else
        {
            this.updateView(null, R.string.no_events_location, true, true, true);
        }
    }

    @Override
    public void onStop()
    {
        if (this.apiClient != null && this.apiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.apiClient, this);
            this.apiClient.disconnect();
        }

        this.stopTimerLocation();

        this.swipeRefreshLayout.setRefreshing(false);

        super.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (this.loadEventsFromDatabase)
        {
            this.loadingData = true;
            this.updateView(null, 0, false, true, false);
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
        if (!this.eventsLoaded)
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

            this.updateView(events, R.string.no_events, true, false, false);
            this.loadingData = false;
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
        if (!this.loadingData && this.getActivity() != null && !this.getActivity().isFinishing())
        {
            this.location = null;
            this.eventAdapter.setLocation(0, 0);

            if (this.apiClient != null && this.apiClient.isConnected())
            {
                EventDatabaseAsyncTask task = new EventDatabaseAsyncTask(this.getActivity(), null, true);
                task.execute();

                this.updateView(null, 0, false, true, true);
                this.handleLocationUpdate();
            } else
            {
                this.updateView(null, R.string.no_events_location, true, true, true);
            }
        } else
        {
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        if (!this.loadingData && this.totalEvents == 0)
        {
            this.location = null;
            this.eventAdapter.setLocation(0, 0);
            this.handleLocationUpdate();
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        this.updateView(null, R.string.no_events_location, true, true, true);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        this.updateView(null, R.string.no_events_location, true, true, true);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        this.stopTimerLocation();

        if(this.apiClient != null && this.apiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.apiClient, this);
        }

        this.location = location;

        if (location != null)
        {
            this.eventAdapter.setLocation(location.getLatitude(), location.getLongitude());
            this.getEvents();
        } else
        {
            this.eventAdapter.setLocation(0, 0);
            this.updateView(null, R.string.no_events_location, true, true, true);
        }
    }

    private void handleLocationUpdate()
    {
        SnackbarManager.dismiss();

        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        if (this.getActivity() != null && !this.getActivity().isFinishing())
        {
            LocationManager lm = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }

        if (gpsEnabled || networkEnabled)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.apiClient, this.locationRequest, this);
            this.stopTimerLocation();
            this.startTimerLocation();
        } else
        {
            this.getEvents();
            if (this.getActivity() != null && !this.getActivity().isFinishing())
                this.showLocationSnackbar(this.getActivity());
        }
    }

    private void getEvents()
    {
        this.loadingData = true;
        String coordinates = this.location != null ? String.valueOf(this.location.getLatitude()) + "," + this.location.getLongitude() : null;
        String sort = TextUtils.isEmpty(coordinates) ? "startdate asc" : "geodist() asc";
        String locationType = TextUtils.isEmpty(coordinates) ? null : "physical_gis";
        String radius = TextUtils.isEmpty(coordinates) ? null : "10";

        ApiHelper.getServiceOAuth().getCurrentEvents(this.layoutManager.getItemCount(), coordinates, sort, locationType, radius, new Callback<RootObject>()
        {
            @Override
            public void success(RootObject rootObject, Response response)
            {
                CurrentFragment.this.loadingData = false;

                if (CurrentFragment.this.getActivity() != null && !CurrentFragment.this.getActivity().isFinishing())
                {
                    List<UitEvent> events = new ArrayList<>();

                    if (rootObject != null && rootObject.getEvents() != null)
                    {
                        for (Event event : rootObject.getEvents())
                            if (event != null && event.getEvent() != null)
                                events.add(new UitEvent(event.getEvent()));
                        CurrentFragment.this.totalEvents = rootObject.getTotalNumberOfEvents();
                        EventDatabaseAsyncTask task = new EventDatabaseAsyncTask(CurrentFragment.this.getActivity(), events, false);
                        task.execute();
                    }

                    CurrentFragment.this.updateView(events, R.string.no_events, true, false, false);
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                CurrentFragment.this.loadingData = false;
                CurrentFragment.this.updateView(null, R.string.no_events_internet, true, false, false);
            }
        });
    }

    private void updateView(List<UitEvent> events, int errorMessageResource, boolean stopRefreshing, boolean clearList, boolean clearTotalEvents)
    {
        if (clearTotalEvents)
        {
            this.totalEvents = 0;
        }

        if (clearList)
        {
            this.eventAdapter.clearList();
        } else
        {
            this.eventAdapter.addEvents(events);
        }

        if (stopRefreshing)
        {
            this.swipeRefreshLayout.setRefreshing(false);
        }

        boolean empty = this.eventAdapter.getItemCount() == 0;
        this.recyclerViewEvents.setVisibility(empty ? View.GONE : View.VISIBLE);
        this.layoutEmpty.setVisibility(empty && errorMessageResource != 0 ? View.VISIBLE : View.GONE);
        if (errorMessageResource != 0)
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
                if (favorite)
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

    public void startTimerLocation()
    {
        this.handlerLocation.postDelayed(this.callbackLocation, TIMER_LOCATION);
    }

    public void stopTimerLocation()
    {
        this.handlerLocation.removeCallbacks(this.callbackLocation);
    }

    private void showLocationSnackbar(Context context)
    {
        Snackbar snackbar = Snackbar.with(context)
                .text(R.string.current_no_location_message)
                .actionLabel(R.string.current_no_location_action)
                .actionListener(new ActionClickListener()
                {
                    @Override
                    public void onActionClicked(Snackbar snackbar)
                    {
                        if (CurrentFragment.this.getActivity() != null && !CurrentFragment.this.getActivity().isFinishing())
                        {
                            try
                            {
                                Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                CurrentFragment.this.startActivity(locationSettingsIntent);
                            } catch (ActivityNotFoundException e)
                            {
                                Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                                CurrentFragment.this.startActivity(settingsIntent);
                            }
                        }
                    }
                })
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .color(this.getResources().getColor(R.color.general_accent))
                .textColor(this.getResources().getColor(R.color.general_button_text))
                .actionColor(this.getResources().getColor(R.color.general_button_text))
                .textTypeface(TypefaceHelper.getTypefaceRegular(context))
                .actionLabelTypeface(TypefaceHelper.getTypefaceBold(context))
                .eventListener(new EventListenerImpl()
                {
                    @Override
                    public void onShow(Snackbar snackbar)
                    {
                        CurrentFragment.this.snackbarVisible = true;
                    }

                    @Override
                    public void onDismiss(Snackbar snackbar)
                    {
                        CurrentFragment.this.snackbarVisible = false;
                    }
                });

        SnackbarManager.show(snackbar, this.layoutContent);
    }
}
