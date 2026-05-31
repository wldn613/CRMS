/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cse.oop2.crms.schedule;

/**
 *
 * @author wooye
 */

public interface NotificationIterator {
    boolean hasNext();
    Notification next();
}

class CombinedNotificationIterator implements NotificationIterator {
    private java.util.List<Notification> list;
    private int position = 0;

    public CombinedNotificationIterator(java.util.List<Notification> list) {
        this.list = list;
    }

    @Override
    public boolean hasNext() { return position < list.size(); }

    @Override
    public Notification next() {
        return this.hasNext() ? list.get(position++) : null;
    }
}