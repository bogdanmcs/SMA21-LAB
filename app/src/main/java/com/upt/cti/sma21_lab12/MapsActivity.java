package com.upt.cti.sma21_lab12;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.upt.cti.sma21_lab12.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private final int REQ_PERMISSION = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap = googleMap;

        if (checkPermission()) {
            mMap.setMyLocationEnabled(true);
        } else {
            askPermission();
        }

        //
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // center the map on a location
        LatLng upt = new LatLng(45.74744258851616, 21.2262348494032);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(upt));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        // customize marker
        mMap.addMarker(new MarkerOptions()
                .position(upt)
                .title("Marker in UPT"))
                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.uni));

        // draw line on map
        List<LatLng> latLngList = new ArrayList<>();
        LatLng vest = new LatLng(45.74713471641559, 21.23151170690118);
        latLngList.add(upt);
        latLngList.add(vest);
        drawPolyLineOnMap(latLngList, mMap);

        // show toast
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (marker.getPosition().equals(upt)) {
                    Toast.makeText(getApplicationContext(), "Distance: " + SphericalUtil.computeDistanceBetween(upt, vest), Toast.LENGTH_LONG).show();
                } else {
                    //
                }
                return false;
            }
        });
    }

    public void drawPolyLineOnMap(List<LatLng> list, GoogleMap googleMap) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.GREEN);
        polylineOptions.width(8);
        polylineOptions.addAll(list);
        googleMap.addPolyline(polylineOptions);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng: list) {
            builder.include(latLng);
        }
        builder.build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkPermission()) {
                    mMap.setMyLocationEnabled(true);
                } else {
                    // Permissions denied
                }
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED);
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION
        );
    }
}