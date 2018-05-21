package it.matteoavanzini.comelit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by emme on 14/05/2018.
 */

public class Post implements Parcelable {
    private int id;
    private int userId;
    private String title;
    private String body;
    private boolean preferred;

    public Post() {}

    protected Post(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        title = in.readString();
        body = in.readString();
        byte b = in.readByte();
        preferred = b == 1 ? true : false;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeString(title);
        dest.writeString(body);

        byte isPreferred = (byte) (preferred ? 1 : 0);
        dest.writeByte(isPreferred);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Post && ((Post) o).getId() == this.id) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
