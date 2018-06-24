package employer;

import android.com.traineeshare.CompanyCheckApplicationActivity;
import android.com.traineeshare.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import model.Vacancy;
import student.JobDetailActivity;
import viewHolder.CompanyJobHolder;


public class CompanyMainFragment extends Fragment {

    private Button btn_NewJob;

    private RecyclerView mCreatedJobList;
    private FirebaseRecyclerAdapter<Vacancy, CompanyJobHolder> adapter;
    private DatabaseReference rootRef;
    private Query query;
    private FirebaseAuth mAuth;
    private String uId;
    private RecyclerView mJobList;

    public CompanyMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_company_main, container, false);
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        mJobList = rootView.findViewById(R.id.rv_createdJob);
        query = rootRef.child("vacancy").orderByChild("company_id").equalTo(uId);

        FirebaseRecyclerOptions<Vacancy> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Vacancy>()
                .setQuery(query, Vacancy.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Vacancy, CompanyJobHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final CompanyJobHolder holder, final int position, @NonNull final Vacancy model) {

                holder.setTv_title(model.getJob_title());

                holder.setTv_desc(model.getJob_description());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), CompanyCheckApplicationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("JobID",getRef(position).getKey());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public CompanyJobHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company_created_job,parent,false);
                final CompanyJobHolder jobHolder = new CompanyJobHolder(view);
                return jobHolder;
            }
        };
        mJobList.setHasFixedSize(true);
        mJobList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mJobList.setAdapter(adapter);

        btn_NewJob = rootView.findViewById(R.id.btn_NewJob);

        btn_NewJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentControl(CompanyCreateJobFragment.class);
            }
        });
        return rootView;

    }

    @Override
    public void onStart(){
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop(){
        super.onStop();

        if(adapter!=null){
            adapter.stopListening();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public void fragmentControl(Class fragmentClass) {
        Fragment fragment = null;
        Class fragmentCls = fragmentClass;

        try {
            fragment = (Fragment) fragmentCls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.company_flContent,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
