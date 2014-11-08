package ca.ualberta.cmput301f14t16.easya.Model.Data;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ca.ualberta.cmput301f14t16.easya.Exceptions.NoContentAvailableException;
import ca.ualberta.cmput301f14t16.easya.Model.GeneralHelper;
import ca.ualberta.cmput301f14t16.easya.Model.Pending;
import ca.ualberta.cmput301f14t16.easya.Model.User;

/**
 * Phone Memory Client (really silly name)
 * Takes care of accessing the phone memory for saving and retrieving.
 * Will be used by Queue and Cache
 * @author Cauani
 *
 */
public class PMClient {
	public PMClient(){}
	
	public void saveQTitle(String text){
		PMDataParser.saveUserPreference(GeneralHelper.QTITLE, text);
	}
	
	public String getQTitle(){
		return PMDataParser.recoverUserPreference(GeneralHelper.QTITLE);
	}
	
	public void saveQBody(String text){
		PMDataParser.saveUserPreference(GeneralHelper.QBODY, text);
	}
	
	public String getQBody(){
		return PMDataParser.recoverUserPreference(GeneralHelper.QBODY);
	}
	
	public void saveABody(String text){
		PMDataParser.saveUserPreference(GeneralHelper.ABODY, text);
	}
	
	public String getABody(){
		return PMDataParser.recoverUserPreference(GeneralHelper.ABODY);
	}
	
	public void clearQ(Context ctx){
		saveQTitle("");
		saveQBody("");
	}
	
	public void clearA(Context ctx){
		saveABody("");
	}
	
	public void saveUser(User user){
		Gson gson = new Gson();
		PMDataParser.saveUserPreference(User.USERKEY, gson.toJson(user, User.class));
	}
	
	public User getUser() throws NoContentAvailableException{
		Gson gson = new Gson();
		String aux = PMDataParser.recoverUserPreference(User.USERKEY);
		if (aux == null|| aux.equals("")){
			throw new NoContentAvailableException();
		}
		return gson.fromJson(aux, User.class);
	}

	public void savePending(Pending p) {
		Gson gson = new Gson();
		List<Pending> aux = getPendings();
		aux.add(p);
		PMDataParser.saveJson(PMFilesEnum.QUEUE, gson.toJson(aux));	
	}
	
	public void deletePending(Pending p) {
		Gson gson = new Gson();
		List<Pending> aux = getPendings();
		if (aux.remove(p))
			PMDataParser.saveJson(PMFilesEnum.QUEUE, gson.toJson(aux));	
	}
	
	public List<Pending> getPendings(){
		Gson gson = new Gson();
		Type listType = new TypeToken<List<Pending>>(){}.getType();
		return gson.fromJson(PMDataParser.loadJson(PMFilesEnum.QUEUE), listType);
	}
	
	protected final static List<Pending> Sorter(List<Pending> lst)
    {
        //First sort the list by date of creation
        Collections.sort(lst, new Comparator<Pending>() {
            @Override
            public int compare(Pending obj1, Pending obj2) {
                return obj2.getContent().getDate().compareTo(obj1.getContent().getDate());
            }
        });
        return lst;
    }
}
