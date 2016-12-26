package app.com.biodiv.speciestraits;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoadRestWebServiceTask.Listener, LoadImageTask.Listener {

    private static final int Q1_ANSWER = R.id.question_1_first_radio;
    private static final int Q2_ANSWER = R.id.question_2_second_radio;
    private static final String Q4_ANSWER_MIN = "35";
    private static final String Q4_ANSWER_MAX = "38";
    private static final int Q5_ANSWER = R.id.question_5_first_radio;

    private ImageView mImageView;
    private Button mBtLoadImage;

    public static final String serverURL = "http://indiabiodiversity.org/api/species/257493?format=json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new LoadRestWebServiceTask(this, MainActivity.this).execute(serverURL);

        mImageView = (ImageView) findViewById(R.id.species_image);
 //       mBtLoadImage = (Button) findViewById(R.id.btn_load_image);
 //       mBtLoadImage.setOnClickListener(this);
    }

    @Override
    public void onDataLoaded(JSONObject json) {
        System.out.println(json);
        String imageUrl = "";
        try {
            imageUrl = json.getJSONObject("instance").getJSONArray("resource").getJSONObject(0).getString("icon");

        if(imageUrl != null && imageUrl !="") {
            new LoadImageTask(this).execute(imageUrl);
//            ((TextView) findViewById(R.id.species_title)).setText(json.getJSONObject("instance").getString("title").replaceAll("</?i>",""));
//            ((TextView) findViewById(R.id.species_description)).setText(json.getJSONObject("instance").getString("summary").replaceAll("</?.*>",""));
        }
        } catch(JSONException e) {
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void onImageLoaded(Bitmap image) {
        mImageView.setImageBitmap(image);
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error loading data !", Toast.LENGTH_SHORT).show();
    }

    public void checkQuiz(View v) {
        ArrayList<String> incorrectAnswersList = new ArrayList<String>();

        int numberOfQuestionsCorrect = 0;

        if( checkQuestion1() ){
            numberOfQuestionsCorrect++;
        } else {
            incorrectAnswersList.add("Question 1");
        }

        if( checkQuestion2() ){
            numberOfQuestionsCorrect++;
        } else {
            incorrectAnswersList.add("Question 2");
        }

        if( checkQuestion3() ){
            numberOfQuestionsCorrect++;
        } else {
            incorrectAnswersList.add("Question 3");
        }

        if( checkQuestion4() ){
            numberOfQuestionsCorrect++;
        } else {
            incorrectAnswersList.add("Question 4");
        }

        if( checkQuestion5() ){
            numberOfQuestionsCorrect++;
        } else {
            incorrectAnswersList.add("Question 5");
        }

        StringBuilder sb = new StringBuilder();
        for (String s : incorrectAnswersList)
        {
            sb.append(s);
            sb.append("\n");
        }

        Context context = getApplicationContext();
        CharSequence text = "You got " + numberOfQuestionsCorrect + "/5 answers right.\n\nRecheck the following:\n" + sb.toString();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private boolean checkQuestion1() {
        RadioGroup rg = (RadioGroup) findViewById(R.id.question_1_radio_group);

        if( rg.getCheckedRadioButtonId() == Q1_ANSWER ) {
            return true;
        }

        return false;
    }

    private boolean checkQuestion2() {
        RadioGroup rg = (RadioGroup) findViewById(R.id.question_2_radio_group);

        if( rg.getCheckedRadioButtonId() == Q2_ANSWER ) {
            return true;
        }

        return false;
    }

    private boolean checkQuestion3() {
        CheckBox c1 = (CheckBox) findViewById(R.id.question_3_1_checkbox);
        CheckBox c2 = (CheckBox) findViewById(R.id.question_3_2_checkbox);
        CheckBox c3 = (CheckBox) findViewById(R.id.question_3_3_checkbox);
        CheckBox c4 = (CheckBox) findViewById(R.id.question_3_4_checkbox);

        if (!c1.isChecked() && c2.isChecked() && c3.isChecked() && !c4.isChecked()) {
            return true;
        }

        return false;
    }

    private boolean checkQuestion4() {
        EditText et_min = (EditText)findViewById(R.id.question_4_edit_text_min);
        EditText et_max = (EditText)findViewById(R.id.question_4_edit_text_max);

        return et_min.getText().toString().equalsIgnoreCase(Q4_ANSWER_MIN) && et_max.getText().toString().equalsIgnoreCase(Q4_ANSWER_MAX);
    }

    private boolean checkQuestion5() {
        RadioGroup rg = (RadioGroup) findViewById(R.id.question_5_radio_group);

        if( rg.getCheckedRadioButtonId() == Q5_ANSWER ) {
            return true;
        }

        return false;
    }

}
