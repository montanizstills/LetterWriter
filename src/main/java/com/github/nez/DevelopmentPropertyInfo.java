package com.github.nez;

public enum DevelopmentPropertyInfo {
    JENNINGS_VILLAGE(
            "patriot",
            "Patriot Village",
            "360 Pennington Ave",
            "Trenton",
            "NJ",
            "08618",
            "patriotvillagenj.com"
    ),
    PATRIOT_VILLAGE(
            "patvlg2",
            "Jennings Village",
            "461-471 Brunswick Ave",
            "Trenton",
            "NJ",
            "08638",
            "jenningsvillage.com"
    ),
    CONCORD_RESIDENCES(
            "concord",
            "Concord Residences",
            "10 Concord St",
            "Hillsborough",
            "NJ",
            "08540",
            "concordresidences.com"
    );

    DevelopmentPropertyInfo(String propertyCode, String propertyName, String addressStreet, String addressCity, String addressState, String addressZip, String propertyWebsite) {
        this.propertyCode = propertyCode;
        this.propertyName = propertyName;
        this.addressStreet = addressStreet;
        this.addressCity = addressCity;
        this.addressState = addressState;
        this.addressZip = addressZip;
        this.propertyWebsite = propertyWebsite;
    }

    private final String propertyCode;
    private final String propertyName;
    private final String addressStreet;
    private final String addressCity;
    private final String addressState;
    private final String addressZip;
    private final String propertyWebsite;


    public String getPropertyName() {
        return propertyName;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public String getAddressState() {
        return addressState;
    }

    public String getAddressZip() {
        return addressZip;
    }

    public String getPropertyWebsite() {
        return propertyWebsite;
    }

    public String getPropertyCode() {
        return propertyCode;
    }
}
