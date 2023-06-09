package com.example.pjk.mapd_721_final_project.fragments;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.pjk.mapd_721_final_project.R;
import com.example.pjk.mapd_721_final_project.services.NotificationService;


public class SettingsFragment extends Fragment {

    private Handler handler;
    private Runnable runnable;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        Button buttonSaveSettings = rootView.findViewById(R.id.buttonSaveSettings);
        Switch switchNotification = rootView.findViewById(R.id.switchNotification);
        Spinner spinnerTime = rootView.findViewById(R.id.spinnerTime);

        String[] items = {"1 Hour", "2 Hours", "4 Hours", "10 seconds"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapter);

        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean sn = sharedPreferences.getBoolean("notificationSwitch", false);
        String occurence  = sharedPreferences.getString("notificationOccurence", "");
        switchNotification.setChecked(sn);

        if (occurence != null) {
            int position = adapter.getPosition(occurence);
            spinnerTime.setSelection(position);
        }

        if(switchNotification.isChecked())
        {
            spinnerTime.setEnabled(true);
        }
        else
        {
            spinnerTime.setEnabled(false);
        }

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchNotification.isChecked())
                {
                    spinnerTime.setEnabled(true);
                }
                else
                {
                    spinnerTime.setEnabled(false);
                }
            }
        });

        buttonSaveSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String selectedTime = spinnerTime.getSelectedItem().toString();
                System.out.println(selectedTime);
                Boolean notificationSwitch = switchNotification.isChecked();
                sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("notificationSwitch", notificationSwitch);
                editor.putString("notificationOccurence",selectedTime );
                editor.apply();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Settings Updated");
                if(switchNotification.isChecked())
                {
                    int seconds;
                    switch(selectedTime) {
                        case "1 Hour":
                            seconds = 3600;
                            break;
                        case "2 Hours":
                            seconds = 7200;
                            break;
                        case "4 Hours":
                            seconds = 14400;
                            break;
                        default:
                            seconds = 10;
                    }

                    Intent intent = new Intent(getActivity(), NotificationService.class);
                    intent.putExtra("seconds", String.valueOf(seconds));
                    getActivity().startService(intent);
                    builder.setMessage("You will be Notified to Check in every " + spinnerTime.getSelectedItem());
                }
                else
                {
                    Intent intent = new Intent(getActivity(), NotificationService.class);
                    getActivity().stopService(intent);
                    builder.setMessage("Notification Disabled");
                }

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do something here
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        return rootView;
    }


}