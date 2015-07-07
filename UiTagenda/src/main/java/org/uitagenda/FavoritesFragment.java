package org.uitagenda;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import org.uitagenda.utils.UiTFavoriteDataSource;
import org.uitagenda.utils.UiTagenda;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {

    private final String TAG = "FavoritesFragment";
    ArrayList<String> favList;
    int totalEvents;
    ArrayList<UiTEvent> mainEventList = new ArrayList<UiTEvent>();
    UiTEventAdapter uiTEventAdapter;

    ProgressDialog progress;
    ProgressBar progressBar;
    ListView listView;
    LinearLayout llNoResults;
    TextView tvNoResults;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FavoritesFragment.
     */

    UiTFavoriteDataSource favoriteDS;

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        uiTEventAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        favoriteDS = new UiTFavoriteDataSource(getActivity());

        favList = new ArrayList<String>();
        favList.addAll(favoriteDS.findAllFavorites());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        UiTagenda.trackGoogleAnalytics(view.getContext(), "Android: Favorieten");
        Crashlytics.setString("ClassName", this.getClass().getSimpleName());

        listView = (ListView) view.findViewById(R.id.lv_favorites);
        tvNoResults = (TextView) view.findViewById(R.id.tv_noresults);
        llNoResults = (LinearLayout) view.findViewById(R.id.ll_noresults);

        uiTEventAdapter = new UiTEventAdapter(view.getContext(), mainEventList, true, false, 0, 0);
        listView.setAdapter(uiTEventAdapter);

        checkFetchEvents();

        return view;
    }

    private void checkFetchEvents() {
        if (UiTagenda.isNetworkAvailable()) {
            if (favList.size() > 0) {
                FetchEvents fetchEvents = new FetchEvents();
                fetchEvents.execute();

                setLoadingModal();
            }else {
                listView.setEmptyView(llNoResults);
            }

        } else {
            tvNoResults.setText(getString(R.string.noresults_nointernet));
            listView.setEmptyView(llNoResults);
        }
    }

    private void setLoadingModal() {
        progress = ProgressDialog.show(getActivity(), getString(R.string.loading_favorites), getString(R.string.loading_message));
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public class FetchEvents extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            SearchQuery searchQuery = new SearchQuery(favList);
            String parametersURL = searchQuery.createSearchQueryFavorites();

            String completeURL = getString(R.string.base_url) + parametersURL;
            Crashlytics.log("url" + completeURL);

            ApiClientOAuth task = new ApiClientOAuth(getActivity(), completeURL, 0);
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
                            //totalEvents = resultArray.getJSONObject(0).getInt("Long");
                        } else {
                            UiTEvent newEvent = new UiTEvent(resultArray.getJSONObject(i));
                            mainEventList.add(newEvent);
                        }
                    }
                    uiTEventAdapter.notifyDataSetChanged();
                    listView.setEmptyView(llNoResults);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            progress.dismiss();
        }
    }
}
