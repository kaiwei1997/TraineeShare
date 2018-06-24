package employer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.com.traineeshare.R;
import android.widget.Button;
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

public class CompanyProfileFragment extends Fragment {

    private ImageView logo;
    private EditText name, address, contact, fn, ln;
    private Button edit;

    private FirebaseAuth mAuth;
    private DatabaseReference dbR;

    private String companyID, comName, comAddress, comContact, comContactFN, comContactLN, companyLogoURL;

    public CompanyProfileFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_company_profile, container, false);

        logo = rootView.findViewById(R.id.iv_CompanyLogo);
        name = rootView.findViewById(R.id.et_CompanyName);
        address = rootView.findViewById(R.id.et_CompanyAddress);
        contact = rootView.findViewById(R.id.et_CompanyContact);
        fn = rootView.findViewById(R.id.et_CompanyContactFN);
        ln = rootView.findViewById(R.id.et_CompanyContactLN);

        edit = rootView.findViewById(R.id.btn_edit);

        mAuth = FirebaseAuth.getInstance();
        companyID = mAuth.getCurrentUser().getUid();
        dbR = FirebaseDatabase.getInstance().getReference().child("company");

        loadCompanyLogo(companyID);
        loadCompanyProfile(companyID);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address.setEnabled(true);
                contact.setEnabled(true);
                fn.setEnabled(true);
                ln.setEnabled(true);
                edit.setText("Save");
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(checkInput() == true){
                            Company company = new Company(comName,comAddress,comContactFN,comContactLN,comContact);
                            dbR.child(companyID).child("company_address").setValue(company.company_address);
                            dbR.child(companyID).child("contact_person_Fn").setValue(company.contact_person_Fn);
                            dbR.child(companyID).child("contact_person_Ln").setValue(company.contact_person_Ln);
                            dbR.child(companyID).child("company_contact").setValue(company.company_contact);
                            Toast.makeText(getActivity(),"Update Successful",Toast.LENGTH_SHORT).show();
                            loadCompanyLogo(companyID);
                            loadCompanyProfile(companyID);
                            edit.setText("Edit");
                        }
                    }
                });

            }
        });
        return rootView;
    }

    public boolean checkInput(){
        boolean checked = true;
        comName = name.getText().toString();
        comAddress = address.getText().toString();
        comContact = contact.getText().toString();
        comContactFN = fn.getText().toString();
        comContactLN = ln.getText().toString();

        if(TextUtils.isEmpty(comAddress)){
            address.setError("Cannot be empty");
            checked = false;
        }
        if(TextUtils.isEmpty(comContact)){
            contact.setError("Cannot be empty");
            checked = false;
        }
        if(TextUtils.isEmpty(comContactFN)){
            fn.setError("Cannot be empty");
            checked = false;
        }
        if(TextUtils.isEmpty(comContactLN)){
            ln.setError("Cannot be empty");
            checked = false;
        }

        return checked;
    }

    public void loadCompanyLogo(String companyID){
        DatabaseReference current_company_logo = dbR.child(companyID).child("company_logo_url");
        current_company_logo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                companyLogoURL = dataSnapshot.getValue(String.class);
                Picasso.get()
                        .load(dataSnapshot.getValue(String.class))
                        .resize(250, 250)
                        .into(logo, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) logo.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                logo.setImageDrawable(imageDrawable);
                            }

                            @Override
                            public void onError(Exception e) {
                                logo.setImageResource(R.drawable.ic_company);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadCompanyProfile(String companyID){
        DatabaseReference current_company = dbR.child(companyID);
        current_company.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Company company = dataSnapshot.getValue(Company.class);
                name.setText(company.company_name);
                address.setText(company.company_address);
                contact.setText(company.company_contact);
                fn.setText(company.contact_person_Fn);
                ln.setText(company.contact_person_Ln);

                name.setEnabled(false);
                address.setEnabled(false);
                contact.setEnabled(false);
                fn.setEnabled(false);
                ln.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
