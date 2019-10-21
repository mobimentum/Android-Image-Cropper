package com.theartofdev.edmodo.cropper;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class PdfFile implements Parcelable {

    private static final String TAG = "PdfFile";

    private Uri uri;

    public PdfFile(Uri uri) {
        this.uri = uri;
    }

    public PdfFile(Parcel in) {
        Uri.Builder builder = new Uri.Builder();
        this.uri = builder.path(in.readString()).build();
    }

    public Uri getUri() {
        return this.uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uri.toString());
    }

    public static final Creator CREATOR = new Creator() {
        public PdfFile createFromParcel(Parcel in) {
            return new PdfFile(in);
        }

        public PdfFile[] newArray(int size) {
            return new PdfFile[size];
        }
    };

    @Override
    public String toString() {
        return "PdfFile{" +
                "uri=" + uri +
                '}';
    }
}
