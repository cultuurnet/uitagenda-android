package org.uitagenda;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uitagenda.apiclient.ApiClientOAuth;
import org.uitagenda.model.UiTEvent;
import org.uitagenda.utils.UiTEventAdapter;
import org.uitagenda.utils.UiTSearchDataSource;
import org.uitagenda.utils.UiTagenda;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

/**
 * Created by alexf_000 on 1/17/14.
 */
public class SearchResultFragment extends Fragment implements AbsListView.OnScrollListener {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean haveAlreadyReceivedCoordinates = false;
    private double locationLatitude, locationLongitude;

    private FetchEvents fetchEvents;
    LinearLayout ll_saveOrDismiss, llNoResults;

    public UiTEventAdapter uiTEventAdapter;
    ArrayList<UiTEvent> eventList;
    ListView listView;

    ProgressDialog progress;
    ProgressBar pbFooter;

    TextView tvFooter;
    AlertDialog.Builder alert;

    int start, totalEvents;
    boolean loadMore, checkCurrentLocation, queryAlreadySaved;

    String searchQuery, saveSearchString;
    View rootView;

    public static SearchResultFragment newInstance() {
        SearchResultFragment fragment = new SearchResultFragment();
        return fragment;
    }

    public SearchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        UiTagenda.trackGoogleAnalytics(getActivity(), "Android: Zoeken");
        Crashlytics.setString("ClassName", this.getClass().getSimpleName());

        rootView = inflater.inflate(R.layout.fragment_searchresults, container, false);

        listView = (ListView) rootView.findViewById(R.id.lv_searchresults);

        tvFooter = new TextView(getActivity());
        tvFooter.setText(getString(R.string.nomore_results));
        tvFooter.setGravity(Gravity.CENTER);

        ll_saveOrDismiss = (LinearLayout) rootView.findViewById(R.id.ll_saveordismiss);
        llNoResults = (LinearLayout) rootView.findViewById(R.id.ll_noresults);

        listView.setEmptyView(llNoResults);
        rootView.setVisibility(View.INVISIBLE);

        checkCurrentLocation = getActivity().getIntent().getExtras().getBoolean("currentLocation");
        queryAlreadySaved = getActivity().getIntent().getExtras().getBoolean("queryAlreadySaved", false);
        searchQuery = getActivity().getIntent().getExtras().getString("searchQuery");
        saveSearchString = getActivity().getIntent().getExtras().getString("saveQueryString");

        Crashlytics.log("checkCurrentLocation :" + checkCurrentLocation);
        Crashlytics.log("queryAlreadySaved :" + queryAlreadySaved);
        Crashlytics.log("searchQuery :" + searchQuery);
        Crashlytics.log("saveSearchString :" + saveSearchString);

        setupSaveQueryView(rootView);

        listView.setOnScrollListener(this);

        eventList = new ArrayList<UiTEvent>();
        uiTEventAdapter = new UiTEventAdapter(getActivity(), eventList, false, checkCurrentLocation, locationLatitude, locationLongitude);
        listView.setAdapter(uiTEventAdapter);

        if (checkCurrentLocation) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }

        if (UiTagenda.isNetworkAvailable()) {
            setupFirstFetch();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internetconnection), Toast.LENGTH_LONG).show();
        }

        setupProgressBar();


       return rootView;
    }

    private void setupSaveQueryView(View rootView) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (!queryAlreadySaved) {
            ll_saveOrDismiss = (LinearLayout) layoutInflater.inflate(R.layout.savequery_view, null, false);
            final Button btnSave = (Button) ll_saveOrDismiss.findViewById(R.id.btn_save);
            final ImageButton btnDismiss = (ImageButton) ll_saveOrDismiss.findViewById(R.id.btn_dismiss);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert = new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.question_save_query));

                    alert.setPositiveButton(getString(R.string.query_saved), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = saveSearchString;

                            if (!value.matches("")) {
                                UiTSearchDataSource searchDS = new UiTSearchDataSource(getActivity());

                                boolean titleExists = searchDS.containsTitle(saveSearchString);

                                if (titleExists) {
                                    searchDS.addSearchUrl(value, searchQuery, checkCurrentLocation);
                                    btnSave.setText(getString(R.string.searchquery_saved));
                                    btnSave.setBackgroundColor(0xFF417505);
                                    btnDismiss.setBackgroundColor(0xFF417505);
                                    btnSave.setClickable(false);
                                } else {
                                    Toast.makeText(getActivity(), getString(R.string.savequery_title_already_exists), Toast.LENGTH_LONG).show();
                                    listView.removeHeaderView(ll_saveOrDismiss);
                                }
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.savequery_no_title), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });

                    alert.show();
                }
            });

            btnDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listView.removeHeaderView(ll_saveOrDismiss);
                }
            });
        }
        else {
            ll_saveOrDismiss = (LinearLayout) layoutInflater.inflate(R.layout.savequery_view_empty, null, false);
        }

        listView.addHeaderView(ll_saveOrDismiss);
    }

    private void setupProgressBar() {
        pbFooter = new ProgressBar(getActivity());
        pbFooter.setVisibility(View.INVISIBLE);
        listView.addFooterView(pbFooter);
    }

    private void setupFirstFetch() {
        if (checkCurrentLocation) {
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            boolean network_enabled = locationManager.isProviderEnabled(locationProvider);
            if (network_enabled) {
                setLoadingModal();
                checkLocation();
                locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_location), Toast.LENGTH_LONG).show();
            }
        } else {
            setLoadingModal();
            fetchEvents();
        }
    }

    private void fetchEvents() {
        fetchEvents = new FetchEvents();
        fetchEvents.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }

        if (fetchEvents != null && fetchEvents.getStatus() != AsyncTask.Status.FINISHED) {
            fetchEvents.cancel(true);
        }
    }

    protected void checkLocation() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!haveAlreadyReceivedCoordinates) {
                    if (location != null) {
                        locationLatitude = location.getLatitude();
                        locationLongitude = location.getLongitude();
                        locationManager.removeUpdates(locationListener);

                        fetchEvents();
                    }
                }
                haveAlreadyReceivedCoordinates = true;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        uiTEventAdapter.notifyDataSetChanged();
    }

    private void setLoadingModal() {
        progress = ProgressDialog.show(getActivity(), getString(R.string.loading_events), getString(R.string.loading_message));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.global, menu);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (loadMore && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 2) && eventList.size() > 10) {
            start += 15;
            loadMore = false;
            fetchEvents();
        }
    }

    public class FetchEvents extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            String completeURL;

            if (checkCurrentLocation) {
                completeURL = getString(R.string.base_url) + searchQuery + "&pt=" + locationLatitude + "," + locationLongitude + "";
            } else {
                completeURL = getString(R.string.base_url) + searchQuery;

            }

            Crashlytics.log("completeUrl :" + completeURL);

            ApiClientOAuth task = new ApiClientOAuth(getActivity(), completeURL, start);
            try {
                return task.fetchData();
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                try {
                    JSONArray resultArray = result.getJSONArray("rootObject");

                    for (int i = 0; i < resultArray.length(); i++) {
                        if (isCancelled()) {
                            break;
                        }

                        if (i == 0) {
                            totalEvents = resultArray.getJSONObject(0).getInt("Long");
                            if (totalEvents == 0) {
                                // show image NO RESULTS
                            }
                        } else {
                            UiTEvent newEvent = new UiTEvent(resultArray.getJSONObject(i));

                            eventList.add(newEvent);
                        }
                    }

                    if (checkCurrentLocation) {
                        uiTEventAdapter.locationLat = locationLatitude;
                        uiTEventAdapter.locationLon = locationLongitude;
                    }


                    uiTEventAdapter.notifyDataSetChanged();
                    pbFooter.setVisibility(View.VISIBLE);
                    if (totalEvents != eventList.size()) {
                        loadMore = true;
                    } else {
                        loadMore = false;
                        listView.removeFooterView(pbFooter);
                        if (start > 0) {
                            listView.addFooterView(tvFooter);
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {

                pbFooter.setVisibility(View.GONE);
            }
            listView.setEmptyView(llNoResults);
            rootView.setVisibility(View.VISIBLE);
            progress.dismiss();
        }
    }
}