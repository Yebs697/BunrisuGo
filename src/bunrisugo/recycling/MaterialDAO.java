package bunrisugo.recycling;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 재활용 소재 관련 데이터베이스 조회를 담당하는 클래스
 */
public class MaterialDAO {
    private Connection conn;
    
    public MaterialDAO(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * 소재 이름을 가져옵니다.
     * @param materialId 소재 ID
     * @return 소재 이름
     */
    public String getMaterialName(int materialId) {
        String name = "material.name";
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT name "
                + "FROM materials "
                + "WHERE id = ?"
            );
            stmt.setInt(1, materialId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }
    
    /**
     * 대상 품목 목록을 가져옵니다.
     * @param materialId 소재 ID
     * @return 품목 이름 리스트
     */
    public List<String> getItems(int materialId) {
        List<String> itemList = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT name "
                + "FROM items "
                + "WHERE material_id = ? "
                + "ORDER BY id"
            );
            stmt.setInt(1, materialId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String itemName = rs.getString("name");
                itemList.add(itemName);
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return itemList;
    }
    
    /**
     * 예외 소재 목록을 가져옵니다.
     * @param materialId 소재 ID
     * @return 예외 소재 리스트 (형식: "notes,content")
     */
    public List<String> getExMaterial(int materialId) {
        List<String> exMaterials = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT notes, content "
                + "FROM item_notes "
                + "WHERE materials_id = ? "
                + "ORDER BY note_order"
            );
            stmt.setInt(1, materialId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String note = rs.getString("notes");
                String content = rs.getString("content");
                exMaterials.add(note + "," + content);		
            }
        } catch (SQLException e) {
            e.printStackTrace();//디버깅용 추후 삭제 혹은 변경
        }
        return exMaterials;
    }
    
    /**
     * 공통 배출 방법을 가져옵니다.
     * @param materialId 소재 ID
     * @return 배출 방법 설명 리스트
     */
    public List<String> getMethod(int materialId) {
        List<String> commonMethod = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT description "
                + "FROM common_methods "
                + "WHERE material_id = ? "
                + "ORDER BY step_number"
            );
            stmt.setInt(1, materialId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String description = rs.getString("description");
                commonMethod.add(description);
            }
        } catch (SQLException e) {
            e.printStackTrace();//디버깅용 추후 삭제 혹은 변경
        }
        return commonMethod;
    }
    
    /**
     * 예외 배출 방법을 가져옵니다.
     * @param materialId 소재 ID
     * @return 품목별 예외 배출 방법 맵 (키: 품목명, 값: 배출 방법 설명 리스트)
     */
    public Map<String, List<String>> getExMethod(int materialId) {
        Map<String, List<String>> exMethodMap = new LinkedHashMap<>();
        
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT i.name AS item_name, e.description "
                +"FROM exeption_methods e "
                +"JOIN items i ON e.items_id = i.id "
                +"WHERE i.material_id = ? "
                +"ORDER BY i.id, e.step_number"
            );
            stmt.setInt(1, materialId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String itemName = rs.getString("item_name");
                String description = rs.getString("description");
                
                exMethodMap.computeIfAbsent(itemName, k -> new ArrayList<>()).add(description);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return exMethodMap;
    }
    
    /**
     * 검색 키워드로 material_id를 찾습니다.
     * @param keyword 검색 키워드
     * @return material_id, 검색 결과가 없으면 -1
     */
    public int searchMaterialId(String keyword) {
        int materialId = -1;
        
        // 가능한 테이블 이름과 컬럼 이름 조합 시도
        String[] tableNames = {"searchItem", "serchItem", "SearchItem", "serchitem", "searchitem"};
        String[] columnNames = {"serchkeyword", "searchkeyword", "serchKeyword", "searchKeyword"};
        
        for (String tableName : tableNames) {
            for (String columnName : columnNames) {
                try {
                    // 정확한 일치로 검색
                    PreparedStatement stmt = conn.prepareStatement(
                        "SELECT material_id "
                        + "FROM " + tableName + " "
                        + "WHERE " + columnName + " = ? "
                        + "LIMIT 1"
                    );
                    stmt.setString(1, keyword);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        materialId = rs.getInt("material_id");
                        rs.close();
                        stmt.close();
                        return materialId;
                    }
                    rs.close();
                    stmt.close();
                    
                    // 정확한 일치가 없으면 부분 일치로 검색
                    stmt = conn.prepareStatement(
                        "SELECT material_id "
                        + "FROM " + tableName + " "
                        + "WHERE " + columnName + " LIKE ? "
                        + "LIMIT 1"
                    );
                    stmt.setString(1, "%" + keyword + "%");
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        materialId = rs.getInt("material_id");
                        rs.close();
                        stmt.close();
                        return materialId;
                    }
                    rs.close();
                    stmt.close();
                } catch (SQLException e) {
                    // 이 조합이 실패했으므로 다음 조합 시도
                    continue;
                }
            }
        }
        
        return materialId;
    }

}

