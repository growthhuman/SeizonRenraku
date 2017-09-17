package jp.techacademy.original.seizonrenraku;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnCompleteListener<Void>, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapsActivity";
    //Preferenceの変数
    private SharedPreferences mPreference;

    //Geofenceの経度と緯度
    private double geofence_latitude;
    private double geofence_longitude;

    //lastcurrentlocationの経度と緯度
    private double last_latitude;
    private double last_longitude;

    //Geofenceの設定 半径は100m
    public static final float GEOFENCE_RADIUS_IN_METERS = 100;

    private static final int PERMISSIONS_REQUEST_CODE_MULTIPLE = 00;
    private static final int PERMISSIONS_REQUEST_CODE_SMS = 10;
    private static final int PERMISSIONS_REQUEST_CODE = 34;

    //For this sample, geofences expire after twelve hours.
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    private FusedLocationProviderClient mFusedLocationClient;
    private GeofencingClient mGeofencingClient;
    private GoogleMap mMap;

    private LatLng lastCurrentLocation;

    private ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    private PendingIntent mGeofencePendingIntent;
    private Marker marker;
    private Circle circle;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //---------------------------------------------------------------------------------------

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //---------------------------------------------------------------------------------------
        //SharedPreferencesクラスのオブジェクトを取得
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);

        //Geofenceの経度と緯度をPreferenceから取得する。
        geofence_latitude = mPreference.getFloat("Latitude", (float) 0.0);
        geofence_longitude = mPreference.getFloat("Longitude", (float) 0.0);

        Log.d(TAG, String.valueOf(geofence_latitude));
        Log.d(TAG, String.valueOf(geofence_longitude));

        //GeofencingClientのインスタンスを取得
        mGeofencingClient = LocationServices.getGeofencingClient(this);

        //ToDo　これはうえで設定しても良いのでは？宣言はoncreateの外、初期化はoncreate以降で見たいな？
        mGeofencePendingIntent = null;

        //ToDo あとで消すこと。
        Log.d(TAG, "onCreate 1");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLastLocation OK");

                //ToDo あとで消すかどうするか考える
                getLastCuurentLocation();

                // Geofence作成処理
                // 以前に設定していたGeofenceを引き継ぐためにも必要な処理。インストール後初回は、経度0,緯度0が設定値となる。
                makeGeofence();

                //GeofencingClientからIntentServiceへリクエスト
                addGeofences();
                return;
            } else {
                // Show rationale and request permission.
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.SEND_SMS},
                        PERMISSIONS_REQUEST_CODE_MULTIPLE);
                Log.d(TAG, "getLastLocation NG");
                return;
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //ここで最初にgoogleMapを生成。Map関連の処理はすべてこの後でやること
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                Log.d(TAG, "onMapReady Permission OK");
            } else {
                // Show rationale and request permission.
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
                Log.d(TAG, "onMapReady Permission Not yet");
            }
        }
        makeMarkerCircle(new LatLng(geofence_latitude, geofence_longitude));

    }

    public void getLastCuurentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLastLocation OK");

                //最後に持っていた場所を取得して表示する。
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    //Get the latitude, in degrees.
                                    //Get the longitude, in degrees.
                                    last_latitude = location.getLatitude();
                                    last_longitude = location.getLongitude();

                                    lastCurrentLocation = new LatLng(last_latitude, last_longitude);

                                    // Add a marker in Sydney, Australia, and move the camera.
                                    mMap.addMarker(new MarkerOptions().position(lastCurrentLocation).title("LastCurrentLocation"));
                                    //以下ズームできるように変更
                                    float zoomLevel = 16.0f;
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastCurrentLocation, zoomLevel));
                                    Log.d(TAG, "getLastLocation Listner 2");
                                }
                            }
                        });
            } else {
                // Show rationale and request permission.
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
                Log.d(TAG, "getLastLocation NG");
            }
        }
    }


    //ジオフェンスのオブジェクトを作成する。
    public void makeGeofence() {
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                //ToDo　このアプリでは１つしかジオフェンスを設定しないので1固定
                .setRequestId("1")
                //ジオフェンスの中心点となる緯度と経度を設定、半径を設定
                .setCircularRegion(
                        geofence_latitude,
                        geofence_longitude,
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    //ジオフェンスのオブジェクトを作成する。
    public void makeGeofence(LatLng latLng) {
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                //ToDo　このアプリでは１つしかジオフェンスを設定しないので1固定
                .setRequestId("1")
                //ジオフェンスの中心点となる緯度と経度を設定、半径を設定
                .setCircularRegion(
                        latLng.latitude,
                        latLng.longitude,
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }


    private GeofencingRequest getGeofencingRequest() {
        Log.d(TAG, "GetGeofencingRequest");
        Log.d(TAG, "4");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private void addGeofences() {
        Log.d(TAG, "addGeofences OK");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    private PendingIntent getGeofencePendingIntent() {
        Log.d(TAG, "getGeofencePendingIntent");
        // Reuse the PendingIntent if we already have it.

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        Log.d(TAG, "2");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "longclick");

        makeMarkerCircle(latLng);

        makeGeofence(latLng);
        addGeofences();

        putData(latLng);

        Toast.makeText(getApplicationContext(), "Setting new Geofence", Toast.LENGTH_LONG).show();
        System.out.print("aaa");
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {

    }

    public void makeMarkerCircle(LatLng latLng) {
        if (marker != null && circle != null) {
            marker.remove();
            circle.remove();
        }
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("GeoFence"));
        circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .fillColor(0x5500ff00)
                .strokeColor(Color.WHITE).radius(GEOFENCE_RADIUS_IN_METERS)
        );
    }

    public void putData(LatLng latLng) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putFloat("Latitude", (float) latLng.latitude);
        editor.putFloat("Longitude", (float) latLng.longitude);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        }

        Log.d(TAG, String.valueOf((float) latLng.latitude));
        Log.d(TAG, String.valueOf((float) latLng.longitude));

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_MULTIPLE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    Log.d(TAG, "PERMISSIONS_REQUEST_CODE_MULTIPLE許可された");
                } else {

                    Log.d(TAG, "PERMISSIONS_REQUEST_CODE_MULTIPLE許可されなかった");

                }
                break;
            //ACCESS_FINE_LOCATIONの場合
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "PERMISSIONS_REQUEST_CODE許可された");
                } else {
                    Log.d(TAG, "PERMISSIONS_REQUEST_CODE許可されなかった");
                }
                break;
            case PERMISSIONS_REQUEST_CODE_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "PERMISSIONS_REQUEST_CODE_SMS許可された");
                } else {
                    Log.d(TAG, "PERMISSIONS_REQUEST_CODE_SMS許可されなかった");
                }
                break;
            default:
                break;

        }
    }

}
