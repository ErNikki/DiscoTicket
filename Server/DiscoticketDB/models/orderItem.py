from django.db import models
from .order import Order
from django.contrib.auth.models import User
#from .reviews import Reviews

class OrderItem(models.Model):
    name=models.CharField(max_length=100, blank = False, null = False)
    quantity=models.IntegerField()
    unitaryPrice=models.FloatField()
    type=models.CharField()
    order=models.ForeignKey(Order, on_delete=models.CASCADE)
    