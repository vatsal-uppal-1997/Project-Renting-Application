/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.bson.Document;

/**
 *
 * @author cereal
 */
public class UserDao {

    // CREATE READ UPDATE DELETE
    private final MongoClient mc;
    private final MongoDatabase md;
    private final MongoCollection<Document> coll;
    private final List<String>validFields;
    public UserDao() {
        this.mc = MongoSingleton.getMongoClient();
        this.md = this.mc.getDatabase("project");
        this.coll = this.md.getCollection("users");
        this.validFields = new ArrayList<>();
        this.validFields.add("id");
        this.validFields.add("username");
        this.validFields.add("locality");
        this.validFields.add("email");
        this.validFields.add("mobile");
    }

    //Documents are stuff that reside in a mongo db collection (tables)
    private Document getDocument(UserBean ub) {
        Document doc = new Document();
        doc.append("_id", ub.getId());
        doc.append("username", ub.getUsername());
        doc.append("locality", ub.getLocality());
        doc.append("email", ub.getEmail());
        doc.append("mobile", ub.getMobile());
        return doc;
    }

    // Create --- Creates a user entry
    public boolean createUser(UserBean ub, String password) throws IllegalArgumentException {
        if (checkIfExists("username", ub.getUsername())) 
            throw new IllegalArgumentException("The username alreay exists");
        if (checkIfExists("email", ub.getEmail()))
            throw new IllegalArgumentException("The email alreay exists");
        if (checkIfExists("mobile", ub.getMobile()))
            throw new IllegalArgumentException("The mobile number alreay exists");
        Document doc = getDocument(ub);
        coll.insertOne(doc);
        (new KeyMaster()).createKey(ub.getId(), password);
        return true;
    }

    // Read --- Returns a user bean, Given the user id
    public UserBean readUser(String getBy, String value) throws NoSuchElementException {
        UserBean temp = new UserBean();
        Boolean found = false;
        getBy = getBy.toLowerCase();
        value = value.toLowerCase();
        for (Document i : coll.find(new Document().append(getBy, value))) {
            temp.setId(i.getString("_id"));
            temp.setUsername(i.getString("username"));
            temp.setLocality(i.getString("locality"));
            temp.setMobile(i.getString("mobile"));
            temp.setEmail(i.getString("email"));
            found = true;
            break;
        }
        if (found == false)
            throw new NoSuchElementException();
        return temp;
    }
    

    public boolean checkIfExists(String field, String toFind) {
        
        if (!this.validFields.contains(field))
            return false;
        if (field.equals("id"))
            field = "_id";
        for (Document i : coll.find(new Document().append(field, toFind))) 
            return true;
        return false;
        
    }

    public boolean updateUser(String id, String updateField, String updateWith) throws NoSuchElementException {
        
        if (!this.validFields.contains(updateField))
            throw new IllegalArgumentException("Invalid Field Data !");
        
        if (updateField.equals("id"))
            return false;
        
        UserBean getUser;
        
        try {
            getUser = readUser("id", id);
        } catch (NoSuchElementException e) {
            throw e;
        }
        
        updateField = updateField.toLowerCase();
        
        
        
        Document newValues = new Document();
        Document oldValues = new Document();
        Document updateQuery = new Document();
        oldValues.put("_id", getUser.getId());
        newValues.put(updateField, updateWith);
        updateQuery.put("$set", newValues);
        
        /*
        {
            "$set" : {
                        "someValue" : "xyz"
            }
        }
        */
        coll.updateOne(oldValues, updateQuery);
        return true;
    }
    
    public boolean deleteUser(String id) {
        Document doc = new Document();
        doc.put("_id", id);
        DeleteResult dr = coll.deleteOne(doc);
        return dr.getDeletedCount() != 0 && (new KeyMaster()).deleteKey(id);
    }

}
