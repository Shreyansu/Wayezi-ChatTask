package com.example.wayezi_onetoonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private String CurrentUserID;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;
    private String SetUserNumber,setName;
    private TabsAccessAdapter myTabsAccessorAdapter;
    private ViewPager myviewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        myviewPager = (ViewPager)findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessAdapter(getSupportFragmentManager());
        myviewPager.setAdapter(myTabsAccessorAdapter);


        CurrentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        SetUserNumber = getIntent().getExtras().get("number").toString();
        setName= getIntent().getExtras().get("name").toString();

        UpdateSettings();

    }


    private void UpdateSettings()
    {

        if(TextUtils.isEmpty(SetUserNumber))
        {
            Toast.makeText(HomeActivity.this, "Enter UserNumber", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid",CurrentUserID);
            profileMap.put("number",SetUserNumber);
            profileMap.put("name",setName);

            RootRef.child("Users").child(CurrentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {

//                                Toast.makeText(HomeActivity.this,"Profile updated successfully",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(HomeActivity.this,"Error:" + message,Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }

    }



}