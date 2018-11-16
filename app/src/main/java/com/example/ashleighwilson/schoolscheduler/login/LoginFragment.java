package com.example.ashleighwilson.schoolscheduler.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.OverviewActivity;
import com.example.ashleighwilson.schoolscheduler.R;

import java.util.ArrayList;

public class LoginFragment extends Fragment implements OnLoginListener
{
    private static final String TAG = "LoginFragment";

    EditText email;
    EditText password;
    SessionManager session;
    String stringEmail;
    String stringPassword;
    LoginAlertDialog alert = new LoginAlertDialog();
    SharedPreferences preferences;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        rootView.findViewById(R.id.forgot_password).setOnClickListener(v ->
            Toast.makeText(getContext(), "Forgot password clicked", Toast.LENGTH_SHORT).show());

        session = new SessionManager(getActivity());

        preferences = rootView.getContext().getSharedPreferences("SchoolScheduler", Context.MODE_PRIVATE);

        email = rootView.findViewById(R.id.login_email);
        password = rootView.findViewById(R.id.login_password);

        return rootView;
    }

    @Override
    public void login() {
        stringEmail = email.getText().toString().trim();
        stringPassword = password.getText().toString().trim();

        if (stringEmail.length() > 0 && password.length() > 0)
        {
            String email = null;
            String pass = null;

            if (preferences.contains(SessionManager.KEY_EMAIL)) {
                email = preferences.getString(SessionManager.KEY_EMAIL, "");
            }
            if (preferences.contains(SessionManager.KEY_PASS)) {
                pass = preferences.getString(SessionManager.KEY_PASS, "");
            }

            if (stringEmail.equals(email) && stringPassword.equals(pass))
            {
                session.createLoginSession(email, pass);

                Intent intent = new Intent(getActivity(), OverviewActivity.class);
                startActivity(intent);
            }
            else
            {
                alert.showAlertDialog(getContext(), "Login failed..", "Username/Password" +
                        "is incorrect", false);
            }
        }
        else
        {
            alert.showAlertDialog(getContext(), "Login failed..", "Please enter " +
                    "username and password", false);
        }
    }
}
