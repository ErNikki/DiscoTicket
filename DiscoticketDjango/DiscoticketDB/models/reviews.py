from django.db import models
from django.contrib.auth.models import User
from .clubs import Clubs
from django.conf import settings 
from django.contrib.postgres.fields import ArrayField


class Reviews(models.Model):
    user=models.ForeignKey(User, blank=True, null=True, on_delete=models.CASCADE)
    club=models.ForeignKey(Clubs, on_delete=models.CASCADE)
    date=models.DateField()
    description=models.TextField(blank=True, null=True)
    rating=models.FloatField()
    images=models.ImageField(upload_to = 'images/reviews',blank=True, null=True)