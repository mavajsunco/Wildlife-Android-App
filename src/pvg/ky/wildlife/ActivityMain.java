package pvg.ky.wildlife;


import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.actionbarsherlock.R.integer;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;

public class ActivityMain extends SherlockActivity implements ActionBar.TabListener{
	 private static final String TAG = "Touch";
	    @SuppressWarnings("unused")
	    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

	    // These matrices will be used to scale points of the image
	    Matrix matrix = new Matrix();
	    Matrix savedMatrix = new Matrix();

	    // The 3 states (events) which the user is trying to perform
	    static final int NONE = 0;
	    static final int DRAG = 1;
	    static final int ZOOM = 2;
	    int mode = NONE;

	    // these PointF objects are used to record the point(s) the user is touching
	    PointF start = new PointF();
	    PointF mid = new PointF();
	    float oldDist = 1f;
	    String[] tabs;
	    Integer nature[] = { R.drawable.gallery,
	    		R.drawable.gallery2,
	    		R.drawable.gallery3,
	    		R.drawable.gallery4,
	    		R.drawable.gallery5,
	    		R.drawable.gallery6,
               };

	    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        //setContentView(R.layout.layout_gallery);
        
       
        tabs = getResources().getStringArray(R.array.gallery);
        
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (int i = 0; i <= 0; i++) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            String name = tabs[i];
            
            tab.setText(name);
            
          
        
            
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }
        
        
}
	 public boolean onTouch(View v, MotionEvent event) 
	    {
	        ImageView galleryFlow = (ImageView) v;
	        galleryFlow.setScaleType(ImageView.ScaleType.MATRIX);
	        float scale;

	        dumpEvent(event);
	        // Handle touch events here...

	        switch (event.getAction() & MotionEvent.ACTION_MASK) 
	        {
	            case MotionEvent.ACTION_DOWN:   // first finger down only
	                                                savedMatrix.set(matrix);
	                                                start.set(event.getX(), event.getY());
	                                                Log.d(TAG, "mode=DRAG"); // write to LogCat
	                                                mode = DRAG;
	                                                break;

	            case MotionEvent.ACTION_UP: // first finger lifted

	            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

	                                                mode = NONE;
	                                                Log.d(TAG, "mode=NONE");
	                                                break;

	            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

	                                                oldDist = spacing(event);
	                                                Log.d(TAG, "oldDist=" + oldDist);
	                                                if (oldDist > 5f) {
	                                                    savedMatrix.set(matrix);
	                                                    midPoint(mid, event);
	                                                    mode = ZOOM;
	                                                    Log.d(TAG, "mode=ZOOM");
	                                                }
	                                                break;

	            case MotionEvent.ACTION_MOVE:

	                                                if (mode == DRAG) 
	                                                { 
	                                                    matrix.set(savedMatrix);
	                                                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
	                                                } 
	                                                else if (mode == ZOOM) 
	                                                { 
	                                                    // pinch zooming
	                                                    float newDist = spacing(event);
	                                                    Log.d(TAG, "newDist=" + newDist);
	                                                    if (newDist > 5f) 
	                                                    {
	                                                        matrix.set(savedMatrix);
	                                                        scale = newDist / oldDist; // setting the scaling of the
	                                                                                    // matrix...if scale > 1 means
	                                                                                    // zoom in...if scale < 1 means
	                                                                                    // zoom out
	                                                        matrix.postScale(scale, scale, mid.x, mid.y);
	                                                    }
	                                                }
	                                                break;
	        }

	        galleryFlow.setImageMatrix(matrix); // display the transformation on screen

	        return true; // indicate event was handled
	    }

	    /*
	     * --------------------------------------------------------------------------
	     * Method: spacing Parameters: MotionEvent Returns: float Description:
	     * checks the spacing between the two fingers on touch
	     * ----------------------------------------------------
	     */

	    private float spacing(MotionEvent event) 
	    {
	        float x = event.getX(0) - event.getX(1);
	        float y = event.getY(0) - event.getY(1);
	        return FloatMath.sqrt(x * x + y * y);
	    }

	    /*
	     * --------------------------------------------------------------------------
	     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
	     * Description: calculates the midpoint between the two fingers
	     * ------------------------------------------------------------
	     */

	    private void midPoint(PointF point, MotionEvent event) 
	    {
	        float x = event.getX(0) + event.getX(1);
	        float y = event.getY(0) + event.getY(1);
	        point.set(x / 2, y / 2);
	    }

	    /** Show an event in the LogCat view, for debugging */
	    private void dumpEvent(MotionEvent event) 
	    {
	        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
	        StringBuilder sb = new StringBuilder();
	        int action = event.getAction();
	        int actionCode = action & MotionEvent.ACTION_MASK;
	        sb.append("event ACTION_").append(names[actionCode]);

	        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) 
	        {
	            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
	            sb.append(")");
	        }

	        sb.append("[");
	        for (int i = 0; i < event.getPointerCount(); i++) 
	        {
	            sb.append("#").append(i);
	            sb.append("(pid ").append(event.getPointerId(i));
	            sb.append(")=").append((int) event.getX(i));
	            sb.append(",").append((int) event.getY(i));
	            if (i + 1 < event.getPointerCount())
	                sb.append(";");
	        }

	        sb.append("]");
	        Log.d("Touch Events ---------", sb.toString());
	    }
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if(tab.getPosition()  == 0){
				
				
				
			        
			        ImageAdapter adapter = new ImageAdapter(this,nature);
			        adapter.createReflectedImages();

			        GalleryFlow galleryFlow = (GalleryFlow) findViewById(R.id.Gallery01);
			        galleryFlow.setAdapter(adapter);
				
			}
			
			
		}
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
}