from django import forms

class ClubByIdForm(forms.Form):
    id = forms.IntegerField()