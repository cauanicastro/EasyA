package ca.ualberta.cmput301f14t16.easya.View;

import ca.ualberta.cmput301f14t16.easya.R;
import ca.ualberta.cmput301f14t16.easya.Exceptions.NoContentAvailableException;
import ca.ualberta.cmput301f14t16.easya.Model.GeneralHelper;
import ca.ualberta.cmput301f14t16.easya.Model.MainModel;
import ca.ualberta.cmput301f14t16.easya.Model.Question;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 
 * @author Cauani
 *
 */
public class QuestionActivity extends Activity {
	private static Question question;
	private ProgressDialog pd;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_view);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);        
        (new GetQuestionListTask(this)).execute();
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.question, menu);
		return true;
	}
	
	private void ShowNoContentView(){
		//TODO: display the NoContentView
		//setContentView(R.layout.no_content_view);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);        
		return;
	}
	
	private void SetAdapter(Question q){
		question = q;
    	QuestionViewAdapter adapter = new QuestionViewAdapter(this, question, (LinearLayout)findViewById(R.id.question_scrollview_container));
        adapter.build();
	}
	
	private class GetQuestionListTask extends AsyncTask<Void, Void, Question> {
		private Context ctx;
		
		public GetQuestionListTask(Context ctx){
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
    		pd = new ProgressDialog(ctx);
			pd.setTitle("Loading question...");
			pd.setMessage("Please wait.");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}
		
		@Override
        protected Question doInBackground(Void...voids) {
        	try{
        		String aux = (getIntent()).getStringExtra(MainActivity.QUESTION_KEY);
        		if (aux == null || aux.equals(""))
        			return null;
        		return MainModel.getInstance().getQuestionById(aux);
        	}catch(NoContentAvailableException ex){
        		return null;
        	}
        }

		@Override
        protected void onPostExecute(Question result){
        	if (result == null){
        		ShowNoContentView();
        	}else{
        		SetAdapter(result);
        	}
        	
        	if (pd!=null) {
				pd.dismiss();
			} 
        }
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (item.getItemId() == R.id.menu_question_favourite){
        	Toast.makeText(getApplicationContext(), "To be implemented", Toast.LENGTH_SHORT).show();
        	//TODO: favourite a question
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	public void AddNewAnswer(View v){
		if (question != null){
			Intent i = new Intent(v.getContext(), SubmitAnswerActivity.class);
	    	//i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	    	i.putExtra(GeneralHelper.AQUESTION_KEY, question.getId());
	        this.startActivity(i);
		}
	}
}
