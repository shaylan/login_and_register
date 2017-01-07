package com.example.vim.loginapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Vim on 05/01/2017.
 */

public class BackgroundTask extends AsyncTask <String,Void,String> {
    String register_url = "http://192.168.0.21/loginapp/register.php"; //Declaring the appropriate URL's
    String login_url = "http://192.168.0.21/loginapp/login.php";
    Context ctx;
    Activity activity;
    AlertDialog.Builder builder;
    ProgressDialog progressDialog;

    public BackgroundTask(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity)ctx;
    }

    @Override
    protected void onPreExecute() {

        builder = new AlertDialog.Builder(activity);
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Connecting to server...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        //This section weill help identify whether its a registration or log in
        String method = params[0];

        if(method.equals("register")) //If it is register then the if statement below will be followed
        {
            try {
                URL url = new URL(register_url); //Creates object of URL, passing the 'reg_url' variable

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //Creates an object of 'HttpURLConnection'
                httpURLConnection.setRequestMethod("POST"); //Sets the parameter. The POST method submits the data to be processed. But must be encoded before sending off the data
                httpURLConnection.setDoOutput(true); //Passes information into the URL (server)
                httpURLConnection.setDoInput(true);

                //This bit writes the information
                OutputStream OS = httpURLConnection.getOutputStream(); //Gets output stream from http url connection, OutputStream allows data to be written
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8")); //Psses output stream object, then passes the encding format of UTF-8
                /* UTF stands for Unicode Transformation Format,the '8' means it uses 8-bit blocks to represent a character. */

                //Need to get the name, email and password the user types in, therefore need to create the variables
                String name = params[1];
                String email = params[2];
                String password = params[3];

                //Below encodes the data
                String data = URLEncoder.encode("name", "UTF-8") +"="+URLEncoder.encode(name, "UTF-8") +"&"+
                        URLEncoder.encode("email", "UTF-8") +"="+URLEncoder.encode(email, "UTF-8") +"&"+
                        URLEncoder.encode("password", "UTF-8") +"="+ URLEncoder.encode(password, "UTF-8");

                bufferedWriter.write(data); //Writes the encoded data above into the buffered writer
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                InputStream IS = httpURLConnection.getInputStream(); //gets server response whether insertion was sucessful or not

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS));
                StringBuilder stringBuilder = new StringBuilder(); //able to read the json object

                //Reads the buffered reader
                String line = "";
                while ((line = bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(line+"\n");
                }

                httpURLConnection.disconnect();
                Thread.sleep(5000);
                return stringBuilder.toString().trim();







            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        else if (method.equals("login"))
        {
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //Creates an object of 'HttpURLConnection'
                httpURLConnection.setRequestMethod("POST"); //Sets the parameter. The POST method submits the data to be processed. But must be encoded before sending off the data
                httpURLConnection.setDoOutput(true); //Passes information into the URL (server)
                httpURLConnection.setDoInput(true);

                //This bit writes the information
                OutputStream OS = httpURLConnection.getOutputStream(); //Gets output stream from http url connection, OutputStream allows data to be written
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8")); //Psses output stream object, then passes the encding format of UTF-8
                /* UTF stands for Unicode Transformation Format,the '8' means it uses 8-bit blocks to represent a character. */

                String email, password;
                email = params[1];
                password = params [2];

                //Below encodes the data
                String data = URLEncoder.encode("email", "UTF-8") +"="+URLEncoder.encode(email, "UTF-8") +"&"+
                        URLEncoder.encode("password", "UTF-8") +"="+ URLEncoder.encode(password, "UTF-8");

                bufferedWriter.write(data); //Writes the encoded data above into the buffered writer
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                //gets response from server which is a json and decodes it, have to get 'code' and 'message' from server

                InputStream IS = httpURLConnection.getInputStream(); //gets server response whether insertion was sucessful or not

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS));
                StringBuilder stringBuilder = new StringBuilder(); //able to read the json object

                //Reads the buffered reader
                String line = "";
                while ((line = bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(line+"\n");
                }

                httpURLConnection.disconnect();
                Thread.sleep(5000);
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }







        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String json) {

        try {
            progressDialog.dismiss();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            JSONObject  JO = jsonArray.getJSONObject(0);

            String code = JO.getString("code");
            String message = JO.getString("message");

            if(code.equals("reg_true"))
            {
                showDialog("Registration Success", message, code);
            }
            else if (code.equals("reg_false"))
            {
               showDialog("Registration Failed", message, code);
            }


            else if(code.equals("login_true"))
            {
                Intent intent = new Intent(activity, HomeActivity.class);
                intent.putExtra("message", message);
                activity.startActivity(intent);
            }

            else if (code.equals("login_false"))
            {
                showDialog("Login Error", message, code);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void showDialog(String title, String message, String code)
    {

        builder.setTitle(title);
        if (code.equals("reg_true")||code.equals("reg_false"))
        {
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                    activity.finish();

                }
            });


        }

        else if(code.equals("login_false"))
        {
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    EditText email,password;
                    email = (EditText) activity.findViewById(R.id.email);
                    password = (EditText) activity.findViewById(R.id.password);

                    email.setText("");
                    password.setText("");
                    dialog.dismiss();

                }
            });


        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}



