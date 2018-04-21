package com.anshulsg.mypict.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import com.anshulsg.mypict.R;
import com.anshulsg.mypict.service.background.Updater;
import com.anshulsg.mypict.util.Utility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {



    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText userView;
    private EditText passwordView;
    private View mProgressView;
    private View mLoginFormView;
    private SharedPreferences sysKeyVals, userKeyVals;
    private SharedPreferences.Editor modifier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sysKeyVals= getSharedPreferences(Utility.SystemSharedPreferences.FLAGS.toString(), MODE_PRIVATE);
        userKeyVals= getSharedPreferences(Utility.UserSharedPreferences.USER.toString(), MODE_PRIVATE);
        // Set up the login form.
        userView = (EditText)findViewById(R.id.input_user_id);
        userView.setText(userKeyVals.getString(Utility.UserSharedPreferences.U_ID.toString(), ""));

        passwordView = (EditText) findViewById(R.id.input_password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button)findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        userView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = userView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            userView.setError(getString(R.string.error_field_required));
            focusView = userView;
            cancel = true;
        } else if (!isIDValid(email)) {
            userView.setError(getString(R.string.error_invalid_email));
            focusView = userView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isIDValid(String email) {
        //TODO: Replace this with your own logic
        return email.length()==11;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String userId;
        private final String password;

        UserLoginTask(String userId, String password) {
            this.userId = userId;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                Log.d("LoginActivity", userId+":"+password);
                final String obj = "loginid="+userId+"&password="+password+"&dbConnVar=PICT&service_id=";
                HttpURLConnection auth = (HttpURLConnection) Utility.getAuthURL().openConnection();
                auth.setRequestMethod("POST");
                auth.setDoOutput(true);
                new DataOutputStream(auth.getOutputStream()).writeBytes(obj);
                String content= readFrom(auth.getInputStream());
                Document html= Jsoup.parse(content);
                String val=html.selectFirst("title").text();
                Log.d("LoginActivity", val);
                if(val.trim().equalsIgnoreCase("Activities -->Dashboard".trim())){
                    Updater.startSync(LoginActivity.this, userId, password);
                    return true;
                }
                return false;

            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                SharedPreferences.Editor uModifier= userKeyVals.edit();
                SharedPreferences.Editor sModifier= sysKeyVals.edit();
                sModifier.putBoolean(Utility.SystemSharedPreferences.F_LOGIN.toString(), true);
                uModifier.putString(Utility.UserSharedPreferences.U_ID.toString(), userId);
                uModifier.putString(Utility.UserSharedPreferences.U_PASSWORD.toString(), password);
                uModifier.apply();
                sModifier.apply();
                Utility.scheduleChecker(LoginActivity.this);
                Intent intent= new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    public static String readFrom(InputStream is){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String content="";
            while (true) {
                String temp = br.readLine();
                if(temp==null) return content;
                content= content.concat(temp.trim());
            }
        }
        catch (IOException exc){
            Log.d("Updater", "IO Read Error.");
            return null;
        }
    }
}

