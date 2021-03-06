package com.example.detectingfaceapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton ibtnAdd;
    private RecyclerView rvFaceDetection;
    private FaceDetectionAdapter adapterFaceDetection;
    private View view;
    private Realm realm;
    private RealmResults<FaceDetectionObject> realmResults;
    private List<FaceDetectionObject> listItem;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ConnectView();
        LoadListFaceDetection();
        return view;
    }

    private void ConnectView() {
        ibtnAdd = (ImageButton) view.findViewById(R.id.ibtn_add);
        rvFaceDetection = (RecyclerView) view.findViewById(R.id.rv_facedetection);

        ibtnAdd.setOnClickListener(HomeFragment.this);

        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();
    }

    private void LoadListFaceDetection() {
        realmResults = realm.where(FaceDetectionObject.class).sort("name", Sort.ASCENDING).findAll();
        listItem = realm.copyFromRealm(realmResults);
        adapterFaceDetection = new FaceDetectionAdapter(listItem);
        rvFaceDetection.setAdapter(adapterFaceDetection);
        rvFaceDetection.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ibtn_add:
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_detectionFragment);
                break;
        }
    }
}
