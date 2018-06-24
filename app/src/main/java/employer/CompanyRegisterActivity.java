package employer;

import android.Manifest;
import android.app.ProgressDialog;
import android.com.traineeshare.LoginActivity;
import android.com.traineeshare.R;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import model.Company;
import util.Password_Validator;

import static android.app.Activity.RESULT_OK;


public class CompanyRegisterActivity extends Fragment {

    private Button buttonSelectImg, buttonReg, buttonCancelReg;
    private EditText et_comEmail, et_ComName, et_ComAddr, et_contact_fn, et_contact_ln, et_company_contact,
            et_pass, et_con_pass;

    private ImageView com_logo;

    private Uri filePath = null;

    private int SELECT_IMAGE_REQUEST = 68;

    private String comName, comEmail, comAddr, comCtctFn, comCtctLn, comContact, comPass, comConfPass;

    private int comStatus;

    private Company company;

    private static final String COMPANY_CHILD_KEY = "company";

    private static final String COMPANY_ROLE_KEY = "employer";

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private UploadTask uploadTask;

    private DatabaseReference db;

    private ProgressDialog mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_company_register, container, false);

        buttonSelectImg = rootView.findViewById(R.id.btn_select_img);
        com_logo = rootView.findViewById(R.id.iv_company_logo);
        et_comEmail = rootView.findViewById(R.id.et_company_email);
        et_pass = rootView.findViewById(R.id.et_company_pass);
        et_ComName = rootView.findViewById(R.id.et_company_name);
        et_ComAddr = rootView.findViewById(R.id.et_company_address);
        et_contact_fn = rootView.findViewById(R.id.et_contact_person_fn);
        et_contact_ln = rootView.findViewById(R.id.et_contact_person_ln);
        et_company_contact = rootView.findViewById(R.id.et_company_contact);
        et_con_pass = rootView.findViewById(R.id.et_confirm_company_pass);
        buttonReg = rootView.findViewById(R.id.btn_company_reg);
        buttonCancelReg = rootView.findViewById(R.id.btn_company_cencel_reg);

        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        db = FirebaseDatabase.getInstance().getReference().child(COMPANY_CHILD_KEY);

        mProgress = new ProgressDialog(getActivity());

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkRegister() == true) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.employer_TC)
                            .setCancelable(false);
                    builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startRegister();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getActivity(), "Please correct error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        com_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        buttonCancelReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRegister();
            }
        });

        return rootView;
    }

    public boolean checkRegister() {
        boolean checked = true;
        comName = et_ComName.getText().toString().trim();
        comEmail = et_comEmail.getText().toString().trim();
        comAddr = et_ComAddr.getText().toString().trim();
        comCtctFn = et_contact_fn.getText().toString().trim();
        comCtctLn = et_contact_ln.getText().toString().trim();
        comContact = et_company_contact.getText().toString().trim();
        comPass = et_pass.getText().toString().trim();
        comConfPass = et_con_pass.getText().toString().trim();

        if (filePath == null) {
            buttonSelectImg.setError("Logo cannot be empty");
            checked = false;
        }
        if (TextUtils.isEmpty(comName)) {
            et_ComName.setError("Company name cannot be empty");
            checked = false;
        }
        if (TextUtils.isEmpty(comEmail)) {
            et_comEmail.setError("Company email cannot be empty");
            checked = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(comEmail).matches()) {
            et_comEmail.setError("Invalid email format");
            checked = false;
        }
        if (TextUtils.isEmpty(comAddr)) {
            et_ComAddr.setError("Company address cannot be empty");
            checked = false;
        }
        if (TextUtils.isEmpty(comCtctFn)) {
            et_contact_fn.setError("Contact person first name cannot be empty");
            checked = false;
        }
        if (TextUtils.isEmpty(comCtctLn)) {
            et_contact_ln.setError("Contact person last name cannot be empty");
            checked = false;
        }
        if (TextUtils.isEmpty(comContact)) {
            et_company_contact.setError("Company contact number cannot be empty");
            checked = false;
        }
        if (comContact.length() < 9 || comContact.length() > 11) {
            et_company_contact.setError("Company contact number length invalid");
            checked = false;
        }
        if (TextUtils.isEmpty(comPass)) {
            et_pass.setError("Password cannot be empty");
            checked = false;
        }
        if (!Password_Validator.validate(comPass)) {
            et_pass.setError("Password must have Alphanumeric include 1 Capital letter and special character and at least 8 character and not more than 15 character");
            checked = false;
        }
        if (TextUtils.isEmpty(comConfPass)) {
            et_pass.setError("Please reenter password");
            checked = false;
        }
        if (!comPass.equals(comConfPass)) {
            et_con_pass.setError("Password not matches");
            checked = false;
        }
        return checked;
    }

    public void selectImage() {
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_IMAGE_REQUEST);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Company Logo"), SELECT_IMAGE_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                com_logo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void startRegister() {
        mProgress.setMessage("Signing Up....");
        mProgress.show();
        mAuth.createUserWithEmailAndPassword(comEmail, comPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    mProgress.dismiss();
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    String company_id = mAuth.getCurrentUser().getUid();
                    final DatabaseReference current_company = db.child(company_id);
                    final DatabaseReference role = FirebaseDatabase.getInstance().getReference().child("role");
                    final DatabaseReference verification = FirebaseDatabase.getInstance().getReference().child("verification");

                    //Upload image
                    final StorageReference ref = storageReference.child("CompanyLogo/" + UUID.randomUUID().toString());
                    uploadTask = ref.putFile(filePath);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                current_company.child("company_logo_url").setValue(downloadUri.toString());
                            } else {
                                Toast.makeText(getActivity(), "Failed ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    comStatus = 0;
                    company = new Company(comName, comAddr, comCtctFn, comCtctLn, comContact);
                    current_company.setValue(company);
                    role.child(company_id).setValue(COMPANY_ROLE_KEY);
                    verification.child(company_id).child("status").setValue(comStatus);
                    mAuth.getCurrentUser().sendEmailVerification();
                    Toast.makeText(getActivity(), "Company sign up successful, verification email have sent to " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                    startLoginIntent();
                }
            }
        });
    }

    public void startLoginIntent() {
        Intent loginIntent = new Intent(getContext(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    public void cancelRegister() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You are about to cancel register.Confirm cancel?")
                .setCancelable(false);
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startLoginIntent();
            }
        });
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
