from django.db import models
from .gpsCoords import GPSCoords
from .musicGenres import MusicGenres
#from .reviews import Reviews

class Clubs(models.Model):
    name=models.CharField(max_length=15, unique=True, blank = False, null = False)
    address=models.CharField(max_length=200, unique=True, blank = False, null = False)
    rating=models.FloatField(blank = False, null = False)
    imgUrl=models.URLField(max_length=300)
    description=models.TextField()
    locationType=models.CharField()
    simpleTicketPrice=models.DecimalField(max_digits=6,decimal_places=2)
    tableTicketPrice=models.DecimalField(max_digits=6,decimal_places=2)
    gpsCoords=models.OneToOneField(GPSCoords, on_delete=models.CASCADE)
    MusicGenres=models.ManyToManyField(MusicGenres)
    #reviews=models.ForeignKey(Reviews, blank = True ,on_delete=models.CASCADE)

    