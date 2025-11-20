package attendance;

public class AttendanceDTO {
    
    private boolean todayAttended;    // 오늘 출석 했는지 ("V" 또는 "X")
    private int consecutiveDays;      // 연속 출석일
    private int cumulativeDays;       // 누적 출석일
    private int totalPoints;          // 보유 포인트

    
    public boolean isTodayAttended() { return todayAttended; }
    public void setTodayAttended(boolean todayAttended) { this.todayAttended = todayAttended; }
    
    public int getConsecutiveDays() { return consecutiveDays; }
    public void setConsecutiveDays(int consecutiveDays) { this.consecutiveDays = consecutiveDays; }
    
    public int getCumulativeDays() { return cumulativeDays; }
    public void setCumulativeDays(int cumulativeDays) { this.cumulativeDays = cumulativeDays; }
    
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
}