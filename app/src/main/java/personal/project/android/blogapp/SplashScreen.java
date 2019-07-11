package personal.project.android.blogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        auth=FirebaseAuth.getInstance();
        Thread t=new Thread(){
            public void run(){
                try{
                    sleep(2500);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally
                {

                    if(auth.getCurrentUser()!=null){
                        Intent intent=new Intent(SplashScreen.this,PostActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }
        };
        t.start();

    }
}
