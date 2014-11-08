package ca.ualberta.cmput301f14t16.easya.View;

import java.io.File;

import ca.ualberta.cmput301f14t16.easya.R;
import ca.ualberta.cmput301f14t16.easya.Controller.NewAnswerController;
import ca.ualberta.cmput301f14t16.easya.Controller.NewQuestionController;
import ca.ualberta.cmput301f14t16.easya.Model.GeneralHelper;
import ca.ualberta.cmput301f14t16.easya.Model.MainModel;
import ca.ualberta.cmput301f14t16.easya.Model.PixelBitmap;
import ca.ualberta.cmput301f14t16.easya.Model.User;
import ca.ualberta.cmput301f14t16.easya.Model.Data.PMClient;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 
 * @author Cauani
 *
 */
public class SubmitAnswerActivity extends Activity {
	private ProgressDialog pd;
    private ImageView imageview, addimage;
    private PixelBitmap pixelbitmap;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit_answer);
		addimage = (ImageView)findViewById(R.id.submit_answer_picture_add);
        imageview = (ImageView)findViewById(R.id.submit_answer_imageView_pic);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        addimage.setOnClickListener(new OnClickListener() {          	  
            @Override  
            public void onClick(View v) {  
   			 Intent intent = new Intent(Intent.ACTION_PICK, null);
		     intent.setType("image/*");
		     startActivityForResult(intent,1);
            }  
        });  
	}		

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.menu_submit:
	        	(new submitAnswerTask(this)).execute();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
    public void onPause(){
        PMClient pm = new PMClient();
        //TODO: pass this to the controller
        pm.saveABody(((EditText)findViewById(R.id.submit_answer_body)).getText().toString());
        super.onPause();
    }

    @Override
    public void onResume(){
    	PMClient pm = new PMClient();
    	((EditText)findViewById(R.id.submit_answer_body)).setText(pm.getABody());
        super.onResume();
    }
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     
		if (requestCode == 1 && resultCode == RESULT_OK) {	          
			Uri selectedImageUri = data.getData();
			String imagepath = getPath(selectedImageUri);
			Bitmap bitmap=BitmapFactory.decodeFile(imagepath);
			File file = new File(imagepath);
			long length = file.length();
			int lengthint=(int)length;
			pixelbitmap=new PixelBitmap(bitmap.getHeight(),bitmap.getHeight());
			pixelbitmap.getColors(bitmap);
			imageview.setImageBitmap(bitmap);
	      
	    }
	}
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}    
    private class submitAnswerTask extends AsyncTask<Void, Void, Boolean> {
    	private NewAnswerController controller;
    	private Context ctx;
    	
    	public submitAnswerTask(Context ctx){
    		this.ctx = ctx;
    	}
    	
    	@Override
		protected void onPreExecute() {
    		pd = new ProgressDialog(ctx);
			pd.setTitle("Submitting answer...");
			pd.setMessage("Please wait.");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}
    	
    	@Override
    	protected Boolean doInBackground(Void...voids) {
            try{
            	try{
	        		controller = 
	        				NewAnswerController.create(
	        						ctx, 
	        						(getIntent()).getStringExtra(GeneralHelper.AQUESTION_KEY), 
	        						((EditText)findViewById(R.id.submit_answer_body)).getText().toString(), 
        							MainModel.getInstance().getCurrentUser().getId());
	        		return controller.submit();	        		
	        	}catch(Exception ex){
	        		System.out.println(ex.getMessage());
	        		return false;
	        		//Deal with things as: user didn't fill out everything
	        	}
            }catch(Exception ex){
            	//Deal with this
            	return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
        	if (result){
        		PMClient pm = new PMClient();	        			
    			pm.clearA(ctx);
    			((EditText)findViewById(R.id.submit_answer_body)).setText("");
    			if (controller.submitedOffline){
    				finish();
    				Toast.makeText(getApplicationContext(), "Your answer will be posted online when you connect to the internet!", Toast.LENGTH_LONG).show();
    			}else{        			
        			String aux = controller.getQuestionId();
        			Intent i = new Intent(ctx,QuestionActivity.class);
        			i.putExtra(MainActivity.QUESTION_KEY, aux);
        			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        			startActivity(i);
        			finish();
    			}
    		}else{    			
    			finish();
    			Toast.makeText(getApplicationContext(), "Something bad happened, try posting your answer again!", Toast.LENGTH_LONG).show();
    		}
        	
        	if (pd!=null) {
				pd.dismiss();
			}        	
        }
    }
}
