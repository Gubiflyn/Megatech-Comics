data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"]
  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }
}

data "aws_iam_instance_profile" "lab_profile" {
  name = var.lab_instance_profile_name
}

resource "aws_key_pair" "deployer" {
  key_name   = var.key_name
  public_key = file(var.public_key_path)
}

resource "aws_security_group" "app_sg" {
  name        = "megatech-comics-sg"
  description = "SSH solo para ti, HTTP 8080 publico (BFF), resto interno"

  ingress {
    description = "SSH solo desde tu IP"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.my_ip]
  }

  ingress {
    description = "BFF - unico puerto expuesto al API Gateway / internet"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "megatech-comics-sg" }
}

resource "aws_instance" "app" {
  ami                    = data.aws_ami.ubuntu.id
  instance_type          = var.instance_type
  key_name               = aws_key_pair.deployer.key_name
  vpc_security_group_ids = [aws_security_group.app_sg.id]
  iam_instance_profile   = data.aws_iam_instance_profile.lab_profile.name

  root_block_device {
    volume_size = 16
    volume_type = "gp3"
  }

  tags = { Name = "megatech-comics-backend" }

  user_data = <<-EOT
    #!/bin/bash
    apt-get update -y
    apt-get install -y openjdk-17-jdk
  EOT
}

resource "aws_eip" "app_eip" {
  instance = aws_instance.app.id
  domain   = "vpc"
  tags     = { Name = "megatech-comics-eip" }
}
