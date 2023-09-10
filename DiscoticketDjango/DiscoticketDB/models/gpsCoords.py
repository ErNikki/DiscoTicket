from django.db import models

# Create your models here.

class GPSCoords(models.Model):
    x=models.FloatField(blank = False, null = False)
    y=models.FloatField(blank = False, null = False)
    
    class Meta:
        unique_together = (("x", "y"),)
    