package bunrisugo.point;

import java.time.LocalDateTime;

public class PointHistoryDTO {
    private int historyId;
    private String userIdentifier;
    private String changeType;
    private int pointsChange;
    private LocalDateTime changeDate;
    
    public PointHistoryDTO() {
    }
    
    public PointHistoryDTO(String userIdentifier, String changeType, int pointsChange, LocalDateTime changeDate) {
        this.userIdentifier = userIdentifier;
        this.changeType = changeType;
        this.pointsChange = pointsChange;
        this.changeDate = changeDate;
    }
    
    public int getHistoryId() {
        return historyId;
    }
    
    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }
    
    public String getUserIdentifier() {
        return userIdentifier;
    }
    
    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }
    
    public String getChangeType() {
        return changeType;
    }
    
    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
    
    public int getPointsChange() {
        return pointsChange;
    }
    
    public void setPointsChange(int pointsChange) {
        this.pointsChange = pointsChange;
    }
    
    public LocalDateTime getChangeDate() {
        return changeDate;
    }
    
    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }
}




