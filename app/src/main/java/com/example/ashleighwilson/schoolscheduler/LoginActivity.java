package com.example.ashleighwilson.schoolscheduler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.ashleighwilson.schoolscheduler.databinding.MainLoginBinding;
import com.example.ashleighwilson.schoolscheduler.login.LoginFragment;
import com.example.ashleighwilson.schoolscheduler.login.SignUpFragment;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.ashleighwilson.schoolscheduler.login.FlexibleFrameLayout.ORDER_LOGIN_STATE;
import static com.example.ashleighwilson.schoolscheduler.login.FlexibleFrameLayout.ORDER_SIGN_UP_STATE;


public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private MainLoginBinding binding;
    private boolean isLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_login);

        LoginFragment topLoginFragment = new LoginFragment();
        SignUpFragment topSignUpFragment = new SignUpFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sign_up_fragment, topSignUpFragment)
                .replace(R.id.login_fragment, topLoginFragment)
                .commit();

        binding.loginFragment.setRotation(-90);

        binding.loginButton.setOnSignUpListener(topSignUpFragment);
        binding.loginButton.setOnLoginListener(topLoginFragment);

        binding.loginButton.setOnButtonSwitched(isLogin -> {
            binding.getRoot()
                    .setBackgroundColor(ContextCompat.getColor(
                            this,
                            isLogin ? R.color.background : R.color.secondPage));
        });

        binding.loginFragment.setVisibility(INVISIBLE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        binding.loginFragment.setPivotX(binding.loginFragment.getWidth() / 2);
        binding.loginFragment.setPivotY(binding.loginFragment.getHeight());
        binding.signUpFragment.setPivotX(binding.signUpFragment.getWidth() / 2);
        binding.signUpFragment.setPivotY(binding.signUpFragment.getHeight());
    }

    public void switchFragment(View v) {
        if (isLogin) {
            binding.loginFragment.setVisibility(VISIBLE);
            binding.loginFragment.animate().rotation(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    binding.signUpFragment.setVisibility(INVISIBLE);
                    binding.signUpFragment.setRotation(90);
                    binding.wrapper.setDrawOrder(ORDER_LOGIN_STATE);
                }
            });
        } else {
            binding.signUpFragment.setVisibility(VISIBLE);
            binding.signUpFragment.animate().rotation(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    binding.loginFragment.setVisibility(INVISIBLE);
                    binding.loginFragment.setRotation(-90);
                    binding.wrapper.setDrawOrder(ORDER_SIGN_UP_STATE);
                }
            });
        }

        isLogin = !isLogin;
        binding.loginButton.startAnimation();
    }
}
