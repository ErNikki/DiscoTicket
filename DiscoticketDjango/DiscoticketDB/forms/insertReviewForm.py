from django import forms
from django.core.validators import validate_email, MinLengthValidator
from multiupload.fields import MultiFileField, MultiMediaField, MultiImageField


class InsertReviewForm(forms.Form):
    userId = forms.IntegerField()
    clubId = forms.IntegerField()
    date = forms.DateField()
    description = forms.CharField()
    rating = forms.FloatField()
    images = MultiImageField(min_num=0, max_num=20)