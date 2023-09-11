from base64 import b64encode
import os
from django.contrib.auth.models import User
from . import models
from django.conf import settings

def generateRandomString(length):
    return b64encode(os.urandom(length), b'__').decode('utf-8')[:length]

def createUserFromProfile(profile):
    username = profile.username
    user = User(username = profile.username, email = profile.email)
    user.set_password(profile.password)
    username = profile.username
    profile.delete()
    user.save()
    user = User.objects.filter(username = username)[0]    
    user.save()
    user = User.objects.filter(username = username)[0]
    return user

def updateUserFromProfile(profile):
    username = profile.username
    user = User.objects.filter(username = profile.username)[0]  
    user.set_password(profile.password)
    user.save()
    user = User.objects.filter(username = username)[0]   
    user.save()
    user = User.objects.filter(username = username)[0]
    profile.password = ''
    profile.validation_code = ''
    profile.save()
    return user
    