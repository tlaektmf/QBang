package com.example.visualmath.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceFragmentCompat;

import com.example.visualmath.HomeActivity;
import com.example.visualmath.R;
import com.example.visualmath.TabFragment;
import com.example.visualmath.VM_SettingsActivity;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
//        View root = inflater.inflate(R.layout.settings_activity, container, false);

//        ((HomeActivity)getActivity()).replaceFragment();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new VM_SettingsActivity.SettingsFragment())
                .commit();

        return root;
    }



}