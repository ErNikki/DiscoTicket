from django.db import models
from django.contrib.auth.models import User
from django.contrib.postgres.fields import ArrayField
from .clubs import Clubs
#from .reviews import Reviews

class Order(models.Model):
    user=models.ForeignKey(User, blank=True, null=True, on_delete=models.CASCADE)
    createdAt=models.FloatField()
    date=models.DateField(null=True, blank=True)
    club=models.ForeignKey(Clubs, on_delete=models.CASCADE)
    tableIds=models.CharField(null=True, blank=True)
    tableIdsList=ArrayField(models.IntegerField(null=True, blank=True),default=list, blank=True, null=True)
    