package com.demo.demoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.demo.demoapp.domain.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.demo.demoapp.Constants.CLIENT_ID;
import static com.demo.demoapp.Constants.GET_OR_CREATE_URL;

public class SignInActivity extends AppCompatActivity {
    private GoogleSignInClient googleSignInClient;
    private SignInButton signInButton;
    private int RC_SIGN_IN = 1;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progress;

    private boolean loginInFlight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Gson gson = new Gson();
        String json = sharedPreferences.getString("User", "");
        User user = gson.fromJson(json, User.class);

        progress = new ProgressDialog(SignInActivity.this);

        if (user!= null && user.getToken() != null  && !user.getToken().isEmpty()) {
            // proceed to next activity
            intentToWelcomeActivityAndFinish();
        } else {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(CLIENT_ID)
                    .requestEmail()
                    .build();

            // Build a GoogleSignInClient with the options specified by gso.
            googleSignInClient = GoogleSignIn.getClient(this, gso);

            signInButton = findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_WIDE);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signInButton.setVisibility(View.INVISIBLE);
                    if (loginInFlight) {
                        return;
                    }

                    progress.setTitle("Log in to continue");
                    progress.setMessage("Let's get you all set up...");
                    // disable dismiss by tapping outside of the dialog
                    progress.setCancelable(false);
                    progress.show();
                    signIn();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void intentToWelcomeActivityAndFinish() {
        Intent nextIntent = new Intent(SignInActivity.this, WelcomeActivity.class);
        //nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        SignInActivity.this.startActivity(nextIntent);
        // finish current activity so it's not on the back stack.
        finish();
    }

    private void signIn() {
        loginInFlight = true;
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            authenticateStudyOne(task);
        }
    }

    private void authenticateStudyOne(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            authenticateStudyOne(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("LandingPageActivity", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(SignInActivity.this, "Sign in failed", Toast.LENGTH_LONG).show();
            progress.dismiss();
            loginInFlight = false;
        }
    }

    private void authenticateStudyOne(final GoogleSignInAccount account) {
        // Instantiate the RequestQueue.
        progress.setTitle("Loading");
        progress.setMessage("Almost there...");
        progress.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", account.getEmail());
            jsonBody.put("password", account.getIdToken());
            final String requestBody = jsonBody.toString();
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_OR_CREATE_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject resp = new JSONObject(response);

                                User user = User.userInstance();
                                user.setEmail(resp.getString("username"));
                                user.setToken(resp.getString("token"));
                                user.setName(account.getDisplayName());
                                user.setPhotoUrl(account.getPhotoUrl().toString());
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                Gson gson = new Gson();
                                editor.putString("User", gson.toJson(user));
                                editor.apply();
                                intentToWelcomeActivityAndFinish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(SignInActivity.this, "Authenticate works!" + "token saved to pref "+
                                    sharedPreferences.getString("token", ""), Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SignInActivity.this, "Authenticate didn't work", Toast.LENGTH_LONG).show();
                    progress.dismiss();
                    loginInFlight = false;
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            progress.dismiss();
            loginInFlight = false;
        }
    }
}
