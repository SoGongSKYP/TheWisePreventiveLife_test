package com.example.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import static android.widget.Toast.*;

public class PageOfToManager extends AppCompatActivity {


    /*Tool Bar 관련 컴포넌트*/
    private Toolbar toolbar;
    private ActionBar actionBar;
    private TextView TitleTextView;

    /*관리자 로그인 관련 컴포넌트*/
    Button toManagerButton;
    Dialog loginDialog;
    private TextInputEditText managerIDEditText, managerPWEditText;
    private Button loginButton, tempButton;
    private ImageButton dismissButton;
    private String managerID, managerPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_manager);

        /*Tool Bar 연결*/
        toolbar = findViewById(R.id.toManager_Toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        TitleTextView = findViewById(R.id.toManager_Title_TextView);
        TitleTextView.setText("사용자 어플리케이션 정보");
        //----------------------------------------------------------------------

        /*관리자 로그인 다이얼로그 연결*/
        loginDialog = new Dialog(PageOfToManager.this);
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loginDialog.setContentView(R.layout.dialog_manager_login);

        managerIDEditText = loginDialog.findViewById(R.id.manager_login_id_EditText);
        managerPWEditText = loginDialog.findViewById(R.id.manager_login_pw_EditText);
        loginButton = loginDialog.findViewById(R.id.manager_login_Button);
        dismissButton = loginDialog.findViewById(R.id.manager_login_dismiss_Button);
        //----------------------------------------------------------------------

        /*다이얼로그 버튼 연결*/
        toManagerButton = findViewById(R.id.switch_to_manager_Button);
        toManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDialogAction();
            }
        });
        //----------------------------------------------------------------------

        /*임시 전환 버튼 (로그인 구현 완료시 삭제)*/
        tempButton = findViewById(R.id.temp_Button);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManagerPages.class);
                startActivity(intent);
            }
        });
        //----------------------------------------------------------------------
    }

    /* 다이얼로그 기능 구현 메소드 */
    private void makeDialogAction() {
        loginDialog.show();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                managerID = managerIDEditText.getText().toString();
                managerPW = managerPWEditText.getText().toString();
                int dbtest = new DBEntity().login(managerID, managerPW);//DB함수 호출하기
                if (dbtest == 1) {
                    makeText(PageOfToManager.this, "로그인", LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), ManagerPages.class);
                    intent.putExtra("managerID", managerID);
                    intent.putExtra("managerPW", managerPW);
                    startActivity(intent);
                    finish();
                } else if (dbtest == 0) {
                    makeText(PageOfToManager.this, "아이디 또는 비밀번호가 틀렸음", LENGTH_SHORT).show();
                    managerIDEditText.setText("");
                    managerPWEditText.setText("");
                } else if (dbtest == -1) {
                    Toast.makeText(PageOfToManager.this, "존재하지 않는 아이디", Toast.LENGTH_SHORT).show();
                    managerIDEditText.setText("");
                    managerPWEditText.setText("");
                }else{
                    Toast.makeText(PageOfToManager.this, "서버 오류", Toast.LENGTH_SHORT).show();
                    managerIDEditText.setText("");
                    managerPWEditText.setText("");
                }
                loginDialog.dismiss();
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginDialog.dismiss();
            }
        });
    }
}
