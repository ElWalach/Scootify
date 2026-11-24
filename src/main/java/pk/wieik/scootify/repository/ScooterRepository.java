package pk.wieik.scootify.repository;

import pk.wieik.scootify.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScooterRepository {
    public record ScooterRow(int id, String kodQr, String model, int maxPredkosc,
                             String strefa, int bateria, String status,
                             double szerokosc, double dlugosc) {}

    public List<ScooterRow> findAll() throws SQLException {
        String sql = """
            SELECT h.id, h.kod_qr,
                   m.producent || ' ' || m.nazwa AS model,
                   m.max_predkosc,
                   s.nazwa AS strefa,
                   h.bateria, h.status, h.szerokosc, h.dlugosc
            FROM hulajnogi h
            JOIN modele m ON m.id = h.model_id
            JOIN strefy s ON s.id = h.strefa_id
            ORDER BY h.id
            LIMIT 200
            """;
        List<ScooterRow> rows = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new ScooterRow(
                        rs.getInt("id"), rs.getString("kod_qr"), rs.getString("model"),
                        rs.getInt("max_predkosc"), rs.getString("strefa"),
                        rs.getInt("bateria"), rs.getString("status"),
                        rs.getDouble("szerokosc"), rs.getDouble("dlugosc")));
            }
        }
        return rows;
    }
}