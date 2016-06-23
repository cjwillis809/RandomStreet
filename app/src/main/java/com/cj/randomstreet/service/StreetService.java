package com.cj.randomstreet.service;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cj.randomstreet.StreetHomeActivity;

public class StreetService extends Service {

    public interface StreetSuccessListener {
        void displayStreetResult(String street);
    }

    private StreetSuccessListener streetSuccessListener;
    private static final String STREET_ENDPOINT =
            "https://9853bc79c4.pythonanywhere.com/RandomStreet/default/streets";
    private RequestQueue requestQueue;
    private View progressView;
    private Context context;

    public StreetService(final StreetSuccessListener listener, Context context, View progressView) {
        streetSuccessListener = listener;
        this.progressView = progressView;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
    }

    public void getRandomStreet() {
        StringRequest request = new StringRequest(Request.Method.GET, STREET_ENDPOINT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty()) {
                    streetSuccessListener.displayStreetResult(response);
                    dismissProgress();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("STREET_SERVICE", "Error: " + error.getMessage());
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        requestQueue.add(request);
        showProgress();
    }

    /**
     * Displays the progress bar.
     */
    protected void showProgress() {
        if (progressView != null) {
            progressView.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT > 10) {
                progressView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public boolean onPreDraw() {
                        progressView.getViewTreeObserver().removeOnPreDrawListener(this);

                        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(progressView, "alpha", 0.0f, 0.7f);
                        alphaAnim.setDuration(context.getResources().getInteger(android.R.integer.config_shortAnimTime));
                        alphaAnim.start();

                        return true;
                    }
                });
            }

        }
    }

    /**
     * Dismisses the progress bar
     */
    protected void dismissProgress() {
        if (progressView != null) {
            if (Build.VERSION.SDK_INT > 10) {
                ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(progressView, "alpha", 0.7f, 0.0f);
                alphaAnim.setDuration(context.getResources().getInteger(android.R.integer.config_shortAnimTime));
                alphaAnim.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressView.setVisibility(View.GONE);
                    }

                });
                alphaAnim.start();
            } else {
                progressView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
