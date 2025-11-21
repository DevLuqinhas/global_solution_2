package br.com.fiap.gs.repository;

import br.com.fiap.gs.model.User;

import java.sql.*;

public class UserRepository {

    private final ConnectionFactory cf = new ConnectionFactory();

    public void salvar(User user) throws SQLException {
        String sql = """
            INSERT INTO USERS (NOME, EMAIL, SENHA_HASH)
            VALUES (?, ?, ?)
        """;

        try (Connection con = cf.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, user.getNome());
            st.setString(2, user.getEmail());
            st.setString(3, user.getSenhaHash());
            st.executeUpdate();
        }
    }

    public User buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE EMAIL = ?";

        try (Connection con = cf.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public User buscarPorId(long id) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE ID_USUARIO = ?";

        try (Connection con = cf.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    private User mapear(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("ID_USUARIO"));
        u.setNome(rs.getString("NOME"));
        u.setEmail(rs.getString("EMAIL"));
        u.setSenhaHash(rs.getString("SENHA_HASH"));
        return u;
    }
}
