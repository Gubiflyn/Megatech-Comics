package com.megatech.usuariosservice.service;

import com.megatech.usuariosservice.dto.ClienteLoginRequest;
import com.megatech.usuariosservice.dto.ClienteRegistroRequest;
import com.megatech.usuariosservice.dto.ClienteResponse;
import com.megatech.usuariosservice.exception.CredencialesInvalidasException;
import com.megatech.usuariosservice.exception.RecursoDuplicadoException;
import com.megatech.usuariosservice.model.Cliente;
import com.megatech.usuariosservice.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteResponse registrarCliente(ClienteRegistroRequest request) {
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new RecursoDuplicadoException(
                    "Ya existe un cliente con email: " + request.getEmail());
        }

        Cliente cliente = Cliente.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getNombreCompleto())
                .build();

        Cliente guardado = clienteRepository.save(cliente);
        return ClienteResponse.fromEntity(guardado);
    }

    @Transactional(readOnly = true)
    public ClienteResponse validarCredenciales(ClienteLoginRequest request) {
        Cliente cliente = clienteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CredencialesInvalidasException("Email o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), cliente.getPasswordHash())) {
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }

        return ClienteResponse.fromEntity(cliente);
    }
}
