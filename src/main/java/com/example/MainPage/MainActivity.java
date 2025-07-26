package com.example.spoo;

import static java.lang.Integer.sum;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView diceImage, diceImage2;
    private TextView diceResult;
    private RadioGroup diceOptionGroup;
    private Button rollDiceButton;
    private Random random;
    private MediaPlayer mediaPlayer;
    private int selectedDiceCount = 1; // Default to 1 die

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        diceImage = findViewById(R.id.diceImage);
        diceImage2 = findViewById(R.id.diceImage2);
        diceResult = findViewById(R.id.diceResult);
        diceOptionGroup = findViewById(R.id.diceOptionGroup);
        rollDiceButton = findViewById(R.id.rollDiceButton);

        random = new Random();

        // Initialize MediaPlayer with dice roll sound
        mediaPlayer = MediaPlayer.create(this, R.raw.dice_roll);

        // Handle RadioGroup selection
        diceOptionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.optionOneDie) {
                    selectedDiceCount = 1;
                } else if (checkedId == R.id.optionTwoDice) {
                    selectedDiceCount = 2;
                }
                updateDiceVisibility();
            }
        });

        rollDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateDiceRoll();
            }
        });
    }

    private void updateDiceVisibility() {
        if (selectedDiceCount == 1) {
            diceImage.setVisibility(View.VISIBLE);
            diceImage2.setVisibility(View.GONE);
        } else if (selectedDiceCount == 2) {
            diceImage.setVisibility(View.VISIBLE);
            diceImage2.setVisibility(View.VISIBLE);
        }
    }

    private void animateDiceRoll() {
        // Play dice rolling sound
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }

        diceImage.setImageResource(R.drawable.dice_placeholder);
        if (selectedDiceCount == 2) {
            diceImage2.setImageResource(R.drawable.dice_placeholder);
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        final int[] images = {
                R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3,
                R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6
        };

        Runnable runnable = new Runnable() {
            int rollCount = 0;

            @Override
            public void run() {
                if (rollCount < 10) { // 10 frames of animation
                    int randomImageIndex1 = random.nextInt(6);
                    diceImage.setImageResource(images[randomImageIndex1]);

                    if (selectedDiceCount == 2) {
                        int randomImageIndex2 = random.nextInt(6);
                        diceImage2.setImageResource(images[randomImageIndex2]);
                    }

                    rollCount++;
                    handler.postDelayed(this, 100);
                } else {
                    int finalResult1 = rollDice(6);
                    diceImage.setImageResource(images[finalResult1 - 1]);

                    if (selectedDiceCount == 2) {
                        int finalResult2 = rollDice(6);
                        diceImage2.setImageResource(images[finalResult2 - 1]);
                        diceResult.setText("You rolled a " + sum(finalResult1 , finalResult2) + "!");
                    } else {
                        diceResult.setText("You rolled a " + finalResult1 + "!");
                    }

                    // Stop the dice rolling sound
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                    }
                }
            }
        };

        handler.post(runnable);
    }

    private int rollDice(int sides) {
        return random.nextInt(sides) + 1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
