/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listing;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.bson.Document;
import user.UserBean;

/**
 *
 * @author root
 */
public class ListingDao {
        // CREATE READ UPDATE DELETE
    private final MongoClient mc;
    private final MongoDatabase md;
    private final MongoCollection<Document> coll;
    private final List<String>validFields;
    public ListingDao() {
        this.mc = MongoSingleton.getMongoClient();
        this.md = this.mc.getDatabase("project");
        this.coll = this.md.getCollection("listings");
        this.validFields = new ArrayList<>();
        this.validFields.add("id");
        this.validFields.add("uid");
        this.validFields.add("title");
        this.validFields.add("locality");
        this.validFields.add("description");
        this.validFields.add("imagePath");
        this.validFields.add("activity");
    }
    
    private Document getDocument(ListingBean lb) {
        Document doc = new Document();
        doc.append("_id", lb.getId());
        doc.append("uid", lb.getUid());
        doc.append("title", lb.getTitle());
        doc.append("locality", lb.getLocality());
        doc.append("description", lb.getDescription());
        doc.append("imagePath", lb.getImagePath());
        doc.append("activity", lb.getActivity());
        return doc;
    }
    // C R U D
    public boolean createListing(ListingBean lb) {
        Document doc = this.getDocument(lb);
        this.coll.insertOne(doc);
        return true;
    }
    
    public String readListing(String getBy, String value) throws NoSuchElementException {
        getBy = getBy.toLowerCase();
        value = value.toLowerCase();
        if (getBy.equals("id"))
            getBy = "_id";
        if (!this.validFields.contains(getBy))
            throw new IllegalArgumentException("Invalid Field Data !");
        boolean found = false;
        List<ListingBean> toConvert = new LinkedList<>();
        System.out.println(getBy+" "+value);
        for (Document i: this.coll.find(new Document().append(getBy, value))) {
            ListingBean lb;
            lb = new ListingBean();
            lb.setId(i.getString("_id"));
            lb.setUid(i.getString("uid"));
            lb.setTitle(i.getString("title"));
            lb.setLocality(i.getString("locality"));
            lb.setDescription(i.getString("description"));
            lb.setImagePath(i.getString("imagePath"));
            lb.setActivity(i.getString("activity"));
            toConvert.add(lb);
            found = true;
        }
        if (!found)
            throw new NoSuchElementException();
        Gson gs = new Gson();
        String json = gs.toJson(toConvert);
        System.out.println(json);
        return json;
    }
    
        
    public boolean checkIfExists(String getBy, String value) throws NoSuchElementException {
        boolean found = false;
        getBy = getBy.toLowerCase();
        if (getBy.equals("id"))
            getBy = "_id";
        for (Document i: this.coll.find(new Document().append(getBy, value))) {
            found = true;
            break;
        }
        if (!found)
            throw new NoSuchElementException();
        return true;
    }
    
    public boolean updateListing(String id, String updateField, String updateWith) throws NoSuchElementException {
        
        if (!this.validFields.contains(updateField))
            throw new IllegalArgumentException("Invalid Field Data !");
        try {
            System.out.println(id);
            checkIfExists("id", id);
        } catch (NoSuchElementException e) {
            throw e;
        }
        if (updateField.equals("id") || updateField.equals("uid"))
            return false;
        
        this.coll.updateOne(new Document().append("_id", id), 
                new Document().append("$set", new Document().append(updateField, updateWith)));
        return true;
    }
    
    public boolean deleteListing(String id) {
        DeleteResult dr = this.coll.deleteOne(new Document().append("_id", id));
        return dr.getDeletedCount() != 0;
    }
    
    public void deleteAll(String uid) {
        this.coll.deleteOne(new Document().append("_uid", uid));
    }
}
