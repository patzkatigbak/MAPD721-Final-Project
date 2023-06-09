package com.example.pjk.mapd_721_final_project.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pjk.mapd_721_final_project.R;
import com.example.pjk.mapd_721_final_project.adapter.CheckinAdapter;
import com.example.pjk.mapd_721_final_project.data.Checkin;
import com.example.pjk.mapd_721_final_project.dialogs.NewCheckin;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private CheckinAdapter checkinAdapter;
    private List<Checkin> checkinList;
    private SharedPreferences sharedPreferences;
    private String username;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        checkinList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        checkinAdapter = new CheckinAdapter(getActivity(), checkinList);
        recyclerView.setAdapter(checkinAdapter);

        loadHistoryCHeckin();

        return rootView;
    }

    private void loadHistoryCHeckin() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(username).child("checkin");

        Query query = ref.orderByChild("timestamp"); // fix this part to sort by date descending
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                checkinList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // get the values for each checkin entry
                    String checkinID = snapshot.getKey();
                    String title = snapshot.child("title").getValue(String.class);
                    String desc = snapshot.child("desc").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);
                    String longitude = snapshot.child("longitude").getValue(String.class);
                    String latitude = snapshot.child("latitude").getValue(String.class);
                    String city = snapshot.child("city").getValue(String.class);
                    String country = snapshot.child("country").getValue(String.class);
                    String remarks = snapshot.child("remarks").getValue(String.class);
                    String postal  = snapshot.child("postal").getValue(String.class);
                    long timestamp = snapshot.child("timestamp").getValue(Long.class);
                    String isFavorite = snapshot.child("isFavorite").getValue(String.class);

                    // create a new Checkin object and add it to the list
                    checkinList.add(new Checkin(checkinID, title, date, time, longitude, latitude, city, country, desc, postal, isFavorite,remarks,timestamp));

                }

                //this will sort from latest to oldest timestamp
                Collections.sort(checkinList, new Comparator<Checkin>() {
                    @Override
                    public int compare(Checkin c1, Checkin c2) {
                        return Long.compare(c2.getTimestamp(), c1.getTimestamp());
                    }
                });

                // update the RecyclerView
                checkinAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ERRPR", "loadPost:onCancelled", databaseError.toException());
            }
        });

    }

}