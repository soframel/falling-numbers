package org.soframel.fallingnumbers;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.soframel.fallingnumbers.characteristics.Difficulty;
import org.soframel.fallingnumbers.characteristics.GameSettings;
import org.soframel.fallingnumbers.characteristics.Operation;
import org.soframel.fallingnumbers.media.SoundMediaPlayer;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: sophie
 * Date: 1/3/14
 */
public class PlayActivity extends Activity {
    public final static String TAG="PlayActivity";
    public final static String GAME_CHARACTERISTICS="characteristics";

    private final static int POINTS_PROBLEM_SOLVED=1;

    private final static int SUCCESS_COLOR=Color.parseColor("#46FF21");
    private final static int FAILURE_COLOR=Color.parseColor("#FF1909");

    private final static int NUMBERS_WIDTH_DP=50;
    private static int NUMBERS_WIDTH_PX;

    public PlayActivity() {
    }

    //game data
    private SoundMediaPlayer soundPlayer;
    private GameSettings settings;
    private int currentResult;
    private List<Button> displayedNumbers;
    private int fallLength=100;
    private int currentTimeLeft;
    private int result;
    private int currentNumber;
    private int points;
    private SimpleDateFormat timeFormat=new SimpleDateFormat("mm:ss");

    //configured from settings
    private int delayBetweenMoves;
    private int delayBetweenNumbers;
    private int randomLimit;
    private int randomLimitForResult;
    private int totalTime;
    private int successAdditionalTime;
    private int failureRemovedTime;

    //timers
    private Timer fallingTimer;
    private Timer appearingTimer;
    private Timer secondsTimer;

    //Views
    private RelativeLayout playingLayout;
    private TextView resultView;
    private TextView timeView;
    private TextView currentNumberView;
    private TextView pointsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play);
        displayedNumbers=new ArrayList<Button>();

        Bundle b=this.getIntent().getExtras();
        String settingsS=b.getString(GAME_CHARACTERISTICS);
        settings=GameSettings.parseSettings(settingsS);
        this.configureFromSettings();

        //find numbers size, depending on display density
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dpi=metrics.densityDpi;
        NUMBERS_WIDTH_PX=NUMBERS_WIDTH_DP*dpi/160;

        //find views
        playingLayout =(RelativeLayout) this.findViewById(R.id.playingLayout);
        resultView=(TextView) this.findViewById(R.id.targetNumber);
        timeView=(TextView) this.findViewById(R.id.timeLeft);
        currentNumberView =(TextView) this.findViewById(R.id.currentNumber);
        pointsView=(TextView) this.findViewById(R.id.points);

        //load sounds
        soundPlayer=new SoundMediaPlayer(this);
        soundPlayer.loadFile(R.raw.failure);
        soundPlayer.loadFile(R.raw.success);
        soundPlayer.loadFile(R.raw.end);
    }

    private void configureFromSettings(){

        if(settings.getDifficulty()== Difficulty.veryEasy){
            randomLimitForResult=20;
            randomLimit=10;
            delayBetweenMoves=2000;
            delayBetweenNumbers=2000;
            totalTime=60;
            successAdditionalTime=20;
            failureRemovedTime=5;
        }
        else if(settings.getDifficulty()== Difficulty.easy){
            randomLimitForResult=35;
            randomLimit=17;
            delayBetweenMoves=1500;
            delayBetweenNumbers=1000;
            totalTime=60;
            successAdditionalTime=18;
            failureRemovedTime=5;
        }
        else if(settings.getDifficulty()== Difficulty.medium){
            randomLimitForResult=50;
            randomLimit=25;
            delayBetweenMoves=1000;
            delayBetweenNumbers=1000;
            totalTime=60;
            successAdditionalTime=18;
            failureRemovedTime=5;
        }
        else if(settings.getDifficulty()== Difficulty.hard){
            randomLimitForResult=100;
            randomLimit=50;
            delayBetweenMoves=500;
            delayBetweenNumbers=1000;
            totalTime=60;
            successAdditionalTime=16;
            failureRemovedTime=5;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        points=0;
        this.updatePointsView();
        currentTimeLeft=totalTime;
        this.newProblem();
    }

    @Override
    protected void onStop() {
        stopTimers();

        super.onStop();
    }

    protected void stopTimers(){
        fallingTimer.cancel();
        appearingTimer.cancel();
        secondsTimer.cancel();
    }

    private void newProblem(){
        result=this.generateInt(randomLimitForResult/2, randomLimitForResult, null, null);
        resultView.setText(Integer.toString(result));

        currentNumber=0;
        if(this.settings.getOperation()== Operation.Minus)
            currentNumber=result;
        this.updateCurrentNumberView();

        this.updateTimeLeftView();

        this.startPlaying();
    }

    protected void updateTimeLeftView(){
        timeView.setText(this.getFormattedTime(currentTimeLeft));
    }
    protected void updateCurrentNumberView(){
        currentNumberView.setText(Integer.toString(currentNumber));
    }
    protected void updatePointsView(){
        pointsView.setText(points+" "+this.getString(R.string.points_suffix));
    }

    private String getFormattedTime(int totalSeconds){
        Date d=new Date(totalSeconds*1000);
        return timeFormat.format(d);
    }

    private void startPlaying(){
        appearingTimer=new Timer("appearingTimer");
        MakeNumbersAppear appearJob=new MakeNumbersAppear();
        appearingTimer.schedule(appearJob, 0, delayBetweenNumbers);

        fallingTimer=new Timer("fallingTimer");
        MakeNumbersFall fallJob=new MakeNumbersFall();
        fallingTimer.schedule(fallJob, 0, delayBetweenMoves);

        secondsTimer=new Timer("secondsTimer");
        DecreaseSeconds timerJob=new DecreaseSeconds();
        secondsTimer.schedule(timerJob, 0, 1000);
    }


    private int generateInt(int min, int max, Integer minNotBetween, Integer maxNotBetween){
        Random r=new Random();
        int result=-1;
        do{
           result=r.nextInt(max);
        }while(result<min
                || (minNotBetween!=null && maxNotBetween!=null && result>minNotBetween && result<maxNotBetween));

        return result;
    }

    public void numberClicked(View v){
        //change button
        Button b=(Button) v;
        b.setClickable(false);
        b.setBackgroundResource(R.drawable.numberusedbg);

        //get number
        String numberS=b.getText().toString();
        int number=Integer.parseInt(numberS);

        if(settings.getOperation()==Operation.Plus){
            currentNumber=currentNumber+number;
            this.updateCurrentNumberView();
            if(currentNumber==result){
                this.problemSolved();
            }
            else if( currentNumber>result){
                this.problemFailed();
            }
        }
        else if(settings.getOperation()==Operation.Minus){
            currentNumber=currentNumber-number;
            this.updateCurrentNumberView();
            if(currentNumber==0){
                this.problemSolved();
            }
            else if( currentNumber<0){
                this.problemFailed();
            }
        }

    }

    private void problemSolved(){
        points=points+POINTS_PROBLEM_SOLVED;
        this.updatePointsView();

        currentTimeLeft=currentTimeLeft+successAdditionalTime;
        this.updateTimeLeftView();

        this.stopTimers();

        //play success sound
        soundPlayer.playFile(R.raw.success);

        //make currentNumberView green
        this.changeCurrentNumberColorAndContinue(SUCCESS_COLOR);
    }
    private void problemFailed(){
        currentTimeLeft=currentTimeLeft-failureRemovedTime;
        this.updateTimeLeftView();

        this.stopTimers();

        //play failed sound
        soundPlayer.playFile(R.raw.failure);

        //make currentNumberView red
        this.changeCurrentNumberColorAndContinue(FAILURE_COLOR);
    }

    private void changeCurrentNumberColorAndContinue(int color){
        final int previousColor=((ColorDrawable)currentNumberView.getBackground()).getColor();
        currentNumberView.setBackgroundColor(color);
        currentNumberView.requestLayout();

        //wait 1 second
        currentNumberView.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentNumberView.setBackgroundColor(previousColor);
                currentNumberView.requestLayout();

                //continue with new problem
                newProblem();
            }
        }, 1000);

    }

    protected void gameIsFinished(){
        stopTimers();

        //play end music
        soundPlayer.playFile(R.raw.end);

        //show a results screen
        Intent intent=new Intent(this, EndOfGameActivity.class);
        Bundle b=new Bundle();
        b.putInt(EndOfGameActivity.GAME_RESULT, points);
        intent.putExtras(b);
        startActivity(intent);

    }

    protected void makeNewNumber(){
        int number=generateInt(1, randomLimit, null, null);

        Button button= (Button) getLayoutInflater().inflate(R.layout.number, null);
        button.setText(Integer.toString(number));

        //get last button position:
        Integer previousX=null;
        if(!displayedNumbers.isEmpty()){
            Button last=displayedNumbers.get(displayedNumbers.size()-1);
            previousX=new Float(last.getX()).intValue();
        }
        //find Range: width of screen
        int width=playingLayout.getWidth();
        if(width<=0){
            //when not yet layed out
            width=this.getWindowManager().getDefaultDisplay().getWidth()-10;
        }

        int x=0;
        if(previousX!=null){
            x=this.generateInt(0, width-NUMBERS_WIDTH_PX, previousX-NUMBERS_WIDTH_PX, previousX+NUMBERS_WIDTH_PX);
        }
        else{
            x=this.generateInt(0, width - NUMBERS_WIDTH_PX, null, null);
        }

        button.setX(x);
        button.setY(0f);

        playingLayout.addView(button);
        displayedNumbers.add(button);

    }

    protected void moveAllNumbers(){
        for(Button v: displayedNumbers){
            //animate
            float y=v.getY();
            ObjectAnimator animator=ObjectAnimator.ofFloat(v, "y", y, y+fallLength);
            animator.setDuration(delayBetweenMoves);
            animator.setInterpolator(null);
            animator.start();

            //remove it from list if outside screen
            int layoutHeight= playingLayout.getLayoutParams().height;
            float bottomOfView=v.getY()+v.getLayoutParams().height;
            if(layoutHeight>0 && bottomOfView>=layoutHeight){
                playingLayout.removeView(v);
                displayedNumbers.remove(v);
            }
        }
    }
    protected void decreaseSeconds(){
        currentTimeLeft=currentTimeLeft-1;
        updateTimeLeftView();

        if(currentTimeLeft<=0){
            gameIsFinished();
        }
    }

    //// Timers

    class MakeNumbersFall extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    moveAllNumbers();
                }
            });
        }
    }

    class MakeNumbersAppear extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeNewNumber();
                }
            });
        }
    }

    class DecreaseSeconds extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    decreaseSeconds();
                }
            });
        }
    }
}
