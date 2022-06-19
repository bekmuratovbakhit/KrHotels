package com.example.krhotels.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class IndividualLocation2  implements Parcelable {

  private String name;
  private String address;
  private String durationDriving;
  private String durationWalking;
  private String distance;
  private String imageUrl;
  private LatLng location;

  public IndividualLocation2(String name, String address, String imageUrl, LatLng location) {
    this.name = name;
    this.address = address;
    this.imageUrl = imageUrl;
    this.location = location;
  }

  protected IndividualLocation2(Parcel in) {
    name = in.readString();
    address = in.readString();
    durationDriving = in.readString();
    durationWalking = in.readString();
    distance = in.readString();
    imageUrl = in.readString();
    location = in.readParcelable(LatLng.class.getClassLoader());
  }

  public static final Creator<IndividualLocation2> CREATOR = new Creator<IndividualLocation2>() {
    @Override
    public IndividualLocation2 createFromParcel(Parcel in) {
      return new IndividualLocation2(in);
    }

    @Override
    public IndividualLocation2[] newArray(int size) {
      return new IndividualLocation2[size];
    }
  };

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }


  public LatLng getLocation() {
    return location;
  }

  public void setLocation(LatLng location) {
    this.location = location;
  }

  public String getDistance() {
    return distance;
  }

  public void setDistance(String distance) {
    this.distance = distance;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getDurationDriving() {
    return durationDriving;
  }

  public void setDurationDriving(String durationDriving) {
    this.durationDriving = durationDriving;
  }

  public String getDurationWalking() {
    return durationWalking;
  }

  public void setDurationWalking(String durationWalking) {
    this.durationWalking = durationWalking;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(this.name);
    parcel.writeString(this.address);
    parcel.writeString(this.distance);
    parcel.writeString(String.valueOf(this.location));
    parcel.writeString(this.durationDriving);
    parcel.writeString(this.durationWalking);
    parcel.writeString(this.imageUrl);

  }


}
