package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inneke on 12/08/2015.
 */
public class EventInner
{
    private String cdbid;
    @SerializedName("agefrom")
    private Integer ageFrom;
    @SerializedName("availableto")
    private long availableTo;
    private Categories categories;
    private Location location;
    @SerializedName("organiser")
    private Organizer organizer;
    @SerializedName("contactinfo")
    private ContactInfos contactInfo;
    @SerializedName("eventdetails")
    private EventDetails eventDetails;
    private UitCalendar calendar;


    public String getCdbid()
    {
        return cdbid;
    }

    public Integer getAgeFrom()
    {
        return ageFrom;
    }

    public long getAvailableTo()
    {
        return availableTo;
    }

    public List<String> getCategories()
    {
        return this.categories != null ? this.categories.getCategories() : null;
    }

    public String getLocation()
    {
        return this.location != null ? this.location.getValue() : null;
    }

    public String getOrganizer()
    {
        return this.organizer != null ? this.organizer.getValue() : null;
    }

    private List<ContactInfo> getContactInfos()
    {
        return this.contactInfo != null ? this.contactInfo.getContactInfos() : null;
    }

    public Address getAddress()
    {
        List<ContactInfo> contactInfos = this.getContactInfos();
        if(contactInfos != null)
            for(ContactInfo contactInfo : contactInfos)
                if(contactInfo != null && contactInfo.getAddress() != null)
                    return contactInfo.getAddress();
        return null;
    }

    public List<String> getReservationList()
    {
        List<String> reservationList = new ArrayList<>();
        List<ContactInfo> contactInfos = this.getContactInfos();
        if(contactInfos != null)
        {
            for(ContactInfo contactInfo : contactInfos)
            {
                if(contactInfo != null)
                {
                    if(contactInfo.getUrl() != null && contactInfo.getUrl().isReservation())
                        reservationList.add(contactInfo.getUrl().getValue());
                    else if(contactInfo.getPhone() != null && contactInfo.getPhone().isReservation())
                        reservationList.add(contactInfo.getPhone().getValue());
                    else if(contactInfo.getMail() != null && contactInfo.getMail().isReservation())
                        reservationList.add(contactInfo.getMail().getValue());
                }
            }
        }
        return reservationList;
    }

    public List<String> getContactList()
    {
        List<String> contactList = new ArrayList<>();
        List<ContactInfo> contactInfos = this.getContactInfos();
        if(contactInfos != null)
        {
            for(ContactInfo contactInfo : contactInfos)
            {
                if(contactInfo != null)
                {
                    if(contactInfo.getUrl() != null && !contactInfo.getUrl().isReservation())
                        contactList.add(contactInfo.getUrl().getValue());
                    else if(contactInfo.getPhone() != null && !contactInfo.getPhone().isReservation())
                        contactList.add(contactInfo.getPhone().getValue());
                    else if(contactInfo.getMail() != null && !contactInfo.getMail().isReservation())
                        contactList.add(contactInfo.getMail().getValue());
                }
            }
        }
        return contactList;
    }

    public EventDetail getEventDetail()
    {
        return this.eventDetails != null ? this.eventDetails.getEventDetail() : null;
    }

    public UitCalendar getCalendar()
    {
        return calendar;
    }
}
