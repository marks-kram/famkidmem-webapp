package de.mherrmann.famkidmem.backend.body.admin;


import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.entity.UserEntity;

import java.util.List;

public class ResponseBodyGetUsers extends ResponseBody {

    private List<UserEntity> users;

    public ResponseBodyGetUsers(){}

    public ResponseBodyGetUsers(List<UserEntity> users){
        super("ok", "Successfully got users");
        this.setUsers(users);
    }

    public ResponseBodyGetUsers(Exception ex){
        super("error", ex.getMessage(), ex);
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }
}
