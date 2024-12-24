package com.millenialzdev.uasdaa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.millenialzdev.uasdaa.presentation.page.auth.LoginActivity;

import java.text.DecimalFormat;

public class OptimasiBiaya extends AppCompatActivity {

    private EditText etAsal, etTujuan;
    private Button btnJalur;
    private CardView cardRating;
    private FirebaseAuth mAuth;
    private FirebaseUser  currentUser ;
    private FirebaseFirestore firestore;

    // stars
    private ImageView[] stars = new ImageView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimasi_biaya);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser  = mAuth.getCurrentUser ();

        // Arahkan ke halaman login jika user tidak ada
        if (currentUser  == null) {
            Intent toLogin = new Intent(this, LoginActivity.class);
            toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(toLogin);
            finish();
            return;
        }

        // Show name in title
        getUserName(currentUser.getUid());

        // rating card
        cardRating = findViewById(R.id.cardRating);
        cardRating.setVisibility(View.INVISIBLE);

        etAsal = findViewById(R.id.etAsal);
        etTujuan = findViewById(R.id.etTujuan);
        btnJalur = findViewById(R.id.btnOpenMaps);

        btnJalur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Asal = etAsal.getText().toString();
                String Tujuan = etTujuan.getText().toString();
                DisplayTrack(Asal, Tujuan);
            }
        });

        // stars
        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
        stars[4] = findViewById(R.id.star5);

        for (int i = 0; i < stars.length; i++) {
            final int index = i;
            stars[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fillStar(index);
                }
            });
        }

        Button buttonKirimRating = findViewById(R.id.buttonKirimRating);
        buttonKirimRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OptimasiBiaya.this, "Terima kasih rating Anda!", Toast.LENGTH_SHORT).show();
                cardRating.setVisibility(View.INVISIBLE);
            }
        });

        // logout
        ImageButton btnLogout = findViewById(R.id.buttonLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent toLogin = new Intent(OptimasiBiaya.this, LoginActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toLogin);
                finish();
            }
        });
    }

    private void getUserName(String userId) {
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            TextView judul = findViewById(R.id.tvJudul);
                            judul.setText("Selamat Datang " + name + ", Ke mana Anda ingin pergi?");
                        } else {
                            Log.d("OptimasiBiaya", "No such document");
                        }
                    } else {
                        Log.d("OptimasiBiaya", "get failed with ", task.getException());
                    }
                });
    }

    private void fillStar(int idx) {
        for (int i = 0; i < stars.length; i++) {
            int color = (i <= idx) ? android.R.color.holo_orange_light : android.R.color.secondary_text_dark;
            stars[i].setImageTintList(ColorStateList.valueOf(getResources().getColor(color)));
        }
    }

    private void DisplayTrack(String asal, String tujuan) {
        try {
            // Penarikan API lokasi awal dan tujuan
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + asal + "/" + tujuan);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cardRating.setVisibility(View.VISIBLE);
                }
            }, 2000);
            // Connect API Maps
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}