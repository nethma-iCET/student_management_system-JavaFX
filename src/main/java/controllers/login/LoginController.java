package controllers.login;

public class LoginController {
    public boolean checkUserNameandPassword(String userName, String password) {
        if(userName.equals("nimal") && password.equals("1234")){
            return true;
        }
        return false;
    }
}
