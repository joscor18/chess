package service;

import dataaccess.DataAccess;
import datamodel.AuthData;
import datamodel.UserData;

import javax.xml.crypto.Data;
import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws Exception {
        if(dataAccess.getUser(user.username()) != null){
            throw new Exception("already taken");
        }
        dataAccess.createUser(user);
        var authData = new AuthData(user.username(), genAuthToken());
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData login(UserData user)throws Exception{
        if(dataAccess.getUser(user.username()) == null || !dataAccess.getUser(user.username()).password().equals(user.password())){
            throw new Exception("unauthorized");
        }
        var authData = new AuthData(user.username(), genAuthToken());
        dataAccess.createAuth(authData);
        return authData;
    }

    public void logout(String authToken)throws Exception{
        if(dataAccess.getAuth(authToken) == null ){
            throw new Exception("unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }

    public void list(String authToken)throws Exception{
        if(dataAccess.getAuth(authToken) == null ){
            throw new Exception("unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }


    private String genAuthToken(){
        return UUID.randomUUID().toString();
    }

}
