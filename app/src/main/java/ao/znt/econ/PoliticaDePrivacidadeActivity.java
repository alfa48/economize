package ao.znt.econ;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import java.util.Objects;

public class PoliticaDePrivacidadeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica_de_privacidade);

        setSupportActionBar(findViewById(R.id.toolbarPoliticaPrivacidade));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        WebView webView = findViewById(R.id.webview_politica_privacidade);
        webView.loadUrl("file:///android_asset/politica_privacidade.html");
    }
}