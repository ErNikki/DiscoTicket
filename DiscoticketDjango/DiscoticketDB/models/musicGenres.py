from django.db import models

# Create your models here.

class MusicGenres(models.Model):
    type=models.CharField(max_length=20,primary_key=True)