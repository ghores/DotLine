package project;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

public class G extends Application {
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static Resources resources;
    public static DisplayMetrics displayMetrics;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        resources = context.getResources();
        displayMetrics = resources.getDisplayMetrics();
        Log.i("Test", "onCreate: from G");
    }
}
