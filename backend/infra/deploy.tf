resource "null_resource" "deploy_service" {
  for_each = local.services_with_env

  triggers = {
    jar_hash = filemd5(each.value.jar)
  }

  connection {
    type        = "ssh"
    host        = aws_eip.app_eip.public_ip
    user        = "ubuntu"
    private_key = file(var.private_key_path)
    timeout     = "3m"
  }

  provisioner "remote-exec" {
    inline = [
      "sudo mkdir -p /opt/${each.key}",
      "sudo chown ubuntu:ubuntu /opt/${each.key}"
    ]
  }

  provisioner "file" {
    source      = each.value.jar
    destination = "/opt/${each.key}/app.jar"
  }

  provisioner "file" {
    content = templatefile("${path.module}/templates/systemd.service.tftpl", {
      service_name = each.key
      port         = each.value.port
      env_vars     = each.value.env
    })
    destination = "/tmp/${each.key}.service"
  }

  provisioner "remote-exec" {
    inline = [
      "sudo mv /tmp/${each.key}.service /etc/systemd/system/${each.key}.service",
      "sudo systemctl daemon-reload",
      "sudo systemctl enable ${each.key}.service",
      "sudo systemctl restart ${each.key}.service"
    ]
  }

  depends_on = [aws_eip.app_eip]
}
