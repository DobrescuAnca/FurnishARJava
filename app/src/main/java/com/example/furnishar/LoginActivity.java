package com.example.furnishar;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final String EMAIL = "email";
    private static final int GOOGLE_SIGN_IN_REQUEST = 1123;

    private CallbackManager facebookCallbackManager;
    private GoogleApiClient googleApiClient;

    @BindView(R.id.login_facebooklogin)
    RelativeLayout fbkBtn;

    @BindView(R.id.login_googlelogin)
    RelativeLayout googleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        fbkSetup();
        googleSetup();
    }

    void fbkSetup() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFacebookUserCredentials(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
//                showAlertDialog(getString(R.string.login_fail_title), getString(R.string.facebook_login_fail_message));
            }
        });
    }

    void googleSetup() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestEmail()
                .requestProfile()
                .build();
        googleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .enableAutoManage(LoginActivity.this, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void getFacebookUserCredentials(final AccessToken facebookAccessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(facebookAccessToken, (object, response) -> {
            try {
                if (object == null) {
                    Toast.makeText(this, R.string.login_failed, Toast.LENGTH_LONG).show();
                    return;
                }
                final String email = (object.has("email")) ? object.getString("email") : "";
                final String facebookId = object.getString("id");
                final String firstName = object.getString("first_name");
                final String lastName = object.getString("last_name");
                SessionManager.getInstance(LoginActivity.this).setLoginMethod(SessionManager.FACEBOOK);
                SessionManager.getInstance(LoginActivity.this).setUserEmail(email);
                sendLoginRequest(facebookId, email, firstName, lastName);
            } catch (JSONException e) {
                Toast.makeText(this, R.string.login_failed, Toast.LENGTH_LONG).show();
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    private void getGoogleUserCredentials(GoogleSignInResult signInResult) {
        if (signInResult != null) {
            if (signInResult.isSuccess()) {
                try {
                    GoogleSignInAccount signInAccount = signInResult.getSignInAccount();
                    if(signInAccount !=  null) {
                        String email = signInAccount.getEmail();
                        String googleId = signInAccount.getId();
                        String firstName = signInAccount.getGivenName();
                        String lastName = signInAccount.getFamilyName();
                        SessionManager.getInstance(LoginActivity.this).setLoginMethod(SessionManager.GOOGLE);
                        SessionManager.getInstance(LoginActivity.this).setUserEmail(email);
                        sendLoginRequest(googleId, email, firstName, lastName);
                    }
                    } catch (Exception e) {
                        Toast.makeText(this, R.string.login_failed, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, R.string.login_failed, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.login_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void sendLoginRequest(String accountId, String email, String firstName, String lastName) {
        Toast.makeText(this, "email: "+ email, Toast.LENGTH_LONG).show();
        if(!DataModel.getInstance().verifyUser(email))
            DataModel.getInstance().addUser(firstName, email);
        gotoMainActivity();
    }

    @OnClick(R.id.login_facebooklogin)
    public void onFacebookLoginPressed() {
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
    }

    @OnClick(R.id.login_googlelogin)
    public void onGoogleLoginPressed() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("LoginLactivity response", "" + requestCode);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN_REQUEST) {
            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            getGoogleUserCredentials(signInResult);
        }
    }

    public void gotoMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
