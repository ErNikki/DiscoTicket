from django.db import models
from django.contrib.auth.models import User
from django.core.validators import MinValueValidator, MaxValueValidator, MinLengthValidator

class EmailConfirmationCode(models.Model):
    user = models.OneToOneField(User,unique=True, on_delete=models.CASCADE, blank = False, null = False, related_name='confCodeUser')
    code= models.SlugField(unique=True, max_length = 64, validators = [MinValueValidator(64)], blank = True, null = False)
    flag = models.BooleanField(default=False, blank=False, null=False)
    
    def __str__(self):
        return self.user.email+'\n'+self.code