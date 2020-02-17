package dev.saul1317.uxdribbble.Animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import dev.saul1317.uxdribbble.R;

public class RevealAnimation {

    private FrameLayout button;
    private ProgressBar progressBar;
    private TextView textView;
    private Context context;
    private View reveal;

    public RevealAnimation(Context context, FrameLayout button, ProgressBar progressBar, TextView textView, View reveal) {
        this.button = button;
        this.progressBar = progressBar;
        this.textView = textView;
        this.context = context;
        this.reveal = reveal;
    }

    //Tamaño del boton cuando se encuentra en fab
    private int getFabWidth() {
        return (int) context.getResources().getDimension(R.dimen.fab_size);
    }

    //se activa la transformación del botón
    public void transformFabButton() {
        button.setEnabled(false);
        ValueAnimator anim = ValueAnimator.ofInt(button.getMeasuredWidth(), getFabWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fadeOutTextAndShowProgressDialog();
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                layoutParams.width = val;
                button.requestLayout();
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    private void fadeOutTextAndShowProgressDialog() {
        textView.animate().alpha(0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        showProgressBar();
                    }
                })
                .start();
    }

    //Muestra el progress dentro del botón
    private void showProgressBar() {
        progressBar.setAlpha(1f);
        progressBar
                .getIndeterminateDrawable()
                .setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void buttonResetRevealAnimation(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                transformOriginalButton();
            }
        }, 500);
    }

    //retorna el botón a su tamaño original
    private void transformOriginalButton() {
        reveal.setVisibility(View.INVISIBLE);
        button.setEnabled(true);
        int toValue = ((View) button.getParent()).getWidth();
        ValueAnimator anim = ValueAnimator.ofInt(button.getMeasuredWidth(),toValue);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fadeInTextAndShowProgressDialog();
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                layoutParams.width = val;
                button.requestLayout();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    button.setElevation(4f);
                }
            }
        });

        anim.setDuration(250);
        anim.start();
    }

    private void fadeInTextAndShowProgressDialog() {
        textView.animate().alpha(1f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        hideProgressBar();
                    }
                })
                .start();
    }

    //Desaparece el progress bar
    private void hideProgressBar() {
        progressBar.animate().alpha(0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        }).start();
    }


    public void revealButton(final RevealAnimationOnListener revealAnimationOnListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setElevation(0f);
        }
        reveal.setVisibility(View.VISIBLE);
        int cx = reveal.getWidth();
        int cy = reveal.getHeight();
        int x = (int) (getFabWidth() / 2 + button.getX());
        int y = (int) (getFabWidth() / 2 + button.getY());

        float finalRadius = Math.max(cx, cy) * 1.2f;

        Animator revealAnimator = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            revealAnimator = ViewAnimationUtils.createCircularReveal(reveal, x, y, getFabWidth(), finalRadius);
        }

        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                revealAnimationOnListener.revealAnimationOnListerner();
            }
        });

        revealAnimator.setDuration(500);
        revealAnimator.start();
    }

}
