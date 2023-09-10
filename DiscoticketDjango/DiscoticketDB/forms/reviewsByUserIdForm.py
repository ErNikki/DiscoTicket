from django import forms

class ReviewsByUserIdForm(forms.Form):
    id = forms.IntegerField()