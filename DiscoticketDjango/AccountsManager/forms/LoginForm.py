from django import forms
from django.core.validators import validate_email, MinLengthValidator
from ..validators.UserValidator import *

class LoginForm(forms.Form):
    username = forms.CharField(label='', max_length=32, validators=[MinLengthValidator(3)],
							   widget= forms.TextInput(attrs={'class':'username','id':'field', 'placeholder':"Username*"}))
    password = forms.CharField(label = '',max_length = 64, validators=[MinLengthValidator(3)],
							   widget= forms.TextInput(attrs={'class':'password','id':'field', 'placeholder':"Password*", "type": "password"}))
    