/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FinalProject;

import java.time.LocalDate;
import java.util.Date;

/**
 *
 * @author deemashaath
 */
public class Orders {
    private int id;
    private int user_id;
    private LocalDate date;

    public Orders(int id, int user_id, LocalDate orderDate){
        this.id  = id;
        this.user_id = user_id;
        this.date = orderDate;
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the user_id
     */
    public int getUser_id() {
        return user_id;
    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    /**
     * @return the orderDate
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param orderDate the orderDate to set
     */
    public void setDate(LocalDate orderDate) {
        this.date = orderDate;
    }
    
}
