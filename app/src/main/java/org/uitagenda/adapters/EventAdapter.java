package org.uitagenda.adapters;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.uitagenda.R;
import org.uitagenda.model.UitEvent;
import org.uitagenda.utils.CustomTypefaceSpan;
import org.uitagenda.utils.FavoritesHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inneke on 13/08/2015.
 */
public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private int layoutEvent;
    private OnItemClickListener listener;
    private List<UitEvent> events;
    private int colorTextLight;
    private double latitude;
    private double longitude;
    private boolean favorites;

    private static final int TYPE_EVENT = 0;

    public EventAdapter(Context context, boolean favorites)
    {
        this.context = context;
        this.favorites = favorites;
        this.colorTextLight = context.getResources().getColor(R.color.general_text_light);
        this.layoutEvent = R.layout.listitem_event;
    }

    public void setListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    public void clearList()
    {
        this.events = null;
        this.notifyDataSetChanged();
    }

    public void addEvents(List<UitEvent> events)
    {
        if(events != null)
        {
            if (this.events == null)
                this.events = new ArrayList<>();
            int firstInsertedItem = this.events.size();
            this.events.addAll(events);
            this.notifyItemRangeInserted(firstInsertedItem, events.size());
        }
    }

    public void updateEvent(String cdbid)
    {
        if(this.events != null)
            for(int i = 0; i < this.events.size(); i++)
                if(this.events.get(i).getCdbid() != null && this.events.get(i).getCdbid().equals(cdbid))
                    this.notifyItemChanged(i);
    }

    public String getCdbid(int position)
    {
        if(this.events != null && this.events.size() > position)
            return this.events.get(position).getCdbid();
        return null;
    }

    public void setLocation(double latitude, double longitude)
    {
        if(this.latitude != latitude || this.longitude != longitude)
        {
            this.latitude = latitude;
            this.longitude = longitude;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return TYPE_EVENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int viewType)
    {
        switch (viewType)
        {
            case TYPE_EVENT:
                View viewEvent = LayoutInflater.from(viewGroup.getContext()).inflate(this.layoutEvent, viewGroup, false);
                return new EventViewHolder(viewEvent);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i)
    {
        switch (this.getItemViewType(i))
        {
            case TYPE_EVENT:
                this.onBindEventViewHolder((EventViewHolder) viewHolder, i);
                break;
        }
    }

    private void onBindEventViewHolder(EventViewHolder viewHolder, int i)
    {
        UitEvent event = this.events.get(i);

        viewHolder.textViewTitle.setText(event.getTitle());
        viewHolder.textViewCategory.setText(!TextUtils.isEmpty(event.getFirstCategory()) ? event.getFirstCategory() : "-");
        viewHolder.textViewLocation.setText(event.getLocation());
        String distance = this.latitude != 0 || this.longitude != 0 ? this.getDistance(event.getLatitude(), event.getLongitude()) : null;
        CharSequence cityAndDistance = !TextUtils.isEmpty(distance) ? this.createSpannedText(event.getCity(), " (" + distance + ")") : event.getCity();
        viewHolder.textViewCityDistance.setText(cityAndDistance);
        viewHolder.imageViewOverlay.setVisibility(event.isForChildren() ? View.VISIBLE : View.GONE);
        viewHolder.imageViewFavorite.setImageResource(!TextUtils.isEmpty(event.getCdbid()) && FavoritesHelper.isFavorite(this.context, event.getCdbid()) ? R.drawable.ic_favorite_selected : R.drawable.ic_favorite_default);

        if(!TextUtils.isEmpty(event.getImageURL()))
            Picasso.with(this.context)
                    .load(event.getImageURL())
                    .resizeDimen(R.dimen.list_item_image_width, R.dimen.list_item_min_height)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(viewHolder.imageViewEvent);
        else
            viewHolder.imageViewEvent.setImageResource(R.drawable.placeholder);

        viewHolder.layoutEvent.setAlpha(this.favorites && event.getAvailableTo() < System.currentTimeMillis() ? 0.4f: 1.0f);
    }

    private String getDistance(double latitude, double longitude)
    {
        float[] distance = new float[1];
        Location.distanceBetween(latitude, longitude, this.latitude, this.longitude, distance);
        float distanceInMeter = distance[0];
        if(distanceInMeter < 1000)
            return this.context.getString(R.string.event_distance_m, (int) (distanceInMeter));
        return this.context.getString(R.string.event_distance_km, distanceInMeter / 1000);
    }

    private SpannableString createSpannedText(String city, String distance)
    {
        if(city == null)
            city = "";
        if(distance == null)
            distance = "";
        SpannableString result = new SpannableString(city + distance);
        result.setSpan(new ForegroundColorSpan(this.colorTextLight), city.length(), city.length() + distance.length(), 0);
        result.setSpan(new CustomTypefaceSpan(this.context, CustomTypefaceSpan.REGULAR), city.length(), city.length() + distance.length(), 0);

        return result;
    }

    @Override
    public int getItemCount()
    {
        return this.events != null ? this.events.size() : 0;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        RelativeLayout layoutEvent;
        ImageView imageViewEvent;
        ImageView imageViewOverlay;
        ImageView imageViewFavorite;
        TextView textViewTitle;
        TextView textViewCategory;
        TextView textViewLocation;
        TextView textViewCityDistance;

        public EventViewHolder(View itemView)
        {
            super(itemView);

            this.layoutEvent = (RelativeLayout) itemView.findViewById(R.id.layout_event);
            this.imageViewEvent = (ImageView) itemView.findViewById(R.id.imageView_event);
            this.imageViewOverlay = (ImageView) itemView.findViewById(R.id.imageView_overlay);
            this.imageViewFavorite = (ImageView) itemView.findViewById(R.id.imageView_favorite);
            this.textViewTitle = (TextView) itemView.findViewById(R.id.textView_title);
            this.textViewCategory = (TextView) itemView.findViewById(R.id.textView_category);
            this.textViewLocation = (TextView) itemView.findViewById(R.id.textView_location);
            this.textViewCityDistance = (TextView) itemView.findViewById(R.id.textView_city);

            this.imageViewFavorite.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if(EventAdapter.this.listener != null)
                EventAdapter.this.listener.onEventClick(v, this.getAdapterPosition());
        }
    }

    public static interface OnItemClickListener
    {
        public void onEventClick(View caller, int position);
    }
}
