package com.megatech.usuariosservice.repository;

import com.megatech.usuariosservice.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByAzureOid(String azureOid);

    boolean existsByEmail(String email);

    boolean existsByAzureOid(String azureOid);
}
