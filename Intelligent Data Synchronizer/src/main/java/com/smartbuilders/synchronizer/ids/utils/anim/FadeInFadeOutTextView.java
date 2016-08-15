package com.smartbuilders.synchronizer.ids.utils.anim;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

import com.smartbuilders.synchronizer.ids.R;

public class FadeInFadeOutTextView {

	Context context;
    private TextView textView;
    private Animation fadeIn;
    private Animation fadeOut;
    // Listeners que detectan el fin de la animacion
    private LocalFadeInAnimationListener myFadeInAnimationListener = new LocalFadeInAnimationListener();
    private LocalFadeOutAnimationListener myFadeOutAnimationListener = new LocalFadeOutAnimationListener();


    /**
     *
     * @param context
     * @param text
     */
    public FadeInFadeOutTextView(Context context, TextView text){
        this.context = context;
        this.textView = text;
    }
 
    private void launchOutAnimation() {
    	if(textView!=null && fadeOut!=null){
    		textView.startAnimation(fadeOut);
        }
    }
 
    /**
     * Performs the actual fade-in
     */
    private void launchInAnimation() {
    	if(textView!=null && fadeIn!=null){
    		textView.startAnimation(fadeIn);
        }
    }
 
    /**
     * Comienzo de la animaci�n
     */
    public void startAnimations() {
        //uso de las animaciones
        fadeIn = AnimationUtils.loadAnimation(this.context, R.anim.fadein);
        fadeIn.setAnimationListener( myFadeInAnimationListener );
        fadeOut = AnimationUtils.loadAnimation(this.context, R.anim.fadeout);
        fadeOut.setAnimationListener( myFadeOutAnimationListener );
        // And start
        launchInAnimation();
    }
    
    /**
     * Fin de la animacion
     */
    public void stopAnimations() {
    	if(fadeIn!=null){
    		fadeIn.cancel();
    		fadeIn.setAnimationListener(null);
    		fadeIn = null;
    	}
    	if(fadeOut!=null){
    		fadeOut.cancel();
    		fadeOut.setAnimationListener(null);
    		fadeOut = null;
    	}
    	if(textView!=null){
    		textView.clearAnimation();
    	}
    }
 
    // Runnable que arranca la animacion
    private Runnable mLaunchFadeOutAnimation = new Runnable() {
        public void run() {
            launchOutAnimation();
        }
    };
 
    private Runnable mLaunchFadeInAnimation = new Runnable() {
        public void run() {
            launchInAnimation();
        }
    };
 
    /**
     * Listener para la animacion del Fadeout
     *
     * @author moi
     *
     */
    private class LocalFadeInAnimationListener implements AnimationListener {
        public void onAnimationEnd(Animation animation) {
        	if(textView!=null){
        		textView.post(mLaunchFadeOutAnimation);
            }
        }
        public void onAnimationRepeat(Animation animation){
        }
        public void onAnimationStart(Animation animation) {
        }
    };
 
    /**
     * Listener de animaci�n para el Fadein
     */
    private class LocalFadeOutAnimationListener implements AnimationListener {
        public void onAnimationEnd(Animation animation) {
        	if(textView!=null){
        		textView.post(mLaunchFadeInAnimation);
            }
        }
        public void onAnimationRepeat(Animation animation) {
        }
        public void onAnimationStart(Animation animation) {
        }
    };
}
