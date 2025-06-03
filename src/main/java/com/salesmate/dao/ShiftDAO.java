package com.salesmate.dao;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.Shift;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ShiftDAO {

    
     //Lấy một ca theo ID.
   
    public Shift findById(int shiftId) {
        String sql = "SELECT shift_id, shift_name, start_time, end_time, break_time, description, is_active "
                   + "FROM SHIFT WHERE shift_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shiftId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Shift s = new Shift();
                    s.setShiftId(rs.getInt("shift_id"));
                    s.setShiftName(rs.getString("shift_name"));
                    s.setStartTime(rs.getString("start_time"));
                    s.setEndTime(rs.getString("end_time"));
                    int bt = rs.getInt("break_time");
                    if (!rs.wasNull()) {
                        s.setBreakTime(bt);
                    }
                    s.setDescription(rs.getString("description"));
                    s.setActive(rs.getInt("is_active") == 1);
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
     // Lấy tất cả ca (bao gồm inactive).
     
    public List<Shift> findAll() {
        List<Shift> list = new ArrayList<>();
        String sql = "SELECT shift_id, shift_name, start_time, end_time, break_time, description, is_active FROM SHIFT";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Shift s = new Shift();
                s.setShiftId(rs.getInt("shift_id"));
                s.setShiftName(rs.getString("shift_name"));
                s.setStartTime(rs.getString("start_time"));
                s.setEndTime(rs.getString("end_time"));
                int bt = rs.getInt("break_time");
                if (!rs.wasNull()) {
                    s.setBreakTime(bt);
                }
                s.setDescription(rs.getString("description"));
                s.setActive(rs.getInt("is_active") == 1);
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

  
    // Lấy danh sách ca đang active.
   
    public List<Shift> findAllActive() {
        List<Shift> list = new ArrayList<>();
        String sql = "SELECT shift_id, shift_name, start_time, end_time, break_time, description "
                   + "FROM SHIFT WHERE is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Shift s = new Shift();
                s.setShiftId(rs.getInt("shift_id"));
                s.setShiftName(rs.getString("shift_name"));
                s.setStartTime(rs.getString("start_time"));
                s.setEndTime(rs.getString("end_time"));
                int bt = rs.getInt("break_time");
                if (!rs.wasNull()) {
                    s.setBreakTime(bt);
                }
                s.setDescription(rs.getString("description"));
                s.setActive(true);
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    
     //Tạo mới một ca (is_active mặc định = 1).
    
    public boolean insert(Shift shift) {
        String sql = "INSERT INTO SHIFT (shift_name, start_time, end_time, break_time, description, is_active) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, shift.getShiftName());
            stmt.setString(2, shift.getStartTime());
            stmt.setString(3, shift.getEndTime());
            if (shift.getBreakTime() != null) {
                stmt.setInt(4, shift.getBreakTime());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setString(5, shift.getDescription());
            stmt.setInt(6, shift.isActive() ? 1 : 0);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

   
     //Cập nhật một ca.
  
    public boolean update(Shift shift) {
        String sql = "UPDATE SHIFT SET shift_name = ?, start_time = ?, end_time = ?, "
                   + "break_time = ?, description = ?, is_active = ? WHERE shift_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, shift.getShiftName());
            stmt.setString(2, shift.getStartTime());
            stmt.setString(3, shift.getEndTime());
            if (shift.getBreakTime() != null) {
                stmt.setInt(4, shift.getBreakTime());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setString(5, shift.getDescription());
            stmt.setInt(6, shift.isActive() ? 1 : 0);
            stmt.setInt(7, shift.getShiftId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
     //Disable một ca (set is_active = 0).
   
    public boolean disable(int shiftId) {
        String sql = "UPDATE SHIFT SET is_active = 0 WHERE shift_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shiftId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
