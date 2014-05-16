package org.uitagenda.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jarno on 14/01/14.
 */
public class UiTEvent implements Serializable {

    public String title, calendarSummary, location, city, imageURL, cdbid, contactHouseNr, contactZipcode, contactStreet, longDescription, shortDescription,
            priceDescription, priceValue, organiser;
    public Integer ageFrom;
    public boolean datePassed;
    public ArrayList<String> contactInfo, reservationInfo, categories, mediaInfo, performers;

    public double latCoordinate, lonCoordinate;

    public UiTEvent(JSONObject event) throws JSONException {

        this.title = "";
        this.calendarSummary = "";
        this.location = "";
        this.city = "";
        this.imageURL = null;
        this.ageFrom = null;
        this.cdbid = "";
        this.datePassed = false;
        this.contactHouseNr = "";
        this.contactStreet = "";
        this.contactZipcode = "";
        this.contactInfo = new ArrayList<String>();
        this.reservationInfo = new ArrayList<String>();
        this.longDescription = "";
        this.shortDescription = "";
        this.priceDescription = "";
        this.priceValue = "";
        this.organiser = "";
        this.categories = new ArrayList<String>();
        this.mediaInfo = new ArrayList<String>();
        this.performers = new ArrayList<String>();

        this.latCoordinate = 0;
        this.lonCoordinate = 0;


        JSONObject eventDetail = event.getJSONObject("event").getJSONObject("eventdetails").getJSONArray("eventdetail").getJSONObject(0);

        this.title = eventDetail.getString("title").trim();
        this.cdbid = event.getJSONObject("event").getString("cdbid");

        if (eventDetail.has("calendarsummary")) {
            String calendarSummaryValue = eventDetail.getString("calendarsummary");
           /* if (calendarSummaryValue.length() > 2) {
                Log.d("/n", "opvangen" + calendarSummaryValue.substring(0, 2));
                if (calendarSummaryValue.substring(0, 2).equals("\\n")) {
                    this.calendarSummary = calendarSummaryValue.substring(2);
                } else*/
            {
                if (!calendarSummaryValue.equals("null")) {
                    this.calendarSummary = calendarSummaryValue.trim();
                }
                // }
            }
        }

        if (event.getJSONObject("event").getJSONObject("location").getJSONObject("label").has("value")) {
            String locationValue = event.getJSONObject("event").getJSONObject("location").getJSONObject("label").getString("value");
            if (!locationValue.equals("null")) {
                this.location = locationValue.trim();
            }
        }

        JSONArray arrayWithContactInfo = event.getJSONObject("event").getJSONObject("contactinfo").getJSONArray("addressAndMailAndPhone");

        for (int i = 0; i < arrayWithContactInfo.length(); i++) {
            if (arrayWithContactInfo.getJSONObject(i).has("address")) {
                this.city = arrayWithContactInfo.getJSONObject(i).getJSONObject("address").getJSONObject("physical").getJSONObject("city").getString("value").trim();

                this.latCoordinate = arrayWithContactInfo.getJSONObject(i).getJSONObject("address").getJSONObject("physical").getJSONObject("gis").getDouble("ycoordinate");
                this.lonCoordinate = arrayWithContactInfo.getJSONObject(i).getJSONObject("address").getJSONObject("physical").getJSONObject("gis").getDouble("xcoordinate");

                if (!arrayWithContactInfo.getJSONObject(i).getJSONObject("address").getJSONObject("physical").isNull("housenr")) {
                    this.contactHouseNr = arrayWithContactInfo.getJSONObject(i).getJSONObject("address").getJSONObject("physical").getString("housenr");
                }
                if (!arrayWithContactInfo.getJSONObject(i).getJSONObject("address").getJSONObject("physical").isNull("housenr")) {
                    this.contactZipcode = arrayWithContactInfo.getJSONObject(i).getJSONObject("address").getJSONObject("physical").getString("zipcode");
                }
                if (!arrayWithContactInfo.getJSONObject(i).getJSONObject("address").getJSONObject("physical").isNull("street")) {
                    if (!arrayWithContactInfo.getJSONObject(i).getJSONObject("address").getJSONObject("physical").getJSONObject("street").isNull("value")) {
                        this.contactStreet = arrayWithContactInfo.getJSONObject(i).getJSONObject("address").getJSONObject("physical").getJSONObject("street").getString("value");
                    }
                }
            }
            if (arrayWithContactInfo.getJSONObject(i).has("url")) {
                if (arrayWithContactInfo.getJSONObject(i).getJSONObject("url").isNull("reservation")) { //CHECK RESERVATION
                    if (!arrayWithContactInfo.getJSONObject(i).getJSONObject("url").isNull("value")) {
                        contactInfo.add(arrayWithContactInfo.getJSONObject(i).getJSONObject("url").getString("value"));
                    }
                } else {
                    reservationInfo.add(arrayWithContactInfo.getJSONObject(i).getJSONObject("url").getString("value"));
                }
            }
            if (arrayWithContactInfo.getJSONObject(i).has("phone")) {
                if (arrayWithContactInfo.getJSONObject(i).getJSONObject("phone").isNull("reservation")) { //CHECK RESERVATION
                    if (!arrayWithContactInfo.getJSONObject(i).getJSONObject("phone").isNull("value")) {
                        contactInfo.add(arrayWithContactInfo.getJSONObject(i).getJSONObject("phone").getString("value"));
                    }
                } else {
                    reservationInfo.add(arrayWithContactInfo.getJSONObject(i).getJSONObject("phone").getString("value"));
                }
            }
            if (arrayWithContactInfo.getJSONObject(i).has("mail")) {
                if (arrayWithContactInfo.getJSONObject(i).getJSONObject("mail").isNull("reservation")) { //CHECK RESERVATION
                    if (!arrayWithContactInfo.getJSONObject(i).getJSONObject("mail").isNull("value")) {
                        contactInfo.add(arrayWithContactInfo.getJSONObject(i).getJSONObject("mail").getString("value"));
                    }
                } else {
                    reservationInfo.add(arrayWithContactInfo.getJSONObject(i).getJSONObject("mail").getString("value"));
                }
            }
        }

        if (!eventDetail.isNull("performers")) {
            for (int i = 0; i < eventDetail.getJSONObject("performers").getJSONArray("performer").length(); i++) {
              this.performers.add(eventDetail.getJSONObject("performers").getJSONArray("performer").getJSONObject(i).getJSONObject("label").getString("value"));
            }
        }

            if (!event.getJSONObject("event").isNull("agefrom")) {
            this.ageFrom = event.getJSONObject("event").getInt("agefrom");
        }

        if (!eventDetail.isNull("media")) {
            JSONArray mediaFileInfo = eventDetail.getJSONObject("media").getJSONArray("file");
            for (int i = 0; i < mediaFileInfo.length(); i++) {
                if (mediaFileInfo.getJSONObject(i).getString("mediatype").equals("photo") && !mediaFileInfo.getJSONObject(i).isNull("hlink")) {
                    this.imageURL = mediaFileInfo.getJSONObject(i).getString("hlink");


                    if (this.imageURL.startsWith("//")) {
                        this.imageURL = "http:" + mediaFileInfo.getJSONObject(i).getString("hlink");
                    }
                }
            }
        }
        if (!event.getJSONObject("event").isNull("availableto")) {
            this.datePassed = isDatePassed(event.getJSONObject("event").getLong("availableto"));
        }

        if (!eventDetail.isNull("shortdescription")) {
            this.shortDescription = eventDetail.getString("shortdescription");
        }
        if (!eventDetail.isNull("longdescription")) {
            this.longDescription = eventDetail.getString("longdescription");
        }
        if (!eventDetail.isNull("price")) {
            if (!eventDetail.getJSONObject("price").isNull("pricevalue")) {
                this.priceValue = eventDetail.getJSONObject("price").getString("pricevalue");
                if (this.priceValue.equals("0.0")) {
                    this.priceValue = "0";
                }
            }
            if (!eventDetail.getJSONObject("price").isNull("pricedescription")) {
                this.priceDescription = eventDetail.getJSONObject("price").getString("pricedescription");

            }
        }
        if (!event.getJSONObject("event").isNull("organiser") && !event.getJSONObject("event").getJSONObject("organiser").isNull("label")) {
            this.organiser = event.getJSONObject("event").getJSONObject("organiser").getJSONObject("label").getString("value");
        }

        if (!event.getJSONObject("event").isNull("categories")) {
            for (int i = 0; i < event.getJSONObject("event").getJSONObject("categories").getJSONArray("category").length(); i++) {
                if (event.getJSONObject("event").getJSONObject("categories").getJSONArray("category").getJSONObject(i).getString("type").contains("eventtype")) {
                    this.categories.add(event.getJSONObject("event").getJSONObject("categories").getJSONArray("category").getJSONObject(i).getString("value"));
                }
            }
        }

        if (!eventDetail.isNull("media")) {
            for (int i = 0; i < eventDetail.getJSONObject("media").getJSONArray("file").length(); i++) {
                if (eventDetail.getJSONObject("media").getJSONArray("file").getJSONObject(i).getString("mediatype").equals("webresource") || eventDetail.getJSONObject("media").getJSONArray("file").getJSONObject(i).getString("mediatype").equals("facebook")) {
                    this.mediaInfo.add(eventDetail.getJSONObject("media").getJSONArray("file").getJSONObject(i).getString("hlink"));
                }
            }
        }
    }

    private boolean isDatePassed(Long date) {
        Date availableToDate = new Date(date);
        Date currentDate = new Date();

        // 0 equals
        // res < 0 availableToDate before currentDate (event passed)
        // res > 0 availableToDate after currentDate (event not passed)
        if (availableToDate.compareTo(currentDate) < 0) {
            return true;
        }
        return false;
    }

}
