package org.uitagenda;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;
import org.uitagenda.model.SearchQuery;
import org.uitagenda.model.UiTSettingsItem;
import org.uitagenda.model.UiTSettingsListAdapter;
import org.uitagenda.model.UiTSettingsListItemHeader;
import org.uitagenda.model.UiTSettingsListItemTextCheck;
import org.uitagenda.utils.UiTLatestLocationsDataSource;
import org.uitagenda.utils.UiTSearchAdapter;
import org.uitagenda.utils.UiTagenda;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class SearchFragment extends Fragment {

    public enum Search {
        WHAT, WHEN, WHERE, RADIUS
    }

    Search search;

    AlertDialog myalertDialog;
    TextView tv_where, tv_when, tv_radius, tv_what;
    CheckBox cb_kids, cb_free, cb_noCourses;

    LinkedHashMap<String, String> radiusMap, whenMap, flandersRegionMap;

    private SortedMap<String, String> eventTypesMap;

    String selectedWhere, selectedRadius, selectedWhen;

    ArrayList<String> selectedWhat;

    ArrayList<String> currentWhat, tempWhat;

    EditText et_searchTerm;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        UiTagenda.trackGoogleAnalytics(rootView.getContext(), "Android: Zoeken");
        Crashlytics.setString("ClassName", this.getClass().getSimpleName());

        selectedWhere = "0000";
        selectedRadius = "10";
        selectedWhen = "today";

        currentWhat = new ArrayList<String>();
        tempWhat = new ArrayList<String>();
        selectedWhat = new ArrayList<String>();

        et_searchTerm = (EditText) rootView.findViewById(R.id.et_searchTerm);

        setupWhere(rootView);
        setupRadius(rootView);
        setupWhen(rootView);
        setupWhat(rootView);
        setupKids(rootView);
        setupFree(rootView);
        setupNoCourses(rootView);

        createRadiusMap();
        createWhenMap();

        Button btnSearch = (Button) rootView.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(
                new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         // radius selected, but no where

                         // Create SaveQueryString
                         ArrayList<String> saveQuery = new ArrayList<String>();
                         if (!et_searchTerm.getText().toString().matches("")) {
                             saveQuery.add(et_searchTerm.getText().toString());
                             EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent("Uitgebreid zoeken", "Android: Zoekveld", et_searchTerm.getText().toString(), null).build());
                         }
                         if (!tv_where.getText().toString().matches("")) {
                             saveQuery.add(tv_where.getText().toString());
                             EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent("Uitgebreid zoeken", "Android: Waar", tv_where.getText().toString(), null).build());
                         }
                         if (!tv_radius.getText().toString().matches("")) {
                             saveQuery.add(tv_radius.getText().toString());
                             EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent("Uitgebreid zoeken", "Android: Straal", tv_radius.getText().toString().replace(" km", ""), null).build());
                         }
                         if (!tv_when.getText().toString().matches("")) {
                             saveQuery.add(tv_when.getText().toString());
                             EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent("Uitgebreid zoeken", "Android: Wanneer", tv_when.getText().toString(), null).build());
                         }
                         if (!tv_what.getText().toString().matches("")) {
                             saveQuery.add(tv_what.getText().toString());

                             String[] eventTypes = tv_what.getText().toString().split(", ", -1);
                             for (String eventType : eventTypes) {
                                 EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent("Uitgebreid zoeken", "Android: Wat", eventType, null).build());
                             }

                         }
                         if (cb_kids.isChecked()) {
                             saveQuery.add("Enkel voor kinderen");
                             EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent("Uitgebreid zoeken", "Android: Extra zoekcriteria", "Enkel voor kinderen", null).build());
                         }
                         if (cb_free.isChecked()) {
                             saveQuery.add("Enkel gratis");
                             EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent("Uitgebreid zoeken", "Android: Extra zoekcriteria", "Enkel gratis", null).build());
                         }
                         if (cb_noCourses.isChecked()) {
                             saveQuery.add("Geen cursussen en workshops");
                             EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent("Uitgebreid zoeken", "Android: Extra zoekcriteria", "Geen cursussen en workshops", null).build());
                         } else {
                         }


                         String saveQueryString = TextUtils.join(" - ", saveQuery);

                        /*if (saveQueryString.length() > 40) {
                            saveQueryString = saveQueryString.substring(0, 40) + "...";
                        }*/

                         boolean checkCurrentLocation = false;
                         if (selectedWhere.equals("0000")) {
                             checkCurrentLocation = true;
                         }

                         if (selectedWhere.equals("000000")) {
                             selectedWhere = "";
                         }

                         SearchQuery newSearchQuery = new SearchQuery(
                                 checkCurrentLocation,
                                 et_searchTerm.getText().toString(),
                                 selectedWhere,
                                 selectedRadius,
                                 selectedWhen,
                                 selectedWhat,
                                 cb_kids.isChecked(),
                                 cb_free.isChecked(),
                                 cb_noCourses.isChecked());

                         String searchString = newSearchQuery.createSearchQueryFilter();

                         Crashlytics.log("searchString" + searchString);

                         Intent intent = new Intent(getActivity().getApplicationContext(), SearchResultActivity.class);
                         intent.putExtra("currentLocation", checkCurrentLocation);
                         intent.putExtra("queryAlreadySaved", false);
                         intent.putExtra("searchQuery", searchString);
                         intent.putExtra("saveQueryString", saveQueryString);
                         startActivity(intent);
                     }
                }
        );

        return rootView;
    }

    private void createRadiusMap() {
        radiusMap = new LinkedHashMap<String, String>();
        radiusMap.put("1", "1 km");
        radiusMap.put("2", "2 km");
        radiusMap.put("5", "5 km");
        radiusMap.put("10", "10 km");
        radiusMap.put("15", "15 km");
        radiusMap.put("25", "25 km");
    }

    private void createWhenMap() {
        whenMap = new LinkedHashMap<String, String>();
        whenMap.put("today", "Vandaag");
        whenMap.put("tomorrow", "Morgen");
        whenMap.put("thisweekend", "Dit weekend");
        whenMap.put("nextweekend", "Volgend weekend");
        whenMap.put("next7days", "Volgende 7 dagen");
        whenMap.put("next30days", "Volgende 30 dagen");
        whenMap.put("next3months", "Volgende 3 maanden");
        whenMap.put("next12months", "Volgende 12 maanden");
        whenMap.put("permanent", "Permanent");
    }

    private void setupWhere(View rootView) {
        LinearLayout ll_where = (LinearLayout) rootView.findViewById(R.id.ll_where);
        tv_where = (TextView) rootView.findViewById(R.id.tv_where_subtitle);
        tv_where.setText("Huidige locatie");
        ll_where.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildAlertDialog(search.WHERE);
            }
        });
    }

    private void setupRadius(View rootView) {
        LinearLayout ll_radius = (LinearLayout) rootView.findViewById(R.id.ll_straal);
        tv_radius = (TextView) rootView.findViewById(R.id.tv_straal_subtitle);
        tv_radius.setText("10 km");
        ll_radius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildAlertDialog(search.RADIUS);
            }
        });
    }

    private void setupWhen(View rootView) {
        LinearLayout ll_when = (LinearLayout) rootView.findViewById(R.id.ll_when);
        tv_when = (TextView) rootView.findViewById(R.id.tv_when_subtitle);
        tv_when.setText("Vandaag");
        ll_when.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildAlertDialog(search.WHEN);
            }
        });
    }

    private void setupWhat(View rootView) {
        LinearLayout ll_what = (LinearLayout) rootView.findViewById(R.id.ll_what);
        tv_what = (TextView) rootView.findViewById(R.id.tv_what_subtitle);
        tv_what.setText("Alles");
        ll_what.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildAlertDialog(search.WHAT);
            }
        });
    }

    private void setupKids(View rootView) {
        LinearLayout ll_kids = (LinearLayout) rootView.findViewById(R.id.ll_kids);
        cb_kids = (CheckBox) rootView.findViewById(R.id.cb_kids);
        ll_kids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFocusEditText();
                if (cb_kids.isChecked()) {
                    cb_kids.setChecked(false);
                } else {
                    cb_kids.setChecked(true);
                }
            }
        });

        cb_kids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFocusEditText();
            }
        });
    }

    private void setupFree(View rootView) {
        LinearLayout ll_free = (LinearLayout) rootView.findViewById(R.id.ll_free);
        cb_free = (CheckBox) rootView.findViewById(R.id.cb_free);
        ll_free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFocusEditText();
                if (cb_free.isChecked()) {
                    cb_free.setChecked(false);
                } else {
                    cb_free.setChecked(true);
                }
            }
        });

        cb_free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFocusEditText();
            }
        });
    }

    private void setupNoCourses(View rootView) {
        LinearLayout ll_noCourses = (LinearLayout) rootView.findViewById(R.id.ll_nocourses);
        cb_noCourses = (CheckBox) rootView.findViewById(R.id.cb_nocourses);
        cb_noCourses.setChecked(true);
        ll_noCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFocusEditText();
                if (cb_noCourses.isChecked()) {
                    cb_noCourses.setChecked(false);
                } else {
                    cb_noCourses.setChecked(true);
                }
            }
        });

        cb_noCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFocusEditText();
            }
        });

    }

    private void removeFocusEditText() {
        // remove focus + keyboard et_searchTerm
        if (et_searchTerm.hasFocus()) {
            et_searchTerm.clearFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void buildAlertDialog(final Search search) {
        removeFocusEditText();

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());

        final EditText editText = new EditText(getActivity());
        final ListView listview = new ListView(getActivity());

        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> searchTermList = new ArrayList<String>();

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        UiTSearchAdapter searchAdapter = null;

        switch (search) {
            case WHERE:
                layout.addView(editText);
                if (flandersRegionMap == null) {
                    createFlandersRegionMap();
                    editText.setHint(getString(R.string.search_where_hint));
                }
                ArrayList<UiTSettingsItem> whereList = new ArrayList<UiTSettingsItem>();
                UiTLatestLocationsDataSource latestLocationsDS = new UiTLatestLocationsDataSource(getActivity());

                if (latestLocationsDS.findLatestLocations().size() > 0) {
                    whereList.add(new UiTSettingsListItemHeader("Favoriete locaties"));

                    for (int i = latestLocationsDS.findLatestLocations().size() - 1; i >= 0; i--) {
                        whereList.add(new UiTSettingsListItemTextCheck(getActivity(), latestLocationsDS.findLatestLocations().get(i), null, tv_where.getText().toString()));
                    }
                }

                whereList.add(new UiTSettingsListItemHeader("Alle locaties"));

                list.addAll(flandersRegionMap.keySet());
                for (int i = 0; i < flandersRegionMap.keySet().size(); i++) {
                    whereList.add(new UiTSettingsListItemTextCheck(getActivity(), list.get(i), null, tv_where.getText().toString()));
                }
                UiTSettingsListAdapter settingsAdapter = new UiTSettingsListAdapter(getActivity(), whereList);
                listview.setAdapter(settingsAdapter);
                break;
            case RADIUS:
                list.addAll(radiusMap.values());
                searchAdapter = new UiTSearchAdapter(getActivity(), list, null, tv_radius.getText().toString());
                listview.setAdapter(searchAdapter);
                break;
            case WHEN:
                list.addAll(whenMap.values());
                searchAdapter = new UiTSearchAdapter(getActivity(), list, null, tv_when.getText().toString());
                listview.setAdapter(searchAdapter);
                break;
            case WHAT:
                layout.addView(editText);
                if (eventTypesMap == null) {
                    createEventTypesMap();
                }

                for (int i = tempWhat.size()-1; i > -1; i--) {
                    if (!selectedWhat.contains(eventTypesMap.get(tempWhat.get(i)))) {
                        tempWhat.remove(i);
                    }
                }

                list.addAll(eventTypesMap.keySet());
                searchAdapter = new UiTSearchAdapter(getActivity(), list, tempWhat, "");
                listview.setAdapter(searchAdapter);
                break;
        }

        layout.addView(listview);
        myDialog.setView(layout);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedFromList;

                if (!(listview.getItemAtPosition(position) instanceof UiTSettingsListItemHeader)) {
                    CheckedTextView cbtv = (CheckedTextView) view.findViewById(R.id.ctv);
                    if (!cbtv.isChecked()) {
                        cbtv.setChecked(true);
                    } else {
                        cbtv.setChecked(false);
                    }
                    switch (search) {
                        case WHERE:
                            if ((listview.getItemAtPosition(position) instanceof UiTSettingsListItemTextCheck)) {
                                UiTSettingsListItemTextCheck listitem = (UiTSettingsListItemTextCheck) (listview.getItemAtPosition(position));
                                selectedFromList = listitem.searchOption;
                            } else {
                                selectedFromList = (String) listview.getItemAtPosition(position);
                            }
                            if (selectedFromList.equals(tv_where.getText().toString())) {
                                tv_where.setText("Alle locaties");
                                selectedWhere = "";
                            } else {
                                tv_where.setText(selectedFromList);
                                for (int j = 0; j < flandersRegionMap.values().size(); j++) {
                                    if ((flandersRegionMap.keySet().toArray()[j]).equals(selectedFromList)) {
                                        selectedWhere = flandersRegionMap.values().toArray()[j].toString();
                                        UiTLatestLocationsDataSource latestLocationsDS = new UiTLatestLocationsDataSource(getActivity());
                                        latestLocationsDS.addLocation(selectedFromList);
                                    }
                                }
                            }
                            myalertDialog.dismiss();

                            break;
                        case RADIUS:
                            selectedFromList = (String) (listview.getItemAtPosition(position));
                            if (selectedFromList.equals(tv_radius.getText().toString())) {
                                tv_radius.setText("");
                                selectedRadius = "";
                            } else {
                                tv_radius.setText(selectedFromList);
                                selectedRadius = radiusMap.keySet().toArray()[position].toString();
                            }
                            myalertDialog.dismiss();
                            break;
                        case WHEN:
                            selectedFromList = (String) (listview.getItemAtPosition(position));
                            if (selectedFromList.equals(tv_when.getText().toString())) {
                                tv_when.setText("");
                                selectedWhen = "";
                            } else {
                                tv_when.setText(selectedFromList);
                                selectedWhen = whenMap.keySet().toArray()[position].toString();
                            }
                            myalertDialog.dismiss();
                            break;
                        case WHAT:
                            selectedFromList = (String) (listview.getItemAtPosition(position));
                            if (tempWhat.contains(selectedFromList)) {
                                tempWhat.remove(selectedFromList);
                            } else {
                                tempWhat.add(selectedFromList);
                            }
                            break;
                    }
                }
            }
        });

        editText.setSingleLine();
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchTermList.clear();

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).toLowerCase().contains(s.toString().toLowerCase())) {
                        searchTermList.add(list.get(i));
                    }
                }
                if (search == Search.WHAT) {
                    listview.setAdapter(new UiTSearchAdapter(getActivity(), searchTermList, tempWhat, ""));

                } else {
                    listview.setAdapter(new UiTSearchAdapter(getActivity(), searchTermList, null, tv_where.getText().toString()));
                }
            }
        });

        myDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                tempWhat.clear();
                dialog.dismiss();
            }
        });

        if (search == search.WHAT) {
            myDialog.setPositiveButton(getString(R.string.select), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ArrayList<String> newSelectedItems = new ArrayList<String>(tempWhat);
                    currentWhat = newSelectedItems;
                    tv_what.setText(TextUtils.join(", ", currentWhat));

                    selectedWhat.clear();
                    for (int j = 0; j < eventTypesMap.values().size(); j++) {
                        if (currentWhat.contains(eventTypesMap.keySet().toArray()[j])) {
                            selectedWhat.add(eventTypesMap.values().toArray()[j].toString());
                        }
                    }

                    if (currentWhat.size() == 0) {
                        tv_what.setText("Alles");
                    }

                    dialog.dismiss();
                }
            });
        }

        myalertDialog = myDialog.show();
    }

    private void createEventTypesMap() {
        eventTypesMap = new TreeMap<String, String>();
        String eventTypesString = UiTagenda.readFromFile("eventtypes.txt");
        try {
            JSONObject object = new JSONObject(eventTypesString);
            eventTypesMap.putAll(UiTagenda.getEventTypes(object.getJSONObject("categorisation").getJSONArray("term")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createFlandersRegionMap() {
        flandersRegionMap = new LinkedHashMap<String, String>();
        flandersRegionMap.put("Alle locaties", "000000");
        flandersRegionMap.put("Huidige locatie", "0000");
        String flandersRegionString = UiTagenda.readFromFile("flandersregion.txt");
        try {
            JSONObject object = new JSONObject(flandersRegionString);
            flandersRegionMap.putAll(UiTagenda.getFlandersRegion(object.getJSONObject("categorisation").getJSONArray("term")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
