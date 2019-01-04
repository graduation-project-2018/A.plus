package edu.iau.abjad.AbjadApp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class new_child_photo extends menu_educator  {
    menu_variables m = new menu_variables();
    private ImageView childImg;
    private ImageView next;
    private ImageView pre;
    private ArrayList<String> imgsUri;
    private firebase_connection FBchildPhotoUri, r;
    private int imgCont;
    private int imgIndex;
    private String photo_url;
    private Bundle childObj;
    private child_info_new completeObj;
    private Button addChild;
    private Intent backEducatorHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        m.title = (TextView) findViewById(R.id.interface_title);
        m.title.setText("إضافة طفل");
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_new_child_photo, null, false);
        mDrawerLayout.addView(contentView, 0);
        //Intiilaization
        next = findViewById(R.id.next);
        pre = findViewById(R.id.lesson2Stars);
        childImg = findViewById(R.id.lesson1Stars);
        imgsUri = new ArrayList<String>();
        FBchildPhotoUri = new firebase_connection();
        imgIndex = 0;
        photo_url="";
        addChild = findViewById(R.id.addChild);
        r=new firebase_connection();
        backEducatorHome = new Intent(this,educator_home.class);

        childObj = getIntent().getExtras();
        if(childObj!=null){
            completeObj=(child_info_new)childObj.getSerializable("object");
        }

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                m.setButton_text_XLarge(addChild);
                m.setTitle_XLarge();
                Log.i("scsize","X Large" );
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                m.setButton_text_Large(addChild);
                m.setTitle_Large();
                Log.i("scsize","Large" );

                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                m.setButton_text_Normal(addChild);
                m.setTitle_Normal();
                Log.i("scsize","Normal" );
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                m.setButton_text_Small(addChild);
                m.setTitle_Small();
                Log.i("scsize","Small" );
                break;
            default:
                m.setButton_text_Default(addChild);
                m.setTitle_Default();

        }//end switch

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        FBchildPhotoUri.ref.child("ChildPhoto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {

                    String photoUri = childSnapShot.getValue().toString();
                    imgsUri.add(photoUri);
                }
                // load();
                imgCont = (int)dataSnapshot.getChildrenCount();
                Picasso.get().load(imgsUri.get(0)).fit().centerInside().into(childImg);
                photo_url = imgsUri.get(0);

                //add button listener
                addChild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        completeObj.photo_URL=photo_url;

                        // get random ID from firebase, then use that ID to add the new child
                        String push_id = r.ref.push().getKey();
                        r.ref.child("Children").child(push_id).setValue(completeObj);
                        r.ref.child("Children").child(push_id).child("educator_id").setValue(signin_new.id_edu);
                        r.ref.child("educator_home").child(signin_new.id_edu).child(push_id).child("photo_URL").setValue(photo_url);
                        r.ref.child("educator_home").child(signin_new.id_edu).child(push_id).child("first_name").setValue(completeObj.first_name);
                        Toast.makeText(new_child_photo.this, "تمت إضافة الطفل بنجاح", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(backEducatorHome);
                        /*Query query = r.ref.child("Children").orderByKey();
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot child_id : dataSnapshot.getChildren()){
                                        Log.i("lolo", child_id.child("first_name").getValue().toString());
                                        Log.i("name id7",child_id.child("first_name").getKey());

                                        if(child_id.child("first_name").getValue().toString().equals(completeObj.first_name)
                                                && child_id.child("last_name").getValue().toString().equals(completeObj.last_name)){
                                            child_ID = child_id.getKey();
                                            Log.i("123456", child_ID);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });*/
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(new_child_photo.this, "Error", Toast.LENGTH_LONG).show();

            }

        });

        //load();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // load();
                imgIndex++;
                if (imgIndex < imgCont) {
                    Picasso.get().load(imgsUri.get(imgIndex)).fit().centerInside().into(childImg);
                    photo_url=imgsUri.get(imgIndex);
                } else {
                    imgIndex = 0;
                    Picasso.get().load(imgsUri.get(imgIndex)).fit().centerInside().into(childImg);
                    photo_url=imgsUri.get(imgIndex);

                }

            }
        });
        //load();
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // load();
                imgIndex--;
                if (imgIndex < imgCont & imgIndex > 0) {
                    Picasso.get().load(imgsUri.get(imgIndex)).fit().centerInside().into(childImg);
                    photo_url=imgsUri.get(imgIndex);

                } else if (imgIndex == -1) {
                    imgIndex = 5;
                    Picasso.get().load(imgsUri.get(imgIndex)).fit().centerInside().into(childImg);
                    photo_url=imgsUri.get(imgIndex);

                } else {
                    imgIndex = 0;
                    Picasso.get().load(imgsUri.get(imgIndex)).fit().centerInside().into(childImg);
                    photo_url=imgsUri.get(imgIndex);

                }
            }
        });
        //load();
    }




}