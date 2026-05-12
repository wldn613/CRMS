package cse.oop2.crms.service;

import cse.oop2.crms.model.Reservation;
import java.util.List;

/**
 * SFR-101: 강의실 현황 조회를 위한 추상 클래스 (Template Method Pattern)
 * 일별, 주별, 월별 조회의 '틀'을 정의합니다.
 */
public abstract class RoomStatusViewer {

    // 1. 템플릿 메소드: 전체적인 조회 프로세스를 정의 (자식 클래스에서 변경 불가)
    public final void displayStatus() {
        List<Reservation> reservations = fetchData(); // 데이터 가져오기
        sortData(reservations);                       // 정렬 (추상 메소드)
        renderOutput(reservations);                   // 출력 (추상 메소드)
    }

    // 2. 공통 구현: 데이터 가져오기는 공통 로직일 확률이 높음
    protected List<Reservation> fetchData() {
        // DataRepository에서 데이터를 가져오는 로직 (팀원 C와 협업)
        return null; 
    }

    // 3. 추상 메소드: 하위 클래스(일별/주별/월별)에서 반드시 구현해야 함
    protected abstract void sortData(List<Reservation> data);
    protected abstract void renderOutput(List<Reservation> data);
}