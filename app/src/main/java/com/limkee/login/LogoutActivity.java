package com.limkee.login;

        import android.content.Intent;;
        import android.os.Bundle;

        import com.limkee.BaseActivity;
        import com.limkee.R;


public class LogoutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}