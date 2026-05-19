package cse.oop2.crms.model.dto;

// [추가] SFR-203, SFR-301: 조교/학생 역할 구분을 위해 User 클래스 구현
// 기존 파일이 비어있어 내용을 채움
public class User {

    private String userId;
    private String password;
    private String role;    // "학생" 또는 "조교"
    private String name;

    public User(String userId, String password, String role, String name) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.name = name;
    }

    // users.txt 한 줄 → User 객체로 변환
    // 파일 형식: userId|password|role|name
    public static User fromCsv(String csv) {
        String[] data = csv.split("\\|");
        return new User(data[0], data[1], data[2], data[3]);
    }

    // users.txt 저장용 CSV 변환
    public String toCsv() {
        return String.join("|", userId, password, role, name);
    }

    // [SFR-203, SFR-301] 조교 여부 확인
    public boolean isAssistant() {
        return "조교".equals(this.role);
    }

    public String getUserId()   { return userId; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }
    public String getName()     { return name; }
}
