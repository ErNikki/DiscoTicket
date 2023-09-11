from django.db import models
from django.conf import settings 
from .reviews import Reviews


class ImagesReview(models.Model):
    image=models.ImageField(upload_to = 'images/reviews',blank=True, null=True)
    review=models.ForeignKey(Reviews, on_delete=models.CASCADE)