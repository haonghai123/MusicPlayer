package com.example.soc_macmini_15.musicplayer.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soc_macmini_15.musicplayer.Adapter.TestAdapter;
import com.example.soc_macmini_15.musicplayer.Adapter.ViewPagerAdapter;
import com.example.soc_macmini_15.musicplayer.Fragments.AllSongFragment;
import com.example.soc_macmini_15.musicplayer.Fragments.CurrentSongFragment;
import com.example.soc_macmini_15.musicplayer.Fragments.FavSongFragment;
import com.example.soc_macmini_15.musicplayer.Fragments.PlayListFragment;
import com.example.soc_macmini_15.musicplayer.Interfaces.OnBackPressedListener;
import com.example.soc_macmini_15.musicplayer.Model.PlayList;
import com.example.soc_macmini_15.musicplayer.Model.Song;
import com.example.soc_macmini_15.musicplayer.R;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AllSongFragment.createDataParse, FavSongFragment.createDataParsed, CurrentSongFragment.createDataParsed, PlayListFragment.createDataParse, PlayListFragment.createDataParsed {

    private Menu menu;

    private ImageButton imgBtnPlayPause, imgbtnReplay, imgBtnPrev, imgBtnNext, imgBtnSetting;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SeekBar seekbarController;
    private DrawerLayout mDrawerLayout;
    private TextView tvCurrentTime, tvTotalTime;

    private ArrayList<Song> songList;
    private int currentPosition;
    private String searchText = "";
    private Song currSong;

    private static boolean checkFlag = false, repeatFlag = false, playContinueFlag = false, playlistFlag = false;
    private final int MY_PERMISSION_REQUEST = 100;
    private int allSongLength;

    public MediaPlayer mediaPlayer;
    Handler handler;
    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        init();
        setPagerLayout();

    }

    /**
     * Initialising the views
     */

    private void init() {
        imgBtnPrev = findViewById(R.id.img_btn_previous);
        imgBtnNext = findViewById(R.id.img_btn_next);
        imgbtnReplay = findViewById(R.id.img_btn_replay);
        imgBtnSetting = findViewById(R.id.img_btn_setting);

        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        FloatingActionButton refreshSongs = findViewById(R.id.btn_refresh);
        seekbarController = findViewById(R.id.seekbar_controller);
        viewPager = findViewById(R.id.songs_viewpager);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        imgBtnPlayPause = findViewById(R.id.img_btn_play);
        Toolbar toolbar = findViewById(R.id.toolbar);
        handler = new Handler();
        mediaPlayer = new MediaPlayer();

        toolbar.setTitleTextColor(getResources().getColor(R.color.text_color));
        setSupportActionBar(toolbar);



        imgBtnNext.setOnClickListener(this);
        imgBtnPrev.setOnClickListener(this);
        imgbtnReplay.setOnClickListener(this);
        refreshSongs.setOnClickListener(this);
        imgBtnPlayPause.setOnClickListener(this);
        imgBtnSetting.setOnClickListener(this);

    }

    /**
     * Setting up the tab layout with the viewpager in it.
     */

    private void setPagerLayout() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getContentResolver());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    /**
     * Function to show the dialog for about us.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                int pos=tabLayout.getSelectedTabPosition();
                queryText();
                setPagerLayout();
                viewPager.setCurrentItem(pos);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
            case R.id.menu_search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_favorites:
                if (checkFlag)
                    if (mediaPlayer != null) {
                        TestAdapter testAdapter = new TestAdapter(this);
                        if (!songList.get(currentPosition).isFav()) {
                            item.setIcon(R.drawable.ic_favorite_filled);
                            testAdapter.updateFav(songList.get(currentPosition).getTitle(),
                                    songList.get(currentPosition).getSubTitle(), songList.get(currentPosition).getPath(),!songList.get(currentPosition).isFav());
                            Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                            int pos=tabLayout.getSelectedTabPosition();
                            setPagerLayout();
                            viewPager.setCurrentItem(pos);
                        }
                        else {
                            item.setIcon(R.drawable.favorite_icon);
                            if(queryText()==""){
                                testAdapter.updateFav(songList.get(currentPosition).getTitle(),
                                        songList.get(currentPosition).getSubTitle(), songList.get(currentPosition).getPath(),!songList.get(currentPosition).isFav());

                            }
                            else{
                                testAdapter.updateFav(songList.get(currentPosition).getTitle(),
                                        songList.get(currentPosition).getSubTitle(), songList.get(currentPosition).getPath(),!songList.get(currentPosition).isFav());

                            }
                            Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                            int pos=tabLayout.getSelectedTabPosition();
                            setPagerLayout();
                            viewPager.setCurrentItem(pos);
                        }
                    }
                return true;
        }

        return super.onOptionsItemSelected(item);

    }


    /**
     * Function to handle the click events.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_play:
                if (checkFlag) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        imgBtnPlayPause.setImageResource(R.drawable.play_icon);
                    } else if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        imgBtnPlayPause.setImageResource(R.drawable.pause_icon);
                        playCycle();
                    }
                } else {
                    Toast.makeText(this, "Select the Song ..", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_refresh:
                Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show();
                setPagerLayout();
                break;
            case R.id.img_btn_replay:

                if (repeatFlag) {
                    Toast.makeText(this, "Replaying Removed..", Toast.LENGTH_SHORT).show();
                    mediaPlayer.setLooping(false);
                    imgbtnReplay.setImageResource(R.drawable.undo_icon);
                    repeatFlag = false;
                } else {
                    Toast.makeText(this, "Replaying Added..", Toast.LENGTH_SHORT).show();
                    mediaPlayer.setLooping(true);
                    imgbtnReplay.setImageResource(R.drawable.undo_icon_black);
                    repeatFlag = true;
                }
                break;
            case R.id.img_btn_previous:
                if (checkFlag) {
                    if (mediaPlayer.getCurrentPosition() > 10) {
                        if (currentPosition - 1 > -1) {
                            attachMusic(songList.get(currentPosition - 1).getTitle(), songList.get(currentPosition - 1).getSubTitle(), songList.get(currentPosition - 1).getPath(), songList.get(currentPosition - 1).isFav());
                            currentPosition = currentPosition - 1;
                        } else {
                            attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getSubTitle(), songList.get(currentPosition).getPath(), songList.get(currentPosition).isFav());
                        }
                    } else {
                        attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getSubTitle(), songList.get(currentPosition).getPath(), songList.get(currentPosition).isFav());
                    }
                } else {
                    Toast.makeText(this, "Select a Song . .", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_btn_next:
                if (checkFlag) {
                    if (currentPosition + 1 < songList.size()) {
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getSubTitle(), songList.get(currentPosition + 1).getPath(), songList.get(currentPosition + 1).isFav());
                        currentPosition += 1;
                    } else {
                        Toast.makeText(this, "Playlist Ended", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Select the Song ..", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_btn_setting:
                if (!playContinueFlag) {
                    imgBtnSetting.setImageResource(R.drawable.ic_settings_black);
                    playContinueFlag = true;
                    Toast.makeText(this, "Loop Added", Toast.LENGTH_SHORT).show();
                } else {
                    imgBtnSetting.setImageResource(R.drawable.ic_settings);
                    playContinueFlag = false;
                    Toast.makeText(this, "Loop Removed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Function to attach the song to the music player
     *
     * @param title
     * @param path
     */

    private void attachMusic(String title, String subTitle, String path, boolean isFav) {
        imgBtnPlayPause.setImageResource(R.drawable.play_icon);
        setTitle(title);
        if(!isFav)
        {
            menu.getItem(1).setIcon(R.drawable.favorite_icon);
        }
        else{
            menu.getItem(1).setIcon(R.drawable.ic_favorite_filled);
        }

        try {
            mediaPlayer.reset();
            URL url = new URL(path);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();
            mediaPlayer.setDataSource(String.valueOf(url));
            mediaPlayer.prepare();
            setControls();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imgBtnPlayPause.setImageResource(R.drawable.play_icon);
                if (playContinueFlag) {
                    if (currentPosition + 1 < songList.size()) {
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getSubTitle(),songList.get(currentPosition + 1).getPath(),songList.get(currentPosition + 1).isFav());
                        currentPosition += 1;
                    } else {
                        Toast.makeText(MainActivity.this, "PlayList Ended", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Function to set the controls according to the song
     */

    private void setControls() {
        seekbarController.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
        playCycle();
        checkFlag = true;
        if (mediaPlayer.isPlaying()) {
            imgBtnPlayPause.setImageResource(R.drawable.pause_icon);
            tvTotalTime.setText(getTimeFormatted(mediaPlayer.getDuration()));
        }

        seekbarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(getTimeFormatted(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * Function to play the song using a thread
     */
    private void playCycle() {
        try {
            seekbarController.setProgress(mediaPlayer.getCurrentPosition());
            tvCurrentTime.setText(getTimeFormatted(mediaPlayer.getCurrentPosition()));
            if (mediaPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        playCycle();

                    }
                };
                handler.postDelayed(runnable, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTimeFormatted(long milliSeconds) {
        String finalTimerString = "";
        String secondsString;

        //Converting total duration into time
        int hours = (int) (milliSeconds / 3600000);
        int minutes = (int) (milliSeconds % 3600000) / 60000;
        int seconds = (int) ((milliSeconds % 3600000) % 60000 / 1000);

        // Adding hours if any
        if (hours > 0)
            finalTimerString = hours + ":";

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // Return timer String;
        return finalTimerString;
    }


    /**
     * Function Overrided to receive the data from the fragment
     *
     * @param title
     * @param path
     */

    @Override
    public void onDataPass(String title, String subTitle, String path, boolean isFav) {
        Toast.makeText(this, title, Toast.LENGTH_LONG).show();
        attachMusic(title, subTitle, path, isFav);
    }

    @Override
    public void getLength(int length) {
        this.allSongLength = length;
    }

    @Override
    public void fullSongList(ArrayList<Song> songList, int position) {
        this.songList = songList;
        this.currentPosition = position;
        this.playlistFlag = songList.size() == allSongLength;
        this.playContinueFlag = !playlistFlag;
    }
    public void onClickFirstFragButton(int index){
        viewPager.setCurrentItem(index);
    }
    @Override
    public void onDataPass(String name, int size) {
        Toast.makeText(this, name, Toast.LENGTH_LONG).show();
        //onClickFirstFragButton(3);
    }

    @Override
    public void fullPlayList(ArrayList<PlayList> songList, int position) {

    }

    @Override
    public String queryText() {
        return searchText.toLowerCase();
    }

    @Override
    public void currentSong(PlayList playList) {

    }

    @Override
    public Song getSong() {
        currentPosition = -1;
        return currSong;
    }

    @Override
    public boolean getPlaylistFlag() {
        return playlistFlag;
    }

    @Override
    public void currentSong(Song song) {
        this.currSong = song;
    }

    @Override
    public int getPosition() {
        return currentPosition;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected OnBackPressedListener onBackPressedListener;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);


        onBackPressedListener = null;
    }




    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
        else
            super.onBackPressed();
    }


}
