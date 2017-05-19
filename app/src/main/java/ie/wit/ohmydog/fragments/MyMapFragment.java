package ie.wit.ohmydog.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ie.wit.ohmydog.R;
import ie.wit.ohmydog.main.OhMyDogApp;
import ie.wit.ohmydog.models.Dog;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class MyMapFragment extends MapFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        LocationListener,
        OnMapReadyCallback {

    // public OhMyDogApp app = OhMyDogApp.getmInstance();

    // private GoogleApiClient googleApiClient;
    // private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    //  private List<Dog> mCoffeeList;

    public OhMyDogApp app = OhMyDogApp.getmInstance();

    //private ChildEventListener mChildEventListener;
    private Dog marker;
    private DatabaseReference mDatabaseDogs = FirebaseDatabase.getInstance().getReference().child("Dogs");


    private long UPDATE_INTERVAL = 30000; /* 30 secs */
    private long FASTEST_INTERVAL = 1000; /* 5 secs */
    private GoogleMap mMap;

    private LatLngBounds Ireland = new LatLngBounds(
            new LatLng(51.40, -10.56), new LatLng(55.16, -5.31));
    /**
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private ChildEventListener mChildEventListener;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int PERMISSION_REQUEST_CODE = 200;

    private final int[] MAP_TYPES = {
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE
    };

    private int curMapTypeIndex = 1;
    private float zoom = 13f;


    public MyMapFragment() {
        // Required empty public constructor
    }

    public static MyMapFragment newInstance() {
        MyMapFragment fragment = new MyMapFragment();

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView titleBar = (TextView) getActivity().findViewById(R.id.recentAddedBarTextView);
        titleBar.setText("Dogs Map");

        app.mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // googleApiClient.connect();*/

        //app.mGoogleApiClient.registerConnectionCallbacks(this);
        app.mGoogleApiClient.connect();

        // initListeners();
        Log.v("TTT", " Google Client Connected OnViewCreated() MapsFragment screen: "
                + (app.mGoogleApiClient.isConnected() ? "YES" : "NO") + app.mGoogleApiClient);

    }

    private void initListeners() {
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
    }

    private void initCamera(Location location) {
        if (zoom != 13f && zoom != mMap.getCameraPosition().zoom) {
            zoom = mMap.getCameraPosition().zoom;
        }

        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(14f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();


        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //    app.mGoogleApiClient.registerConnectionCallbacks(this);
    }

    @Override
    public void onConnected(Bundle bundle) {

        startLocationUpdates();
        getMapAsync(this);

        // Display the connection status
        try {
            app.mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(app.mGoogleApiClient);
        } catch (SecurityException se) {
            Toast.makeText(getActivity(), "Check Your Permissions", Toast.LENGTH_SHORT).show();
        }
        if (app.mCurrentLocation != null) {

            Toast.makeText(getActivity(), "GPS location was found!", Toast.LENGTH_SHORT).show();
//LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        } else {
            //  startLocationUpdates();
            Toast.makeText(getActivity(), "Current location was null, Setting Default Values!", Toast.LENGTH_SHORT).show();
            app.mCurrentLocation = new Location("Waterford City Default");
            app.mCurrentLocation.setLatitude(52.2462);
            app.mCurrentLocation.setLongitude(-7.1402);
        }

        //    initCamera(mCurrentLocation);

        Log.v("TTT", " Google Client Connected OnConnected() MapsFragment screen: "
                + (app.mGoogleApiClient.isConnected() ? "YES" : "NO") + app.mGoogleApiClient);

    }

    @Override
    public void onStart() {
        super.onStart();

        if (!app.mGoogleApiClient.isConnected()) {
            app.mGoogleApiClient.connect();
        }
        Log.v("TTT", " Google Client Connected OnStart() MapsFragment screen: "
                + (app.mGoogleApiClient.isConnected() ? "YES" : "NO") + app.mGoogleApiClient);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (app.mGoogleApiClient.isConnected()) {
            app.mGoogleApiClient.disconnect();
        }
        Log.v("TTT", " Google Client Connected OnStop() MapsFragment screen: "
                + (app.mGoogleApiClient.isConnected() ? "YES" : "NO") + app.mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(getActivity(), "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(getActivity(), "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
// Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
/* * Thrown if Google Play services canceled the original * PendingIntent */
            } catch (IntentSender.SendIntentException e) {
                // e.printStackTrace();
                app.mGoogleApiClient.connect();
            }
        } else {
            Toast.makeText(getActivity(), "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        app.mCurrentLocation = location;
        initCamera(app.mCurrentLocation);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMapType(MAP_TYPES[curMapTypeIndex]);

        if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap.setMyLocationEnabled(true);
        } else {

            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }


        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        initListeners();
        initCamera(app.mCurrentLocation);
        startLocationUpdates();


        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(Ireland, 0));

        addMarkersToMap(mMap);
    }

    private void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(app.mGoogleApiClient, mLocationRequest, this);

        /*try{
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        }catch (SecurityException se){
            Toast.makeText(getActivity(), "Check your permissions on Updates",Toast.LENGTH_SHORT).show();
        }*/
    }

    private void requestPermission() {
        // ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        requestPermissions(new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

    }

    private boolean checkPremission() {

        //int result = ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);
        int result = checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void addMarkersToMap(final GoogleMap googleMap) {

        mChildEventListener = mDatabaseDogs.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                int da;
                Dog marker = dataSnapshot.getValue(Dog.class);//(Dog.class);

                String breed = marker.getBreed();
                String LoF = marker.getLostORfound();
                // String latitude = marker.getLat();
                // String longitude = marker.getLon();

                double latit = Double.parseDouble(marker.getLat().toString());

                double longit = Double.parseDouble(marker.getLon().toString());
                // to use trim();
                //  Double latitude = Double.valueOf(marker.lat);
                //   Double longitude = Double.valueOf(marker.lon);

                LatLng location = new LatLng(latit, longit);

                if (LoF.equals("Lost")) {
                    da = R.drawable.marker_ye;
                } else {
                    da = R.drawable.marker_gr;
                }

                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.addMarker(new MarkerOptions().position(location).title(breed).icon(BitmapDescriptorFactory.fromResource(da)));
                //


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                Log.v("TAG1", String.valueOf(grantResults));

                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (locationAccepted) {
                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED  ){
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        mMap.setMyLocationEnabled(true);
                    }

                    Snackbar.make(getView(), "Permission Granted, Now you can access location data.", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getView(), "Permission Denied, You cannot access location data.", Snackbar.LENGTH_LONG).show();


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                            showMessageOKCancel("You need to allow access to the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                                                        PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }

                break;

        }


    }
    private void showMessageOKCancel(String s, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(s)
                .setPositiveButton("OK", onClickListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
