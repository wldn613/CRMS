# CRMS
소프트웨어공학(키스톤) 팀 프로젝트 / 강의실 예약 시스템

CRMS.java가 Main임.


cse.oop2.crms.model (팀원 A, B 담당)
내용: 시스템에서 다루는 데이터 객체들
예시: User.java, Room.java, Reservation.java

cse.oop2.crms.service (팀원 A, B, C 담당)
내용: 핵심 비즈니스 로직 및 인터페이스 (SFR-602 추상화 핵심)
예시: ReservationService.java (인터페이스), ReservationServiceImpl.java (구현체)

cse.oop2.crms.repository (팀원 B, C 담당)
내용: 파일 기반 데이터 저장 및 로드 (SFR-603 Singleton)
예시: FileDatabase.java, DataRepository.java

cse.oop2.crms.ui (팀원 D 담당)
내용: Java Swing 화면 구성 요소 (SFR-602 Interface 기반 UI)
예시: MainFrame.java, LoginPanel.java, BookingPanel.java

cse.oop2.crms.util (팀원 C, D 담당)
내용: 공통 도구 및 알림 기능 (SFR-401 Iterator)
예시: NotificationManager.java, DateValidator.java