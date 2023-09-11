from django import forms

class GetTableIdsForm(forms.Form):
    id = forms.IntegerField()
    date = forms.DateField()