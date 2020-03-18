package com.example.epayco_pse_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import co.epayco.android.Epayco;
import co.epayco.android.models.Authentication;
import co.epayco.android.models.Pse;
import co.epayco.android.util.EpaycoCallback;


public class MainActivity extends AppCompatActivity {

    Bank[] banks;
    Epayco epayco;

    WebView webView_bank;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Authentication auth = new Authentication();
        auth.setApiKey("your api key");
        auth.setPrivateKey("your private key");
        auth.setLang("ES");
        auth.setTest(false);

        epayco = new Epayco(auth);

        prefs = getApplicationContext().getSharedPreferences("Variable", Context.MODE_PRIVATE);

        getBanks("https://secure.payco.co/restpagos/pse/bancos.json");

        Pse pse = new Pse();
        //Hacer las validaciones
        pse.setBank("code Bank");
        pse.setTypePerson("");
        pse.setDocType("");
        pse.setDocNumber("");
        pse.setName("");
        pse.setLastName("");
        pse.setEmail("");
        pse.setInvoice("");
        pse.setDescription("Pago realizado en Vueltas.com");
        pse.setValue("50000");
        pse.setTax("0");
        pse.setTaxBase("50000");
        pse.setPhone("");
        pse.setCurrency("COP");
        pse.setCountry("CO");
        pse.setUrlResponse("");
        pse.setUrlConfirmation("");

        //Optional
        pse.setExtra1("");
        pse.setExtra2("");
        pse.setExtra3("");
        pse.setCity("");
        pse.setDepto("");
        pse.setAddress("");

        epayco.createPseTransaction(pse, new EpaycoCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) throws JSONException {
                String urlBank = jsonObject.getString("urlbank");
                String transactionId = jsonObject.getString("transactionID");

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("transaccionId", transactionId);
                editor.apply();

                webView_bank.setVisibility(View.VISIBLE);
                webView_bank.setWebViewClient(new WebViewClient());
                webView_bank.getSettings().setJavaScriptEnabled(true);
                webView_bank.loadUrl(urlBank);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    public static String buildURL(String url, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(url).buildUpon();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return builder.build().toString();
    }

    public void getBanks(String Url) {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("public_key", "your api key");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {

            URL url = new URL(buildURL(Url, body));
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url.toString(),
                    (String) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            final JSONArray jsonBanks = null;
                            try {
                                jsonBanks = response.getJSONArray("data");
                                banks = new Bank[jsonBanks.length()];
                                for (int i = 0; i < jsonBanks.length(); i++) {
                                    JSONObject aux = jsonBanks.getJSONObject(i);
                                    banks[i] = new Bank(aux.getString("bankCode"), aux.getString("bankName"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("", " " + error.getMessage());
                        }
                    }
            );
            requestQueue.add(jsArrayRequest);
        } catch (Exception e) {
            Log.d("", "Exception");
        }
    }
}
