package employer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.com.traineeshare.R;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import model.Application;
import model.Vacancy;
import student.JobDetailActivity;
import viewHolder.ApplicationHolder;
import viewHolder.VacancyHolder;

public class CheckApplicationFragment extends Fragment {
    private RecyclerView mApplicationList;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<Application, ApplicationHolder> adapter;
    private DatabaseReference rootRef, studentRef, vacancyRef;
    private Query query;

    public CheckApplicationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_check_application, container, false);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mApplicationList = rootView.findViewById(R.id.rv_application);

        query = rootRef.child("application").child(mAuth.getCurrentUser().getUid());
        FirebaseRecyclerOptions<Application> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Application>()
                .setQuery(query, Application.class)
                .build();

        return rootView;
    }
}
