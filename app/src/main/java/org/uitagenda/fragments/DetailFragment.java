package org.uitagenda.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.uitagenda.R;
import org.uitagenda.adapters.TimestampAdapter;
import org.uitagenda.model.FormattedTimestamp;
import org.uitagenda.model.UitEvent;
import org.uitagenda.utils.DateUtils;
import org.uitagenda.utils.FavoritesHelper;
import org.uitagenda.utils.UrlNoUnderlineSpan;
import org.uitagenda.views.Button;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Inneke on 18/08/2015.
 */
public class DetailFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener
{
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Calendar selectedDate;
    private Calendar selectedTime;
    private TextView textViewDay;
    private TextView textViewHour;
    private int selectedPosition;
    private TextView textViewDjubble;
    private TextView textViewDjubbleInfo;
    private Button buttonDjubble;
    private UitEvent event;

    public static final String KEY_EVENT = "event";
    private static final String DJUBBLE_PACKAGE = "com.djubble.client.forms";

    @Override

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        ImageView imageViewEvent = (ImageView) view.findViewById(R.id.imageView_event);
        ImageView imageViewOverlay = (ImageView) view.findViewById(R.id.imageView_overlay);
        TextView textViewTitle = (TextView) view.findViewById(R.id.textView_title);
        TextView textViewCategory = (TextView) view.findViewById(R.id.textView_category);
        LinearLayout layoutCategory = (LinearLayout) view.findViewById(R.id.layout_category);
        TextView textViewCalendar = (TextView) view.findViewById(R.id.textView_calendar);
        TextView textViewAddress = (TextView) view.findViewById(R.id.textView_address);
        LinearLayout layoutCalendar = (LinearLayout) view.findViewById(R.id.layout_calendar);
        LinearLayout layoutAddress = (LinearLayout) view.findViewById(R.id.layout_address);
        LinearLayout layoutCalendarAndAddress = (LinearLayout) view.findViewById(R.id.layout_calendarAndAddress);
        TextView textViewLocation = (TextView) view.findViewById(R.id.textView_location);
        TextView textViewShortDescription = (TextView) view.findViewById(R.id.textView_shortDescription);
        View dividerShortDescription = view.findViewById(R.id.divider_shortDescription);
        TextView textViewOrganizerTitle = (TextView) view.findViewById(R.id.textView_organiserTitle);
        TextView textViewOrganizer = (TextView) view.findViewById(R.id.textView_organiser);
        View dividerOrganiser = view.findViewById(R.id.divider_organiser);
        TextView textViewContactTitle = (TextView) view.findViewById(R.id.textView_contactTitle);
        TextView textViewContact = (TextView) view.findViewById(R.id.textView_contact);
        View dividerContact = view.findViewById(R.id.divider_contact);
        TextView textViewReservationTitle = (TextView) view.findViewById(R.id.textView_reservationTitle);
        TextView textViewReservation = (TextView) view.findViewById(R.id.textView_reservation);
        View dividerReservation = view.findViewById(R.id.divider_reservation);
        TextView textViewLinksTitle = (TextView) view.findViewById(R.id.textView_linksTitle);
        TextView textViewLinks = (TextView) view.findViewById(R.id.textView_links);
        View dividerLinks = view.findViewById(R.id.divider_links);
        TextView textViewPriceTitle = (TextView) view.findViewById(R.id.textView_priceTitle);
        TextView textViewPriceValue = (TextView) view.findViewById(R.id.textView_priceValue);
        TextView textViewPriceDescription = (TextView) view.findViewById(R.id.textView_priceDescription);
        View dividerPrice = view.findViewById(R.id.divider_price);
        this.textViewDjubble = (TextView) view.findViewById(R.id.textView_djubbleDownload);
        this.textViewDjubbleInfo = (TextView) view.findViewById(R.id.textView_djubbleDescription);
        this.buttonDjubble = (Button) view.findViewById(R.id.button_djubble);
        TextView textViewPerformersTitle = (TextView) view.findViewById(R.id.textView_performersTitle);
        TextView textViewPerformers = (TextView) view.findViewById(R.id.textView_performers);
        View dividerPerformers = view.findViewById(R.id.divider_performers);
        TextView textViewLongDescriptionTitle = (TextView) view.findViewById(R.id.textView_longDescriptionTitle);
        TextView textViewLongDescription = (TextView) view.findViewById(R.id.textView_longDescription);

        Calendar now = Calendar.getInstance();
        this.datePickerDialog = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        this.timePickerDialog = TimePickerDialog.newInstance(
                this,
                now.get(selectedDate.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true);

        if (this.getArguments().getSerializable(KEY_EVENT) != null)
        {
            this.event = (UitEvent) this.getArguments().getSerializable(KEY_EVENT);

            String address = this.event.getFullAddress();
            String fullLocation = this.event.getFullLocation();
            String shortDescription = this.event.getShortDescription();
            String priceValue = this.event.getPriceValue();

            boolean hasImageUrl = !TextUtils.isEmpty(this.event.getImageURL());
            boolean hasCategories = !TextUtils.isEmpty(this.event.getCategories());
            boolean hasCalendar = !TextUtils.isEmpty(this.event.getCalendarSummary());
            boolean hasAddress = !TextUtils.isEmpty(address);
            boolean hasLocation = !TextUtils.isEmpty(fullLocation);
            boolean hasShortDescription = !TextUtils.isEmpty(shortDescription);
            boolean hasOrganiser = !TextUtils.isEmpty(this.event.getOrganiser());
            boolean hasContactInfo = !TextUtils.isEmpty(this.event.getContactInfo());
            boolean hasReservationInfo = !TextUtils.isEmpty(this.event.getReservationInfo());
            boolean hasLinks = !TextUtils.isEmpty(this.event.getMediaInfo());
            boolean hasPriceValue = !TextUtils.isEmpty(priceValue);
            boolean hasPriceDescription = !TextUtils.isEmpty(this.event.getPriceDescription());
            boolean hasPerformers = !TextUtils.isEmpty(this.event.getPerformers());
            boolean hasLongDescription = !TextUtils.isEmpty(this.event.getLongDescription());

            if (hasImageUrl)
            {
                WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int viewWidth = size.x;
                int viewHeight = (int) this.getResources().getDimension(R.dimen.detail_image_height);

                String newUrl = this.event.getImageURL() + "?width=" + viewWidth + "&height=" + viewHeight + "&crop=auto";

                Picasso.with(view.getContext())
                        .load(newUrl)
                        .resize(viewWidth, viewHeight)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(imageViewEvent);
            }

            imageViewEvent.setVisibility(hasImageUrl ? View.VISIBLE : View.GONE);
            imageViewOverlay.setVisibility(hasImageUrl && this.event.isForChildren() ? View.VISIBLE : View.GONE);

            textViewTitle.setText(this.event.getTitle());
            textViewCategory.setText(this.event.getCategories());
            layoutCategory.setVisibility(hasCategories ? View.VISIBLE : View.GONE);

            textViewCalendar.setText(this.event.getCalendarSummary());
            textViewAddress.setText(address);
            textViewAddress.setTextColor(this.getResources().getColor(this.event.getLatitude() != 0 || this.event.getLongitude() != 0 ? R.color.general_text_accent : R.color.general_text));
            layoutCalendar.setVisibility(hasCalendar ? View.VISIBLE : View.GONE);
            layoutAddress.setVisibility(hasAddress ? View.VISIBLE : View.GONE);
            layoutAddress.setOnClickListener(this);
            layoutCalendarAndAddress.setVisibility(hasCalendar || hasAddress ? View.VISIBLE : View.GONE);

            textViewLocation.setText(fullLocation);
            textViewLocation.setVisibility(hasLocation ? View.VISIBLE : View.GONE);
            textViewShortDescription.setText(shortDescription);
            textViewShortDescription.setVisibility(hasShortDescription ? View.VISIBLE : View.GONE);
            dividerShortDescription.setVisibility(hasLocation || hasShortDescription ? View.VISIBLE : View.GONE);

            textViewOrganizer.setText(this.event.getOrganiser());
            textViewOrganizerTitle.setVisibility(hasOrganiser ? View.VISIBLE : View.GONE);
            textViewOrganizer.setVisibility(hasOrganiser ? View.VISIBLE : View.GONE);
            dividerOrganiser.setVisibility(hasOrganiser ? View.VISIBLE : View.GONE);

            textViewContact.setText(this.event.getContactInfo());
            Linkify.addLinks(textViewContact, Linkify.ALL);
            this.stripUnderlines(textViewContact);
            textViewContactTitle.setVisibility(hasContactInfo ? View.VISIBLE : View.GONE);
            textViewContact.setVisibility(hasContactInfo ? View.VISIBLE : View.GONE);
            dividerContact.setVisibility(hasContactInfo ? View.VISIBLE : View.GONE);

            textViewReservation.setText(this.event.getReservationInfo());
            Linkify.addLinks(textViewReservation, Linkify.ALL);
            this.stripUnderlines(textViewReservation);
            textViewReservationTitle.setVisibility(hasReservationInfo ? View.VISIBLE : View.GONE);
            textViewReservation.setVisibility(hasReservationInfo ? View.VISIBLE : View.GONE);
            dividerReservation.setVisibility(hasReservationInfo ? View.VISIBLE : View.GONE);

            textViewLinks.setText(this.event.getMediaInfo());
            Linkify.addLinks(textViewLinks, Linkify.ALL);
            this.stripUnderlines(textViewLinks);
            textViewLinksTitle.setVisibility(hasLinks ? View.VISIBLE : View.GONE);
            textViewLinks.setVisibility(hasLinks ? View.VISIBLE : View.GONE);
            dividerLinks.setVisibility(hasLinks ? View.VISIBLE : View.GONE);

            boolean free = priceValue == null || priceValue.equals("0") || priceValue.equals("0.0");
            textViewPriceValue.setText(free ? this.getString(R.string.event_price_value_free) : this.getString(R.string.event_price_value) + priceValue);
            textViewPriceDescription.setText(this.getString(R.string.event_price_description) + this.event.getPriceDescription());
            textViewPriceValue.setVisibility(hasPriceValue ? View.VISIBLE : View.GONE);
            textViewPriceDescription.setVisibility(hasPriceDescription ? View.VISIBLE : View.GONE);
            textViewPriceTitle.setVisibility(hasPriceValue || hasPriceDescription ? View.VISIBLE : View.GONE);
            dividerPrice.setVisibility(hasPriceValue || hasPriceDescription ? View.VISIBLE : View.GONE);

            textViewPerformers.setText(this.event.getPerformers());
            textViewPerformersTitle.setVisibility(hasPerformers ? View.VISIBLE : View.GONE);
            textViewPerformers.setVisibility(hasPerformers ? View.VISIBLE : View.GONE);
            dividerPerformers.setVisibility(hasPerformers ? View.VISIBLE : View.GONE);

            textViewLongDescription.setText(hasLongDescription ? Html.fromHtml(this.event.getLongDescription()) : "");
            Linkify.addLinks(textViewLongDescription, Linkify.ALL);
            this.stripUnderlines(textViewLongDescription);
            textViewLongDescriptionTitle.setVisibility(hasLongDescription ? View.VISIBLE : View.GONE);
            textViewLongDescription.setVisibility(hasLongDescription ? View.VISIBLE : View.GONE);

            this.textViewDjubble.setOnClickListener(this);
            this.buttonDjubble.setOnClickListener(this);

            this.updateDjubbleLayout();
        }

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.updateDjubbleLayout();
    }

    private void stripUnderlines(TextView textView)
    {
        if (!TextUtils.isEmpty(textView.getText().toString()) && textView.getText() instanceof Spannable)
        {
            Spannable text = (Spannable) textView.getText();
            URLSpan[] spans = text.getSpans(0, text.length(), URLSpan.class);

            for (URLSpan span : spans)
            {
                int start = text.getSpanStart(span);
                int end = text.getSpanEnd(span);
                text.removeSpan(span);
                span = new UrlNoUnderlineSpan(span.getURL());
                text.setSpan(span, start, end, 0);
            }
            textView.setText(text);
        }
    }

    private boolean isAppInstalled(String packageName)
    {
        if (this.getActivity() != null)
        {
            PackageManager pm = this.getActivity().getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(0);

            if (packages != null)
            {
                for (ApplicationInfo packageInfo : packages)
                {
                    if (packageInfo != null && packageInfo.packageName != null && packageInfo.packageName.equals(packageName))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        String cdbid = this.event != null ? this.event.getCdbid() : null;
        boolean favorite = !TextUtils.isEmpty(cdbid) && this.getActivity() != null && !this.getActivity().isFinishing() && FavoritesHelper.isFavorite(this.getActivity(), cdbid);
        menu.findItem(R.id.action_favoriteAdd).setVisible(!favorite);
        menu.findItem(R.id.action_favoriteRemove).setVisible(favorite);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_share:
                if (this.event != null)
                {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.event_share_text, event.getTitle(), event.getCdbid()));
                    intent.putExtra(Intent.EXTRA_SUBJECT, this.event.getTitle());
                    this.startActivity(intent);
                }
                return true;

            case R.id.action_favoriteAdd:
                if (this.event != null && !TextUtils.isEmpty(this.event.getCdbid()) && this.getActivity() != null && !this.getActivity().isFinishing())
                {
                    FavoritesHelper.addFavorite(this.getActivity(), this.event.getCdbid());
                    this.getActivity().invalidateOptionsMenu();
                }
                return true;

            case R.id.action_favoriteRemove:
                if (this.event != null && !TextUtils.isEmpty(this.event.getCdbid()) && this.getActivity() != null && !this.getActivity().isFinishing())
                {
                    FavoritesHelper.removeFavorite(this.getActivity(), this.event.getCdbid());
                    this.getActivity().invalidateOptionsMenu();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.layout_address:
                if (this.event != null && (this.event.getLatitude() != 0 || this.event.getLongitude() != 0))
                {
                    String latLong = this.event.getLatitude() + "," + this.event.getLongitude();
                    Uri uri = Uri.parse("geo:" + latLong + "?q=" + Uri.encode(latLong + "(" + this.event.getTitle() + ")") + "&z=16");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    try
                    {
                        this.startActivity(intent);
                    } catch (ActivityNotFoundException e)
                    {
                    }
                }
                break;

            case R.id.textView_djubbleDownload:
                try
                {
                    this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + DJUBBLE_PACKAGE)));
                } catch (ActivityNotFoundException e)
                {
                    this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + DJUBBLE_PACKAGE)));
                }
                break;

            case R.id.button_djubble:
                if (this.event != null)
                {
                    List<FormattedTimestamp> dates = this.event.getTimestamps(v.getContext());
                    if (this.event.isPermanent())
                        this.showDjubbleDialog(v.getContext(), null, null);
                    else if (this.event.getDateFrom() != null && this.event.getDateTo() != null)
                        this.showDjubbleDialog(v.getContext(), this.event.getDateFrom(), this.event.getDateTo());
                    else if (dates != null)
                        this.showTimestampDialog(v.getContext(), dates);
                }
                break;

            case R.id.textView_day:
                if (this.getActivity() != null)
                    this.datePickerDialog.show(this.getActivity().getFragmentManager(), null);
                break;

            case R.id.textView_hour:
                if (this.getActivity() != null)
                    this.timePickerDialog.show(this.getActivity().getFragmentManager(), null);
                break;
        }
    }

    private void showDjubbleDialog(Context context, String start, String end)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_date_picker, null);
        TextView textViewTitle = (TextView) dialogView.findViewById(R.id.textView_dialogTitle);
        this.textViewDay = (TextView) dialogView.findViewById(R.id.textView_day);
        this.textViewHour = (TextView) dialogView.findViewById(R.id.textView_hour);

        this.textViewDay.setOnClickListener(this);
        this.textViewHour.setOnClickListener(this);

        if (start != null && end != null)
        {
            Calendar calendarStart = DateUtils.millisToCalendar(start);
            Calendar calendarEnd = DateUtils.millisToCalendar(end);

            textViewTitle.setText(this.getString(R.string.djubble_dialog_period_title, DateUtils.formatShort(calendarStart), DateUtils.formatShort(calendarEnd)));
            this.datePickerDialog.setMinDate(DateUtils.isNotBeforeToday(calendarStart) ? calendarStart : Calendar.getInstance());
            this.datePickerDialog.setMaxDate(calendarEnd);
        }

        this.selectedDate = null;
        this.selectedTime = null;

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(dialogView, false)
                .positiveText(R.string.djubble_dialog_ok)
                .negativeText(R.string.djubble_dialog_cancel)
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        if (DetailFragment.this.selectedDate != null && DetailFragment.this.selectedTime != null)
                        {
                            String formattedDate = DateUtils.formatDjubbleDate(DetailFragment.this.selectedDate, DetailFragment.this.selectedTime);
                            DetailFragment.this.openDjubble(DetailFragment.this.event, formattedDate);
                            dialog.dismiss();
                        } else if (DetailFragment.this.getActivity() != null)
                        {
                            Toast.makeText(DetailFragment.this.getActivity(), getString(R.string.djubble_dialog_error_incomplete), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog)
                    {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showTimestampDialog(Context context, List<FormattedTimestamp> dates)
    {
        if (dates != null && dates.size() > 0)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_timestamp, null);
            RecyclerView recyclerViewTimestamp = (RecyclerView) view.findViewById(R.id.recyclerView_timestamps);

            recyclerViewTimestamp.setLayoutManager(new LinearLayoutManager(context));
            final TimestampAdapter timestampAdapter = new TimestampAdapter(dates, this.selectedPosition);
            recyclerViewTimestamp.setAdapter(timestampAdapter);

            final MaterialDialog dialog = new MaterialDialog.Builder(context)
                    .customView(view, false)
                    .positiveText(R.string.dialog_positive)
                    .negativeText(R.string.dialog_negative)
                    .autoDismiss(false)
                    .callback(new MaterialDialog.ButtonCallback()
                    {
                        @Override
                        public void onPositive(MaterialDialog dialog)
                        {
                            DetailFragment.this.selectedPosition = timestampAdapter.getSelectedTimestampPosition();
                            FormattedTimestamp selectedTimestamp = timestampAdapter.getTimestampForAdapterPosition(DetailFragment.this.selectedPosition);
                            if (selectedTimestamp != null)
                            {
                                DetailFragment.this.openDjubble(DetailFragment.this.event, DateUtils.formatDjubbleDate(selectedTimestamp));
                                dialog.dismiss();
                            } else if (DetailFragment.this.getActivity() != null)
                            {
                                Toast.makeText(DetailFragment.this.getActivity(), getString(R.string.djubble_dialog_error_incomplete), Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .show();
        } else
        {
            Toast.makeText(context, getString(R.string.djubble_dialog_past), Toast.LENGTH_LONG).show();
        }
    }

    private void openDjubble(UitEvent event, String date)
    {
        if (event != null && date != null)
        {
            try
            {
                String url = URLEncoder.encode(this.getString(R.string.djubble_base_url, event.getCdbid()), "UTF-8");
                Uri uri = Uri.parse(this.getString(R.string.djubble_uri, event.getTitle(), url, event.getLocation(), event.getLongitude(), event.getLatitude(), date));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                this.startActivity(intent);
            } catch (UnsupportedEncodingException | ActivityNotFoundException e)
            {
            }
        }
    }

    private void updateDjubbleLayout()
    {
        if (this.isAppInstalled(DJUBBLE_PACKAGE))
        {
            this.textViewDjubble.setVisibility(View.GONE);
            this.textViewDjubbleInfo.setVisibility(View.GONE);
            this.buttonDjubble.setVisibility(View.VISIBLE);
        } else
        {
            this.textViewDjubble.setVisibility(View.VISIBLE);
            this.textViewDjubbleInfo.setVisibility(View.VISIBLE);
            this.buttonDjubble.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second)
    {
        this.selectedTime = Calendar.getInstance();
        this.selectedTime.set(0, 0, 0, hourOfDay, minute);
        this.textViewHour.setText(DateUtils.formatTime(this.selectedTime));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {
        this.selectedDate = Calendar.getInstance();
        this.selectedDate.set(year, monthOfYear, dayOfMonth);
        this.textViewDay.setText(DateUtils.formatLong(this.selectedDate));
    }
}