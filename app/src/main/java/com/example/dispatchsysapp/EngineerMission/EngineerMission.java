package com.example.dispatchsysapp.EngineerMission;

public class EngineerMission {
    String id;
    String missionDescription;
    String longitude;
    String latitude;

    String location;
    public EngineerMission(String id, String missionDescription, String longitude, String latitude) {
        this.id = id;
        this.missionDescription = missionDescription;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = "未知";
    }


    public EngineerMission(String id, String missionDescription, String longitude, String latitude, String location) {
        this.id = id;
        this.missionDescription = missionDescription;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMissionDescription() {
        return missionDescription;
    }

    public void setMissionDescription(String missionDescription) {
        this.missionDescription = missionDescription;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", missionDescription='" + missionDescription + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", location='" + location ;
    }
}
