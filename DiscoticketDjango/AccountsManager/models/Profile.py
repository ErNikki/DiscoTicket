from django.db import models
from django.core.validators import MinValueValidator, MaxValueValidator, MinLengthValidator, validate_email
from django.contrib.auth.models import User
from django.db.models.signals import post_save
from django.dispatch import receiver

# Create your models here.

class Profile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, blank = True, null = True, related_name='profile')
    username = models.CharField(max_length=32, validators=[MinLengthValidator(3)], unique=True)
    email = models.CharField(max_length = 64, validators=[validate_email], unique=True)
    validation_code = models.SlugField(unique=True, max_length = 64, validators = [MinValueValidator(64)], blank = True, null = True)
    password = models.CharField(validators=[MinLengthValidator(6)], max_length = 64)
    def __str__(self):
        return self.username
    """
    @receiver(post_save, sender=User)
    def create_user_profile(sender, instance, created, **kwargs):
        if created:
            Profile.objects.create(user=instance)



    @receiver(post_save, sender=User)
    def save_user_profile(sender, instance, **kwargs):
        instance.profile.username = instance.username
        instance.profile.email = instance.email
        instance.profile.save()
    """