package org.uitagenda;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uitagenda.apiclient.ApiClientOAuth;
import org.uitagenda.model.SearchQuery;
import org.uitagenda.model.UiTEvent;
import org.uitagenda.utils.UiTEventAdapter;
import org.uitagenda.utils.UiTagenda;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends ListFragment implements OnRefreshListener, AbsListView.OnScrollListener {

    private PullToRefreshLayout mPullToRefreshLayout;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private boolean haveAlreadyReceivedCoordinates = false;
    private double locationLatitude;
    private double locationLongitude;

    private FetchEvents fetchEvents;

    ArrayList<UiTEvent> mainEventList = new ArrayList<UiTEvent>();
    public UiTEventAdapter uiTEventAdapter;
    ProgressDialog progress;
    ProgressBar pbFooter;
    TextView tvFooter;

    int start, totalEvents;
    boolean loadMore;
    View rootView;
    ListView listView;
    TextView tvNoResults;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        uiTEventAdapter = new UiTEventAdapter(getActivity(), mainEventList, false, true, locationLatitude, locationLongitude);
        setListAdapter(uiTEventAdapter);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        UiTagenda.trackGoogleAnalytics(getActivity(), "Android: Home");

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        listView = (ListView) rootView.findViewById(android.R.id.list);
        listView.setOnScrollListener(this);

        tvFooter = new TextView(getActivity());
        tvFooter.setText(getString(R.string.nomore_results));
        tvFooter.setGravity(Gravity.CENTER);

        tvNoResults = (TextView) rootView.findViewById(R.id.tv_noresults);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Now find the PullToRefreshLayout to setup
        mPullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.ptr_layout);


        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set the OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

        if (UiTagenda.isNetworkAvailable()) {
            setupFirstFetch();
        } else {
            tvNoResults.setText(getString(R.string.noresults_nointernet));
            listView.setEmptyView(rootView.findViewById(R.id.ll_noresults));
        }

        pbFooter = new ProgressBar(getActivity());
        pbFooter.setVisibility(View.INVISIBLE);
        listView.addFooterView(pbFooter);

        return rootView;
    }

    private void setupFirstFetch() {
        String locationProvider = LocationManager.NETWORK_PROVIDER;

        boolean network_enabled = locationManager.isProviderEnabled(locationProvider);

        if (network_enabled) {
            setLoadingModal();
            checkLocation();
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
        } else {
            tvNoResults.setText(getString(R.string.noresults_nolocation));
            listView.setEmptyView(rootView.findViewById(R.id.ll_noresults));
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
                        uiTEventAdapter.notifyDataSetChanged();

                        haveAlreadyReceivedCoordinates = true;

                        fetchEvents();
                    } else {

                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_filter) {

            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.container, SearchFragment.newInstance())
                    .commit();

            Activity activity = getActivity();

            if (activity instanceof MainActivity) {
                ((MainActivity) activity).setmTitle(getString(R.string.action_filter));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefreshStarted(View view) {
        haveAlreadyReceivedCoordinates = false;
        listView.setEmptyView(rootView.findViewById(android.R.id.list));
        mainEventList.clear();
        uiTEventAdapter.notifyDataSetChanged();
        tvFooter.setVisibility(View.GONE);
        pbFooter.setVisibility(view.INVISIBLE);

        start = 0;
        if (UiTagenda.isNetworkAvailable()) {
            setupFirstFetch();
        } else {
            mPullToRefreshLayout.setRefreshComplete();
            rootView.setVisibility(View.VISIBLE);
            listView.setEmptyView(rootView.findViewById(R.id.ll_noresults));
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (loadMore && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 2) && mainEventList.size() > 10) {

            start += 15;
            loadMore = false;

            fetchEvents();
        }
    }

    public class FetchEvents extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rootView.findViewById(R.id.ll_noresults).setVisibility(View.INVISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            SearchQuery searchQuery = new SearchQuery("" + locationLatitude + "," + locationLongitude);
            String parametersURL = searchQuery.createSearchQueryHome();

            String completeURL = getString(R.string.base_url) + parametersURL;

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
                        } else {
                            UiTEvent newEvent = new UiTEvent(resultArray.getJSONObject(i));
                            mainEventList.add(newEvent);
                        }
                    }

                    uiTEventAdapter.locationLat = locationLatitude;
                    uiTEventAdapter.locationLon = locationLongitude;
                    uiTEventAdapter.notifyDataSetChanged();

                    pbFooter.setVisibility(View.VISIBLE);

                    if (totalEvents != mainEventList.size()) {
                        loadMore = true;
                    } else {
                        loadMore = false;
                        getListView().removeFooterView(pbFooter);
                        if (start > 0) {
                            getListView().addFooterView(tvFooter);
                        }
                    }
                } catch (JSONException e) {
                    //Toast.makeText(getActivity(), "Error: " + e, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                mPullToRefreshLayout.setRefreshComplete();
            } else {
            }
            rootView.setVisibility(View.VISIBLE);
            listView.setEmptyView(rootView.findViewById(R.id.ll_noresults));
            progress.dismiss();
            mPullToRefreshLayout.setRefreshComplete();
        }
    }
}
