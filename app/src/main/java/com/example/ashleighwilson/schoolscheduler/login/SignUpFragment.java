package com.example.ashleighwilson.schoolscheduler.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ashleighwilson.schoolscheduler.OverviewActivity;
import com.example.ashleighwilson.schoolscheduler.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpFragment extends Fragment implements OnSignUpListener
{
    private static final String TAG = SignUpFragment.class.getSimpleName();

    @BindView(R.id.signup_email_ET)
    EditText emailET;
    @BindView(R.id.signup_pass_ET)
    EditText passET;
    @BindView(R.id.signup_confirm_ET)
    EditText confirmET;
    SessionManager session;
    String email, pass, confirm;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    LoginAlertDialog dialog = new LoginAlertDialog();


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, rootView);

        session = new SessionManager(getActivity());
        preferences = rootView.getContext().getSharedPreferences("SchoolScheduler", Context.MODE_PRIVATE);

        editor = preferences.edit();

        return rootView;
    }

    @Override
    public void signUp() {
        email = emailET.getText().toString().trim();
        pass = passET.getText().toString().trim();
        confirm = confirmET.getText().toString().trim();

        if (pass.equals(confirm)) {
            editor.putString(SessionManager.KEY_EMAIL, email);
            editor.putString(SessionManager.KEY_PASS, pass);
            editor.commit();

            session.createLoginSession(pass, email);

            Intent intent = new Intent(getActivity(), OverviewActivity.class);
            startActivity(intent);

        } else {
            dialog.showAlertDialog(getContext(), "Password doesn't match!",
                    "Please confirm password.", false);
        }
    }
}
