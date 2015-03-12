package org.uitagenda;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.uitagenda.model.SearchQueryListItem;
import org.uitagenda.utils.SwipeDismissListViewTouchListener;
import org.uitagenda.utils.UiTSearchDataSource;
import org.uitagenda.utils.UiTSearchQueryAdapter;
import org.uitagenda.utils.UiTagenda;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link SearchQueryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchQueryFragment extends Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchQueryFragment.
     */
    UiTSearchDataSource searchDS;
    UiTSearchQueryAdapter searchQueryAdapter;
    ArrayList<SearchQueryListItem> list;

    public static SearchQueryFragment newInstance() {
        SearchQueryFragment fragment = new SearchQueryFragment();
        return fragment;
    }

    public SearchQueryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        UiTagenda.trackGoogleAnalytics(getActivity(), "Android: Zoekopdrachten");
        Crashlytics.setString("ClassName", this.getClass().getSimpleName());

        View rootView = inflater.inflate(R.layout.fragment_search_query, container, false);

        searchDS = new UiTSearchDataSource(getActivity());

        ListView lv = (ListView) rootView.findViewById(R.id.lv_searchquery);
        list = new ArrayList<SearchQueryListItem>();
        list.addAll(searchDS.findAllSearchUrls());

        lv.setEmptyView(rootView.findViewById(R.id.ll_noresults));

        searchQueryAdapter = new UiTSearchQueryAdapter(getActivity(), list);

        lv.setAdapter(searchQueryAdapter);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    searchDS.deleteSearchQuery(list.get(position).title);
                                    list.remove(list.get(position));
                                }
                                searchQueryAdapter.notifyDataSetChanged();
                            }
                        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SearchResultActivity.class);

                SearchQueryListItem selectedSearchQuery = list.get(position);

                intent.putExtra("currentLocation", selectedSearchQuery.currentLocation);
                intent.putExtra("queryAlreadySaved", true);
                intent.putExtra("searchQuery", selectedSearchQuery.url);
                intent.putExtra("saveQueryString", "");

                startActivity(intent);
            }
        });

        lv.setOnTouchListener(touchListener);
        lv.setOnScrollListener(touchListener.makeScrollListener());

        return rootView;
    }
}
