package com.example.sosapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddContacts extends AppCompatActivity {

    EditText editText1;
    EditText editText2;
    EditText editText3;


    Button submitBotton;
    public static final String MyPREFERENCES = "PhoneNumber" ;
    public static final String phonenumber1 = "First";
    public static final String phonenumber2 = "Second";
    public static final String phonenumber3= "Third";


    public String first;
    public String second;
    public String third;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        submitBotton=(Button)findViewById(R.id.buttonSubmit);
        editText1=(EditText)findViewById(R.id.editText1);
        editText2=(EditText)findViewById(R.id.editText2);
        editText3=(EditText)findViewById(R.id.editText3);
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        submitBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                first=editText1.getText().toString();
                second=editText2.getText().toString();
                third=editText3.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(phonenumber1,first);
                editor.putString(phonenumber2,second);
                editor.putString(phonenumber3,third);
                editor.commit();
                Toast.makeText(getApplicationContext(),"submit succesful",Toast.LENGTH_SHORT).show();
            }
        });


    }
}