package com.example.krhotels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.krhotels.Adapter.SliderAdapter;
import com.example.krhotels.Model.Hotel;
import com.example.krhotels.Model.ImageItem;
import com.example.krhotels.Model.Review;


import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class DetailItemActivity extends AppCompatActivity {


    DatabaseReference refGetDetail;
    DatabaseReference refGetDetail2;
    SliderAdapter sliderAdapter;
    MaterialButton btnAddReview, btnGoToMap, btnCancel;
    DatabaseReference refReview;
    String uid;
    String dateText;
    FirebaseUser fUser;
    FirebaseAuth fAuth;

    ArrayList<ImageItem> imageList;



    com.example.krhotels.Model.Item Item;

    String ratingResult;

    RecyclerView rv_review;
    ImageView image_main,image1, image2, image3, image4;
    TextView tv_desc, tv_phone, tv_address, tv_workhour, tv_name;


    private static final String SPREAD_SHEET_ID = "1BTLGpK0Y0r7r4MMuUHcLxbhePXW2yPyL_Usx8Mj4YHU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);

        initialize();
        getDetailInfo();



        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        dateText = dateFormat.format(currentDate).toString();

        imageList = new ArrayList<>();

     /*   btnAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CircleImageView cancel;
                Button sendReview;
                RatingBar ratingBar;
                EditText text_review;




                View alertCustomdialog = LayoutInflater.from(DetailItemActivity.this).inflate(R.layout.custom_dialog_review,null);

                AlertDialog.Builder alert = new AlertDialog.Builder(DetailItemActivity.this);


                alert.setView(alertCustomdialog);
                sendReview = alertCustomdialog.findViewById(R.id.btnSendReview);
                cancel = alertCustomdialog.findViewById(R.id.cancel_button);
                ratingBar = alertCustomdialog.findViewById(R.id.review_rating);
                text_review = alertCustomdialog.findViewById(R.id.review_text);
                final AlertDialog dialog = alert.create();

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        ratingResult = String.valueOf(ratingBar);
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                sendReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addReview(uid, "name",ratingResult, dateText, text_review.getText().toString());
                    }
                });
            }
        });*/




    }

    private void addReview(String uid, String name, String rating, String date, String text ) {
        Intent getName = getIntent();
        refReview = FirebaseDatabase.getInstance().getReference();
        refReview.child("Review").child(getName.getStringExtra("restaurantName"))
                .child(dateText);

        Review review = new Review();
    }

    private void getDetailInfo() {
        Intent i = getIntent();




        refGetDetail = FirebaseDatabase.getInstance().getReference().child(SPREAD_SHEET_ID).child(i.getStringExtra("item")).child( i.getStringExtra("name"));
        refGetDetail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Hotel hotel = snapshot.getValue(Hotel.class);



                    String name = hotel.getName().toString();
                    String address = hotel.getAddress();
                    String phone = hotel.getPhone();
                    String workhour = hotel.getWorkhour();
                    String desc = hotel.getDesc();

                    String image1Url = hotel.getImage1();
                    String image2Url = hotel.getImage2();
                    String image3Url = hotel.getImage3();
                    String image4Url = hotel.getImage4();



                    tv_name.setText(name);
                    tv_address.setText(address);
                    tv_phone.setText(phone);
                    tv_workhour.setText(workhour);
                    tv_desc.setText(desc);

                Glide.with(DetailItemActivity.this).load(image1Url).into(image_main);
                Glide.with(DetailItemActivity.this).load(image1Url).into(image1);
                Glide.with(DetailItemActivity.this).load(image2Url).into(image2);
                Glide.with(DetailItemActivity.this).load(image3Url).into(image3);
                Glide.with(DetailItemActivity.this).load(image4Url).into(image4);











            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initialize() {

        rv_review = findViewById(R.id.rv_review);
        tv_desc = findViewById(R.id.hotel_desc);
        image_main = findViewById(R.id.hotel_image);
        tv_workhour = findViewById(R.id.tv_workhour);
        tv_address = findViewById(R.id.tv_address);
        tv_phone = findViewById(R.id.tv_phone);
        tv_name = findViewById(R.id.hotel_name);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
    }
}