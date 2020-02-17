package dev.saul1317.uxdribbble.model;

import android.net.Uri;

import java.util.Date;

public class Gallery {

    private String pictureName;
    private String url;
    private String pictureDescription;
    private String dateUpload;

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPictureDescription() {
        return pictureDescription;
    }

    public void setPictureDescription(String pictureDescription) {
        this.pictureDescription = pictureDescription;
    }

    public String getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(String dateUpload) {
        this.dateUpload = dateUpload;
    }

    @Override
    public String toString() {
        return "Gallery{" +
                "pictureName='" + pictureName + '\'' +
                ", url='" + url + '\'' +
                ", pictureDescription='" + pictureDescription + '\'' +
                ", dateUpload='" + dateUpload + '\'' +
                '}';
    }
}
