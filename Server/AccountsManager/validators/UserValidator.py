from django.contrib.auth.models import User
from django.core.exceptions import ValidationError

from AccountsManager.models.Profile import Profile


def username_already_exists(value): 
    #if Profile.objects.filter(username=value).exists():
        #raise ValidationError('Username already exists.')
    if User.objects.filter(username=value).exists():
        raise ValidationError('Username already exists.')
        
def username_not_exists(value): 
    if not User.objects.filter(username=value).exists():
        raise ValidationError('Username does not exist.')

def email_already_exists(value):
    if User.objects.filter(email=value).exists():
        raise ValidationError('Email already exists.')
