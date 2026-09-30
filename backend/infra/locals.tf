locals {
  jwt_azure_env = {
    AZURE_ISSUER_URI = var.jwt_issuer
    AZURE_AUDIENCE   = var.jwt_audience
  }

  jwt_local_env = {
    LOCAL_JWT_SECRET = var.local_jwt_secret
  }

  services = {
    bff-service = {
      port   = 8080
      jar    = "../bff-service/target/bff-service-0.0.1-SNAPSHOT.jar"
      public = true
      base_env = merge(
        {
          JWT_ISSUER_URI        = var.jwt_issuer
          JWT_JWK_SET_URI       = "https://login.microsoftonline.com/f296b836-9dcb-49b9-ab6c-8e74696608f6/discovery/v2.0/keys"
          JWT_EXPECTED_AUDIENCE = var.jwt_audience
        },
        local.jwt_local_env
      )
    }
    usuarios-service = {
      port     = 8081
      jar      = "../usuarios-service/target/usuarios-service-0.0.1-SNAPSHOT.jar"
      public   = false
      base_env = {}
    }
    catalogo-service = {
      port   = 8082
      jar    = "../catalogo-service/target/catalogo-service-0.0.1-SNAPSHOT.jar"
      public = false
      base_env = {}
    }
    editoriales-service = {
      port   = 8083
      jar    = "../editoriales-service/target/editoriales-service-0.0.1-SNAPSHOT.jar"
      public = false
      # VERIFICADO: no tiene spring-boot-starter-security-oauth2-resource-server
      # en su pom.xml ni SecurityConfig.java - no valida JWT en absoluto hoy.
      # base_env = {} es correcto; agregar AZURE_* aquí no tendría efecto.
      base_env = {}
    }
    inventario-service = {
      port     = 8084
      jar      = "../inventario-service/target/inventario-service-0.0.1-SNAPSHOT.jar"
      public   = false
      base_env = local.jwt_azure_env
    }
    carrito-service = {
      port   = 8085
      jar    = "../carrito-service/target/carrito-service-0.0.1-SNAPSHOT.jar"
      public = false
      base_env = merge(local.jwt_azure_env, local.jwt_local_env)
    }
    pedidos-service = {
      port   = 8086
      jar    = "../pedidos-service/target/pedidos-service-0.0.1-SNAPSHOT.jar"
      public = false
      base_env = merge(local.jwt_azure_env, local.jwt_local_env)
    }
    pagos-service = {
      port   = 8087
      jar    = "../pagos-service/target/pagos-service-0.0.1-SNAPSHOT.jar"
      public = false
      # VERIFICADO: tiene la dependencia oauth2-resource-server y un
      # SecurityConfig.java propio, pero hace anyRequest().permitAll() sin
      # oauth2ResourceServer(...) configurado - no valida JWT hoy (igual que
      # tenía pedidos-service antes de agregarle el dual-issuer resolver).
      # base_env = {} es correcto; agregar AZURE_*/LOCAL_JWT_* aquí no
      # tendría efecto hasta que se actualice su SecurityConfig.java.
      base_env = {}
    }
  }

  services_with_env = {
    for name, svc in local.services : name => merge(
      svc,
      { env = merge(svc.base_env, lookup(var.service_env_vars, name, {})) }
    )
  }
}
