package com.example.visualmath.ui.notifications;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.visualmath.R;
import com.example.visualmath.activity.VM_SettingsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherNotificationsFragment extends Fragment {


    public TeacherNotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new VM_SettingsActivity.SettingsFragment())
                .commit();

        return inflater.inflate(R.layout.fragment_teacher_notifications, container, false);
    }

}
