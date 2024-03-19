/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FinalProject;

/**
 *
 * @author deemashaath
 */
public class ClientSession {

    private static Users currentUser;

    public static void setCurrentUser(Users user) {
        currentUser = user;
    }

    public static Users getCurrentUser() {
        return currentUser;
    }
}
