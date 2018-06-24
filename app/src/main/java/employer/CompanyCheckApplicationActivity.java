package employer;

import android.com.traineeshare.R;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import model.Company;
import model.Vacancy;

public class CompanyCheckApplicationActivity extends AppCompatActivity {

    private ImageView companyLogo;
    private String job_id, uid;
    private EditText companyName, companyEmail, companyAddress, companyContact, jobTitle, jobDescrip, jobRequire, jobSalary, internPeriod, jobDetails;
    private FirebaseAuth mAuth;

    private DatabaseReference db, dbV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_check_application);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        Bundle bundle = getIntent().getExtras();
        job_id = bundle.getString("JobID");
        db = FirebaseDatabase.getInstance().getReference().child("company");
        dbV = FirebaseDatabase.getInstance().getReference().child("vacancy");

        companyLogo = findViewById(R.id.company_logo);
        companyName = findViewById(R.id.company_name);
        companyContact = findViewById(R.id.company_contact);
        companyAddress = findViewById(R.id.company_address);
        companyEmail = findViewById(R.id.company_email);
        jobTitle = findViewById(R.id.jobTitle);
        jobDescrip = findViewById(R.id.jobDescription);
        jobRequire = findViewById(R.id.jobRequirement);
        jobSalary = findViewById(R.id.jobSalary);
        internPeriod = findViewById(R.id.internPeriod);
        jobDetails = findViewById(R.id.job_details);
        companyEmail.setText(mAuth.getCurrentUser().getEmail());

        loadProfilePic(uid);
        loadCompanyProfile(uid);
        loadJobProfile(job_id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //perhaps use intent if needed but i'm sure there's a specific intent action for up you can use to handle
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                Toast.makeText(CompanyCheckApplicationActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CompanyCheckApplicationActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(CompanyCheckApplicationActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
