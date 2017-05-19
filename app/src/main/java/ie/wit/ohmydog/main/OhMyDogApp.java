package ie.wit.ohmydog.main;

import android.app.Application;
import android.location.Location;
import android.net.Uri;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by dumitrubrinza on 01/05/2017.
 */

public class OhMyDogApp extends Application {

    public  static OhMyDogApp mInstance;
    public GoogleApiClient mGoogleApiClient;
    public Location mCurrentLocation;
    public Uri googlePhotoURL;
    public String googleName;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso build = builder.build();
        build.setIndicatorsEnabled(false);
        build.setLoggingEnabled(true);
        Picasso.setSingletonInstance(build);
    }



    public static synchronized OhMyDogApp getmInstance(){
        return mInstance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
