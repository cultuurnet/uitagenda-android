package org.uitagenda;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.uitagenda.model.UiTEvent;
import org.uitagenda.utils.UiTFavoriteDataSource;
import org.uitagenda.utils.UiTagenda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alexf_000 on 1/15/14.
 */
public class DetailActivity extends ActionBarActivity {

    UiTEvent shownEvent;

    protected ImageLoader imageLoader;
    DisplayImageOptions options;

    private UiTFavoriteDataSource favoriteDS;
    ImageButton ibFavorite;
    int position;
    ArrayList<UiTEvent> eventList;
    List<Address> addresses;
    int screenHeight, screenWidth;

    private ImageLoadingListener myImageLoadingListener = new MyImageLoadingListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UiTagenda.trackGoogleAnalytics(this, "Android: Detail");

        position = getIntent().getIntExtra("positionInList", 0);
        eventList = (ArrayList<UiTEvent>)getIntent().getBundleExtra("bundle").getSerializable("list");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.detail));
        actionBar.setDisplayHomeAsUpEnabled(true);

        fillView(eventList.get(position));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }

    public void fillView(UiTEvent event) {

        setContentView(R.layout.activity_detail);

        shownEvent = event;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nothumbnail)
                .showImageForEmptyUri(R.drawable.nothumbnail)
                .showImageOnFail(R.drawable.nothumbnail)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(options)
                .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        ImageView iv_eventImage = (ImageView) findViewById(R.id.iv_detailImage);
        ImageView iv_forKids = (ImageView) findViewById(R.id.iv_forKids);

        imageLoader.displayImage(event.imageURL + "?width=" + screenWidth + "&height=" + screenHeight / 2 + "&crop=auto", iv_eventImage, options, myImageLoadingListener);
        // imageLoader.displayImage(event.imageURL, iv_eventImage, options, myImageLoadingListener);

        if (event.ageFrom != null) {
            if (event.ageFrom.intValue() <= 11) {
                iv_forKids.setVisibility(View.VISIBLE);
            }
        }
        ImageButton ibNext = (ImageButton) findViewById(R.id.ib_next);
        if (position == eventList.size() - 1) {
            ibNext.setClickable(false);
            ibNext.setImageBitmap(null);
        } else {
            ibNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position += 1;
                    fillView(eventList.get(position));
                }
            });
        }

        ImageButton ibPrevious = (ImageButton) findViewById(R.id.ib_previous);
        if (position == 0) {
            ibPrevious.setClickable(false);
            ibPrevious.setImageBitmap(null);
        } else {
            ibPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position -= 1;
                    fillView(eventList.get(position));
                }
            });
        }
        TextView tvTitle, tvCalendarSummary, tvLocation,
                tvContactInfo, tvReservationInfo, tvShortDescription, tvPriceDescription,
                tvPriceValue, tvOrganiser, tvCategories, tvMediaInfo, tvDescriptionLocation, tvPerformers;

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvCalendarSummary = (TextView) findViewById(R.id.tv_calendarSummary);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvContactInfo = (TextView) findViewById(R.id.tv_contactinfo);
           tvReservationInfo = (TextView) findViewById(R.id.tv_reservationinfo);
        WebView wvLongDescription = (WebView) findViewById(R.id.wv_longdescription);
        tvShortDescription = (TextView) findViewById(R.id.tv_shortdescription);
        tvPriceDescription = (TextView) findViewById(R.id.tv_pricedescription);
        tvPriceValue = (TextView) findViewById(R.id.tv_pricevalue);
        tvOrganiser = (TextView) findViewById(R.id.tv_organiser);
        tvCategories = (TextView) findViewById(R.id.tv_categories);
        tvMediaInfo = (TextView) findViewById(R.id.tv_mediaInfo);
        tvDescriptionLocation = (TextView) findViewById(R.id.tv_description_location);
        tvPerformers = (TextView) findViewById(R.id.tv_performers);


        LinearLayout llCalendar = (LinearLayout) findViewById(R.id.ll_calendar);
        LinearLayout llLocation = (LinearLayout) findViewById(R.id.ll_location);
        LinearLayout llOrganiser = (LinearLayout) findViewById(R.id.ll_organiser);
        LinearLayout llContact = (LinearLayout) findViewById(R.id.ll_contact);
        LinearLayout llReservation = (LinearLayout) findViewById(R.id.ll_reservation);
        LinearLayout llPrice = (LinearLayout) findViewById(R.id.ll_price);
        LinearLayout llLink = (LinearLayout) findViewById(R.id.ll_links);
        LinearLayout llShare = (LinearLayout) findViewById(R.id.ll_button_share);
        LinearLayout llFavorite = (LinearLayout) findViewById(R.id.ll_button_favorite);
        LinearLayout llLongDescription = (LinearLayout) findViewById(R.id.ll_longdescription);
        LinearLayout llPerformers = (LinearLayout) findViewById(R.id.ll_performers);
        RelativeLayout rlImage = (RelativeLayout) findViewById(R.id.rl_image);

        tvTitle.setText(event.title);
        tvCalendarSummary.setText(event.calendarSummary);
        tvLocation.setText(event.contactStreet + " " + event.contactHouseNr + ", " + event.contactZipcode + " " + event.city);
        tvDescriptionLocation.setText(event.location + ", " + event.city);
        if (event.contactInfo.size() == 0) {
            tvContactInfo.setVisibility(View.GONE);
        } else {
            tvContactInfo.setText(TextUtils.join("\n\n", event.contactInfo));
            Linkify.addLinks(tvContactInfo, Linkify.ALL);
            stripUnderlines(tvContactInfo);
        }
        if (event.reservationInfo.size() == 0) {
            tvReservationInfo.setVisibility(View.GONE);
        } else {
            tvReservationInfo.setText(TextUtils.join("\n\n", event.reservationInfo));
            Linkify.addLinks(tvReservationInfo, Linkify.ALL);
            stripUnderlines(tvReservationInfo);
        }
        if (event.shortDescription.equals("") || event.shortDescription.equals("NB")) {
            tvShortDescription.setVisibility(View.GONE);
        }
        else {
            tvShortDescription.setText(event.shortDescription.trim());
        }
        if (event.imageURL == null) {
            rlImage.setVisibility(View.GONE);
        }
        if (event.performers.size() == 0) {
            llPerformers.setVisibility(View.GONE);
        } else {
            tvPerformers.setText(TextUtils.join("\n", event.performers));
        }
        if (event.longDescription.equals("")) {
            llLongDescription.setVisibility(View.GONE);
        } else {
            //wvLongDescription.loadDataWithBaseURL(null, "<style type='text/css'>* { font-family: \"PT Sans Narrow\"; } </style>" + event.longDescription, "text/html", "UTF-8", null);
            wvLongDescription.loadDataWithBaseURL(null, "<style type='text/css'>* { font-size: 14; } </style>" + event.longDescription, "text/html", "UTF-8", null);
           /* tvLongDescription.setText(Html.fromHtml(event.longDescription.trim()));
            Linkify.addLinks(tvLongDescription, Linkify.ALL);
            stripUnderlines(tvLongDescription);*/
        }
        if (event.organiser.equals("")) {
            llOrganiser.setVisibility(View.GONE);
        } else {
            tvOrganiser.setText(event.organiser);
        }

        tvCategories.setText(TextUtils.join(", ", event.categories));

        if (event.priceDescription.equals("")) {
            tvPriceDescription.setVisibility(View.GONE);
        } else {
            tvPriceDescription.setText(getResources().getString(R.string.extra_info) + event.priceDescription);
        }
        if (event.priceValue.equals("")) {
            tvPriceValue.setVisibility(View.GONE);
        } else if (event.priceValue.equals("0")) {
            tvPriceValue.setText("Gratis");
        } else {
            tvPriceValue.setText("€ " + event.priceValue);
        }

        ibFavorite = (ImageButton) findViewById(R.id.ib_favorite);
        favoriteDS = new UiTFavoriteDataSource(getApplicationContext());

        llFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favoriteDS.containsId(shownEvent.cdbid)) {
                    ibFavorite.setSelected(false);
                    favoriteDS.deleteFromFavorites(shownEvent.cdbid);
                } else {
                    ibFavorite.setSelected(true);
                    favoriteDS.addFavorite(shownEvent.cdbid);
                }
            }
        });

        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, shownEvent.title + " " + getResources().getString(R.string.share_url) + shownEvent.cdbid + "?utm_campaign=UiTagenda&utm_medium=android&utm_source=app&utm_content=share" + " (Via #UiTagenda)");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, shownEvent.title);
            /*   try {
                   URI uri = new URI(shownEvent.imageURL);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                }
                catch (URISyntaxException e) {
                    e.printStackTrace();
                }*/
                startActivity(intent);
            }
        });

        if (event.calendarSummary.equals("")) {
            llCalendar.setVisibility(View.GONE);
        }
        if (event.location.equals("") && event.contactStreet.equals("") && event.city.equals("") && event.contactZipcode.equals("")) {
            llLocation.setVisibility(View.GONE);
        }
        if (event.organiser.equals("")) {
            llOrganiser.setVisibility(View.GONE);
        }
        if (event.contactInfo.size() == 0) {
            llContact.setVisibility(View.GONE);
        }
        if (event.reservationInfo.size() == 0) {
            llReservation.setVisibility(View.GONE);
        }
        if (event.priceDescription.equals("") && event.priceValue.equals("")) {
            llPrice.setVisibility(View.GONE);
        }
        if (event.mediaInfo.size() == 0) {
            llLink.setVisibility(View.GONE);
        } else {
            tvMediaInfo.setText(TextUtils.join("\n\n", event.mediaInfo));
            Linkify.addLinks(tvMediaInfo, Linkify.ALL);
            stripUnderlines(tvMediaInfo);
        }
        if (event.categories.size() == 0) {
            tvCategories.setVisibility(View.GONE);
        }
        Geocoder geocoder = new Geocoder(getApplicationContext());
        addresses = new ArrayList<Address>();
        try {
            if (shownEvent.contactHouseNr.equalsIgnoreCase("z/n"))
                addresses = geocoder.getFromLocationName(shownEvent.contactStreet + ", " + shownEvent.city, 1);
            else {
                addresses = geocoder.getFromLocationName(shownEvent.contactStreet + " " + shownEvent.contactHouseNr + ", " + shownEvent.city, 1);
            }
            if (addresses.size() > 0) {
                tvLocation.setTextColor(getResources().getColor(R.color.red_color));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (addresses.size() > 0) {
                    double latitude = addresses.get(0).getLatitude();
                    double longitude = addresses.get(0).getLongitude();
                    String uriBegin = "geo:" + latitude + "," + longitude;
                    String query = latitude + "," + longitude + "(" + shownEvent.title + ")";
                    String encodedQuery = Uri.encode(query);
                    String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                    Uri uri = Uri.parse(uriString);
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        if (favoriteDS.containsId(shownEvent.cdbid)) {
            ibFavorite.setSelected(true);
        } else {
            ibFavorite.setSelected(false);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, R.anim.anim_out);
            return true;
        }

        if (item.getItemId() == R.id.action_favorite) {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        UiTagenda.trackGoogleAnalytics(this, "Android: Detail");

        if (favoriteDS.containsId(shownEvent.cdbid)) {
            ibFavorite.setSelected(true);
        } else {
            ibFavorite.setSelected(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, R.anim.anim_out);
    }

    private static class MyImageLoadingListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    private void stripUnderlines(TextView textView) {
        if(textView.getText() != "") {
            if (textView.getText().length() > 0) {
                Spannable s = (Spannable) textView.getText();
                URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
                for (URLSpan span : spans) {
                    int start = s.getSpanStart(span);
                    int end = s.getSpanEnd(span);
                    s.removeSpan(span);
                    span = new URLSpanNoUnderline(span.getURL());
                    s.setSpan(span, start, end, 0);
                }
                textView.setText(s);
            }
        }
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}