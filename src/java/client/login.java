/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author DEREK
 */
@ManagedBean
@SessionScoped
public class login {

    String username;
    String password;

    boolean success, message;

    public login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public login() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isMessage() {
        return message;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void login() throws IOException {

        if (username.equals("admin") && password.equals("admin")) {
            this.success = true;
            FacesContext.getCurrentInstance().getExternalContext().redirect("menu.xhtml");

        } else {
            this.message = true;
           FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
        }
    }
    public void goBackLogin() throws IOException{
        this.message = false;
        this.success = false;
        username = "";
        password = "";
        FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
    }

}
