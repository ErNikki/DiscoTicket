from django import forms

class ReviewsByClubIdForm(forms.Form):
    id = forms.IntegerField()