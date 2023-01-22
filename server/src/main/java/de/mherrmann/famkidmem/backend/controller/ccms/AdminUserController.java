package de.mherrmann.famkidmem.backend.controller.ccms;

import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyDeleteUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyResetPassword;
import de.mherrmann.famkidmem.backend.body.admin.ResponseBodyGetUsers;
import de.mherrmann.famkidmem.backend.service.ccms.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/ccms/admin/user")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Autowired
    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostMapping(value = "/add")
    public ResponseEntity<ResponseBody> addUser(@RequestBody RequestBodyAddUser addUserRequest){
        try {
            adminUserService.addUser(addUserRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully added user: " + addUserRequest.getUsername()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<ResponseBody> deleteUser(@RequestBody RequestBodyDeleteUser deleteUserRequest){
        try {
            adminUserService.deleteUser(deleteUserRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully deleted user: " + deleteUserRequest.getUsername()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @GetMapping(value = "/get")
    public ResponseEntity<ResponseBodyGetUsers> getUser(){
        try {
            ResponseBodyGetUsers usersResponse = adminUserService.getUsers();
            return ResponseEntity.ok(usersResponse);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBodyGetUsers(ex));
        }
    }

    @PostMapping(value = "/reset")
    public ResponseEntity<ResponseBody> resetPassword(@RequestBody RequestBodyResetPassword resetPasswordRequest){
        try {
            adminUserService.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully reset password for user: " + resetPasswordRequest.getUsername()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @PostMapping(value = "/enablePermission2/{username}")
    public ResponseEntity<ResponseBody> enablePermission2(@PathVariable String username){
        try {
            adminUserService.setPermission2(username, true);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully enabled permission2 for user: " + username));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @PostMapping(value = "/disablePermission2/{username}")
    public ResponseEntity<ResponseBody> disablePermission2(@PathVariable String username){
        try {
            adminUserService.setPermission2(username, false);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully disabled permission2 for user: " + username));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }
}
