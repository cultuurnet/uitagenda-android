package org.uitagenda.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
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
import org.uitagenda.asynctasks.SearchDatabaseAsyncTask;
import org.uitagenda.database.SearchTable;
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
 * Created by Inneke on 21/08/2015.
 */
public class SearchResultFragment extends Fragment implements EventAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, FavoritesHelper.FavoritesListener
{
    private RelativeLayout layoutContent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewEvents;
    private ScrollView layoutEmpty;
    private TextView textViewEmpty;
    private EventAdapter eventAdapter;
    private LinearLayoutManager layoutManager;
    private GoogleApiClient apiClient;
    private LocationRequest locationRequest;
    private Handler handlerLocation;
    private Runnable callbackLocation;
    private int totalEvents;
    private boolean loadingData;
    private double latitude;
    private double longitude;
    private boolean current;

    private static final long TIMER_LOCATION = 5000;

    public static final String KEY_COMPLETE_QUERY = "complete_query";
    public static final String KEY_QUERY_CATEGORY = "query_category";
    public static final String KEY_QUERY_TERM = "query_term";
    public static final String KEY_QUERY_SORT = "query_sort";
    public static final String KEY_QUERY_CURRENT = "query_current";
    public static final String KEY_QUERY_DISTANCE = "query_distance";
    public static final String KEY_QUERY_ZIP_CODE = "query_zip_code";
    public static final String KEY_QUERY_DATE = "query_date";
    public static final String KEY_QUERY_KIDS = "query_kids";
    public static final String KEY_QUERY_FREE = "query_free";
    public static final String KEY_QUERY_NO_COURSES = "query_no_courses";
    public static final String KEY_OFFER_SAVE = "offer_save";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        this.layoutContent = (RelativeLayout) view.findViewById(R.id.layout_content);
        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_events);
        this.recyclerViewEvents = (RecyclerView) view.findViewById(R.id.recyclerView_events);
        this.layoutEmpty = (ScrollView) view.findViewById(R.id.layout_empty);
        this.textViewEmpty = (TextView) view.findViewById(R.id.textView_empty);

        this.loadingData = false;
        this.current = this.getArguments().getString(KEY_QUERY_CURRENT) != null;

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
                int visibleItemCount = SearchResultFragment.this.layoutManager.getChildCount();
                int totalItemCount = SearchResultFragment.this.layoutManager.getItemCount();
                int firstVisiblePosition = SearchResultFragment.this.layoutManager.findFirstVisibleItemPosition();
                int topRowVerticalPosition = (recyclerView == null || totalItemCount == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                SearchResultFragment.this.swipeRefreshLayout.setEnabled(firstVisiblePosition == 0 && topRowVerticalPosition >= 0);

                boolean atEndOfList = (firstVisiblePosition + visibleItemCount) > (totalItemCount - 4);
                boolean lastPage = totalItemCount == SearchResultFragment.this.totalEvents;
                if(atEndOfList && !SearchResultFragment.this.loadingData && !lastPage)
                    SearchResultFragment.this.getEvents();
            }
        });

        this.swipeRefreshLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                SearchResultFragment.this.swipeRefreshLayout.setRefreshing(true);
            }
        });

        EventDatabaseAsyncTask task = new EventDatabaseAsyncTask(view.getContext(), null, true);
        task.execute();

        if(this.current)
        {
            if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(view.getContext()) == ConnectionResult.SUCCESS)
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
        }
        else
        {
            this.getEvents();
        }

        this.handlerLocation = new Handler();
        this.callbackLocation = new Runnable()
        {
            @Override
            public void run()
            {
                if(SearchResultFragment.this.apiClient != null && SearchResultFragment.this.apiClient.isConnected())
                {
                    LocationServices.FusedLocationApi.removeLocationUpdates(SearchResultFragment.this.apiClient, SearchResultFragment.this);
                    SearchResultFragment.this.updateView(null, R.string.no_events_location, true, true);
                }
            }
        };

        if(this.getArguments().getBoolean(KEY_OFFER_SAVE, false))
            this.showSnackbar(view.getContext());

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
    public void onStart()
    {
        super.onStart();

        if(this.getActivity() != null && !this.getActivity().isFinishing())
            TrackerHelper.trackScreen(this.getActivity(), TrackerHelper.SCREEN_SEARCH_RESULTS);

        if(this.current)
        {
            if (this.apiClient != null)
            {
                this.apiClient.connect();
            } else
            {
                this.updateView(null, R.string.no_events_location, true, true);
            }
        }
    }

    @Override
    public void onStop()
    {
        if(this.apiClient != null && this.apiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.apiClient, this);
            this.apiClient.disconnect();
        }

        this.stopTimerLocation();

        this.swipeRefreshLayout.setRefreshing(false);

        super.onStop();
    }

    @Override
    public void onRefresh()
    {
        if(!this.loadingData && this.getActivity() != null && !this.getActivity().isFinishing())
        {
            if(this.current)
            {
                if (this.apiClient != null && this.apiClient.isConnected())
                {
                    EventDatabaseAsyncTask task = new EventDatabaseAsyncTask(this.getActivity(), null, true);
                    task.execute();

                    this.updateView(null, 0, false, true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(this.apiClient, this.locationRequest, this);
                    this.stopTimerLocation();
                    this.startTimerLocation();
                } else
                {
                    this.updateView(null, R.string.no_events_location, true, true);
                }
            }
            else
            {
                EventDatabaseAsyncTask task = new EventDatabaseAsyncTask(this.getActivity(), null, true);
                task.execute();

                this.updateView(null, 0, false, true);
                this.getEvents();
            }
        }
        else
        {
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        if(!this.loadingData && this.totalEvents == 0)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.apiClient, this.locationRequest, this);
            this.stopTimerLocation();
            this.startTimerLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        this.updateView(null, R.string.no_events_location, true, true);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        this.updateView(null, R.string.no_events_location, true, true);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        this.stopTimerLocation();
        LocationServices.FusedLocationApi.removeLocationUpdates(this.apiClient, this);

        if(location != null)
        {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
            this.eventAdapter.setLocation(this.latitude, this.longitude);
            this.getEvents();
        }
        else
        {
            this.updateView(null, R.string.no_events_location, true, true);
        }
    }

    private void getEvents()
    {
        this.loadingData = true;

        String category = this.getArguments().getString(KEY_QUERY_CATEGORY);
        String term = this.getArguments().getString(KEY_QUERY_TERM);
        String sort = this.getArguments().getString(KEY_QUERY_SORT);
        String current = this.getArguments().getString(KEY_QUERY_CURRENT);
        String distance = this.getArguments().getString(KEY_QUERY_DISTANCE);
        String zipCode = this.getArguments().getString(KEY_QUERY_ZIP_CODE);
        String date = this.getArguments().getString(KEY_QUERY_DATE);
        String kids = this.getArguments().getString(KEY_QUERY_KIDS);
        String free = this.getArguments().getString(KEY_QUERY_FREE);
        String noCourses = this.getArguments().getString(KEY_QUERY_NO_COURSES);
        String latLong = this.current ? this.latitude+","+this.longitude : null;

        List<String> fq = new ArrayList<>();
        if(!TextUtils.isEmpty(category))
            fq.add(category);
        if(!TextUtils.isEmpty(kids))
            fq.add(kids);
        if(!TextUtils.isEmpty(free))
            fq.add(free);
        if(!TextUtils.isEmpty(noCourses))
            fq.add(noCourses);

        ApiHelper.getServiceOAuth().getFilteredEvents(this.layoutManager.getItemCount(), term, sort, current, latLong, distance, zipCode, date, fq, new Callback<RootObject>()
        {
            @Override
            public void success(RootObject rootObject, Response response)
            {
                SearchResultFragment.this.loadingData = false;

                if (SearchResultFragment.this.getActivity() != null && !SearchResultFragment.this.getActivity().isFinishing())
                {
                    List<UitEvent> events = new ArrayList<>();

                    if (rootObject != null && rootObject.getEvents() != null)
                    {
                        for (Event event : rootObject.getEvents())
                            if (event != null && event.getEvent() != null)
                                events.add(new UitEvent(event.getEvent()));
                        SearchResultFragment.this.totalEvents = rootObject.getTotalNumberOfEvents();
                        EventDatabaseAsyncTask task = new EventDatabaseAsyncTask(SearchResultFragment.this.getActivity(), events, false);
                        task.execute();
                    }

                    SearchResultFragment.this.updateView(events, R.string.no_events, true, false);
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                SearchResultFragment.this.loadingData = false;
                SearchResultFragment.this.updateView(null, R.string.no_events_internet, true, false);
            }
        });
    }

    private void updateView(List<UitEvent> events, int errorMessageResource, boolean stopRefreshing, boolean clearList)
    {
        if(clearList)
        {
            this.eventAdapter.clearList();
            this.totalEvents = 0;
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
                intent.putExtra(DetailActivity.KEY_NOT_STARTED_FROM_MAIN, true);
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

    private void showSnackbar(Context context)
    {
        Typeface typefaceRegular = TypefaceHelper.getTypefaceRegular(context);
        Typeface typefaceBold = TypefaceHelper.getTypefaceBold(context);

        Snackbar snackbar = Snackbar.with(context)
                .text(R.string.search_results_save_message)
                .actionLabel(R.string.search_results_save_action)
                .actionListener(new ActionClickListener()
                {
                    @Override
                    public void onActionClicked(Snackbar snackbar)
                    {
                        if (SearchResultFragment.this.getActivity() != null && !SearchResultFragment.this.getActivity().isFinishing())
                        {
                            SearchDatabaseAsyncTask task = new SearchDatabaseAsyncTask(SearchResultFragment.this.getActivity(), SearchResultFragment.this.getContentValues());
                            task.execute();
                            SearchResultFragment.this.showConfirmSnackbar(SearchResultFragment.this.getActivity());
                        }
                    }
                })
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .color(this.getResources().getColor(R.color.general_accent))
                .textColor(this.getResources().getColor(R.color.general_button_text))
                .actionColor(this.getResources().getColor(R.color.general_button_text))
                .textTypeface(typefaceRegular)
                .actionLabelTypeface(typefaceBold)
                .eventListener(new EventListenerImpl()
                {
                    @Override
                    public void onShow(Snackbar snackbar)
                    {
                        if(SearchResultFragment.this.getActivity() != null && !SearchResultFragment.this.getResources().getBoolean(R.bool.tablet))
                            SearchResultFragment.this.showUpAnimation(snackbar.getHeight());
                    }

                    @Override
                    public void onDismiss(Snackbar snackbar)
                    {
                        if(SearchResultFragment.this.getActivity() != null && !SearchResultFragment.this.getResources().getBoolean(R.bool.tablet))
                            SearchResultFragment.this.showDownAnimation();
                    }
                });

        SnackbarManager.show(snackbar, this.layoutContent);
    }

    private void showConfirmSnackbar(Context context)
    {
        Typeface typefaceRegular = TypefaceHelper.getTypefaceRegular(context);

        Snackbar snackbar = Snackbar.with(context)
                .text(R.string.search_results_saved_message)
                .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                .color(this.getResources().getColor(R.color.snackbar_confirm))
                .textColor(this.getResources().getColor(R.color.general_button_text))
                .textTypeface(typefaceRegular)
                .eventListener(new EventListenerImpl()
                {
                    @Override
                    public void onShow(Snackbar snackbar)
                    {
                        if(SearchResultFragment.this.getActivity() != null && !SearchResultFragment.this.getResources().getBoolean(R.bool.tablet))
                            SearchResultFragment.this.showUpAnimation(snackbar.getHeight());
                    }

                    @Override
                    public void onDismiss(Snackbar snackbar)
                    {
                        if(SearchResultFragment.this.getActivity() != null && !SearchResultFragment.this.getResources().getBoolean(R.bool.tablet))
                            SearchResultFragment.this.showDownAnimation();
                    }
                });

        SnackbarManager.show(snackbar, this.layoutContent);
    }

    private ContentValues getContentValues()
    {
        ContentValues cv = new ContentValues();

        cv.put(SearchTable.COLUMN_COMPLETE_QUERY, this.getArguments().getString(KEY_COMPLETE_QUERY));
        cv.put(SearchTable.COLUMN_CATEGORY, this.getArguments().getString(KEY_QUERY_CATEGORY));
        cv.put(SearchTable.COLUMN_TERM, this.getArguments().getString(KEY_QUERY_TERM));
        cv.put(SearchTable.COLUMN_SORT, this.getArguments().getString(KEY_QUERY_SORT));
        cv.put(SearchTable.COLUMN_CURRENT, this.getArguments().getString(KEY_QUERY_CURRENT));
        cv.put(SearchTable.COLUMN_DISTANCE, this.getArguments().getString(KEY_QUERY_DISTANCE));
        cv.put(SearchTable.COLUMN_ZIP_CODE, this.getArguments().getString(KEY_QUERY_ZIP_CODE));
        cv.put(SearchTable.COLUMN_DATE, this.getArguments().getString(KEY_QUERY_DATE));
        cv.put(SearchTable.COLUMN_KIDS, this.getArguments().getString(KEY_QUERY_KIDS));
        cv.put(SearchTable.COLUMN_FREE, this.getArguments().getString(KEY_QUERY_FREE));
        cv.put(SearchTable.COLUMN_NO_COURSES, this.getArguments().getString(KEY_QUERY_NO_COURSES));

        return cv;
    }

    private void showUpAnimation(final int endMargin)
    {
        this.recyclerViewEvents.clearAnimation();

        final int startMargin = ((RelativeLayout.LayoutParams) this.recyclerViewEvents.getLayoutParams()).bottomMargin;

        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t)
            {
                int newMargin = startMargin + (int) ((endMargin - startMargin) * interpolatedTime);
                RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) SearchResultFragment.this.recyclerViewEvents.getLayoutParams());
                params.bottomMargin = newMargin;
                SearchResultFragment.this.recyclerViewEvents.setLayoutParams(params);
            }

            @Override
            public boolean willChangeBounds()
            {
                return true;
            }
        };
        animation.setDuration(300);

        this.recyclerViewEvents.startAnimation(animation);
    }

    private void showDownAnimation()
    {
        this.recyclerViewEvents.clearAnimation();

        final int startMargin = ((RelativeLayout.LayoutParams) this.recyclerViewEvents.getLayoutParams()).bottomMargin;

        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t)
            {
                int newMargin = (int) (startMargin * (1 - interpolatedTime));
                RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) SearchResultFragment.this.recyclerViewEvents.getLayoutParams());
                params.bottomMargin = newMargin;
                SearchResultFragment.this.recyclerViewEvents.setLayoutParams(params);
            }

            @Override
            public boolean willChangeBounds()
            {
                return true;
            }
        };
        animation.setDuration(300);

        this.recyclerViewEvents.startAnimation(animation);
    }
}
