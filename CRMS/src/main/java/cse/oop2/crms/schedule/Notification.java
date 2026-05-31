/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cse.oop2.crms.schedule;

/**
 *
 * @author wooye
 */

public class Notification {
    private String userId;
    private String message;

    public Notification(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }
    public String getUserId() { return userId; }
    public String getMessage() { return message; }
}