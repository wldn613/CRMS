/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cse.oop2.crms.model;

/**
 *
 * @author mink0
 */
public class ClassRoom {
    private String roomId;    // 강의실 번호 (예: 413호)
    private String building;  // 건물명 (SFR-104, 107 특정 건물 검색용)
    private int capacity;     // 수용 인원 (SFR-106 수용 인원 50% 계산용)
    private String type;      // 강의실 또는 실습실 구분 (SFR-101)

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
