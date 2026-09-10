#!/bin/bash

# ==========================================================
# Megatech Comics - Variables para Amazon RDS MySQL
# ARCHIVO DE EJEMPLO - NO COLOCAR CONTRASEÑAS REALES AQUÍ
# ==========================================================

RDS_HOST="TU_ENDPOINT_RDS"
RDS_PORT="3306"
RDS_USER="TU_USUARIO_RDS"
RDS_PASSWORD="TU_PASSWORD_RDS"

# Carrito
export CARRITO_DB_URL="jdbc:mysql://${RDS_HOST}:${RDS_PORT}/megatech_carrito?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export CARRITO_DB_USERNAME="${RDS_USER}"
export CARRITO_DB_PASSWORD="${RDS_PASSWORD}"

# Catálogo
export CATALOGO_DB_URL="jdbc:mysql://${RDS_HOST}:${RDS_PORT}/megatech_catalogo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export CATALOGO_DB_USERNAME="${RDS_USER}"
export CATALOGO_DB_PASSWORD="${RDS_PASSWORD}"

# Editoriales
export EDITORIALES_DB_URL="jdbc:mysql://${RDS_HOST}:${RDS_PORT}/megatech_editoriales?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export EDITORIALES_DB_USERNAME="${RDS_USER}"
export EDITORIALES_DB_PASSWORD="${RDS_PASSWORD}"

# Inventario
export INVENTARIO_DB_URL="jdbc:mysql://${RDS_HOST}:${RDS_PORT}/megatech_inventario?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export INVENTARIO_DB_USERNAME="${RDS_USER}"
export INVENTARIO_DB_PASSWORD="${RDS_PASSWORD}"

# Pagos
export PAGOS_DB_URL="jdbc:mysql://${RDS_HOST}:${RDS_PORT}/megatech_pagos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export PAGOS_DB_USERNAME="${RDS_USER}"
export PAGOS_DB_PASSWORD="${RDS_PASSWORD}"

# Pedidos
export PEDIDOS_DB_URL="jdbc:mysql://${RDS_HOST}:${RDS_PORT}/megatech_pedidos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export PEDIDOS_DB_USERNAME="${RDS_USER}"
export PEDIDOS_DB_PASSWORD="${RDS_PASSWORD}"

# Usuarios
export USUARIOS_DB_URL="jdbc:mysql://${RDS_HOST}:${RDS_PORT}/usuarios_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export USUARIOS_DB_USERNAME="${RDS_USER}"
export USUARIOS_DB_PASSWORD="${RDS_PASSWORD}"