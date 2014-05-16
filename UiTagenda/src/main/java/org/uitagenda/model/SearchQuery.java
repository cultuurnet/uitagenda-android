package org.uitagenda.model;

import android.text.TextUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jarno on 13/01/14.
 */
public class SearchQuery {

    String currentLocation;
    ArrayList<String> favList;

    List<NameValuePair> params = new LinkedList<NameValuePair>();

    String searchTerm, where, radius, when;
    ArrayList<String> what;
    boolean kids, free, nocourses, current_Location;

    public SearchQuery(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public SearchQuery(ArrayList<String> favList) {
        this.favList = favList;
    }

    public SearchQuery(boolean currentLocation, String searchTerm, String where, String radius, String when, ArrayList<String> what, boolean kids, boolean free, boolean nocourses) {
        this.current_Location = currentLocation;
        this.searchTerm = searchTerm;
        this.where = where;
        this.radius = radius;
        this.when = when;
        this.what = what;
        this.kids = kids;
        this.free = free;
        this.nocourses = nocourses;
    }

    public void setUpBasicsStringParams() {
        params.add(new BasicNameValuePair("fq", "type:event"));
        params.add(new BasicNameValuePair("fq", "language:nl"));
        params.add(new BasicNameValuePair("group", "event"));
    }

    public String createSearchQueryHome() {
        setUpBasicsStringParams();
        params.add(new BasicNameValuePair("rows", "15"));
        params.add(new BasicNameValuePair("fq", "-category_id:0.3.1.0.0"));
        params.add(new BasicNameValuePair("datetype", "today"));
        params.add(new BasicNameValuePair("q", "*:*"));
        params.add(new BasicNameValuePair("d", "10"));
        params.add(new BasicNameValuePair("pt", currentLocation));
        params.add(new BasicNameValuePair("sfield", "physical_gis"));
        params.add(new BasicNameValuePair("sort", "geodist() asc"));

        return URLEncodedUtils.format(params, "utf-8");
    }

    public String createSearchQueryFavorites() {
        setUpBasicsStringParams();
        String cdbid = "cdbid: ";
        for (int i = 0; i < favList.size(); i++) {
            cdbid += "\"" + favList.get(i) + "\" ";
            if (i + 1 < favList.size())
                cdbid += "OR ";
        }
        params.add(new BasicNameValuePair("q", cdbid));
        params.add(new BasicNameValuePair("past", "true"));
        params.add(new BasicNameValuePair("sort", "startdate asc"));

        return URLEncodedUtils.format(params, "utf-8");
    }

    public String createSearchQueryFilter() {
        setUpBasicsStringParams();
        params.add(new BasicNameValuePair("rows", "15"));
        if (searchTerm.matches("")) {
            params.add(new BasicNameValuePair("q", "*:*"));
        } else {
            params.add(new BasicNameValuePair("q", searchTerm.replaceAll(" ", " AND ")));
        }

        if (!current_Location) {
            params.add(new BasicNameValuePair("sort", "startdate asc"));
            if (!where.matches("")) {
                if (!radius.matches("")) {
                    params.add(new BasicNameValuePair("zipcode", where + "!" + radius));
                } else {
                    params.add(new BasicNameValuePair("zipcode", where));
                }
            }
        } else {
            params.add(new BasicNameValuePair("sfield", "physical_gis"));
            params.add(new BasicNameValuePair("sort", "geodist() asc"));

            if (!radius.matches("")) {
                params.add(new BasicNameValuePair("d", radius));
            }
        }

        if (!when.matches("")) {
            params.add(new BasicNameValuePair("datetype", when));
        }

        if (what.size() > 0) {
            String whatItems = TextUtils.join(" OR ", what);
            params.add(new BasicNameValuePair("fq", "category_id:(" +whatItems + ")"));
        }

        if (kids) {
            params.add(new BasicNameValuePair("fq", "agefrom:[* TO 11] OR keywords:\"ook voor kinderen\""));
        }

        if (free) {
            params.add(new BasicNameValuePair("fq", "price:0"));
        }

        if (nocourses) {
            params.add(new BasicNameValuePair("fq", "-category_id:0.3.1.0.0"));
        }

        return URLEncodedUtils.format(params, "utf-8");
    }
}