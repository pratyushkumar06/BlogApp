package personal.project.android.blogapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class PostActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FloatingActionButton floatingActionButton;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private AccountFragment accountFragment;
    private NotificationFragment notificationFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        floatingActionButton=findViewById(R.id.floatingActionButton2);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostActivity.this,AddPost.class);
                startActivity(intent);
            }
        });
        mAuth= FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {

            homeFragment = new HomeFragment();
            accountFragment = new AccountFragment();
            notificationFragment = new NotificationFragment();

            initialiseFrag();

            replaceFragment(homeFragment);
            bottomNavigationView = findViewById(R.id.nav);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.hm:
                            replaceFragment(homeFragment);
                            return true;

                        case R.id.notf:
                            replaceFragment(notificationFragment);
                            return true;

                        case R.id.acc:
                            replaceFragment(accountFragment);
                            return true;

                        default:
                            return false;
                    }
                }
            });
        }
    }

    private void initialiseFrag() {
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame,homeFragment);
        transaction.add(R.id.frame,accountFragment);
        transaction.add(R.id.frame,notificationFragment);

        transaction.hide(accountFragment);
        transaction.hide(notificationFragment);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null && user.getDisplayName()==null && user.getPhotoUrl()==null){  //As creating a profile is an essential process it will cause to create profile
            Intent in=new Intent(PostActivity.this,AccountSettings.class);
            startActivity(in);
            Toast.makeText(PostActivity.this,"Create Profile to proceed",Toast.LENGTH_LONG).show();
        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.logout){
            mAuth.signOut();
            Intent in=new Intent(PostActivity.this,MainActivity.class);
            startActivity(in);
            finish();
        }
        if(id==R.id.settings){
            Intent in=new Intent(PostActivity.this,AccountSettings.class);
            startActivity(in);

        }
        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();

        if(fragment==homeFragment){
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(notificationFragment);
        }

        else if(fragment==accountFragment){
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(notificationFragment);
        }


        else if(fragment==notificationFragment){
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(accountFragment);
        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }
}
