package org.uitagenda.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.uitagenda.R;
import org.uitagenda.model.SearchQueryListItem;

import java.util.ArrayList;

/**
 * Created by jarno on 18/01/14.
 */
public class UiTSearchQueryAdapter extends BaseAdapter {

    Context context;
    ArrayList<SearchQueryListItem> searchQueryListItems;

    public UiTSearchQueryAdapter(Context context, ArrayList<SearchQueryListItem> searchQueryListItems) {
        this.context = context;
        this.searchQueryListItems = searchQueryListItems;
    }

    @Override
    public int getCount() {
        return searchQueryListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return searchQueryListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.searchquery_listitem, null);
        }

        TextView tv_searchQueryTitle = (TextView) convertView.findViewById(R.id.tv_searchQueryTitle);
        tv_searchQueryTitle.setText(searchQueryListItems.get(position).title);

        return convertView;
    }
}