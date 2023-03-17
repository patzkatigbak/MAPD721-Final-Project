package com.example.pjk.mapd_721_final_project.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.pjk.mapd_721_final_project.GpsTracker;
import com.example.pjk.mapd_721_final_project.MainScreenActivity;
import com.example.pjk.mapd_721_final_project.R;
import com.example.pjk.mapd_721_final_project.RegisterActivity;
import com.example.pjk.mapd_721_final_project.data.Checkin;
import com.example.pjk.mapd_721_final_project.data.User;
import com.example.pjk.mapd_721_final_project.dialogs.NewCheckin;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GpsTracker gpsTracker;
    private DatabaseReference databaseReferencCheckin;
    double longitude;
    double latitude;
    String cityName;
    String countryName;
    String postalCode;
    String addressName;
    String stateName;
    String nearby;
    String username;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        Button buttonCheckin = rootView.findViewById(R.id.buttonCheckin);

        buttonCheckin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonCheckinClicked();

            }
        });


        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        try {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;

    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        GpsTracker gpsTracker = new GpsTracker(this.getContext());
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        cityName = gpsTracker.getCityName();
        countryName = gpsTracker.getCountryName();
        postalCode = gpsTracker.getPostalCode();
        addressName = gpsTracker.getAddressName();
        stateName = gpsTracker.getStateName();
        nearby = gpsTracker.getNearbyPlace();

        // Add a marker in Sydney and move the camera
        LatLng centennial = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(centennial).title(addressName));
        // below line is use to add custom marker on our map.

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centennial, 15));

    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    public void buttonCheckinClicked()
    {


        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(currentTime);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String timeString = timeFormat.format(currentTime);


//        databaseReferencCheckin = FirebaseDatabase.getInstance().getReference("user");
//        String checkinId = databaseReferencCheckin.push().getKey();

        String date = dateString;
        String time = timeString;
        String sLongitude = String.valueOf(longitude);
        String sLatitude = String.valueOf(latitude);
        String desc = addressName;
        String remarks = "this is my remarks and its longer than my description. yes no yes yes";
        String city = cityName;
        String country = countryName;

//        Checkin checkin = new Checkin(checkinId,date, time, sLongitude, sLatitude, city,country, desc, remarks);
//        databaseReferencCheckin.child(username).child("checkin").child(checkinId).setValue(checkin);
//        Toast.makeText(getContext(), "Successfully Checked in!", Toast.LENGTH_SHORT).show();
//

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("checkin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("longitude", sLongitude);
        editor.putString("latitude", sLatitude);
        editor.putString("city", city);
        editor.putString("country", country);
        editor.putString("address", desc);
        editor.apply();

        NewCheckin popupWindow = new NewCheckin(getContext());
        popupWindow.show();
    }
}