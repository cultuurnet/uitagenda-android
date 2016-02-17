package org.uitagenda.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.uitagenda.R;
import org.uitagenda.api.ApiHelper;
import org.uitagenda.model.About;
import org.uitagenda.utils.TrackerHelper;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Inneke on 11/08/2015.
 */
public class AboutFragment extends Fragment
{
    private TextView textViewAbout;
    private boolean apiFinished;
    private String aboutText;

    private static final String KEY_API_FINISHED = "api_finished";
    private static final String KEY_ABOUT_TEXT = "about_text";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        this.textViewAbout = (TextView) view.findViewById(R.id.textView_about);

        this.apiFinished = savedInstanceState != null && savedInstanceState.getBoolean(KEY_API_FINISHED);
        this.aboutText = savedInstanceState != null ? savedInstanceState.getString(KEY_ABOUT_TEXT) : null;

        if(!this.apiFinished)
            this.startAboutApiCall();
        else if(!TextUtils.isEmpty(this.aboutText))
            this.textViewAbout.setText(this.aboutText);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putBoolean(KEY_API_FINISHED, this.apiFinished);
        outState.putString(KEY_ABOUT_TEXT, this.aboutText);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if(this.getActivity() != null && !this.getActivity().isFinishing())
            TrackerHelper.trackScreen(this.getActivity(), TrackerHelper.SCREEN_ABOUT);
    }

    private void startAboutApiCall()
    {
        ApiHelper.getService().getAbout(new Callback<About>()
        {
            @Override
            public void success(About about, Response response)
            {
                AboutFragment.this.apiFinished = true;
                if(about != null && !TextUtils.isEmpty(about.getAboutText()))
                {
                    AboutFragment.this.aboutText = about.getAboutText();
                    AboutFragment.this.textViewAbout.setText(about.getAboutText());
                }
                else if(AboutFragment.this.getActivity() != null && !AboutFragment.this.getActivity().isFinishing())
                {
                    Toast.makeText(AboutFragment.this.getActivity(), R.string.no_about, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                AboutFragment.this.apiFinished = true;
                if(AboutFragment.this.getActivity() != null && !AboutFragment.this.getActivity().isFinishing())
                    Toast.makeText(AboutFragment.this.getActivity(), R.string.no_about_internet, Toast.LENGTH_LONG).show();
            }
        });
    }
}
