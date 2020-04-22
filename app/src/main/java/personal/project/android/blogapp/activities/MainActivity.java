package personal.project.android.blogapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import personal.project.android.blogapp.R;

public class MainActivity extends AppCompatActivity {
    private Button button,signup;
    private EditText mEmail,mPassword;
    private FirebaseAuth mAuth;
    String email,pass;
    private FirebaseAuth.AuthStateListener stateListener;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signup=findViewById(R.id.button);
        button=findViewById(R.id.button2);
        mEmail=findViewById(R.id.editText);
        mPassword=findViewById(R.id.editText2);
        progressBar=findViewById(R.id.progressBar);


        mAuth=FirebaseAuth.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(MainActivity.this, SignUp.class);
                startActivity(in);
                finish();
            }
        });
        stateListener= new FirebaseAuth.AuthStateListener() {  //acts according to the change in the authentication states If a user Has logged in previously he will stay logged in
            //He won't have to login again and again
            @Override
            public void onAuthStateChanged( FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){

                    Intent intent=new Intent(MainActivity.this, PostActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };

       // mAuth.addAuthStateListener(stateListener);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(stateListener);
    }

    private void SignIn() {
        email = mEmail.getText().toString();
        pass = mPassword.getText().toString();
        if(email.isEmpty() ||pass.isEmpty())
        { if (email.isEmpty()) {
            mEmail.setError("Username cannot be left blank");
            mEmail.requestFocus();
        }
            if(pass.isEmpty()){
                mPassword.setError("Password cannot be left blank");
                mPassword.requestFocus();
            }
        }
        else {

            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete( Task<AuthResult> task) {  //Inside The task Variable the results are Stored
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Username or Password is Incorrect", Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}
