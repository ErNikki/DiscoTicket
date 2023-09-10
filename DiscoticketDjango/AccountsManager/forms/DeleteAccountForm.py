from django import forms
from django.core.validators import MinLengthValidator

class DeleteAccountForm(forms.Form):
    username = forms.CharField(label='Username', max_length=256, validators=[MinLengthValidator(3)])
    password = forms.CharField(label = 'Password',max_length = 64, validators=[MinLengthValidator(3)])
    
