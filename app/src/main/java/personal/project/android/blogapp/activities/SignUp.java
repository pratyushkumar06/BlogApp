package personal.project.android.blogapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import personal.project.android.blogapp.R;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    EditText email,pass;
    String username,password;
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        textView=findViewById(R.id.textView2);
        email=findViewById(R.id.editText);
        pass=findViewById(R.id.editText2);
        button=findViewById(R.id.button);

        progressBar=findViewById(R.id.progressBar2);
        mAuth=FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    protected void onStart() {  //To check if any current user is signed in
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();

    }

    private void registerUser() {
        username = email.getText().toString();
        password = pass.getText().toString();
        if(username.isEmpty() ||password.isEmpty()|| password.length()<6)
        { if (username.isEmpty()) {
            email.setError("Username cannot be left blank");
            email.requestFocus();
            return;
        }
            if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
                email.setError("Enter a Valid Email");
                email.requestFocus();
                return;
            }
            if(password.isEmpty() || password.length()<6){
                pass.setError("Minimum Length of password should be 6 charecters");
                pass.requestFocus();
                return;
            }

        }
        else {

            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        finish();
                        Toast.makeText(SignUp.this,"Registered Successfully",Toast.LENGTH_LONG).show();
                    }
                    else {
                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(SignUp.this,"Email id Already Registered",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(SignUp.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}
