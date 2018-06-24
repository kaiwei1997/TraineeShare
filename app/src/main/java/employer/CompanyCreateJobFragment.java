package employer;

import android.app.ProgressDialog;
import android.com.traineeshare.R;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import model.Vacancy;

public class CompanyCreateJobFragment extends Fragment {
    private Spinner spinner_JobFieldSel;
    private EditText et_jobTitle, et_jobDescrip, et_internPeriod, et_jobSalary, et_jobRequire;
    private String job_field, job_title, job_descrip, intern_period, job_salary, job_require, category_period;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private Vacancy vacancy;

    public CompanyCreateJobFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_company_create_job, container, false);
        et_jobTitle = rootView.findViewById(R.id.job);
        et_jobDescrip = rootView.findViewById(R.id.job_descrip);
        et_internPeriod = rootView.findViewById(R.id.intern_period);
        et_jobRequire = rootView.findViewById(R.id.job_Require);
        et_jobSalary = rootView.findViewById(R.id.job_Salary);
        Button createButton = rootView.findViewById(R.id.createButton);
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance().getReference().child("vacancy");

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check() == true) {
                    Toast.makeText(getActivity(), "Job Post Created", Toast.LENGTH_SHORT).show();
                    createJob();
                } else {
                    Toast.makeText(getActivity(), "Please correct error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spinner_JobFieldSel = rootView.findViewById(R.id.spinner_JobFieldSel);
        ArrayAdapter<CharSequence> jobAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.JobFiledSelection, android.R.layout.simple_spinner_dropdown_item);
        jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_JobFieldSel.setAdapter(jobAdapter);
        spinner_JobFieldSel.setOnItemSelectedListener(jobListener);


        return rootView;
    }

    public boolean check(){
        boolean checked = true;
        job_title = et_jobTitle.getText().toString().trim();
        job_descrip = et_jobDescrip.getText().toString().trim();
        intern_period = et_internPeriod.getText().toString().trim();
        job_require = et_jobRequire.getText().toString().trim();
        job_salary = et_jobSalary.getText().toString().trim();
        category_period = job_field +"_"+ intern_period;

        if(TextUtils.isEmpty(job_title)){
            et_jobTitle.setError("Job post cannot be empty.");
            checked = false;
        }
        if(TextUtils.isEmpty(job_descrip)){
            et_jobDescrip.setError("Job description cannot be empty.");
            checked = false;
        }
        if(TextUtils.isEmpty(intern_period)){
            et_internPeriod.setError("Internship period cannot be empty.");
            checked = false;
        }
        if(TextUtils.isEmpty(job_require)){
            et_jobRequire.setError("Job requirement cannot be empty.");
            checked = false;
        }
        if(TextUtils.isEmpty(job_salary)){
            et_jobSalary.setError("Job salary cannot be empty.");
            checked = false;
        }

        return checked;
    }

    public void createJob(){
        vacancy = new Vacancy(job_title, job_descrip, job_require, intern_period,  job_salary, mAuth.getCurrentUser().getUid(), category_period, job_field);
        db.push().setValue(vacancy);
    }


    AdapterView.OnItemSelectedListener jobListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            job_field = spinner_JobFieldSel.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }


    };
}