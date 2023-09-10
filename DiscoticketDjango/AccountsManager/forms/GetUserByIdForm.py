from django import forms
from django.core.validators import validate_email, MinLengthValidator
from ..validators.UserValidator import *

class GetUserByIdForm(forms.Form):
    id = forms.IntegerField()