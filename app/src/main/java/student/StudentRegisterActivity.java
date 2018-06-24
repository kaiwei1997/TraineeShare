package student;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.com.traineeshare.LoginActivity;
import android.com.traineeshare.R;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import model.Student;
import util.Password_Validator;

import static android.app.Activity.RESULT_OK;

public class StudentRegisterActivity extends Fragment {

    private Button buttonSelectImg, buttonReg, buttonCancelReg;
    private ImageView student_pic;
    private EditText et_firstName, et_lastName, et_contact, et_address, et_email, et_pass, et_conf_pass;
    private Uri filePath;

    private int SELECT_IMAGE_REQUEST = 68;
    private int TAKE_PHOTO_REQUEST = 97;

    private String studentFN, studentLN, studentContact, studentAdress, studentEmail, studentPass, studentConfPass;

    private int studentStatus;

    private Student student;

    private static final String STUDENT_CHILD_KEY = "student";

    private static final String STUDENT_ROLE_KEY = "student";

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private UploadTask uploadTask;

    private DatabaseReference db;

    private ProgressDialog mProgress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_student_register, container, false);

        buttonSelectImg = rootView.findViewById(R.id.btn_uploadProfilePic);
        buttonReg = rootView.findViewById(R.id.btn_StudentRegister);
        buttonCancelReg = rootView.findViewById(R.id.btn_StudentCancelReg);
        student_pic = rootView.findViewById(R.id.iv_profilePic);

        et_firstName = rootView.findViewById(R.id.et_FirstName);
        et_lastName = rootView.findViewById(R.id.et_LastName);
        et_contact = rootView.findViewById(R.id.et_ContactNumber);
        et_address = rootView.findViewById(R.id.et_Address);
        et_email = rootView.findViewById(R.id.et_StudentEmail);
        et_pass = rootView.findViewById(R.id.et_StudentPassword);
        et_conf_pass = rootView.findViewById(R.id.et_StudentConfirmPassword);

        et_firstName.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        db = FirebaseDatabase.getInstance().getReference().child(STUDENT_CHILD_KEY);

        mProgress = new ProgressDialog(getActivity());

        buttonSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageChoice();
            }
        });

        student_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageChoice();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkRegister() == true) {
                    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.student_TC)
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
                    android.support.v7.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getActivity(), "Please correct error", Toast.LENGTH_SHORT).show();
                }
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

    public void uploadImageChoice() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    takePhoto();
                } else if (items[item].equals("Choose from Library")) {
                    selectImage();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void selectImage() {
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_IMAGE_REQUEST);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), SELECT_IMAGE_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takePhoto() {
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PHOTO_REQUEST);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(Intent.createChooser(intent, "Take A Selfie For Profile Picture"), TAKE_PHOTO_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE_REQUEST && data != null && data.getData() != null) {
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    student_pic.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == TAKE_PHOTO_REQUEST) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                student_pic.setImageBitmap(bitmap);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;

                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    filePath = Uri.fromFile(destination);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean checkRegister() {
        boolean checked = true;
        studentFN = et_firstName.getText().toString().trim();
        studentLN = et_lastName.getText().toString().trim();
        studentContact = et_contact.getText().toString().trim();
        studentAdress = et_address.getText().toString().trim();
        studentEmail = et_email.getText().toString().trim();
        studentPass = et_pass.getText().toString().trim();
        studentConfPass = et_conf_pass.getText().toString().trim();
        if (filePath == null) {
            buttonSelectImg.setError("Please upload profile picture");
            checked = false;
        }
        if (TextUtils.isEmpty(studentFN)) {
            et_firstName.setError("First name cannot be empty");
            checked = false;
        }
        if (TextUtils.isEmpty(studentLN)) {
            et_lastName.setError("Last name cannot be empty");
            checked = false;
        }
        if (TextUtils.isEmpty(studentContact)) {
            et_contact.setError("Contact number cannot be empty");
            checked = false;
        }
        if (studentContact.length() < 9 || studentContact.length() > 11) {
            et_contact.setError("Contact number length invalid");
            checked = false;
        }
        if (TextUtils.isEmpty(studentAdress)) {
            et_address.setError("Address cannot be empty");
            checked = false;
        }
        if (TextUtils.isEmpty(studentEmail)) {
            et_email.setError("Email cannot be empty");
            checked = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(studentEmail).matches()) {
            et_email.setError("Email format invaid");
            checked = false;
        }
        if (TextUtils.isEmpty(studentPass)) {
            et_pass.setError("Password cannot be empty");
            checked = false;
        }
        if (!Password_Validator.validate(studentPass)) {
            et_pass.setError("Password must have Alphanumeric include 1 Capital letter and special character and at least 8 character and not more than 15 character");
            checked = false;
        }
        if (TextUtils.isEmpty(studentConfPass)) {
            et_conf_pass.setError("Please reenter password");
            checked = false;
        }
        if (!studentPass.equals(studentConfPass)) {
            et_conf_pass.setError("Password not matches");
            checked = false;
        }
        return checked;
    }

    public void startRegister() {
        mProgress.setMessage("Singing up...");
        mProgress.show();
        mAuth.createUserWithEmailAndPassword(studentEmail, studentPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    mProgress.dismiss();
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    String student_id = mAuth.getCurrentUser().getUid();
                    final DatabaseReference current_student = db.child(student_id);
                    final DatabaseReference role = FirebaseDatabase.getInstance().getReference().child("role");
                    final DatabaseReference verification = FirebaseDatabase.getInstance().getReference().child("verification");

                    //upload Image
                    final StorageReference ref = storageReference.child("StudentProfilePic/" + UUID.randomUUID().toString());
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
                                current_student.child("student_profile_pic_url").setValue(downloadUri.toString());
                            } else {
                                Toast.makeText(getActivity(), "Failed ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    studentStatus = 0;
                    student = new Student(studentFN, studentLN, studentContact, studentAdress);
                    current_student.setValue(student);
                    role.child(student_id).setValue(STUDENT_ROLE_KEY);
                    verification.child(student_id).child("status").setValue(studentStatus);
                    mAuth.getCurrentUser().sendEmailVerification();
                    Toast.makeText(getActivity(), "Student sign up successful, verification email have sent to " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
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
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
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

        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
