from django import forms
from django.core.validators import validate_email, MinLengthValidator
from ..validators.UserValidator import *

class MailForm(forms.Form):
    email = forms.CharField(label='', max_length = 256, validators=[validate_email],
							widget= forms.TextInput(attrs={'class':'email','id':'field', 'placeholder':"Email*"}))