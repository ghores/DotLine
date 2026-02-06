package project;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.DisplayMetrics;

public class G extends Application {
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static Resources resources;
    public static DisplayMetrics displayMetrics;
    public static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        resources = context.getResources();
        displayMetrics = resources.getDisplayMetrics();
        handler = new Handler();
    }
}
