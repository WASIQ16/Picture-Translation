package com.example.pictranslation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;



import com.github.dhaval2404.imagepicker.ImagePicker;

import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Locale;

public class Home_screen extends AppCompatActivity {

    ImageButton camera,gallery, share, translates;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView desc;
    Button speak;
    TextToSpeech t1;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView image;
    Button translate,speaker;
    String uriString = "";

    Uri uri;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation);
        gallery = findViewById(R.id.gallery);
        camera = findViewById(R.id.camera);
        image = findViewById(R.id.image);
        translates = findViewById(R.id.translat);
        desc = findViewById(R.id.desc);
        speak = findViewById(R.id.speak);
        share=findViewById(R.id.Share);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(Home_screen.this)
                        .cameraOnly()
                        .crop()
                        .maxResultSize(512,512)
                        .start();

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(Home_screen.this)
                        .galleryOnly()
                        .crop()
                        .maxResultSize(512,512)
                        .start();

            }
        });

        translates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri urii = Uri.parse(uriString);
                extractTextFromUri(getApplicationContext(), urii);

            }
        });

        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR)
                    t1.setLanguage(Locale.US);
            }
        });

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = desc.getText().toString();
                t1.speak(text,TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT,"Picture Translation");
                i.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
                startActivity(Intent.createChooser(i,"Share with"));
            }
        });



        navigationView.bringToFront();
        actionBarDrawerToggle  = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_Open, R.string.menu_Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                int id=item.getItemId();
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id) {
                    case R.id.nav_share:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Picture Translation");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id="
                                + getApplicationContext().getPackageName());
                        startActivity(Intent.createChooser(shareIntent, "Share with"));
                        break;

                    case R.id.nav_rate:
                        // Handle rate option
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.speaktranslate.englishalllanguaguestranslator.ivoicetranslation")));
                            // google play

                        } catch (Exception ex) {
                            startActivity(new
                                    Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.speaktranslate.englishalllanguaguestranslator.ivoicetranslation")));
                                    // website googleplay
                        }
                        break;

                    case R.id.nav_privacy:
                        showTermsDialog();
                        break;

                }

                return true;
            }
        });


    }

    private void extractTextFromUri(Context applicationContext, Uri urii) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        try {
            InputImage image = InputImage.fromFilePath(applicationContext, urii);
            Task<Text> result =
                    recognizer.process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text visionText) {
                                    // Task completed successfully
                                    desc.setText(visionText.getText());
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                        }
                                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         uri = data.getData();
         if (uri!=null)
         {
             try {
                 image.setImageURI(uri);
                 uriString = uri.toString();
             }
             catch (Exception e)
             {
                 e.printStackTrace();
             }
         }
    }

    public void onBackPressed()
    {


            new AlertDialog.Builder(this).setMessage("Are you sure you want to exit?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton("No", null).show();

    }


    private void showTermsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions")
                .setMessage("Terms and Conditions - Picture Translation App\n" +
                        "\n" +
                        "Welcome to the Picture Translation App. Please read these terms and conditions carefully before using the app. By using the app, you agree to be bound by these terms and conditions.\n" +
                        "\n" +
                        "1. App Usage:\n" +
                        "   1.1 The Picture Translation App allows users to translate text in images using their mobile device's camera or by selecting images from their gallery.\n" +
                        "   1.2 The app is provided for personal and non-commercial use only. Any commercial use or distribution of translated content without proper authorization is strictly prohibited.\n" +
                        "\n" +
                        "2. Translation Accuracy:\n" +
                        "   2.1 The Picture Translation App utilizes advanced image processing and optical character recognition (OCR) technologies to perform translations.\n" +
                        "   2.2 While we strive to provide accurate translations, the accuracy of the translations may vary depending on factors such as image quality, text complexity, and language-specific challenges.\n" +
                        "   2.3 The app does not guarantee precise translations and shall not be held liable for any inaccuracies, errors, or misinterpretations in the translated content.\n" +
                        "\n" +
                        "3. User Responsibility:\n" +
                        "   3.1 You are solely responsible for the content you translate using the app.\n" +
                        "   3.2 You shall not use the app to translate content that infringes upon any third-party rights, including but not limited to copyright, trademark, or privacy rights.\n" +
                        "\n")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the Accept button
                        dialog.dismiss();
                        // Perform further actions if needed
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the Decline button
                        dialog.dismiss();
                        // Perform further actions if needed
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
}



}