package com.example.dispatchsysapp.faultDocument;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class FaultDocument implements Parcelable {
    private String id;
    private String faultDescription;

    //0:未接单，1：正在处理，2：已解决
    private String status;

    public FaultDocument(String id, String faultDescription, String status) {
        this.id = id;
        this.faultDescription = faultDescription;
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(faultDescription);
        parcel.writeString(status);
    }

    public FaultDocument(Parcel in){
        id = in.readString();
        faultDescription = in.readString();
        status = in.readString();
    }

    public static final Creator<FaultDocument> CREATOR = new Creator<FaultDocument>() {
        @Override
        public  FaultDocument createFromParcel(Parcel in) {
            return new FaultDocument(in);
        }

        @Override
        public FaultDocument[] newArray(int size) {
            return new FaultDocument[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFaultDescription() {
        return faultDescription;
    }

    public void setFaultDescription(String faultDescription) {
        this.faultDescription = faultDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", faultDescription='" + faultDescription + '\'' +
                ", status='" + status ;
    }
}
