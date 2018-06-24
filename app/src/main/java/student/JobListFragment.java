package student;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.com.traineeshare.R;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import model.Vacancy;
import viewHolder.VacancyHolder;

public class JobListFragment extends Fragment {
    private RecyclerView mJobList;
    private FirebaseRecyclerAdapter<Vacancy, VacancyHolder> adapter;
    private DatabaseReference rootRef, companyRef, companyLogoUrlRef;
    private Query query;

    public JobListFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_job_list, container, false);
        rootRef = FirebaseDatabase.getInstance().getReference();
        companyRef = FirebaseDatabase.getInstance().getReference();
        companyLogoUrlRef = FirebaseDatabase.getInstance().getReference();
        mJobList = rootView.findViewById(R.id.rv_vacancy);

        query = rootRef.child("vacancy").orderByKey();
        FirebaseRecyclerOptions<Vacancy> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Vacancy>()
                .setQuery(query, Vacancy.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Vacancy, VacancyHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final VacancyHolder holder, final int position, @NonNull final Vacancy model) {

                holder.setTv_title(model.getJob_title());
                companyRef.child("company").child(model.getCompany_id()).child("company_name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String companyName = dataSnapshot.getValue(String.class);
                        holder.setTv_company(companyName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("TAG",databaseError.getMessage());
                    }
                });
                companyLogoUrlRef.child("company").child(model.getCompany_id()).child("company_logo_url").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.setIv_companyLogo(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("TAG",databaseError.getMessage());
                    }
                });

                holder.setTv_period(model.getIntern_period());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String test = ((TextView) mJobList.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.tv_jobTitle)).getText().toString();
                        Intent intent = new Intent(getActivity(),JobDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("Test",getRef(position).getKey());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public VacancyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_forum_job,parent,false);
                final VacancyHolder vacancyHolder = new VacancyHolder(view);
                return vacancyHolder;
            }
        };
        mJobList.setHasFixedSize(true);
        mJobList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mJobList.setAdapter(adapter);
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
}
