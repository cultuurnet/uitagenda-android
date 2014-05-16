package org.uitagenda.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;

import org.uitagenda.R;

import java.util.ArrayList;

public class UiTSettingsListItemTextCheck implements UiTSettingsItem {


    Context context;
    public String searchOption;
    ArrayList<String> selectedWhat;
    String selectedItem;


    public UiTSettingsListItemTextCheck(Context context, String searchOption, ArrayList<String> selectedWhat, String selectedItem) {
        this.context = context;
        this.searchOption = searchOption;
        this.selectedWhat = selectedWhat;
        this.selectedItem = selectedItem;

    }


    @Override
    public int getViewType() {
        return UiTSettingsListAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View row = convertView;
        ViewHolder holder = null;

        // Holder represents the elements of the view to use
        // Here are initialized
        if (null == row) {
            row = LayoutInflater.from(context).inflate(R.layout.checked_textview, null);

            holder = new ViewHolder();
            holder.ctv = (CheckedTextView) row.findViewById(R.id.ctv);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.ctv.setText(searchOption);

        if (searchOption.equals(selectedItem)) {
            holder.ctv.setChecked(true);
        } else {
            holder.ctv.setChecked(false);
        }

        return row;
    }


    public static class ViewHolder {
        CheckedTextView ctv;
    }

}
