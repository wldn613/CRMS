/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cse.oop2.crms.model.dto;

public class User {
    // 1. 기존 팀원이 작성해 둔 필드들이 있다면 그대로 두세요.
    // 2. 로그인에 필수적인 4대 필드가 누락되어 있다면 아래와 같이 선언해 줍니다.
    private String id;
    private String password;
    private String name;
    private String role;

    /**
     * [해결책 1] 기본 생성자만 쓰던 팀원 코드를 위해, 
     * 질문자님 UI가 사용하는 4개짜리 오버로딩 생성자를 추가합니다.
     */
    public User(String id, String password, String name, String role) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    // 팀원이 필요로 하는 기본 생성자도 안전하게 유지합니다.
    public User() {
    }

    /**
     * [해결책 2] LoginPanel과 MainTestApp이 찾는 필수 Getter 메소드들 복원
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}