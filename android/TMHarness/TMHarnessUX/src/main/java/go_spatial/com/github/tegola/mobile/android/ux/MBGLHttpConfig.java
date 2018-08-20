package go_spatial.com.github.tegola.mobile.android.ux;

import android.os.Parcel;
import android.os.Parcelable;

public class MBGLHttpConfig implements Parcelable {
    private int cache_size_kb;
    private int connect_timeout_sec;
    private int max_requests_per_host;
    private int read_timeout_sec;

    public MBGLHttpConfig(int cache_size_kb, int connect_timeout_sec, int max_requests_per_host, int read_timeout_sec) {
        this.cache_size_kb = cache_size_kb;
        this.connect_timeout_sec = connect_timeout_sec;
        this.max_requests_per_host = max_requests_per_host;
        this.read_timeout_sec = read_timeout_sec;
    }

    public int getCache_size_kb() {
        return cache_size_kb;
    }
    public void setCache_size_kb(int cache_size_kb) {
        this.cache_size_kb = cache_size_kb;
    }

    public int getConnect_timeout_sec() {
        return connect_timeout_sec;
    }
    public void setConnect_timeout_sec(int connect_timeout_sec) {
        this.connect_timeout_sec = connect_timeout_sec;
    }

    public int getMax_requests_per_host() {
        return max_requests_per_host;
    }
    public void setMax_requests_per_host(int max_requests_per_host) {
        this.max_requests_per_host = max_requests_per_host;
    }

    public int getRead_timeout_sec() {
        return read_timeout_sec;
    }
    public void setRead_timeout_sec(int read_timeout_sec) {
        this.read_timeout_sec = read_timeout_sec;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.cache_size_kb);
        dest.writeInt(this.connect_timeout_sec);
        dest.writeInt(this.max_requests_per_host);
        dest.writeInt(this.read_timeout_sec);
    }

    protected MBGLHttpConfig(Parcel in) {
        this.cache_size_kb = in.readInt();
        this.connect_timeout_sec = in.readInt();
        this.max_requests_per_host = in.readInt();
        this.read_timeout_sec = in.readInt();
    }

    public static final Parcelable.Creator<MBGLHttpConfig> CREATOR = new Parcelable.Creator<MBGLHttpConfig>() {
        @Override
        public MBGLHttpConfig createFromParcel(Parcel source) {
            return new MBGLHttpConfig(source);
        }

        @Override
        public MBGLHttpConfig[] newArray(int size) {
            return new MBGLHttpConfig[size];
        }
    };
}
