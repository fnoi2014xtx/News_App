package com.java.xtxnews;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLogout extends Fragment {


    public FragmentLogout() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        Button logout_button = view.findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsIO.delete();
                NewsIO.currentActivity = null;
                ((MainActivity)(getActivity())).flipLogFragment();
                Toast.makeText(getActivity(),"Logout Successfully",Toast.LENGTH_SHORT).show();
                NewsIO.setLogged(false);
            }
        });



        // Inflate the layout for this fragment
        return view;
    }

}
