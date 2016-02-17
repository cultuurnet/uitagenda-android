package org.uitagenda.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.uitagenda.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Inneke on 20/08/2015.
 */
public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable
{
    private int layoutItem;
    private String[] categories;
    private boolean[] selected;
    private CategoryFilter filter;
    private List<Integer> filteredPositions;

    public CategoryAdapter(String[] categories, boolean[] selected)
    {
        this.layoutItem = R.layout.listitem_search_category;
        this.categories = categories;
        if(selected == null)
            selected = new boolean[categories != null ? categories.length : 0];
        this.selected = Arrays.copyOf(selected, selected.length);

        this.filteredPositions = new ArrayList<>();
        if(categories != null)
            for(int i = 0; i < categories.length; i++)
                this.filteredPositions.add(i);
    }

    public boolean[] getSelected()
    {
        return this.selected;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(this.layoutItem, viewGroup, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i)
    {
        this.onBindCategoryViewHolder((CategoryViewHolder) viewHolder, i);
    }

    private void onBindCategoryViewHolder(CategoryViewHolder viewHolder, int i)
    {
        int position = this.filteredPositions.get(i);
        viewHolder.textViewTitle.setText(this.categories[position]);
        viewHolder.checkBoxControl.setChecked(this.selected[position]);
    }

    @Override
    public int getItemCount()
    {
        return this.filteredPositions != null ? this.filteredPositions.size() : 0;
    }

    @Override
    public CategoryFilter getFilter()
    {
        if (this.filter == null)
            this.filter = new CategoryFilter();

        return this.filter;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView textViewTitle;
        CheckBox checkBoxControl;

        public CategoryViewHolder(View itemView)
        {
            super(itemView);

            this.textViewTitle = (TextView) itemView.findViewById(R.id.title);
            this.checkBoxControl = (CheckBox) itemView.findViewById(R.id.control);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int adapterPosition = this.getAdapterPosition();
            int position = CategoryAdapter.this.filteredPositions.get(adapterPosition);
            CategoryAdapter.this.selected[position] = !CategoryAdapter.this.selected[position];
            CategoryAdapter.this.notifyItemChanged(adapterPosition);
        }
    }

    public class CategoryFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            List<Integer> filteredPositions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0)
            {
                for(int i = 0; i < CategoryAdapter.this.categories.length; i++)
                {
                    filteredPositions.add(i);
                }
            }
            else
            {
                for(int i = 0; i < CategoryAdapter.this.categories.length; i++)
                {
                    if(CategoryAdapter.this.categories[i].toLowerCase().contains(constraint.toString().toLowerCase()))
                    {
                        filteredPositions.add(i);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredPositions;
            results.count = filteredPositions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            CategoryAdapter.this.filteredPositions = (List<Integer>) results.values;
            CategoryAdapter.this.notifyDataSetChanged();
        }
    }
}
