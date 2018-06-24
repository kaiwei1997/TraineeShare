package employer;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.com.traineeshare.LoginActivity;
import android.com.traineeshare.R;
import android.content.Intent;
import android.os.Bundle;
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

import model.Company;

public class CompanyMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference companyRef, statusRef;

    private View navHeader;

    private TextView company_email, company_name;

    private ImageView company_logo;

    private String uid;

    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.company_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.company_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        companyRef = FirebaseDatabase.getInstance().getReference().child("company");
        statusRef = FirebaseDatabase.getInstance().getReference();
        statusRef.child("verification").child(uid).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status = dataSnapshot.getValue(Integer.class);
                if (status == 0 || status == 1 || status == 2) {
                    Intent i = new Intent(CompanyMainActivity.this, CompanyVerificationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("companyID", uid);
                    bundle.putInt("statusCode", status);
                    i.putExtras(bundle);
                    finish();
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CompanyMainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        navHeader = navigationView.getHeaderView(0);
        company_logo = navHeader.findViewById(R.id.iv_company_logo);
        DatabaseReference current_company_logo = companyRef.child(uid).child("company_logo_url");
        current_company_logo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Picasso.get()
                        .load(dataSnapshot.getValue(String.class))
                        .resize(250, 250)
                        .into(company_logo, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) company_logo.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                company_logo.setImageDrawable(imageDrawable);
                            }

                            @Override
                            public void onError(Exception e) {
                                company_logo.setImageResource(R.drawable.ic_company);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CompanyMainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        company_name = navHeader.findViewById(R.id.tv_navHeader_companyName);
        DatabaseReference current_company = companyRef.child(uid);
        current_company.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Company company = dataSnapshot.getValue(Company.class);
                company_name.setText(company.company_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CompanyMainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        company_email = navHeader.findViewById(R.id.tv_navHeader_companyEmail);
        company_email.setText(mAuth.getCurrentUser().getEmail().toString().trim());

        selectDrawerItem(navigationView.getMenu().getItem(0));
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
                startActivity(new Intent(CompanyMainActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.company_drawer_layout);
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
        switch (item.getItemId()) {
            case R.id.nav_vacancy:
                fragmentControl(CompanyMainFragment.class);
                break;

            case R.id.nav_application:

                break;

            case R.id.nav_appointment:

                break;

            case R.id.nav_profile:

                break;

            default:
                fragmentControl(CompanyMainFragment.class);
                break;
        }

        item.setChecked(true);

        setTitle(item.getTitle());

        DrawerLayout drawer = findViewById(R.id.company_drawer_layout);
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
        fragmentTransaction.replace(R.id.company_flContent,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
