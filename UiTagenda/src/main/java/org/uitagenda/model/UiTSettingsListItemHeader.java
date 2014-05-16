package org.uitagenda.model;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.uitagenda.R;

public class UiTSettingsListItemHeader implements UiTSettingsItem {

    private final String name;

    public UiTSettingsListItemHeader(String name) {
        this.name = name;
    }

    @Override
    public int getViewType() {
        return UiTSettingsListAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.header_textview, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView text = (TextView) view.findViewById(R.id.tv_header);
        text.setText(name);

        return view;
    }

}
