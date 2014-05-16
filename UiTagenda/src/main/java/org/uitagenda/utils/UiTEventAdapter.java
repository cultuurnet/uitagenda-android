package org.uitagenda.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.uitagenda.DetailActivity;
import org.uitagenda.R;
import org.uitagenda.model.UiTEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by alexf_000 on 1/13/14.
 */
public class UiTEventAdapter extends BaseAdapter {

    private static final String TAG = "UiTMainListAdapter";

    private final Context context;
    public final ArrayList<UiTEvent> mainList;
    private final boolean isFavorite;
    private final boolean isCurrentLocation;

    protected ImageLoader imageLoader;
    DisplayImageOptions options;

    private UiTFavoriteDataSource favoriteDS;

    public double locationLat;
    public double locationLon;

    private ImageLoadingListener myImageLoadingListener = new MyImageLoadingListener();

    public UiTEventAdapter(Context context, ArrayList<UiTEvent> eenMainList, boolean isFavorite, boolean isCurrentLocation, double locationLat, double locationLon) {
        this.context = context;
        this.mainList = eenMainList;
        this.isFavorite = isFavorite;
        this.isCurrentLocation = isCurrentLocation;
        this.locationLat = locationLat;
        this.locationLon = locationLon;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nothumbnail)
                .showImageForEmptyUri(R.drawable.nothumbnail)
                .showImageOnFail(R.drawable.nothumbnail)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(options)
                .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @Override
    public int getCount() {
        return mainList.size();
    }

    @Override
    public Object getItem(int position) {
        return mainList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Typeface fontFaceRegular = Typeface.createFromAsset(context.getAssets(), "PTN57F.ttf");
        Typeface fontFaceBold = Typeface.createFromAsset(context.getAssets(), "PTN77F.ttf");

        favoriteDS = new UiTFavoriteDataSource(context);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.event_card, null);
        }

        final UiTEvent event = mainList.get(position);
        if (event != null) {
            TextView tv_title = (TextView) convertView.findViewById(R.id.text1);
            TextView tv_eventtype = (TextView) convertView.findViewById(R.id.eventtype);
            TextView tv_location = (TextView) convertView.findViewById(R.id.location);
            TextView tv_city = (TextView) convertView.findViewById(R.id.city);
            final ImageButton ib_favorite = (ImageButton) convertView.findViewById(R.id.favorite);
            ImageView iv_forKids = (ImageView) convertView.findViewById(R.id.forKids);
            ImageView iv_image = (ImageView) convertView.findViewById(R.id.list_image);
            LinearLayout ll_background = (LinearLayout) convertView.findViewById(R.id.background);

            TextView tv_distance = (TextView) convertView.findViewById(R.id.distance);

            tv_title.setText(event.title);
            if (event.categories.size() == 0) {
                tv_eventtype.setText("-");
            } else {
                tv_eventtype.setText(event.categories.get(0));
            }
            tv_location.setText(event.location);
            tv_city.setText(event.city);

            UiTagenda.setFontBoldDirect(tv_title);
            UiTagenda.setFontRegularDirect(tv_eventtype);
            UiTagenda.setFontRegularDirect(tv_location);
            UiTagenda.setFontBoldDirect(tv_city);

            UiTagenda.setFontRegularDirect(tv_distance);

            if (isCurrentLocation) {

                Location locationA = new Location("point A");

                locationA.setLatitude(locationLat);
                locationA.setLongitude(locationLon);

                Location locationB = new Location("point B");

                locationB.setLatitude(event.latCoordinate);
                locationB.setLongitude(event.lonCoordinate);

                double distance = locationA.distanceTo(locationB);

                if (distance > 999) {
                    DecimalFormat df = new DecimalFormat("###.#");
                    tv_distance.setText("(" + df.format(distance/1000) + "km)");
                }
                else {
                    tv_distance.setText("(" + Math.round(distance) + "m)");
                }
            }
            else {
                tv_distance.setText("");
            }

            imageLoader.displayImage(event.imageURL, iv_image, options, myImageLoadingListener);

            iv_forKids.setVisibility(View.INVISIBLE);

            if (event.ageFrom != null) {
                if (event.ageFrom.intValue() <= 11) {
                    iv_forKids.setVisibility(View.VISIBLE);
                }
            }

            if (favoriteDS.containsId(event.cdbid)) {
                ib_favorite.setSelected(true);
            } else {
                if (isFavorite) {
                    mainList.remove(event);
                    notifyDataSetChanged();
                }
                ib_favorite.setSelected(false);
            }

            ib_favorite.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (favoriteDS.containsId(event.cdbid)) {
                        favoriteDS.deleteFromFavorites(event.cdbid);
                        ib_favorite.setSelected(false);
                        if (isFavorite) {
                            mainList.remove(position);
                            notifyDataSetChanged();
                        }
                    } else {
                        favoriteDS.addFavorite(event.cdbid);
                        ib_favorite.setSelected(true);
                    }
                }
            });

            ll_background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getApplicationContext(), DetailActivity.class);
Bundle bundle = new Bundle();
                    bundle.putSerializable("list", mainList);
                    intent.putExtra("bundle", bundle);
                    intent.putExtra("positionInList", position);
                    context.startActivity(intent);
                }
            });

            if (isFavorite) {
                if (event.datePassed) {
                    AlphaAnimation alpha = new AlphaAnimation(0.4F, 0.4F);
                    alpha.setDuration(0);
                    alpha.setFillAfter(true);
                    convertView.startAnimation(alpha);
                }
                else {
                    AlphaAnimation alpha = new AlphaAnimation(1.0F, 1.0F);
                    alpha.setDuration(0);
                    alpha.setFillAfter(true);
                    convertView.startAnimation(alpha);
                }
            }
        }
        return convertView;
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
}
