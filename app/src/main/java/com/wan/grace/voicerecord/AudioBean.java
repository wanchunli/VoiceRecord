package com.wan.grace.voicerecord;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class AudioBean implements Parcelable,Serializable {
    private String url;
    private long duration;

    public String getUrl() {
        return url;
    }

    public long getDuration() {
        return duration;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeLong(this.duration);
    }

    public AudioBean() {
    }

    protected AudioBean(Parcel in) {
        this.url = in.readString();
        this.duration = in.readLong();
    }

    public static final Creator<AudioBean> CREATOR =
            new Creator<AudioBean>() {
                @Override
                public AudioBean createFromParcel(Parcel source) {
                    return new AudioBean(source);
                }

                @Override
                public AudioBean[] newArray(int size) {
                    return new AudioBean[size];
                }
            };
}
