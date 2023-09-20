package com.jica.pts.MainFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jica.pts.R;

public class FragmentCalendar extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Fragment 자체의 xml layout file을 전개
        View fragmentView = inflater.inflate(R.layout.fragment_main_plant_management, container, false);
        return inflater.inflate(R.layout.fragment_main_calendar, container, false);
    }
}