package com.example.pubcrawl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.pubcrawl.fragments.EventsFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.ui.IconGenerator;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseException;
import java.util.List;


import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@SuppressLint("NeedOnRequestPermissionsResult")
@RuntimePermissions
public class MainActivity extends AppCompatActivity implements  GoogleMap.OnMapLongClickListener , GoogleMap.OnMarkerDragListener
{
    final String TAG = "MAIN_ACTIVITY";
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    private ActionBarDrawerToggle drawerToggle;

    private GoogleMap Map;
    private SupportMapFragment mapFragment;
    private LocationRequest mLocationRequest;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private final static String KEY_LOCATION = "location";
   // public List<ParseUser> nearUsers;
    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the toolbar view inside the activity layout
        toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        // display an Up icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        // Set up drawer view
        setupDrawerContent(nvDrawer);

        // temporary hack just to display events fragment
        Fragment fragment = new EventsFragment();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        Log.i(TAG, "Created EventsFragment");

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); // needed for saving users current location
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION))
        {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
        saveCurrentUserLocation();
        //getCurrentUserLocation();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_event_planner:
                fragmentClass = EventsFragment.class;
                break;
            // TODO: add case for logout button
            default:
                fragmentClass = EventsFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // The action bar home action should open/close the drawer
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadMap(GoogleMap googleMap)
    {
        //:TODO Fix Map Permissions and implementation
        Map = googleMap;

        if (Map != null) {
            // Map is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            MainActivityPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            MainActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
            Map.setOnMapLongClickListener(this);
            Map.setOnMarkerDragListener(this);
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }

        //showCurrentUserInMap(Map);
    }



    public void saveCurrentUserLocation() {
        // requesting permission to get user's location
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else {
            // getting last know user's location
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // checking if the location is null
            if(location != null){
                // if it isn't, save it to Back4App Dashboard
                ParseGeoPoint currentUserLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

                ParseUser currentUser = ParseUser.getCurrentUser();

                if (currentUser != null) {
                    currentUser.put("location", currentUserLocation);
                    currentUser.saveInBackground();
                } else {
                    // do something like coming back to the login activity
                }
            }
            else {
                // if it is null, do something like displaying error and coming back to the menu activity
            }
        }
    }

    private void showCurrentUserInMap(final GoogleMap googleMap)
    {

        // calling retrieve user's location method of Step 4
        ParseGeoPoint currentUserLocation = getCurrentUserLocation();

        // creating a marker in the map showing the current user location
        LatLng currentUser = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(currentUser).title(ParseUser.getCurrentUser().getUsername()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        // zoom the map to the currentUserLocation
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUser, 5));
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                saveCurrentUserLocation();
                break;
        }
       // MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //saveCurrentUserLocation();
    public ParseGeoPoint getCurrentUserLocation()
    {
        saveCurrentUserLocation();
        // finding currentUser
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null)
        {
            // if it's not possible to find the user, do something like returning to login activity
        }
        // otherwise, return the current user location
        return currentUser.getParseGeoPoint("location");
    }

    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void getMyLocation() {
        Map.setMyLocationEnabled(true);

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MainActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Display the connection status

        if (mCurrentLocation != null) {
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            Map.animateCamera(cameraUpdate);
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        MainActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location)
    {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        // Report to the UI that the location was updated

        mCurrentLocation = location;
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onMapLongClick(LatLng point)
    {
        Toast.makeText(this, "Long Press", Toast.LENGTH_SHORT).show();
        showAlertDialogForPoint(point);
    }

    private void showAlertDialogForPoint(final LatLng point)
    {
        View messageView = LayoutInflater.from(MainActivity.this).inflate(R.layout.message_item,null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(messageView);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                String Title = ((EditText) alertDialog.findViewById(R.id.etTitle)).getText().toString();
                String Snippit = ((EditText) alertDialog.findViewById(R.id.etSnippit)).getText().toString();

                IconGenerator iconGenerator = new IconGenerator(MainActivity.this);
                iconGenerator.setStyle(IconGenerator.STYLE_BLUE);

                Bitmap bitmap = iconGenerator.makeIcon(Title);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);

                Marker marker = Map.addMarker(new MarkerOptions().position(point).title(Title).snippet(Snippit).icon(icon));
                marker.setDraggable(true);

            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends android.support.v4.app.DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }



    }

    private void showClosestUser(final GoogleMap googleMap){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNear("location", getCurrentUserLocation());

        query.findInBackground(new FindCallback<ParseUser>()
        {

            @Override  public void done(List<ParseUser> nearUsers, ParseException e)
            {
                if (e == null) {
                    // do something with the list of results of your query
                    // avoiding null pointer
                    ParseUser closestUser = ParseUser.getCurrentUser();
                    // set the closestUser to the one that isn't the current user
                    for(int i = 0; i < nearUsers.size(); i++) {
                        if(!nearUsers.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            closestUser = nearUsers.get(i);
                        }
                    }
                    // creating a marker in the map showing the closest user to the current user location
                    LatLng closestUserLocation = new LatLng(closestUser.getParseGeoPoint("Location").getLatitude(), closestUser.getParseGeoPoint("Location").getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(closestUserLocation).title(closestUser.getUsername()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


                } else {
                    // handle the error
                }
            }
        });
        ParseQuery.clearAllCachedResults();
    }
}
