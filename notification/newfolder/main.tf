provider "aws"{
    region = "ap-south-1"

}


resource "aws_vpc" "myvpc" {
    cidr_block = "12.0.0.0/16"
}

resource "aws_subnet" "public" {
    vpc_id = aws_vpc.myvpc.id
    cidr_block = "12.0.1.0/24"
}

resource "aws_