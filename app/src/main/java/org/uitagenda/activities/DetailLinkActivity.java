package org.uitagenda.activities;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;

import org.uitagenda.R;
import org.uitagenda.api.ApiHelper;
import org.uitagenda.fragments.DetailFragment;
import org.uitagenda.model.RootObject;
import org.uitagenda.model.UitEvent;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Inneke on 15/09/2015.
 */
public class DetailLinkActivity extends AppCompatActivity
{
    private MaterialDialog dialogProgress;
    private MaterialDialog dialogError;
    private String eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(!this.getResources().getBoolean(R.bool.tablet))
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_detail_link);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Uri uri = this.getIntent().getData();
        this.eventId = uri != null ? uri.getLastPathSegment() : null;
        if(!TextUtils.isEmpty(this.eventId))
            this.startEventApiCall();
        else
            this.showErrorDialogUri();
    }

    @Override
    protected void onPause()
    {
        if(this.dialogProgress != null && this.dialogProgress.isShowing())
            this.dialogProgress.dismiss();
        if(this.dialogError != null && this.dialogError.isShowing())
            this.dialogError.dismiss();

        super.onPause();
    }

    private void startEventApiCall()
    {
        this.showProgressDialog();

        ApiHelper.getServiceOAuth().getFavoriteEvents("cdbid:\"" + this.eventId + "\"", new Callback<RootObject>()
        {
            @Override
            public void success(RootObject rootObject, Response response)
            {
                if(DetailLinkActivity.this.dialogProgress != null && DetailLinkActivity.this.dialogProgress.isShowing())
                    DetailLinkActivity.this.dialogProgress.dismiss();

                if (rootObject != null && rootObject.getEvents() != null && rootObject.getEvents().size() > 0 && rootObject.getEvents().get(0) != null)
                {
                    UitEvent event = new UitEvent(rootObject.getEvents().get(0).getEvent());
                    DetailLinkActivity.this.setTitle(event.getTitle());
                    DetailLinkActivity.this.addDetailFragment(event);
                }
                else
                {
                    DetailLinkActivity.this.showErrorDialogInternet();
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                if(DetailLinkActivity.this.dialogProgress != null && DetailLinkActivity.this.dialogProgress.isShowing())
                    DetailLinkActivity.this.dialogProgress.dismiss();

                DetailLinkActivity.this.showErrorDialogInternet();
            }
        });
    }

    private void addDetailFragment(UitEvent event)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DetailFragment.KEY_EVENT, event);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(bundle);

        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    private void showProgressDialog()
    {
        this.dialogProgress = new MaterialDialog.Builder(this)
                .content(R.string.event_link_progress)
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    private void showErrorDialogInternet()
    {
        if(this.dialogError != null && this.dialogError.isShowing())
            this.dialogError.dismiss();

        this.dialogError = new MaterialDialog.Builder(this)
                .title(R.string.event_link_error_title)
                .content(R.string.event_link_error_internet)
                .positiveText(R.string.event_link_error_positive)
                .negativeText(R.string.dialog_negative)
                .callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        DetailLinkActivity.this.startEventApiCall();
                    }
                })
                .show();
    }

    private void showErrorDialogUri()
    {
        if(this.dialogError != null && this.dialogError.isShowing())
            this.dialogError.dismiss();

        this.dialogError = new MaterialDialog.Builder(this)
                .title(R.string.event_link_error_title)
                .content(R.string.event_link_error)
                .positiveText(R.string.dialog_positive)
                .show();
    }
}
