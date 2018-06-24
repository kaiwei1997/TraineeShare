package student;

import android.com.traineeshare.LoginActivity;
import android.com.traineeshare.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import model.Student;

public class StudentMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference studentRef, statusRef;
    private FirebaseDatabase db;

    private View navHeader;

    private TextView student_email, student_name;

    private ImageView student_picture;

    private String uid;

    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.student_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView studentNavigationView = findViewById(R.id.student_nav_view);
        studentNavigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        statusRef = FirebaseDatabase.getInstance().getReference();
        statusRef.child("verification").child(uid).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status = dataSnapshot.getValue(Integer.class);
                if(status==0 || status ==1 || status ==2){
                    Intent i = new Intent(StudentMainActivity.this,StudentVerificationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("studentID",uid);
                    bundle.putInt("statusCode",status);
                    i.putExtras(bundle);
                    finish();
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudentMainActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        studentRef = FirebaseDatabase.getInstance().getReference().child("student");

        navHeader = studentNavigationView.getHeaderView(0);
        student_picture = navHeader.findViewById(R.id.iv_student_pic);
        DatabaseReference current_student_pic = studentRef.child(uid).child("student_profile_pic_url");
        current_student_pic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get()
                        .load(dataSnapshot.getValue(String.class))
                        .resize(250, 250)
                        .into(student_picture, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) student_picture.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                student_picture.setImageDrawable(imageDrawable);
                            }

                            @Override
                            public void onError(Exception e) {
                                student_picture.setImageResource(R.drawable.ic_profile);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudentMainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        student_name = navHeader.findViewById(R.id.tv_navHeader_studentName);
        DatabaseReference current_student = studentRef.child(uid);
        current_student.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Student student = dataSnapshot.getValue(Student.class);
                student_name.setText(student.lastName + " " + student.firstName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudentMainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        student_email = navHeader.findViewById(R.id.tv_navHeader_studentEmail);
        student_email.setText(mAuth.getCurrentUser().getEmail());

        selectDrawerItem(studentNavigationView.getMenu().getItem(0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.logout:
                mAuth.signOut();
                finish();
                startActivity(new Intent(StudentMainActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.student_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectDrawerItem(item);
        return true;
    }

    public void selectDrawerItem(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_student_search_job:
                fragmentControl(JobListFragment.class);
                break;
            case R.id.nav_student_profile:
                fragmentControl(StudentProfileFragment.class);
                break;

            default:
                fragmentControl(JobListFragment.class);
        }
        item.setChecked(true);

        setTitle(item.getTitle());

        DrawerLayout drawer = findViewById(R.id.student_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void fragmentControl(Class fragmentClass) {
        Fragment fragment = null;
        Class fragmentCls = fragmentClass;

        try {
            fragment = (Fragment) fragmentCls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContent,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}
