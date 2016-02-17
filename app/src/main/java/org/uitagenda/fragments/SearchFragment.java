package org.uitagenda.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.uitagenda.R;
import org.uitagenda.activities.SearchResultActivity;
import org.uitagenda.adapters.CategoryAdapter;
import org.uitagenda.adapters.CityAdapter;
import org.uitagenda.model.City;
import org.uitagenda.utils.CityHelper;
import org.uitagenda.utils.LastLocationsHelper;
import org.uitagenda.utils.TrackerHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inneke on 11/08/2015.
 */
public class SearchFragment extends Fragment implements View.OnClickListener
{
    private TextView textViewLocation;
    private TextView textViewDistance;
    private TextView textViewDate;
    private TextView textViewCategory;
    private EditText editTextTerm;
    private CheckBox checkBoxKids;
    private CheckBox checkBoxFree;
    private CheckBox checkBoxCourses;

    private MaterialDialog dialogDistance;
    private MaterialDialog dialogDate;
    private MaterialDialog dialogCategory;
    private MaterialDialog dialogCity;
    private int selectedDistance;
    private int selectedDate;
    private boolean[] selectedCategories;
    private City selectedCity;

    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_DATE = "date";
    private static final String KEY_CATEGORIES = "categories";
    private static final String KEY_CITY = "city";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        LinearLayout layoutLocation = (LinearLayout) view.findViewById(R.id.layout_location);
        this.textViewLocation = (TextView) view.findViewById(R.id.textView_location);
        LinearLayout layoutDistance = (LinearLayout) view.findViewById(R.id.layout_distance);
        this.textViewDistance = (TextView) view.findViewById(R.id.textView_distance);
        LinearLayout layoutDate = (LinearLayout) view.findViewById(R.id.layout_date);
        this.textViewDate = (TextView) view.findViewById(R.id.textView_date);
        LinearLayout layoutCategory = (LinearLayout) view.findViewById(R.id.layout_category);
        this.textViewCategory = (TextView) view.findViewById(R.id.textView_category);
        this.editTextTerm = (EditText) view.findViewById(R.id.editText_term);
        this.checkBoxKids = (CheckBox) view.findViewById(R.id.checkBox_kids);
        this.checkBoxFree = (CheckBox) view.findViewById(R.id.checkBox_free);
        this.checkBoxCourses = (CheckBox) view.findViewById(R.id.checkBox_courses);
        Button buttonSearch = (Button) view.findViewById(R.id.button_search);

        layoutLocation.setOnClickListener(this);
        layoutDistance.setOnClickListener(this);
        layoutDate.setOnClickListener(this);
        layoutCategory.setOnClickListener(this);
        buttonSearch.setOnClickListener(this);

        this.selectedDistance = savedInstanceState != null ? savedInstanceState.getInt(KEY_DISTANCE) : 3;
        this.selectedDate = savedInstanceState != null ? savedInstanceState.getInt(KEY_DATE) : 0;
        this.selectedCategories = savedInstanceState != null ? savedInstanceState.getBooleanArray(KEY_CATEGORIES) : null;
        this.selectedCity = savedInstanceState != null ? (City) savedInstanceState.getSerializable(KEY_CITY) : City.newCurrentCity(view.getContext());
        this.updateDistance();
        this.updateDate();
        this.updateCategory();
        this.updateLocation();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(KEY_DISTANCE, this.selectedDistance);
        outState.putInt(KEY_DATE, this.selectedDate);
        outState.putBooleanArray(KEY_CATEGORIES, this.selectedCategories);
        outState.putSerializable(KEY_CITY, this.selectedCity);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if(this.getActivity() != null && !this.getActivity().isFinishing())
            TrackerHelper.trackScreen(this.getActivity(), TrackerHelper.SCREEN_SEARCH);
    }

    @Override
    public void onPause()
    {
        if(this.dialogDistance != null && this.dialogDistance.isShowing())
            this.dialogDistance.dismiss();
        if(this.dialogDate != null && this.dialogDate.isShowing())
            this.dialogDate.dismiss();
        if(this.dialogCategory != null && this.dialogCategory.isShowing())
            this.dialogCategory.dismiss();
        if(this.dialogCity != null && this.dialogCity.isShowing())
            this.dialogCity.dismiss();

        super.onPause();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.layout_location:
                this.showCityDialog(v.getContext());
                break;

            case R.id.layout_distance:
                this.showDistanceDialog(v.getContext());
                break;

            case R.id.layout_date:
                this.showDateDialog(v.getContext());
                break;

            case R.id.layout_category:
                this.showCategoryDialog(v.getContext());
                break;

            case R.id.button_search:
                String termText = this.editTextTerm.getText().toString();
                String locationText = this.textViewLocation.getText().toString();
                String distanceText = this.textViewDistance.getText().toString();
                String dateText = this.textViewDate.getText().toString();
                String categoryText = this.textViewCategory.getText().toString();
                boolean kids = this.checkBoxKids.isChecked();
                boolean free = this.checkBoxFree.isChecked();
                boolean noCourses = this.checkBoxCourses.isChecked();

                if(!TextUtils.isEmpty(termText))
                    TrackerHelper.trackAction(v.getContext(), TrackerHelper.CATEGORY_SEARCH, TrackerHelper.ACTION_TERM, termText);
                if(!TextUtils.isEmpty(locationText))
                    TrackerHelper.trackAction(v.getContext(), TrackerHelper.CATEGORY_SEARCH, TrackerHelper.ACTION_LOCATION, locationText);
                if(!TextUtils.isEmpty(distanceText))
                    TrackerHelper.trackAction(v.getContext(), TrackerHelper.CATEGORY_SEARCH, TrackerHelper.ACTION_DISTANCE, distanceText.replace(" km", ""));
                if(!TextUtils.isEmpty(dateText))
                    TrackerHelper.trackAction(v.getContext(), TrackerHelper.CATEGORY_SEARCH, TrackerHelper.ACTION_DATE, dateText);
                String[] categoryStrings = this.getResources().getStringArray(R.array.search_category);
                if(this.selectedCategories != null)
                    for(int i = 0; i < this.selectedCategories.length; i++)
                        if(this.selectedCategories[i])
                            TrackerHelper.trackAction(v.getContext(), TrackerHelper.CATEGORY_SEARCH, TrackerHelper.ACTION_CATEGORY, categoryStrings[i]);
                if(kids)
                    TrackerHelper.trackAction(v.getContext(), TrackerHelper.CATEGORY_SEARCH, TrackerHelper.ACTION_EXTRA, TrackerHelper.LABEL_KIDS);
                if(free)
                    TrackerHelper.trackAction(v.getContext(), TrackerHelper.CATEGORY_SEARCH, TrackerHelper.ACTION_EXTRA, TrackerHelper.LABEL_FREE);
                if(noCourses)
                    TrackerHelper.trackAction(v.getContext(), TrackerHelper.CATEGORY_SEARCH, TrackerHelper.ACTION_EXTRA, TrackerHelper.LABEL_NO_COURSES);

                StringBuilder sb = new StringBuilder();
                if(!TextUtils.isEmpty(termText))
                {
                    sb.append(termText);
                }
                if(!TextUtils.isEmpty(locationText))
                {
                    if(sb.length() > 0)
                        sb.append(" - ");
                    sb.append(locationText);
                }
                if(!TextUtils.isEmpty(distanceText))
                {
                    if(sb.length() > 0)
                        sb.append(" - ");
                    sb.append(distanceText);
                }
                if(!TextUtils.isEmpty(dateText))
                {
                    if(sb.length() > 0)
                        sb.append(" - ");
                    sb.append(dateText);
                }
                if(!TextUtils.isEmpty(categoryText))
                {
                    if(sb.length() > 0)
                        sb.append(" - ");
                    sb.append(categoryText);
                }
                if(kids)
                {
                    if(sb.length() > 0)
                        sb.append(" - ");
                    sb.append(this.getString(R.string.search_kids_only));
                }
                if(free)
                {
                    if(sb.length() > 0)
                        sb.append(" - ");
                    sb.append(this.getString(R.string.search_free));
                }
                if(noCourses)
                {
                    if(sb.length() > 0)
                        sb.append(" - ");
                    sb.append(this.getString(R.string.search_courses));
                }

                String distance = this.getDistance(this.selectedDistance);
                String zipCode = this.selectedCity != null && !this.selectedCity.isCurrent() && !this.selectedCity.isAll() ? this.selectedCity.getZipCode() : null;

                StringBuilder sbCategories = new StringBuilder();
                if(this.selectedCategories != null)
                    for(int i = 0; i < this.selectedCategories.length; i++)
                        if(this.selectedCategories[i])
                        {
                            if(sbCategories.length() > 0)
                                sbCategories.append(" OR ");
                            sbCategories.append("\"").append(this.getCategory(i)).append("\"");
                        }

                String categories = sbCategories.toString();

                String queryCategory = !TextUtils.isEmpty(categories) ? "category_id:(" + categories + ")" : null;
                String queryTerm = TextUtils.isEmpty(termText) ? "*:*" : termText.replace(" ", " AND ");
                String querySort = this.selectedCity != null && this.selectedCity.isCurrent() ? "geodist() asc" : "startdate asc";
                String queryCurrent = this.selectedCity != null && this.selectedCity.isCurrent() ? "physical_gis" : null;
                String queryDistance = this.selectedCity != null && this.selectedCity.isCurrent() ? distance : null;
                String queryZipCode = !TextUtils.isEmpty(zipCode) ? zipCode + "!" + distance : null;
                String queryDate = this.getDate(this.selectedDate);
                String queryKids = kids ? "agefrom:[* TO 11] OR keywords:\"ook voor kinderen\"" : null;
                String queryFree = free ? "price:0" : null;
                String queryNoCourses = noCourses ? "-category_id:0.3.1.0.0" : null;
                String completeQuery = sb.toString();

                Intent intent = new Intent(v.getContext(), SearchResultActivity.class);
                intent.putExtra(SearchResultActivity.KEY_COMPLETE_QUERY, completeQuery);
                intent.putExtra(SearchResultActivity.KEY_QUERY_CATEGORY, queryCategory);
                intent.putExtra(SearchResultActivity.KEY_QUERY_TERM, queryTerm);
                intent.putExtra(SearchResultActivity.KEY_QUERY_SORT, querySort);
                intent.putExtra(SearchResultActivity.KEY_QUERY_CURRENT, queryCurrent);
                intent.putExtra(SearchResultActivity.KEY_QUERY_DISTANCE, queryDistance);
                intent.putExtra(SearchResultActivity.KEY_QUERY_ZIP_CODE, queryZipCode);
                intent.putExtra(SearchResultActivity.KEY_QUERY_DATE, queryDate);
                intent.putExtra(SearchResultActivity.KEY_QUERY_KIDS, queryKids);
                intent.putExtra(SearchResultActivity.KEY_QUERY_FREE, queryFree);
                intent.putExtra(SearchResultActivity.KEY_QUERY_NO_COURSES, queryNoCourses);
                intent.putExtra(SearchResultActivity.KEY_OFFER_SAVE, true);
                this.startActivity(intent);

                break;
        }
    }

    private String getDistance(int position)
    {
        switch (position)
        {
            case 0: return "1";
            case 1: return "2";
            case 2: return "5";
            case 3: return "10";
            case 4: return "15";
            case 5: return "25";
            default: return "10";
        }
    }

    private String getDate(int position)
    {
        switch (position)
        {
            case 0: return "today";
            case 1: return "tomorrow";
            case 2: return "thisweekend";
            case 3: return "nextweekend";
            case 4: return "next7days";
            case 5: return "next30days";
            case 6: return "next3months";
            case 7: return "next12months";
            case 8: return "permanent";
            default: return "today";
        }
    }

    private String getCategory(int position)
    {
        switch (position)
        {
            case 0: return "0.7.0.0.0";
            case 1: return "0.6.0.0.0";
            case 2: return "0.50.4.0.0";
            case 3: return "0.3.3.0.0";
            case 4: return "0.3.1.0.0";
            case 5: return "0.54.0.0.0";
            case 6: return "1.50.0.0.0";
            case 7: return "0.5.0.0.0";
            case 8: return "0.17.0.0.0";
            case 9: return "0.50.6.0.0";
            case 10: return "0.57.0.0.0";
            case 11: return "0.28.0.0.0";
            case 12: return "0.3.2.0.0";
            case 13: return "0.37.0.0.0";
            case 14: return "0.14.0.0.0";
            case 15: return "0.15.0.0.0";
            case 16: return "0.12.0.0.0";
            case 17: return "0.16.0.0.0";
            case 18: return "0.49.0.0.0";
            case 19: return "0.53.0.0.0";
            case 20: return "0.50.21.0.0";
            case 21: return "0.59.0.0.0";
            case 22: return "0.19.0.0.0";
            case 23: return "0.0.0.0.0";
            case 24: return "0.55.0.0.0";
            case 25: return "0.41.0.0.0";
            case 26: return "0.56.0.0.0";
            default: return null;
        }
    }

    private void updateDistance()
    {
        String distanceText = this.getResources().getStringArray(R.array.search_distance)[this.selectedDistance];
        this.textViewDistance.setText(distanceText);
    }

    private void updateDate()
    {
        String dateText = this.getResources().getStringArray(R.array.search_date)[this.selectedDate];
        this.textViewDate.setText(dateText);
    }

    private void updateCategory()
    {
        String[] categories = this.getResources().getStringArray(R.array.search_category);
        StringBuilder sb = new StringBuilder();

        if(this.selectedCategories != null)
            for(int i = 0; i < this.selectedCategories.length; i++)
                if(this.selectedCategories[i])
                {
                    if(sb.length() > 0)
                        sb.append(", ");
                    sb.append(categories[i]);
                }

        String categoryText = sb.length() > 0 ? sb.toString() : this.getString(R.string.search_category_all);
        this.textViewCategory.setText(categoryText);
    }

    private void updateLocation()
    {
        String locationText = this.selectedCity != null ? this.selectedCity.getName() : this.getString(R.string.search_location_current);
        this.textViewLocation.setText(locationText);
    }

    private void showDistanceDialog(Context context)
    {
        this.dialogDistance = new MaterialDialog.Builder(context)
                .items(R.array.search_distance)
                .itemsCallbackSingleChoice(this.selectedDistance, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                    {
                        SearchFragment.this.selectedDistance = which;
                        SearchFragment.this.updateDistance();
                        return true;
                    }
                })
                .positiveText(R.string.dialog_positive)
                .negativeText(R.string.dialog_negative)
                .show();
    }

    private void showDateDialog(Context context)
    {
        this.dialogDate = new MaterialDialog.Builder(context)
                .items(R.array.search_date)
                .itemsCallbackSingleChoice(this.selectedDate, new MaterialDialog.ListCallbackSingleChoice()
                {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                    {
                        SearchFragment.this.selectedDate = which;
                        SearchFragment.this.updateDate();
                        return true;
                    }
                })
                .positiveText(R.string.dialog_positive)
                .negativeText(R.string.dialog_negative)
                .show();
    }

    private void showCategoryDialog(Context context)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_search_category, null);

        RecyclerView recyclerViewCategories = (RecyclerView) view.findViewById(R.id.recyclerView_categories);
        EditText editTextFilter = (EditText) view.findViewById(R.id.editText_filter);

        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(context));
        final CategoryAdapter categoryAdapter = new CategoryAdapter(this.getResources().getStringArray(R.array.search_category), this.selectedCategories);
        recyclerViewCategories.setAdapter(categoryAdapter);

        editTextFilter.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                categoryAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        this.dialogCategory = new MaterialDialog.Builder(context)
                .customView(view, false)
                .positiveText(R.string.dialog_positive)
                .negativeText(R.string.dialog_negative)
                .callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        SearchFragment.this.selectedCategories = categoryAdapter.getSelected();
                        SearchFragment.this.updateCategory();
                    }
                })
                .show();

        this.dialogCategory.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void showCityDialog(Context context)
    {
        List<City> cities = new ArrayList<>();
        cities.add(City.newAllCity(context));
        cities.add(City.newCurrentCity(context));
        cities.addAll(CityHelper.getCities(context));

        List<City> lastCities = LastLocationsHelper.getLastLocations(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_search_city, null);

        RecyclerView recyclerViewCities = (RecyclerView) view.findViewById(R.id.recyclerView_cities);
        EditText editTextFilter = (EditText) view.findViewById(R.id.editText_filter);

        recyclerViewCities.setLayoutManager(new LinearLayoutManager(context));
        final CityAdapter cityAdapter = new CityAdapter(cities, lastCities, this.selectedCity);
        recyclerViewCities.setAdapter(cityAdapter);

        editTextFilter.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                cityAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        this.dialogCity = new MaterialDialog.Builder(context)
                .customView(view, false)
                .positiveText(R.string.dialog_positive)
                .negativeText(R.string.dialog_negative)
                .callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        SearchFragment.this.selectedCity = cityAdapter.getSelectedCity();
                        SearchFragment.this.updateLocation();

                        if(SearchFragment.this.getActivity() != null && !SearchFragment.this.getActivity().isFinishing())
                            LastLocationsHelper.addLocationAndSave(SearchFragment.this.getActivity(), SearchFragment.this.selectedCity);
                    }
                })
                .show();

        this.dialogCity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
