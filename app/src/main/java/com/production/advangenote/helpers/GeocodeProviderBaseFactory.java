package com.production.advangenote.helpers;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.production.advangenote.R;
import com.production.advangenote.utils.GeocodeHelper;

import io.nlopez.smartlocation.location.LocationProvider;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider;

/**
 * @author vietnh
 * @name GeocodeProviderBaseFactory
 * @date 10/1/20
 **/
public class GeocodeProviderBaseFactory {

    protected GeocodeProviderBaseFactory() {

    }

    public static LocationProvider getProvider(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.P
                && checkHighAccuracyLocationProvider(context)) {
            Toast.makeText(context, R.string.location_set_high_accuracy, Toast.LENGTH_SHORT).show();
            context.startActivity((new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
        }

        return new LocationGooglePlayServicesWithFallbackProvider(context);
    }

    public static boolean checkHighAccuracyLocationProvider(Context context) {
        return GeocodeHelper.checkLocationProviderEnabled(context, LocationManager.GPS_PROVIDER);
    }

}
