package employer;

import android.com.traineeshare.R;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import model.Company;
import model.Student;
import model.Vacancy;


public class CompanyCheckApplication extends Fragment {
    private ImageView companyLogo;
    private String job_id, uid;
    private EditText companyName, companyEmail, companyAddress, companyContact, jobTitle, jobDescrip, jobRequire, jobSalary, internPeriod, jobDetails;
    private Button editButton;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference db, dbV;
    public CompanyCheckApplication() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        job_id = "-LFmLb4vhceli3RkLsAT";
        db = FirebaseDatabase.getInstance().getReference().child("company");
        dbV = FirebaseDatabase.getInstance().getReference().child("vacancy");
        loadProfilePic(uid);
        loadCompanyProfile(uid);
        loadJobProfile(job_id);

    }

    public void loadJobProfile(String job_id){
        DatabaseReference current_vacancy = dbV.child(job_id);
        current_vacancy.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Vacancy vacancy = dataSnapshot.getValue(Vacancy.class);
                jobTitle.setText(vacancy.job_title);
                jobDescrip.setText(vacancy.job_description);
                jobRequire.setText(vacancy.job_requirement);
                jobSalary.setText(vacancy.job_salary);
                internPeriod.setText(vacancy.intern_period);
                jobDetails.setText("Vacancy Details " + vacancy.job_category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void loadCompanyProfile(String uid){
        DatabaseReference current_company = db.child(uid);
        current_company.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Company company = dataSnapshot.getValue(Company.class);
                companyName.setText(company.company_address);
                companyAddress.setText(company.company_address);
                companyContact.setText(company.company_contact);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_company_application, container, false);

        companyLogo = rootView.findViewById(R.id.company_logo);
        companyName = rootView.findViewById(R.id.company_name);
        companyContact = rootView.findViewById(R.id.company_contact);
        companyAddress = rootView.findViewById(R.id.company_address);
        companyEmail = rootView.findViewById(R.id.company_email);
        jobTitle = rootView.findViewById(R.id.jobTitle);
        jobDescrip = rootView.findViewById(R.id.jobDescription);
        jobRequire = rootView.findViewById(R.id.jobRequirement);
        jobSalary = rootView.findViewById(R.id.jobSalary);
        internPeriod = rootView.findViewById(R.id.internPeriod);
        editButton = rootView.findViewById(R.id.editButton);
        jobDetails = rootView.findViewById(R.id.job_details);
        companyEmail.setText(mAuth.getCurrentUser().getEmail());

        return rootView;
    }

    public void loadProfilePic(String uid){
        DatabaseReference current_company_pic = db.child(uid).child("company_logo_url");
        current_company_pic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get()
                        .load(dataSnapshot.getValue(String.class))
                        .resize(130, 103)
                        .into(companyLogo, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) companyLogo.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                companyLogo.setImageDrawable(imageDrawable);
                            }

                            @Override
                            public void onError(Exception e) {
                                companyLogo.setImageResource(R.drawable.ic_profile);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}