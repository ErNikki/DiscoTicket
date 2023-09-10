from django.db import models
from django.contrib.postgres.fields import ArrayField
from .clubs import Clubs
from .musicGenres import MusicGenres

class Events(models.Model):
    club= models.ForeignKey(Clubs, on_delete=models.CASCADE)
    date = models.DateTimeField()
    description = models.TextField()
    MusicGenres = models.ManyToManyField(MusicGenres)
    name=models.CharField(max_length=15, unique=True, blank = False, null = False)
    #imgUrl=models.ImageField(upload_to = 'images/events')
    imgUrl=models.URLField(max_length=200)