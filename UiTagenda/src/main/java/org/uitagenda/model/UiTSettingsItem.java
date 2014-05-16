package org.uitagenda.model;

import android.view.LayoutInflater;
import android.view.View;

public interface UiTSettingsItem {

	public int getViewType();

	public View getView(LayoutInflater inflater, View convertView);

}
