package org.uitagenda.model;

/**
 * Created by Inneke on 12/08/2015.
 */
public class ContactInfo
{
    private Address address;
    private UrlPhoneMail url;
    private UrlPhoneMail phone;
    private UrlPhoneMail mail;

    public Address getAddress()
    {
        return address;
    }

    public UrlPhoneMail getUrl()
    {
        return url;
    }

    public UrlPhoneMail getPhone()
    {
        return phone;
    }

    public UrlPhoneMail getMail()
    {
        return mail;
    }
}
