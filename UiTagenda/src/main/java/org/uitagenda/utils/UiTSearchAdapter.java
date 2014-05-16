package org.uitagenda.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import org.uitagenda.R;

import java.util.ArrayList;

/**
 * Created by alexf_000 on 1/17/14.
 */
public class UiTSearchAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> searchOptions;
    ArrayList<String> selectedWhat;
    String selectedItem;

    public UiTSearchAdapter(Context context, ArrayList<String> searchOptions, ArrayList<String> selectedWhat, String seletedItem) {
        this.context = context;
        this.searchOptions = searchOptions;
        this.selectedWhat = selectedWhat;
        this.selectedItem = seletedItem;
    }

    @Override
    public int getCount() {
        return searchOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return searchOptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.checked_textview, null);
        }

        CheckedTextView cbtv = (CheckedTextView) convertView.findViewById(R.id.ctv);
        cbtv.setText(searchOptions.get(position));

        if (!selectedItem.matches("")) {
            if (searchOptions.get(position).equals(selectedItem)) {
                cbtv.setChecked(true);
            } else {
                cbtv.setChecked(false);
            }
        } else {
            if (selectedWhat != null) {
                if (selectedWhat.size() > 0) {
                    if (selectedWhat.contains(searchOptions.get(position))) {
                        cbtv.setChecked((true));
                    } else {
                        cbtv.setChecked(false);
                    }
                }
            }
        }
        if(searchOptions.get(position).equals("Alle locaties")){
            cbtv.setCheckMarkDrawable(null);
            cbtv.setBackgroundColor(context.getResources().getColor(R.color.date_color));
        }
        return convertView;
    }

}
