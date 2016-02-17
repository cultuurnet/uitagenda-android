package org.uitagenda.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.TextView;

import org.uitagenda.R;
import org.uitagenda.model.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inneke on 20/08/2015.
 */
public class CityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable
{
    private int layoutItem;
    private int layoutHeader;
    private List<City> cities;
    private List<City> lastCities;
    private City selectedCity;
    private CityFilter filter;
    private List<City> filteredCities;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CITY = 1;

    public CityAdapter(List<City> cities, List<City> lastCities, City selectedCity)
    {
        this.layoutItem = R.layout.listitem_search_city;
        this.layoutHeader = R.layout.listitem_search_header;
        if(cities == null)
            cities = new ArrayList<>();
        this.cities = cities;
        this.filteredCities = cities;
        this.selectedCity = City.copyOf(selectedCity);

        this.lastCities = lastCities;
    }

    private City getCityForAdapterPosition(int i)
    {
        int filterSize = this.filteredCities.size();
        int allSize = this.cities.size();
        int lastSize = this.lastCities != null ? this.lastCities.size() : 0;

        if(filterSize == allSize)
        {
            if(lastSize > 0)
            {
                if(i - 1 < lastSize)
                {
                    return this.lastCities.get(i - 1);
                }
                else
                {
                    return this.filteredCities.get(i - lastSize - 2);
                }
            }
            else
            {
                return this.filteredCities.get(i - 1);
            }
        }
        else
        {
            return this.filteredCities.get(i);
        }
    }

    public City getSelectedCity()
    {
        return this.selectedCity;
    }

    @Override
    public int getItemViewType(int position)
    {
        int filterSize = this.filteredCities != null ? this.filteredCities.size() : 0;
        int allSize = this.cities != null ? this.cities.size() : 0;
        int lastSize = this.lastCities != null ? this.lastCities.size() : 0;

        return filterSize == allSize && (position == 0 || (lastSize > 0 && position == lastSize + 1)) ? TYPE_HEADER : TYPE_CITY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        switch (viewType)
        {
            case TYPE_HEADER:
                View headerView = LayoutInflater.from(viewGroup.getContext()).inflate(this.layoutHeader, viewGroup, false);
                return new HeaderViewHolder(headerView);

            case TYPE_CITY:
                View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(this.layoutItem, viewGroup, false);
                return new CityViewHolder(itemView);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i)
    {
        switch (this.getItemViewType(i))
        {
            case TYPE_HEADER:
                this.onBindHeaderViewHolder((HeaderViewHolder) viewHolder, i);
                break;

            case TYPE_CITY:
                this.onBindCityViewHolder((CityViewHolder) viewHolder, i);
                break;
        }
    }

    private void onBindCityViewHolder(CityViewHolder viewHolder, int i)
    {
        City city = this.getCityForAdapterPosition(i);
        boolean selected = city.equals(this.selectedCity);

        viewHolder.bind(city, selected);
    }

    private void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int i)
    {
        int filterSize = this.filteredCities != null ? this.filteredCities.size() : 0;
        int allSize = this.cities != null ? this.cities.size() : 0;
        int lastSize = this.lastCities != null ? this.lastCities.size() : 0;

        viewHolder.textViewTitle.setText(filterSize == allSize && lastSize > 0 && i == 0 ? R.string.search_location_header_last : R.string.search_category_all);
    }

    @Override
    public int getItemCount()
    {
        int filterSize = this.filteredCities != null ? this.filteredCities.size() : 0;
        int allSize = this.cities != null ? this.cities.size() : 0;
        int lastSize = this.lastCities != null ? this.lastCities.size() : 0;

        if(filterSize != allSize)
            return filterSize;

        if(lastSize > 0)
            return allSize + lastSize + 2;

        return allSize + 1;
    }

    @Override
    public CityFilter getFilter()
    {
        if (this.filter == null)
            this.filter = new CityFilter();

        return this.filter;
    }

    public class CityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView textViewTitle;
        RadioButton radioButtonControl;
        City city;

        public CityViewHolder(View itemView)
        {
            super(itemView);

            this.textViewTitle = (TextView) itemView.findViewById(R.id.title);
            this.radioButtonControl = (RadioButton) itemView.findViewById(R.id.control);

            itemView.setOnClickListener(this);
        }

        public void bind(City city, boolean selected)
        {
            this.city = city;
            this.textViewTitle.setText(city.getName());
            this.radioButtonControl.setChecked(selected);
        }

        @Override
        public void onClick(View v)
        {
            if(this.city != null)
            {
                CityAdapter.this.selectedCity = this.city;
                CityAdapter.this.notifyDataSetChanged();
            }
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewTitle;

        public HeaderViewHolder(View itemView)
        {
            super(itemView);

            this.textViewTitle = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public class CityFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0)
            {
                results.values = CityAdapter.this.cities;
                results.count = CityAdapter.this.cities.size();
            }
            else
            {
                List<City> filteredCities = new ArrayList<>();

                for (City city : CityAdapter.this.cities)
                    if (city.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filteredCities.add(city);

                results.values = filteredCities;
                results.count = filteredCities.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            CityAdapter.this.filteredCities = (List<City>) results.values;
            CityAdapter.this.notifyDataSetChanged();
        }
    }
}
