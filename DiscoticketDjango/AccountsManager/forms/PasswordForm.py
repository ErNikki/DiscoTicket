from django import forms
from django.core.validators import validate_email, MinLengthValidator
from ..validators.UserValidator import *

class PasswordForm(forms.Form):
    password = forms.CharField(label = '',max_length = 64, validators=[MinLengthValidator(3)],
							   widget= forms.TextInput(attrs={'class':'password','id':'field', 'placeholder':"Password*", "type": "password"}))