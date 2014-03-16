package org.soframel.fallingnumbers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.soframel.fallingnumbers.R;

/**
 * User: sophie
 * Date: 5/3/14
 */
public class EndOfGameActivity extends Activity {

    public final static String GAME_RESULT="GAME_RESULT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endofgame);

        Bundle b=this.getIntent().getExtras();
        int result=b.getInt(GAME_RESULT);
        TextView resultView=(TextView) this.findViewById(R.id.result);
        resultView.setText(result+"");
    }

    public void returnToMain(View v){
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //prevent back, user should click button instead
    }
}
