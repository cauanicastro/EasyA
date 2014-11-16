package ca.ualberta.cmput301f14t16.easya.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import ca.ualberta.cmput301f14t16.easya.Exceptions.NoContentAvailableException;
import ca.ualberta.cmput301f14t16.easya.Exceptions.NoInternetException;
import ca.ualberta.cmput301f14t16.easya.Model.Data.Cache;
import ca.ualberta.cmput301f14t16.easya.Model.Data.ESClient;
import ca.ualberta.cmput301f14t16.easya.Model.Data.PMClient;
import ca.ualberta.cmput301f14t16.easya.View.MainView;

/**
 * Provides a method of interacting with the data created and stored by the
 * application. Includes several methods to save and retrieve data from a
 * webservice, or local memory.
 * 
 * @author Cauani
 * @author Brett Commandeur (commande)
 */
public class MainModel {
	private static MainModel m;
	private static MainView<?> currentView;

	private List<MainView<?>> views;

	protected MainModel() {
		this.views = new ArrayList<MainView<?>>();
	}

	public static MainModel getInstance() {
		if (m == null)
			m = new MainModel();
		return m;
	}

	public void addView(MainView<?> view) {
		if (!getAllViews().contains(view)) {
			getAllViews().add(view);			
		}
	}

	public void deleteView(MainView<?> view) {
		getAllViews().remove(view);
	}

	public void notifyViews() {
		for (MainView<?> view : getAllViews()) {
			view.update();
		}
	}

	public List<MainView<?>> getAllViews() {
		return this.views;
	}

	public Question getQuestionById(String id)
			throws NoContentAvailableException {
		return Cache.getInstance().getQuestionById(id);
	}

	public User getUserById(String id) throws NoContentAvailableException {
		try {
			return Cache.getInstance().getUserById(id);
		} catch (Exception ex) {
			throw new NoContentAvailableException();
		}
	}

	public User getUserByEmail(String email)
			throws NoContentAvailableException, NoInternetException {
		return Cache.getInstance().getUserByEmail(email);
	}

	public User getCurrentUser() {
		try {
			PMClient pmclient = new PMClient();
			return pmclient.getUser();
		} catch (NoContentAvailableException ex) {
			return null;
		}

	}

	public void saveMainUser(User u) {
		PMClient pm = new PMClient();
		pm.saveUser(u);
		Cache.getInstance().updateAllUsers();
	}

	public boolean updateUsername(User u) {
		ESClient es = new ESClient();
		try {
			return es.setUsernameById(u.getId(), u.getUsername());
		} catch (IOException ex) {
			return false;
		}
	}

	public List<QuestionList> getAllQuestions()
			throws NoContentAvailableException {
		return Cache.getInstance().getAllQuestions();
	}
	
	public List<QuestionList> getAllCachedQuestions() {
		try{
			return Cache.getInstance().getQuestionListFromQuestionsCache();
		}catch(NoContentAvailableException ex){
			return new ArrayList<QuestionList>();
		}
	}
	
	public List<QuestionList> getAllUserFavourites()
			throws NoContentAvailableException{
		return Cache.getInstance().getAllUserFavourites();
	}
	
	public List<QuestionList> getAllUserQuestions()
			throws NoContentAvailableException{
		return Cache.getInstance().getAllUserQuestions();
	}

	public void wipeData() {
		Cache.getInstance().wipeCache();
		PMClient pm = new PMClient();
		pm.wipeData();
	}
}
