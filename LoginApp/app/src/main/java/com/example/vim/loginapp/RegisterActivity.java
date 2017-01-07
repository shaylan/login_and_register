package com.example.vim.loginapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
//get data from registration form
    EditText Name,Email,Pass,ConPass;
    Button reg_button;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Below initialises variables, cast into edit text and fnd it using id method, from xml identifier name
        Name = (EditText)findViewById(R.id.reg_name);
        Email= (EditText)findViewById(R.id.reg_email);
        Pass = (EditText)findViewById(R.id.reg_password);
        ConPass = (EditText)findViewById(R.id.reg_con_password);
        reg_button = (Button)findViewById(R.id.reg_button);
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Name.getText().toString().equals("")||Email.getText().toString().equals("")||Pass.getText().toString().equals(""))
                {
                    builder = new AlertDialog.Builder(RegisterActivity.this); //Initialising created builder
                    builder.setTitle("Something Went Wrong..."); //Attributes
                    builder.setMessage("Please Fill In All Fields"); //Attributes
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { //On click listner for button on the dialog
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                else if(!(Pass.getText().toString().equals(ConPass.getText().toString())))
                {

                    builder = new AlertDialog.Builder(RegisterActivity.this); //Initialising created builder
                    builder.setTitle("Something Went Wrong..."); //Attributes
                    builder.setMessage("Your Passwords are not matching"); //Attributes
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { //On click listner for button on the dialog
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            Pass.setText(""); //Reset password field after user has clicked okay on dialog box
                            ConPass.setText(""); //Reset password field after user has clicked okay on dialog box
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                else

                {
                    //Below passes the information above filled in by a new user to the java class BackgroundTask
                    BackgroundTask backgroundTask = new BackgroundTask(RegisterActivity.this); //Creating an object of BackgroundTask
                    backgroundTask.execute("register",Name.getText().toString(),Email.getText().toString(),Pass.getText().toString()); // Executes the same tasks and pass the information
                }
            }
        });

    }




}
