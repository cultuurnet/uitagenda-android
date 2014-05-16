package org.uitagenda.model;

public class SearchQueryListItem {

	public String title, url;
    public boolean currentLocation;

	public SearchQueryListItem(String title, String url, boolean currentLocation) {
		super();
		this.title = title;
		this.url = url;
        this.currentLocation = currentLocation;
	}

    /**
     * Display title in listview (ArrayAdapter)
     * @return title
     */
    @Override
    public String toString() {
        return title;
    }
}
