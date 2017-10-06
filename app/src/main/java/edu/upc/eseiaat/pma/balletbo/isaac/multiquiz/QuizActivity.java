package edu.upc.eseiaat.pma.balletbo.isaac.multiquiz;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    public static final String CORRECT_ANSWER = "correct_answer";
    public static final String CURRECT_QUESTION = "currect_question";
    public static final String ANSWER_IS_CORRECT = "answer_is_correct";
    public static final String ANSWER = "answer";
    private int ids_answers[] = {
            R.id.answer1, R.id.answer2,R.id.answer3, R.id.answer4
    };
    private String[] all_questions;

    private TextView text_question;
    private RadioGroup group;
    private Button btn_next, btn_prev;

    //Resposta correcta
    private int correct_answer;
    //Resposta actual
    private int current_question;
    //Quines respostes són correctes de les que s'han contestat
    private boolean[] answer_is_correct;
    //Quines s'han contestat
    private int[] answer;

    //Mètode que guarda tot el que volem per quan es reinicii la app o es giri la pantalla
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("lifecycle","onSaveInstanceState");
        super.onSaveInstanceState(outState);

        outState.putInt(CORRECT_ANSWER, correct_answer);
        outState.putInt(CURRECT_QUESTION, current_question);
        outState.putBooleanArray(ANSWER_IS_CORRECT, answer_is_correct);
        outState.putIntArray(ANSWER, answer);
    }

    //Quan se surt de l'app
    @Override
    protected void onStop() {
        Log.i("lifecycle","onStop");
        super.onStop();
    }
    //Quan s'obre
    @Override
    protected void onStart() {
        Log.i("lifecycle","onStart");
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.i("lifecycle","onDestroy");
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("lifecycle","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        text_question = (TextView) findViewById(R.id.text_question);
        group = (RadioGroup) findViewById(R.id.answer_group);
        btn_next = (Button) findViewById(R.id.btn_check);
        btn_prev = (Button) findViewById(R.id.btn_prev);

        all_questions = getResources().getStringArray(R.array.all_questions);

        if(savedInstanceState == null){
            startOver();
        }else{
            Bundle state = savedInstanceState;
            correct_answer = state.getInt(CORRECT_ANSWER);
            current_question = state.getInt(CURRECT_QUESTION);
            answer_is_correct = state.getBooleanArray(ANSWER_IS_CORRECT);
            answer = state.getIntArray(ANSWER);
            showQuestion();
        }

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();

                if(current_question < all_questions.length-1){
                    current_question ++;
                    showQuestion();
                }
                else{
                    checkResults();
                }
            }
        });
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
                if(current_question >0){
                    current_question--;
                    showQuestion();
                }
            }
        });
    }

    private void startOver() {
        answer_is_correct = new boolean[all_questions.length];
        answer = new int[all_questions.length];
        for(int i=0;i<answer.length;i++){
            answer[i]=-1;
        }
        current_question = 0;
        showQuestion();
    }

    private void checkResults() {
        int correctas = 0, incorrectas = 0, nocontestadas = 0;
        //Es busca com son les respostes
        for(int i = 0; i< all_questions.length; i++){
            if(answer_is_correct[i]) correctas++;
            else if (answer[i] == -1) nocontestadas++;
            else incorrectas++;
        }
        //Missatge que es mostrara al quadre de diàleg final, la \n salta a la línia de abaix
        String message =
                String.format("Correctas: %d\nIncorrectas: %d\nNo Contestadas: %d\n",
                        correctas, incorrectas, nocontestadas);

        //Creador de quadre de diàleg
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.results);
        builder.setMessage(message);
        //Pq no es pugui tornar enrere quan surti el quadre de diàleg
        builder.setCancelable(false);
        //Acabar amb l'aplicació
        builder.setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        //Borrar respostes i tornar a la pregunta 0
        builder.setNegativeButton(R.string.start_over, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startOver();
            }
        });
        //Es crea el quadre de diàleg i es mostra
        builder.create().show();

    }

    private void checkAnswer() {
        Log.i("isaac","btn_check");
        int id = group.getCheckedRadioButtonId();
        int ans = -1;
        for (int i = 0; i < ids_answers.length; i++) {
            if (ids_answers[i] == id) {
                ans = i;
            }
        }

        answer_is_correct[current_question] = (ans == correct_answer);
        answer[current_question] = ans;
    }


    private void showQuestion() {
        String q = all_questions[current_question];
        String[] parts = q.split(";");


        group.clearCheck();

        text_question.setText(parts[0]);

        for (int i = 0; i<ids_answers.length; i++){
            RadioButton rb = (RadioButton) findViewById(ids_answers[i]);
            String ans = parts[i+1];
            if (ans.charAt(0)=='*'){
                correct_answer = i;
                ans = ans.substring(i);
            }
            rb.setText(ans);
            if(answer[current_question] == i){
                rb.setChecked(true);
            }
        }
        if(current_question == 0){
            btn_prev.setVisibility(View.GONE);
        }else{
            btn_prev.setVisibility(View.VISIBLE);
        }
        if(current_question == all_questions.length-1){
            btn_next.setText(R.string.finish);
        }else{
            btn_next.setText(R.string.next);
        }


    }
}
