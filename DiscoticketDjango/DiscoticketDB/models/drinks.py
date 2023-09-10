from django.db import models
from django.contrib.postgres.fields import ArrayField

class Drinks(models.Model):
    name=models.CharField(max_length=15, unique=True, blank = False, null = False)
    ingredients=ArrayField(models.CharField(max_length=20))
    imgUrl=models.ImageField(upload_to = 'images/drinks')