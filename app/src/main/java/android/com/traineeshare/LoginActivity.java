package android.com.traineeshare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import employer.CompanyMainActivity;
import student.StudentMainActivity;
import util.Password_Validator;

public class LoginActivity extends AppCompatActivity {
    private static final String COMPANY_ROLE_KEY = "employer";
    private static final String STUDENT_ROLE_KEY = "student";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference userRef;
    private ProgressDialog mPr;
    private Password_Validator password_validator;
    private EditText et_email, et_pass;
    private Button btn_Login;
    private TextView registerLink, forgetPass;
    private String reset_password_email;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_password);
        btn_Login = findViewById(R.id.btn_Login);
        registerLink = findViewById(R.id.tv_Register);
        forgetPass = findViewById(R.id.tv_forgetPass);

        mAuth = FirebaseAuth.getInstance();

        mPr = new ProgressDialog(this);
        password_validator = new Password_Validator();

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_email.getText().toString().trim();
                String pass = et_pass.getText().toString().trim();
                checkLogin(email, pass);
            }
        });
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Forget Password? Please enter email below")
                        .setCancelable(false);
                final EditText input = new EditText(LoginActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setView(input);

                builder.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reset_password_email = input.getText().toString();
                        if (TextUtils.isEmpty(reset_password_email)) {
                            input.setError("Please input email");
                            input.requestFocus();
                            return;
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(reset_password_email).matches()) {
                            input.setError("Please input email");
                            input.requestFocus();
                            return;
                        } else {

                            mAuth.sendPasswordResetEmail(reset_password_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed to send reset email! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    //Login verification function
    public void checkLogin(final String email, final String pass) {
        if (TextUtils.isEmpty(email)) {
            et_email.setError("Email cannot be empty");
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Email invalid");
        }
        if (TextUtils.isEmpty(pass)) {
            et_pass.setError("Password cannot be empty");
        }
        if (!Password_Validator.validate(pass)) {
            et_pass.setError("Password invalid");
        } else {
            mPr.setMessage("Signing in...");
            mPr.show();
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("Login Fail", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed:" + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                                mPr.dismiss();
                                et_pass.setText("");
                            } else if (!mAuth.getCurrentUser().isEmailVerified()) {
                                Log.w("Email unverified", "Please verify email. Verification email have been send");
                                Toast.makeText(LoginActivity.this, "Please verify email.Verification email have sent to " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                mPr.dismiss();
                                mAuth.getCurrentUser().sendEmailVerification();
                                et_pass.setText("");
                            } else {
                                Log.d("Login Successful", "signInWithEmail:success");
                                user = mAuth.getCurrentUser();
                                userRef = FirebaseDatabase.getInstance().getReference();
                                userRef.child("role").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        role = dataSnapshot.getValue(String.class);
                                        if (role.equals(COMPANY_ROLE_KEY)) {
                                            mPr.dismiss();
                                            finish();
                                            startActivity(new Intent(LoginActivity.this, CompanyMainActivity.class));
                                        } else if (role.equals(STUDENT_ROLE_KEY)) {
                                            mPr.dismiss();
                                            finish();
                                            startActivity(new Intent(LoginActivity.this, StudentMainActivity.class));
                                        } else {
                                            mPr.dismiss();
                                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
        }
    }
}


