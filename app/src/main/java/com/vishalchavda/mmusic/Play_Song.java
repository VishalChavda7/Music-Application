package com.vishalchavda.mmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.icu.util.ICUUncheckedIOException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class Play_Song extends AppCompatActivity {
        TextView txtsname,txtsstop,txtsstart;
        ImageView imageView;
        Button fast_rewind,fast_forward,previous,next,play;
        SeekBar seekBar;


        ArrayList<File> mysongs;
        String sname;
        public static final String EXTRA_NAME = "song_name";
        static  MediaPlayer mediaPlayer;
        int position;
        Thread updateseek;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        txtsname = findViewById(R.id.songname);
        txtsstart = findViewById(R.id.currnteTime);
        txtsstop= findViewById(R.id.totaltime);
        fast_rewind = findViewById(R.id.fast_rewind);
        fast_forward = findViewById(R.id.fast_forward);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        play = findViewById(R.id.play);
        seekBar = findViewById(R.id.seekbar);

        imageView = findViewById(R.id.imageView);


        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mysongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songname = intent.getStringExtra("songname");
        position = bundle.getInt("pos",0);
        txtsname.setSelected(true);
        Uri uri = Uri.parse(mysongs.get(position).toString());
        sname = mysongs.get(position).getName();
        txtsname.setText(sname);

        mediaPlayer = MediaPlayer.create(Play_Song.this,uri);
        mediaPlayer.start();

        updateseek = new Thread()
        {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition<totalDuration)
                {
                    try {
                            sleep(500);
                            currentPosition = mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(currentPosition);
                    }catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        String endTime =createTime(mediaPlayer.getDuration());
        txtsstop.setText(endTime);

       final  Handler handler = new Handler();
       final int delay = 1000;

       handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               String currentTime = createTime(mediaPlayer.getCurrentPosition());
               txtsstart.setText(currentTime);
               handler.postDelayed(this,delay);
           }
       },delay);
        seekBar.setMax(mediaPlayer.getDuration());
        updateseek.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.seekbar_colour), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.seekbar_colour),PorterDuff.Mode.SRC_IN);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    play.setBackgroundResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else {
                    play.setBackgroundResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
//        on complete song
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next.performClick();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mysongs.size());
                Uri u = Uri.parse(mysongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(Play_Song.this,u);
                sname = mysongs.get(position).getName().toString();
                txtsname.setText(sname);
                mediaPlayer.start();
                play.setBackgroundResource(R.drawable.pause);
                String endTime =createTime(mediaPlayer.getDuration());
                txtsstop.setText(endTime);
                startAnimation(imageView);
                
            }
        });

        fast_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

        fast_rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?(mysongs.size()-1):(position-1);
                Uri uri1 = Uri.parse(mysongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(Play_Song.this,uri1);
                sname = mysongs.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                play.setBackgroundResource(R.drawable.pause);
                String endTime =createTime(mediaPlayer.getDuration());
                txtsstop.setText(endTime);
                startAnimation(imageView);


            }
        });
    }
    public void startAnimation(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public String createTime(int duration)
    {
        String time = "";

        int min = duration/1000/60;
        int sec = duration/1000%60;

        time+=min+":";
        if (sec<10){
            time+="0";
        }
        time+=sec;
        return time;
    }
}