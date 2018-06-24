package employer;

import android.Manifest;
import android.app.ProgressDialog;
import android.com.traineeshare.LoginActivity;
import android.com.traineeshare.R;
import android.com.traineeshare.SplashActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import student.StudentVerificationActivity;

public class CompanyVerificationActivity extends AppCompatActivity {

    private TextView tv_Description;
    private ImageView iv_Document;
    private Button btn_Upload, btn_Submit;

    private Uri filePath;

    private int SELECT_IMAGE_REQUEST = 68;
    private int TAKE_PHOTO_REQUEST = 97;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    private UploadTask uploadTask;

    private DatabaseReference companyRef;

    private ProgressDialog mProgress;

    private String companyID;

    private int statusCode;

    private final int UPLOAD_SUCCESS_STATUS_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_verification);

        tv_Description = findViewById(R.id.tv_DescriptionCom);
        iv_Document = findViewById(R.id.iv_Document);
        btn_Upload = findViewById(R.id.btn_UploadDoc);
        btn_Submit = findViewById(R.id.btn_SubmitDoc);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        //Extract the data
        companyID = bundle.getString("companyID");
        statusCode = bundle.getInt("statusCode");

        companyRef = FirebaseDatabase.getInstance().getReference().child("verification").child(companyID);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mAuth = FirebaseAuth.getInstance();

        if (statusCode == 0) {
            tv_Description.setText("Please upload local business registration documents to let us verify your account");
        } else if (statusCode == 1) {
            tv_Description.setText("You have submit the document. Kindly wait for the approval");
            iv_Document.setImageResource(R.drawable.ic_check_green_24dp);
            btn_Upload.setVisibility(View.GONE);
            btn_Submit.setVisibility(View.GONE);
        } else if (statusCode == 2) {
            tv_Description.setText("Your application have been reject. Please upload local business registration documents again for us to approve");
        }

        mProgress = new ProgressDialog(CompanyVerificationActivity.this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_switch_acc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.switchAcc:
                mAuth.signOut();
                finish();
                startActivity(new Intent(CompanyVerificationActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    public void uploadImageChoice() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CompanyVerificationActivity.this);
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
            if (ActivityCompat.checkSelfPermission(CompanyVerificationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CompanyVerificationActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_IMAGE_REQUEST);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Local Business Registration Documents"), SELECT_IMAGE_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takePhoto() {
        try {
            if (ActivityCompat.checkSelfPermission(CompanyVerificationActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CompanyVerificationActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PHOTO_REQUEST);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(Intent.createChooser(intent, "Take A Photo of Local Business Registration Documents"), TAKE_PHOTO_REQUEST);
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
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(CompanyVerificationActivity.this.getContentResolver(), filePath);
                    iv_Document.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == TAKE_PHOTO_REQUEST) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                iv_Document.setImageBitmap(bitmap);
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

    public void submitApplication(){
        if(filePath==null){
            btn_Upload.setError("Please download local business registration document");
        }else{
            mProgress.setMessage("Uploading");
            mProgress.show();

            //upload image
            final StorageReference ref = storageReference.child("Business Registration Document/" + UUID.randomUUID().toString());
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
                        companyRef.child("DocumentDownloadURL").setValue(downloadUri.toString());
                    } else {
                        Toast.makeText(CompanyVerificationActivity.this, "Failed ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            companyRef.child("status").setValue(UPLOAD_SUCCESS_STATUS_CODE);
            Toast.makeText(CompanyVerificationActivity.this, "Upload business registration document successful", Toast.LENGTH_SHORT).show();
            mProgress.dismiss();
            finish();
            startActivity(new Intent(CompanyVerificationActivity.this, SplashActivity.class));
        }
    }
}
