package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import datamodel.AuthData;
import datamodel.UserData;
import org.mindrot.jbcrypt.BCrypt;

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
        //adjust for hashed password so that it passes the compared value with BCrypt,
        //for safety reasons
        String hashed = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData hashUser = new UserData(user.username(), user.email(), hashed);
        dataAccess.createUser(hashUser);
        var authData = new AuthData(user.username(), genAuthToken());
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData login(UserData user)throws Exception{
        UserData userdb = dataAccess.getUser(user.username());
        //compares BCrypt password
        if(userdb == null || !BCrypt.checkpw(user.password(), userdb.password())){
            throw new DataAccessException("unauthorized");
        }
        var authData = new AuthData(user.username(), genAuthToken());
        dataAccess.createAuth(authData);
        return authData;
    }

    public void logout(String authToken)throws Exception{
        if(dataAccess.getAuth(authToken) == null ){
            throw new DataAccessException("unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }


    private String genAuthToken(){
        return UUID.randomUUID().toString();
    }

}
