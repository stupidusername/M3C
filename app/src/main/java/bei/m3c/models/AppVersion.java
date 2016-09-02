package bei.m3c.models;

import com.google.gson.annotations.SerializedName;

public class AppVersion {

    public int id;

    public int version;

    @SerializedName("force_update")
    public boolean forceUpdate;

    public String apkUrl;

    public String md5;

    public AppVersion(int id, int version, boolean forceUpdate, String apkUrl, String md5) {
        this.id = id;
        this.version = version;
        this.forceUpdate = forceUpdate;
        this.apkUrl = apkUrl;
        this.md5 = md5;
    }
}
