package org.uitagenda;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.uitagenda.apiclient.ApiClient;
import org.uitagenda.utils.UiTagenda;
import com.crashlytics.android.Crashlytics;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {

    TextView tvAbout;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FavoritesFragment.
     */
    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        UiTagenda.trackGoogleAnalytics(getActivity(), "Android: Over");

        Crashlytics.setString("ClassName", this.getClass().getSimpleName());

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        if (UiTagenda.isNetworkAvailable()) {
            GetAboutData getAboutData = new GetAboutData();
            getAboutData.execute();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internetconnection), Toast.LENGTH_LONG).show();
        }

        tvAbout = (TextView) view.findViewById(R.id.tv_about);

        return view;
    }

    public class GetAboutData extends AsyncTask<String, String, String> {

        GetAboutData() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            ApiClient apiClient = new ApiClient();

            try {
                JSONObject aboutJson = apiClient.makeHttpRequest(getString(R.string.about_url), "GET");

                Crashlytics.log(aboutJson.getString("appinfo"));

                return aboutJson.getString("appinfo");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                tvAbout.setText(s);
            } else {
                Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_LONG).show();
            }
        }
    }
}