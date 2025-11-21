package br.com.fiap.gs.business;

import br.com.fiap.gs.model.User;
import br.com.fiap.gs.model.SessionToken;
import br.com.fiap.gs.repository.UserRepository;
import br.com.fiap.gs.repository.SessionRepository;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class UserBusiness {

    private final UserRepository userRepo = new UserRepository();
    private final SessionRepository sessionRepo = new SessionRepository();

    public Long registrar(String nome, String email, String senha) throws Exception {
        User existente = userRepo.buscarPorEmail(email);
        if (existente != null) throw new Exception("Email já cadastrado");

        // hash com jbcrypt (cost ~= 12)
        String hash = BCrypt.hashpw(senha, BCrypt.gensalt(12));

        User novo = new User();
        novo.setNome(nome);
        novo.setEmail(email);
        novo.setSenhaHash(hash);

        userRepo.salvar(novo);
        User salvo = userRepo.buscarPorEmail(email);
        return salvo.getId();
    }

    public User login(String email, String senha) throws Exception {
        User u = userRepo.buscarPorEmail(email);
        if (u == null) throw new Exception("Email ou senha inválidos");

        boolean ok = BCrypt.checkpw(senha, u.getSenhaHash());
        if (!ok) throw new Exception("Email ou senha inválidos");

        return u;
    }

    public String gerarSessao(Long userId, int horas) throws Exception {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, horas);

        SessionToken st = new SessionToken();
        st.setToken(UUID.randomUUID().toString());
        st.setUserId(userId);
        st.setCreatedAt(new Date());
        st.setExpiresAt(c.getTime());

        sessionRepo.salvar(st);
        return st.getToken();
    }

    public Long validarToken(String token) throws Exception {
        return sessionRepo.validarToken(token);
    }

    public void logout(String token) throws Exception {
        sessionRepo.deletar(token);
    }
}
