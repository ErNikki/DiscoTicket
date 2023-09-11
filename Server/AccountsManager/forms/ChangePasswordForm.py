from django import forms
from django.core.validators import validate_email, MinLengthValidator
from ..validators.UserValidator import *

class ChangePasswordForm(forms.Form):
    oldPassword = forms.CharField(label = '',max_length = 64, validators=[MinLengthValidator(3)],
							   widget= forms.TextInput(attrs={'class':'password','id':'field', 'placeholder':"Password*", "type": "password"}))
    newPassword = forms.CharField(label = '',max_length = 64, validators=[MinLengthValidator(3)],
							   widget= forms.TextInput(attrs={'class':'password','id':'field', 'placeholder':"Password*", "type": "password"}))