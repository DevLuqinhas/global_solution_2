package br.com.fiap.gs.repository;

import br.com.fiap.gs.model.SessionToken;

import java.sql.*;
import java.util.Date;

public class SessionRepository {

    private final ConnectionFactory cf = new ConnectionFactory();

    public void salvar(SessionToken st) throws SQLException {
        String sql = """
            INSERT INTO SESSION_TOKENS (TOKEN, ID_USUARIO, EXPIRES_AT, CREATED_AT)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = cf.getConnection();
             PreparedStatement stt = con.prepareStatement(sql)) {

            stt.setString(1, st.getToken());
            stt.setLong(2, st.getUserId());
            stt.setTimestamp(3, new Timestamp(st.getExpiresAt().getTime()));
            stt.setTimestamp(4, new Timestamp(st.getCreatedAt().getTime()));
            stt.executeUpdate();
        }
    }

    public Long validarToken(String token) throws SQLException {
        String sql = "SELECT ID_USUARIO, EXPIRES_AT FROM SESSION_TOKENS WHERE TOKEN = ?";

        try (Connection con = cf.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, token);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Timestamp exp = rs.getTimestamp("EXPIRES_AT");
                    if (exp != null && exp.after(new Date())) {
                        return rs.getLong("ID_USUARIO");
                    }
                }
            }
        }
        return null;
    }

    public void deletar(String token) throws SQLException {
        String sql = "DELETE FROM SESSION_TOKENS WHERE TOKEN = ?";

        try (Connection con = cf.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, token);
            st.executeUpdate();
        }
    }
}
